package com.forms.task.transfer.model;

import com.forms.platform.core.database.jndi.IJndi;


/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
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
}
