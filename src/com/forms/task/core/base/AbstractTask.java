package com.forms.task.core.base;

import com.forms.task.core.ITask;
import com.forms.task.core.TaskManager;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 任务的抽象实现类<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-7<br>
 */
public abstract class AbstractTask implements ITask{

	private String taskId;
	
	private String comment;
	
	/**
	 * 注册任务
	 */
	public void register() {
		if(null != taskId){
			TaskManager.register(taskId, this);	
		}
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
