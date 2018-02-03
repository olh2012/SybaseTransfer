package com.forms.task.transfer.model.impl;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.spring.util.SpringUtil;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 抽象迁移对象实现类<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-10-30<br>
 */
public abstract class AbstractTransferModel implements ITransferModel{
	
	private String name;
	
	private String schema;
	
	private String type;
	
	private String innerType;
	
	public final String separate = " "; 

	public AbstractTransferModel(String name, String schema, String type, String innerType) {
		this.name = name;
		this.schema = schema;
		this.type = type;
		this.innerType = innerType;
	}
	
	/**
	 * 获取删除对象的语句
	 * @param jndi
	 * @return
	 */
	public String getDropSql(IJndi jndi){
		String type = getType();
		return " IF EXISTS (SELECT 1 FROM sysobjects s JOIN sysusers u on s.uid = u.uid " +
			   " WHERE s.name = '"+getName()+"' AND u.name = '"+getSchema()+"' AND s.type = '"+getInnerType()+"') " +
			   "\n\tDROP "+type+" "+getObjectName();
	}
	
	/**
	 * 判断对象是否存在
	 * @param jndi
	 * @return
	 */
	public boolean exists(IJndi jndi){
		String sql = "select count(1) " +
				   "  from sysobjects s " +
				   "  join sysusers u " +
				   "    on s.uid = u.uid " +
				   " where s.name = ? " +
				   "   and u.name = ? " +
				   "   and s.type = ? ";
		int count = SpringUtil.getQueryBean(sql, int.class, new Object[]{getName(), getSchema(), getInnerType()}, jndi);
		return count == 1;
	}

	private String getSelectObjectName() {
		return schema+"."+name;
	}
	
	public String getObjectName(){
		if(name.contains("-")){
			return schema+".\""+name+"\"";
		}else{
			return getSelectObjectName();
		}
	}
	
	public String getType() {
		return type;
	}

	public String getInnerType() {
		return innerType;
	}

	public String getName(){
		return name;
	}
	
	public String getSchema(){
		return schema;
	}
}
