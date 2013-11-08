package com.forms.task.transfer.model.impl;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.spring.util.SpringUtil;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
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
		String name = getObjectName();
		String type = getType();
		return "IF OBJECT_ID ('"+name+"') IS NOT NULL \n\tDROP "+type+" "+name+" ";
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

	public String getObjectName() {
		return schema+"."+name;
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
