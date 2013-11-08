package com.forms.task.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.forms.platform.core.init.InitManage;
import com.forms.platform.core.logger.CommonLogger;
import com.forms.platform.core.spring.util.SpringHelp;
import com.forms.platform.core.spring.util.SpringUtil;
import com.forms.platform.core.util.Tool;
import com.forms.task.core.base.TaskGroup;
import com.forms.task.core.command.CommandHelp;
import com.forms.task.core.command.ICommandTask;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 任务管理类<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-7<br>
 */
public class TaskManager {

	private static final Map<String, ITask> taskMap = new LinkedHashMap<String, ITask>();
	private static volatile String help = null;
	private static volatile boolean hasInit = false;
	
	/**
	 * 初始化
	 */
	public static void initialize(){
		if(!hasInit){
			if(!SpringHelp.hasInit()){
				new ClassPathXmlApplicationContext("spring/applicationContext.xml");
				InitManage.initialize();
			}
			Map<String, ITask> map = SpringUtil.getBeansOfType(ITask.class);
			if(null != map){
				for(ITask task : map.values()){
					task.register();
				}
			}
			if(!taskMap.isEmpty()){
				StringBuilder sb = new StringBuilder("list of tasks：\n");
				int maxLength = 0;
				for(String key : taskMap.keySet()){
					maxLength = Math.max(maxLength, key.length());
				}
				maxLength += 2;
				for(Map.Entry<String, ITask> entry : taskMap.entrySet()){
					sb.append(entry.getKey()).append(":").append(Tool.STRING.repeat(" ", maxLength-entry.getKey().length())).append(entry.getValue().getComment()).append("\n");
				}
				help = sb.toString();
			}
			hasInit = true;
		}
	}
	
	/**
	 * 获取任务列表，获取失败时打印帮助信息
	 * @param cl
	 * @param options
	 * @param helpOption
	 * @return
	 */
	public static List<ITask> getTaskList(CommandLine cl, Options options, String helpOption){
		boolean print = false;
		if(cl == null){
			print = true;
		}else if(cl.hasOption(helpOption)){
			print = true;
		}else{
			List<ITask> taskList = getTaskList(cl);
			if(null != taskList && !taskList.isEmpty()){
				return taskList;
			}else{
				print = true;
			}
		}
		if(print){
			HelpFormatter hf = new HelpFormatter();
			hf.setOptionComparator(new Comparator<Object>(){//覆盖默认的命令行参数打印顺序
				public int compare(Object o1, Object o2) {
					return 0;
				}
			});
			hf.printHelp("Options", "", options, help);
		}
		return null;
	}
	
	/**
	 * 执行任务
	 * @param taskList
	 * @param count
	 * @return
	 */
	public static int execute(List<ITask> taskList, int count){
		if(null != taskList && !taskList.isEmpty()){
			TaskGroup task = new TaskGroup();
			task.setThreadCount(count);
			task.setTaskList(taskList);
			long start = System.nanoTime();
			CommonLogger.info("提交了"+taskList.size()+"个任务，开始执行...");
			int rs = task.call();
			long time = System.nanoTime()-start;
			CommonLogger.info("任务执行完成，执行时间："+time/1000000000+" s,"+time/1000000+" ms,"+time+" ns");
			return rs;
		}
		return 0;
	}
	
	/**
	 * 根据任务ID获取任务
	 * @param taskId
	 * @return
	 */
	public static ITask getTask(String taskId){
		ITask task = taskMap.get(taskId);
		return task;
	}
	
	/**
	 * 设置命令行参数
	 * @param options
	 */
	public static void setCommandOptions(Options options, String[] args){
		List<String> opts = new ArrayList<String>();
		if(null != args && 0 != args.length){
			for(String arg : args){
				if(null != arg && arg.startsWith("-") && arg.length() >= 2){
					opts.add(arg.substring(1));
				}
			}
		}
		for(ICommandTask ct : getCommandTaskList()){
			CommandHelp.addOption(options, ct.getOption(), null, false, ct.getComment());
			if(opts.contains(ct.getOption())){
				ct.addCommandOptions(options);	
			}
		}
	}
	
	/**
	 * 注册任务
	 * @param taskId
	 * @param task
	 */
	public static void register(String taskId, ITask task){
		if(null != taskId && null != task){
			taskId = taskId.trim();
			if(null != taskMap.get(taskId)){
				CommonLogger.error("发现多个ID为"+taskId+"的任务，请检查Spring任务配置...");
			}else{
				taskMap.put(taskId, task);	
			}
		}
	}
	
	/**
	 * 根据命令行获取任务
	 * @param commandLine
	 * @return
	 */
	private static List<ITask> getTaskList(CommandLine commandLine){
		String[] tasks = commandLine.getArgs();
		List<ITask> taskList = new ArrayList<ITask>();
		for(ICommandTask ct : getCommandTaskList()){
			List<ITask> tl = ct.getTaskList(commandLine);
			if(null != tl && !tl.isEmpty()){
				taskList.addAll(tl);	
			}
		}
		for(int i=0,l=tasks.length; i<l; i++){
			ITask task = TaskManager.getTask(tasks[i]);
			if(null != task){
				taskList.add(task);
			}
		}
		return taskList;
	}
	
	private static List<ICommandTask> getCommandTaskList(){
		try{
			return SpringHelp.getBeanslistOfType(ICommandTask.class);
		}catch(Exception ignore){
			return new ArrayList<ICommandTask>();
		}
	}
}
