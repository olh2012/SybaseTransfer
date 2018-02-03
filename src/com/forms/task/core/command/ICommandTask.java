package com.forms.task.core.command;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.forms.task.core.ITask;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 根据命令行生成任务的接口<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public interface ICommandTask {
	
	/**
	 * 在命令行选项中的名称
	 */
	public String getOption();
	
	/**
	 * 命令行选项注释
	 */
	public String getComment();

	/**
	 * 添加命令行参数
	 * @param options
	 */
	public void addCommandOptions(Options options);
	
	/**
	 * 根据命令行获取任务列表
	 * @param commandLine
	 * @return
	 */
	public List<ITask> getTaskList(CommandLine commandLine);
}
