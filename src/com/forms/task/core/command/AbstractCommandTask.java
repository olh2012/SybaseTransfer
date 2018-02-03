package com.forms.task.core.command;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 命令行生成任务接口的抽象实现类<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public abstract class AbstractCommandTask implements ICommandTask{

	private String option;
	
	private String comment;

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
