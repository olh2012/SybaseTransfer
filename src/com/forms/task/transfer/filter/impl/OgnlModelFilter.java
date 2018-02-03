package com.forms.task.transfer.filter.impl;

import com.forms.platform.core.exception.Throw;
import com.forms.platform.core.util.Tool;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 根据用户和OGNL表达式过滤模型<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class OgnlModelFilter extends SchemaModelFilter{

	private String ognlExpression;
	
	public OgnlModelFilter() {
	}

	public OgnlModelFilter(String ognlExpression) {
		this.ognlExpression = ognlExpression;
	}
	
	public OgnlModelFilter(String schema,String ognlExpression) {
		super(schema);
		this.ognlExpression = ognlExpression;
	}
	
	public boolean accept(ITransferModel obj) {
		String ognlExpression = Throw.throwIfNull(getOgnlExpression(),"the ognlExpression value is not allow empty,please check...");
		return super.accept(obj) && Tool.CHECK.isTrue(obj, ognlExpression);
	}

	public String getOgnlExpression() {
		return ognlExpression;
	}

	public void setOgnlExpression(String ognlExpression) {
		this.ognlExpression = ognlExpression;
	}
}
