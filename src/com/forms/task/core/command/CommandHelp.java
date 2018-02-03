package com.forms.task.core.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import com.forms.platform.core.exception.Throw;
import com.forms.platform.core.spring.util.SpringHelp;
import com.forms.task.core.ITask;
import com.forms.task.core.TaskManager;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 命令行帮助类<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public class CommandHelp {
	
	/**
	 * 动态设置命令行参数
	 * @param options
	 * @param args
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
	 * 根据命令行获取任务列表，获取失败时打印帮助信息
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
			hf.printHelp("Options", "", options, TaskManager.getTaskHelpInfo());
		}
		return null;
	}
	
	/**
	 * 添加命令行参数
	 * @param opts
	 * @param opt
	 * @param longOpt
	 * @param hasArg
	 * @param desc
	 */
	public static void addOption(Options opts, String opt, String longOpt, boolean hasArg, String desc){
		if(opts.hasOption(opt)){
			Throw.throwException("已经设置名称为 "+opt+" 的命令行参数");
		}else if(null != longOpt && opts.hasOption(longOpt)){
			Throw.throwException("已经设置名称为 "+longOpt+" 的命令行参数");
		}else{
			opts.addOption(opt, longOpt, hasArg, desc);
		}
	}

	/**
	 * 解析命令行参数
	 * @param opts
	 * @param args
	 * @return
	 */
	public static CommandLine parseCommandOptions(Options opts, String[] args){
		try{
			BasicParser parser = new BasicParser(); 
			return parser.parse(opts, args);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 解析命令行参数
	 * @param cl
	 * @param option
	 * @param defaultValue
	 * @return
	 */
	public static String parseCommandOption(CommandLine cl, String option, String defaultValue){
		try{
			return cl.getOptionValue(option);
		}catch(Exception e){}
		return defaultValue;
	}
	
	/**
	 * 解析整型命令行参数
	 * @param cl
	 * @param option
	 * @param defaultValue
	 * @return
	 */
	public static int parseIntCommandOption(CommandLine cl, String option, int defaultValue){
		try{
			return Integer.parseInt(cl.getOptionValue(option));
		}catch(Exception e){}
		return defaultValue;
	}
	
	/**
	 * 解析Boolean型命令行参数
	 * @param cl
	 * @param option
	 * @param defaultValue
	 * @return
	 */
	public static boolean parseBooleanCommandOption(CommandLine cl, String option, boolean defaultValue){
		try{
			return Boolean.parseBoolean(cl.getOptionValue(option));
		}catch(Exception e){}
		return defaultValue;
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
