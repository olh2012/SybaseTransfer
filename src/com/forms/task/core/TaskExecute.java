package com.forms.task.core;

import java.util.List;

import org.apache.commons.cli.CommandLine;

import com.forms.task.core.command.CommandHelp;
import com.forms.task.core.command.Options;



/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 任务执行类<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-7<br>
 */
public class TaskExecute {
	
	public static void main(String[] args) throws Exception {
		TaskManager.initialize();
		Options opts = setCommandOptions(args);
		CommandLine cl = CommandHelp.parseCommandOptions(opts, args);
		List<ITask> taskList = TaskManager.getTaskList(cl, opts, "h");
		TaskManager.execute(taskList, CommandHelp.parseIntCommandOption(cl, "tn", 1));
	}
	
	/**
	 * 设置命令行参数
	 * @return
	 */
	private static Options setCommandOptions(String[] args){
		Options opts = new Options();
		opts.addOption("h", "help", false, "打印帮助信息");
		opts.addOption("tn", "threadNumber", true, "提交多个任务时，并发执行的线程数，小于或等于1时表示串行执行");
		TaskManager.setCommandOptions(opts, args);
		return opts;
	}
}
