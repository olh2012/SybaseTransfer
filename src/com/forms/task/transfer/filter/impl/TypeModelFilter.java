package com.forms.task.transfer.filter.impl;

import com.forms.platform.core.exception.Throw;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 根据用户和类型过滤模型<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public class TypeModelFilter extends SchemaModelFilter{

	private String type;
	
	public TypeModelFilter() {
	}

	public TypeModelFilter(String type) {
		this(null, type);
	}
	
	public TypeModelFilter(String schema,String type) {
		super(schema);
		this.setType(type);
	}
	
	public boolean accept(ITransferModel obj) {
		String type = Throw.throwIfNull(getType(),"the type is empty,please check...");
		if(!super.accept(obj)){
			return false;
		}
		return type.equalsIgnoreCase(obj.getType()) || type.equalsIgnoreCase(obj.getInnerType());
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
