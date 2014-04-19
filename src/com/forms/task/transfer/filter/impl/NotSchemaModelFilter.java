package com.forms.task.transfer.filter.impl;

import com.forms.task.transfer.model.ITransferModel;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 根据用户过滤模型<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class NotSchemaModelFilter extends SchemaModelFilter{

	private String schema;
	
	public NotSchemaModelFilter(){
	}

	public NotSchemaModelFilter(String schema){
		this.schema = schema;
	}
	
	public boolean accept(ITransferModel obj) {
		return !super.accept(obj);
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
}
