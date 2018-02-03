package com.forms.task.transfer.filter.impl;

import java.util.ArrayList;
import java.util.List;

import com.forms.task.transfer.filter.IModelFilter;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 排除过滤器<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-13<br>
 */
public class ExcludingModelFilter implements IModelFilter{
	
	private List<String> excluding;

	public boolean accept(ITransferModel obj) {
		return null == excluding || !excluding.contains(obj.getName());
	}

	public void setExcluding(String excluding) {
		if(null != excluding && !"".equals(excluding)){
			this.excluding = new ArrayList<String>();
			for(String s : excluding.split(",")){
				this.excluding.add(s.trim());
			}
		}
	}
}
