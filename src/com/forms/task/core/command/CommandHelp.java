package com.forms.task.core.command;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import com.forms.platform.core.exception.Throw;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 命令行帮助类<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-8<br>
 */
public class CommandHelp {
	
	/**
	 * 添加命令行参数
	 * @param opts
	 * @param opt
	 * @param longOpt
	 * @param hasArg
	 * @param desc
	 */
	public static void addOption(Options opts, String opt, String longOpt, boolean hasArg, String desc){
		if(opts.hasOption(opt)){
			Throw.throwException("已经设置名称为 "+opt+" 的命令行参数");
		}else if(null != longOpt && opts.hasOption(longOpt)){
			Throw.throwException("已经设置名称为 "+longOpt+" 的命令行参数");
		}else{
			opts.addOption(opt, longOpt, hasArg, desc);
		}
	}

	/**
	 * 解析命令行参数
	 * @param opts
	 * @param args
	 * @return
	 */
	public static CommandLine parseCommandOptions(Options opts, String[] args){
		try{
			BasicParser parser = new BasicParser(); 
			return parser.parse(opts, args);
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 解析命令行参数
	 * @param cl
	 * @param option
	 * @param defaultValue
	 * @return
	 */
	public static String parseCommandOption(CommandLine cl, String option, String defaultValue){
		try{
			return cl.getOptionValue(option);
		}catch(Exception e){}
		return defaultValue;
	}
	
	/**
	 * 解析整型命令行参数
	 * @param cl
	 * @param option
	 * @param defaultValue
	 * @return
	 */
	public static int parseIntCommandOption(CommandLine cl, String option, int defaultValue){
		try{
			return Integer.parseInt(cl.getOptionValue(option));
		}catch(Exception e){}
		return defaultValue;
	}
	
	/**
	 * 解析Boolean型命令行参数
	 * @param cl
	 * @param option
	 * @param defaultValue
	 * @return
	 */
	public static boolean parseBooleanCommandOption(CommandLine cl, String option, boolean defaultValue){
		try{
			return Boolean.parseBoolean(cl.getOptionValue(option));
		}catch(Exception e){}
		return defaultValue;
	}
}
