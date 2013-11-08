package com.forms.task.transfer;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.util.Tool;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 迁移环境，用于设置迁移任务的默认值<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class TransferContext {

	private IJndi defaultSrcJndi;
	
	private IJndi defaultTargetJndi;
	
	private String sqlFile;
	
	private boolean executeOnGenerate;

	private String encoding = "UTF-8";
	
	private String logFile;
	
	private String errorFile;
	
	private String warnFile;

	public IJndi getDefaultSrcJndi() {
		return defaultSrcJndi;
	}

	public void setDefaultSrcJndi(IJndi defaultSrcJndi) {
		this.defaultSrcJndi = defaultSrcJndi;
	}

	public IJndi getDefaultTargetJndi() {
		return defaultTargetJndi;
	}

	public void setDefaultTargetJndi(IJndi defaultTargetJndi) {
		this.defaultTargetJndi = defaultTargetJndi;
	}

	public String getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(String sqlFile) {
		Tool.FILE.mkdirs(sqlFile);
		this.sqlFile = sqlFile;
	}

	public boolean isExecuteOnGenerate() {
		return executeOnGenerate;
	}

	public void setExecuteOnGenerate(boolean executeOnGenerate) {
		this.executeOnGenerate = executeOnGenerate;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		Tool.FILE.mkdirs(logFile);
		this.logFile = logFile;
	}

	public String getErrorFile() {
		return errorFile;
	}

	public void setErrorFile(String errorFile) {
		Tool.FILE.mkdirs(errorFile);
		this.errorFile = errorFile;
	}

	public String getWarnFile() {
		return warnFile;
	}

	public void setWarnFile(String warnFile) {
		Tool.FILE.mkdirs(warnFile);
		this.warnFile = warnFile;
	}
}
