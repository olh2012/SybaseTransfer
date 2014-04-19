package com.forms.task.transfer.task;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.exception.Throw;
import com.forms.platform.core.logger.CommonLogger;
import com.forms.platform.core.spring.util.SpringUtil;
import com.forms.platform.core.util.Tool;
import com.forms.task.core.base.AbstractTask;
import com.forms.task.transfer.TransferContext;
import com.forms.task.transfer.filter.IModelFilter;
import com.forms.task.transfer.model.ITransferDataModel;
import com.forms.task.transfer.model.ITransferModel;
import com.forms.task.transfer.model.TransferModelFactory;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 迁移任务的抽象实现类<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public abstract class AbstractTransferTask extends AbstractTask{
	
	/**
	 * 源数据库
	 */
	private IJndi srcJndi;
	
	/**
	 * 目标数据库
	 */
	private IJndi targetJndi;
	
	/**
	 * 迁移模型过滤器
	 */
	private IModelFilter filter;
	
	/**
	 * 是否在生成SQL的时候立即执行
	 */
	private boolean executeOnGenerate;
	private boolean hasInject = false;
	
	/**
	 * 迁移环境，用于设置默认值
	 */
	private TransferContext context;
	
	/**
	 * 生成的SQL文件
	 */
	private String sqlFile;
	
	/**
	 * SQL文件编码
	 */
	private String encoding = "UTF-8";
	
	public Integer call() {
		CommonLogger.debug("execute in thread:"+Thread.currentThread().getName());
		IJndi srcJndi = getSrcJndi();
		IJndi targetJndi = getTargetJndi();
		boolean executeOnGenerate = isExecuteOnGenerate();
		String sqlFile = getSqlFile();
		execute(srcJndi, targetJndi, executeOnGenerate, sqlFile);
		return 0;
	}
	
	protected abstract void execute(IJndi srcJndi,IJndi targetJndi,boolean executeOnGenerate,String sqlFile);
	
	protected abstract int transferObject(IJndi srcJndi, IJndi targetJndi, ITransferModel model, boolean rebuild, boolean executeOnGenerate, OutputStreamWriter writer) throws IOException;
	
	protected int executeSql(IJndi jndi, String sql, ITransferModel model){
		try{
			return SpringUtil.execute(sql, jndi);
		}catch(Throwable t){
			error("执行SQL出现异常，\n对象名:"+model.getObjectName()+"\nSQL:\n"+sql+"Message:"+Throw.getExceptionMessage(t));	
			return -1;
//			if(!model.getInnerType().equalsIgnoreCase("U")){
//				warn("直接执行SQL出现异常，尝试使用DDL存储过程来执行SQL语句\n对象名:"+model.getObjectName()+"\nSQL:\n"+sql);	
//			}
//			return executeSqlWithDdl(jndi, sql, model);	
		}
	}
	
	@SuppressWarnings("unused")
	private int executeSqlWithDdl(IJndi jndi, String sql, ITransferModel model){
		try{
			String execute = "{call merit.execDdl(?)}";
			return SpringUtil.execute(execute, new String[]{sql},jndi);	
		}catch(RuntimeException t){
			error("使用DDL存储过程执行SQL出现异常\n对象名:"+model.getObjectName()+"\nSQL:\n"+sql);
			return -1;
		}
	}
	
	/**
	 * 迁移单个对象
	 * @param srcJndi
	 * @param targetJndi
	 * @param model
	 * @param rebuild
	 * @param executeOnGenerate
	 * @param sqlFile
	 * @param append
	 */
	protected void transferObject(IJndi srcJndi, IJndi targetJndi, ITransferModel model, boolean rebuild, boolean executeOnGenerate, String sqlFile, boolean append, String action,String index,String total) {
		OutputStreamWriter fw = null;
		String name = model.getObjectName();
		long start = System.nanoTime();
		log(action+"执行开始：("+total+"-"+index+")"+name);
		try{
			fw = new OutputStreamWriter(new FileOutputStream(sqlFile, append), getEncoding());
			int count = transferObject(srcJndi, targetJndi, model, rebuild, executeOnGenerate, fw);
			long time = System.nanoTime()-start;
			log(action+"成功完成：("+total+"-"+index+")"+name+"，执行时间："+time/1000000000+" s,"+time/1000000+" ms,"+time+" ns"+(count==-1?"":(", 记录数："+count)));
		}catch (IOException e) {
			Throw.throwException("没有找到输出sql的文件:"+sqlFile, e);
		}catch (RuntimeException e){
			long time = System.nanoTime()-start;
			String info = action+"出现异常：("+total+"-"+index+")"+name+"，执行时间："+time/1000000000+" s,"+time/1000000+" ms,"+time+" ns，info："+e.getLocalizedMessage();
			log(info);
			error(info);
			throw e;
		}finally{
			Tool.IO.closeQuietly(fw);
		}
	}
	
	protected synchronized void log(String log){
		String logFile = getContext().getLogFile();
		if(null == logFile){
			return;
		}
		write(logFile, log);
	}
	
	protected synchronized void error(String log){
		String errorFile = getContext().getErrorFile();
		if(null == errorFile){
			return;
		}
		write(errorFile, log);
	}
	
	protected synchronized void warn(String log){
		String warnFile = getContext().getWarnFile();
		if(null == warnFile){
			return;
		}
		write(warnFile, log);
	}
	
	private void write(String file, String log){
		OutputStreamWriter fw = null;
		try{
			fw = new OutputStreamWriter(new FileOutputStream(file, true), getEncoding());
			fw.write(Thread.currentThread()+":"+Tool.DATE.getDateAndTime()+":"+log+"\n");
			CommonLogger.info(log);
		}catch (IOException e) {
			Throw.throwException("没有找到输出日志的文件:"+file, e);
		}finally{
			Tool.IO.closeQuietly(fw);
		}
	}
	
	public void setSrcJndi(IJndi srcJndi) {
		this.srcJndi = srcJndi;
	}
	
	public void setTargetJndi(IJndi targetJndi) {
		this.targetJndi = targetJndi;
	}
	
	public void setExecuteOnGenerate(boolean executeOnGenerate) {
		this.hasInject = true;
		this.executeOnGenerate = executeOnGenerate;
	}
	
	public void setFilter(IModelFilter filter) {
		this.filter = filter;
	}
	
	public void setContext(TransferContext context) {
		this.context = context;
	}

	public void setSqlFile(String sqlFile) {
		Tool.FILE.mkdirs(sqlFile);
		this.sqlFile = sqlFile;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	protected IJndi getSrcJndi() {
		return srcJndi == null ? getContext().getDefaultSrcJndi() : srcJndi;
	}

	protected IJndi getTargetJndi() {
		return targetJndi == null ? getContext().getDefaultTargetJndi() : targetJndi;
	}

	protected boolean isExecuteOnGenerate() {
		return hasInject ? executeOnGenerate : getContext().isExecuteOnGenerate();
	}

	protected TransferContext getContext() {
		return context;
	}
	
	protected IModelFilter getFilter(){
		return filter;
	}
	
	protected String getSqlFile() {
		return sqlFile == null ? getContext().getSqlFile() : sqlFile;
	}
	
	protected String getEncoding() {
		return encoding == null ? getContext().getEncoding() : encoding;
	}

	protected List<ITransferModel> getTransferModelList(){
		return TransferModelFactory.getTransferModelList(getSrcJndi(), getFilter());
	}
	
	protected List<ITransferDataModel> getTransferDataModelList(){
		return TransferModelFactory.getTransferDataModelList(getSrcJndi(), getFilter());
	}
}
