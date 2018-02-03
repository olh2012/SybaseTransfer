package com.forms.task.transfer.filter.impl;

import java.util.List;

import com.forms.platform.core.util.Tool;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 根据用户和名称列表过滤模型<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public class NameListModelFilter extends SchemaModelFilter{

	private List<String> nameList;
	
	public boolean accept(ITransferModel obj) {
		return super.accept(obj) && nameList.contains(obj.getName());
	}

	public void setNameList(String nameList) {
		this.nameList = Tool.STRING.splitToList(nameList, ",");
	}
}
