package com.forms.task.transfer.filter.impl;

import com.forms.platform.core.util.Tool;
import com.forms.task.transfer.filter.IModelFilter;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 根据用户过滤模型<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class SchemaModelFilter implements IModelFilter{

	private String schema;
	
	public SchemaModelFilter(){
	}

	public SchemaModelFilter(String schema){
		this.schema = schema;
	}
	
	public boolean accept(ITransferModel obj) {
		String schema = getSchema();
		return null != obj && (Tool.CHECK.isEmpty(schema) || schema.equals(obj.getSchema()));
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
}
