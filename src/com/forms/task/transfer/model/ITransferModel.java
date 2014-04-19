package com.forms.task.transfer.model;

import com.forms.platform.core.database.jndi.IJndi;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 迁移对象接口<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-10-30<br>
 */
public interface ITransferModel{

	/**
	 * 对象类型（TABLE、VIEW、PROCEDURE）
	 * @return
	 */
	public String getType();
	
	/**
	 * 内部类型（U、V、P）
	 * @return
	 */
	public String getInnerType();

	/**
	 * 对象名称（含用户）
	 * @return
	 */
	public String getObjectName();
	
	/**
	 * 对象名称（不含用户）
	 * @return
	 */
	public String getName();
	
	/**
	 * 获取模式（用户）名称
	 * @return
	 */
	public String getSchema();
	
	/**
	 * DDL语句
	 * @param jndi
	 * @return
	 */
	public String getDdlSql(IJndi jndi);
	
	/**
	 * 对象删除语句
	 * @param jndi
	 * @return
	 */
	public String getDropSql(IJndi jndi);
	
	/**
	 * 获取授权语句
	 * @param jndi
	 * @return
	 */
	public String getGrantSql(IJndi jndi);
	
	/**
	 * 获取注释语句
	 * @param jndi
	 * @return
	 */
	public String getCommentSql(IJndi jndi);
	
	/**
	 * 对象是否存在
	 * @param jndi
	 * @return
	 */
	public boolean exists(IJndi jndi);
	
	
}