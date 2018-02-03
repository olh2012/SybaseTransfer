package com.forms.task.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.forms.platform.core.init.InitManage;
import com.forms.platform.core.logger.CommonLogger;
import com.forms.platform.core.spring.util.SpringHelp;
import com.forms.platform.core.spring.util.SpringUtil;
import com.forms.platform.core.util.Tool;
import com.forms.task.core.base.TaskGroup;

/**
 * Project : Sybase数据库迁移 <br>
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
	public static void initialize(String configFile){
		if(!hasInit){
			if(!SpringHelp.hasInit()){
				if(null == configFile){
					new ClassPathXmlApplicationContext("spring/applicationContext.xml");
				}else{
					new FileSystemXmlApplicationContext(configFile);
				}
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
	 * 获取任务帮助信息
	 * @return
	 */
	public static String getTaskHelpInfo(){
		return help;
	}
}
