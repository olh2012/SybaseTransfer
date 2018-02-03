package com.forms.task.transfer.model;

import com.forms.platform.core.database.jndi.IJndi;


/**
 * Project : Sybase数据库迁移 <br>
 * Description : 数据迁移模型接口<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public interface ITransferDataModel extends ITransferModel{

	/**
	 * 获取数据迁移SQL
	 * @return
	 */
	public String getTransferDataSql(IJndi srcJndi, IJndi targetJndi);
	
	/**
	 * 获取数据量
	 * @return
	 */
	public long getCount();
}
