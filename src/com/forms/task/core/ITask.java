package com.forms.task.core;

import java.util.concurrent.Callable;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 迁移任务接口<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public interface ITask extends Callable<Integer>,ITaskRegister{
	
	/**
	 * 获取ID
	 * @return
	 */
	public String getTaskId();
	
	/**
	 * 获取注释说明
	 * @return
	 */
	public String getComment();
}
