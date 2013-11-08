package com.forms.task.transfer.filter.impl;

import java.util.ArrayList;
import java.util.List;

import com.forms.task.transfer.filter.IModelFilter;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 过滤器代理<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public class ModelFilterProxy implements IModelFilter{

	private List<IModelFilter> filterList;
	
	public ModelFilterProxy addFilter(IModelFilter filter){
		if(null == this.filterList){
			this.filterList = new ArrayList<IModelFilter>();
		}
		this.filterList.add(filter);
		return this;
	}
	
	public ModelFilterProxy removeFilter(IModelFilter filter){
		if(null != this.filterList){
			this.filterList.remove(filter);
		}
		return this;
	}

	public boolean accept(ITransferModel obj) {
		if(null == this.filterList || this.filterList.isEmpty()){
			return false;
		}
		for(IModelFilter filter : this.filterList){
			if(!filter.accept(obj)){
				return false;
			}
		}
		return true;
	}

}
