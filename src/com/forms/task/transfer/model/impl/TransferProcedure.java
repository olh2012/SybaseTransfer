package com.forms.task.transfer.model.impl;

import java.util.List;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.spring.util.SpringUtil;

/**
 * Project : Sybase数据库迁移 <br>
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
		String sql = "select P.proc_defn " +
				 "  from SYSPROCEDURE P " +
				 "  join SYSUSERS U ON P.creator = U.uid " +
			     " where P.PROC_NAME = ? " +
			     "   and U.name = ? ";
		String result = SpringUtil.getQueryBean(sql, String.class, new String[]{getName(),getSchema()},srcJndi);
		return result;
	}
	
	public String getGrantSql(IJndi jndi) {
		try{
			String sql =" SELECT S.grantee FROM SYSPROCAUTH S JOIN sysusers U ON S.creator = U.name" +
						" WHERE U.uid > 101 AND S.creator = '"+getSchema()+"' AND S.procname = '"+getName()+"'";
			List<String> list = SpringUtil.getQueryList(sql, String.class, jndi);
			if(null != list && !list.isEmpty()){
				StringBuilder sb = new StringBuilder("\nGRANT EXECUTE ON ").append(getObjectName()).append(" TO ").append(list.get(0).trim());
				for(int i=1,s=list.size(); i<s; i++){
					sb.append(",").append(list.get(i).trim());
				}
				return sb.toString();
			}
		}catch(Exception e){
		}
		return "";
	}
	
	public String getCommentSql(IJndi jndi) {
		try{
			String sql = "select P.remarks " +
					 "  from SYSPROCEDURE P " +
					 "  join SYSUSERS U ON P.creator = U.uid " +
				     " where P.PROC_NAME = ? " +
				     "   and U.name = ? ";
			String result = SpringUtil.getQueryBean(sql, String.class, new String[]{getName(),getSchema()},jndi);
			if(null != result && !"".equals(result)){
				result = "comment on procedure "+super.getObjectName()+" is '"+result+"'";
				return result;
			}
		}catch(Exception e){}
		return null;
	}
}
