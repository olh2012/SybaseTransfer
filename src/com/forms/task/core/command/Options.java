package com.forms.task.core.command;

import java.util.LinkedHashMap;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;

import com.forms.platform.core.util.Tool;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 覆盖命令行参数选项类，保留原有顺序<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public class Options extends org.apache.commons.cli.Options{
	
	private static final long serialVersionUID = 1L;
	    
    public Options(){
    	Tool.BEAN.setOgnlValue(this, "shortOpts", new LinkedHashMap<String, Option>());
    	Tool.BEAN.setOgnlValue(this, "longOpts", new LinkedHashMap<String, Option>());
    	Tool.BEAN.setOgnlValue(this, "optionGroups", new LinkedHashMap<String, OptionGroup>());
    }
}
