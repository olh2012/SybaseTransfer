package com.forms.task.transfer.filter.impl;

import com.forms.platform.core.exception.Throw;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 根据用户和名称过滤模型<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public class NameModelFilter extends SchemaModelFilter{

	private String name;
	
	private boolean ignoreCase;
	
	public NameModelFilter() {
	}

	public NameModelFilter(String name) {
		this(null, name, false);
	}
	
	public NameModelFilter(String name,boolean ignoreCase) {
		this(null, name, ignoreCase);
	}
	
	public NameModelFilter(String schema,String name,boolean ignoreCase) {
		super(schema);
		this.setName(name);
		this.ignoreCase = ignoreCase;
	}
	
	public boolean accept(ITransferModel obj) {
		String name = Throw.throwIfNull(getName(),"the name is empty,please check...");
		if(!super.accept(obj)){
			return false;
		}
		String objName = obj.getName();
		if(isIgnoreCase()){
			return name.equalsIgnoreCase(objName);
		}else{
			return name.equals(objName);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(null != name){
			int index = name.indexOf('.');
			if(-1 == index){
				this.name = name;		
			}else{
				super.setSchema(name.substring(0, index));
				this.name = name.substring(index+1);
			}
		}
	}

	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

}
