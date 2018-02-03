package com.forms.task.transfer.filter.impl;

import java.util.regex.Pattern;

import com.forms.task.transfer.model.ITransferModel;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 根据正则表达式过滤模型<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class PatternModelFilter extends SchemaModelFilter {

	private Pattern including;
	
	private Pattern excluding;
	
	public boolean accept(ITransferModel obj) {
		if(!super.accept(obj)){
			return false;
		}
		Pattern including = getIncluding();
		String name = obj.getName();
		if(null != including && !including.matcher(name).find()){
			return false;
		}
		Pattern excluding = getExcluding();
		if(null != excluding && excluding.matcher(name).find()){
			return false;
		}
		return true;
	}

	public Pattern getIncluding() {
		return including;
	}

	public void setIncluding(Pattern including) {
		this.including = including;
	}

	public Pattern getExcluding() {
		return excluding;
	}

	public void setExcluding(Pattern excluding) {
		this.excluding = excluding;
	}
}
