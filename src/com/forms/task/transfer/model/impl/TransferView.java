package com.forms.task.transfer.model.impl;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.spring.util.SpringUtil;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 视图迁移对象<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-10-30<br>
 */
public class TransferView extends AbstractTransferModel {

	public TransferView(String name, String schema) {
		super(name, schema, "VIEW", "V");
	}

	public String getDdlSql(IJndi srcJndi) {
		String sql = "select viewtext from SYSVIEWS where viewname = ? and vcreator = ?";
		String result = SpringUtil.getQueryBean(sql, String.class, new String[]{getName(),getSchema()}, srcJndi);
		//result += "\nGRANT SELECT ON "+getObjectName()+" TO PUBLIC";
		return result;
	}

	public String getGrantSql(IJndi jndi) {
		return "GRANT SELECT ON "+getObjectName()+" TO PUBLIC";
	}

	public String getCommentSql(IJndi jndi) {
		return null;
	}
}
