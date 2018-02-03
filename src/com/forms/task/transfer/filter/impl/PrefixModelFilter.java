package com.forms.task.transfer.filter.impl;

import com.forms.platform.core.exception.Throw;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 根据用户和前缀过滤模型<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class PrefixModelFilter extends SchemaModelFilter{

	private String prefix;
	
	public PrefixModelFilter() {
	}

	public PrefixModelFilter(String prefix) {
		this(null, prefix);
	}
	
	public PrefixModelFilter(String schema,String prefix) {
		super(schema);
		setPrefix(prefix);
	}
	
	public boolean accept(ITransferModel obj) {
		String prefix = Throw.throwIfNull(getPrefix(),"the prefix value is not allow empty,please check...");
		return super.accept(obj) && obj.getName().startsWith(prefix);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		if(null != prefix){
			int index = prefix.indexOf('.');
			if(-1 == index){
				this.prefix = prefix;		
			}else{
				super.setSchema(prefix.substring(0, index));
				this.prefix = prefix.substring(index+1);
			}
		}
	}
}
