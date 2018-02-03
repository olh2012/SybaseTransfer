package com.forms.task.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.forms.platform.core.spring.util.SpringHelp;
import com.forms.task.core.ITask;
import com.forms.task.core.base.TaskGroup;
import com.forms.task.core.command.AbstractCommandTask;
import com.forms.task.core.command.CommandHelp;
import com.forms.task.transfer.filter.IModelFilter;
import com.forms.task.transfer.filter.impl.ModelFilterProxy;
import com.forms.task.transfer.filter.impl.NameModelFilter;
import com.forms.task.transfer.filter.impl.PatternModelFilter;
import com.forms.task.transfer.filter.impl.PrefixModelFilter;
import com.forms.task.transfer.filter.impl.SchemaModelFilter;
import com.forms.task.transfer.task.ClearObjectTask;
import com.forms.task.transfer.task.TransferDataTask;
import com.forms.task.transfer.task.TransferObjectTask;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 根据命令行生成迁移任务<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public class TransferCommandTask extends AbstractCommandTask {

	public void addCommandOptions(Options options) {
		CommandHelp.addOption(options, "c", "clear", false, "执行清除对象任务");
		CommandHelp.addOption(options, "o", "object", false, "执行迁移对象任务");
		CommandHelp.addOption(options, "d", "data", false, "执行迁移数据任务");
		
		CommandHelp.addOption(options, "u", "user", true, "用户名称");
		CommandHelp.addOption(options, "n", "name", true, "单个对象名称");
		CommandHelp.addOption(options, "p", "prefix", true, "根据对象前缀过滤迁移对象");
		CommandHelp.addOption(options, "in", "including", true, "根据对象名称满足的正则表达式过滤迁移对象");
		CommandHelp.addOption(options, "ex", "excluding", true, "根据对象名称不满足的正则表达式过滤迁移对象");
		CommandHelp.addOption(options, "i", "ignoreCase", true, "名称比较时是否忽略大小写");
	}

	public List<ITask> getTaskList(CommandLine commandLine) {
		TransferContext context = SpringHelp.getBean(TransferContext.class);
		IModelFilter filter = getModelFilter(commandLine);
		List<ITask> taskList = new ArrayList<ITask>();
		if(commandLine.hasOption("c")){
			ClearObjectTask clear = new ClearObjectTask();
			clear.setContext(context);
			clear.setFilter(filter);
			taskList.add(clear);
		}
		if(commandLine.hasOption("o")){
			TransferObjectTask object = new TransferObjectTask();
			object.setContext(context);
			object.setFilter(filter);
			taskList.add(object);
		}
		if(commandLine.hasOption("d")){
			TransferDataTask data = new TransferDataTask();
			data.setContext(context);
			data.setFilter(filter);
			taskList.add(data);
		}
		if(!taskList.isEmpty()){
			TaskGroup tg = new TaskGroup();
			tg.setThreadCount(1);
			tg.setTaskList(taskList);
			List<ITask> rs = new ArrayList<ITask>();
			rs.add(tg);
			return rs;
		}
		return null;
	}
	
	/**
	 * 根据命令行参数获取对象过滤器
	 * @param cl
	 * @return
	 */
	private IModelFilter getModelFilter(CommandLine cl){
		ModelFilterProxy filter = new ModelFilterProxy();
		String schema = null;
		if(cl.hasOption("u")){
			schema = cl.getOptionValue("u");
			filter.addFilter(new SchemaModelFilter(schema));
		}
		
		if(cl.hasOption("n")){
			boolean i = CommandHelp.parseBooleanCommandOption(cl,"i", false);
			filter.addFilter(new NameModelFilter(schema, cl.getOptionValue("n"), i));
		}
		if(cl.hasOption("p")){
			filter.addFilter(new PrefixModelFilter(schema, cl.getOptionValue("p")));
		}
		if(cl.hasOption("in")||cl.hasOption("ex")){
			PatternModelFilter pattern = new PatternModelFilter();
			pattern.setSchema(schema);
			if(cl.hasOption("in")){
				pattern.setIncluding(Pattern.compile(cl.getOptionValue("in")));
			}
			if(cl.hasOption("ex")){
				pattern.setExcluding(Pattern.compile(cl.getOptionValue("ex")));
			}
			filter.addFilter(pattern);
		}
		return filter;
	}
}
