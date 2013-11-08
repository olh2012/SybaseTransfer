package com.forms.task.transfer.model.impl;

import java.util.Map;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.spring.util.SpringUtil;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 存储过程或自定义函数迁移对象<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-10-30<br>
 */
public class TransferProcedure extends AbstractTransferModel {

	public TransferProcedure(String name, String schema) {
		super(name, schema, "PROCEDURE", "P");
	}

	public String getDdlSql(IJndi srcJndi) {
		String sql = "select P.proc_defn as procddl, P.remarks " +
				 "  from SYSPROCEDURE P " +
				 "  join SYSUSERS U ON P.creator = U.uid " +
			     " where P.PROC_NAME = ? " +
			     "   and U.name = ? ";
		Map<String, Object> map = SpringUtil.getQueryMap(sql, new String[]{getName(),getSchema()}, srcJndi);
		String result = map.get("procddl") + separate + "\n";
		String memo = (String)map.get("remarks");
		if(null != memo && !"".equals(memo)){
			result += "\ncomment on procedure "+super.getObjectName()+" is '"+memo+"'" + separate;
		}
		return result;
	}
}
