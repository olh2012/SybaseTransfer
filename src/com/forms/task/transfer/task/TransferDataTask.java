package com.forms.task.transfer.task;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.exception.Throw;
import com.forms.platform.core.util.Tool;
import com.forms.task.transfer.model.ITransferDataModel;
import com.forms.task.transfer.model.ITransferModel;
import com.forms.task.transfer.model.TransferModelFactory;

/**
 * Project : Sybase数据库迁移 <br>
 * Description : 数据迁移任务<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class TransferDataTask extends AbstractTransferTask{
	
	/**
	 * 任务内部并发数
	 */
	private int threadCount = -1;
	
	private boolean formTable = false;
	
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	
	protected int getThreadCount() {
		return threadCount;
	}
	
	public void setFormTable(boolean formTable) {
		this.formTable = formTable;
	}

	@Override
	public void execute(IJndi srcJndi,IJndi targetJndi, boolean executeOnGenerate, String sqlFile) {
		List<ITransferDataModel> list = null;
		if(formTable){
			list = TransferModelFactory.getTransferDataModelList(InitDbObjTask.getTransferModelList(srcJndi), getFilter());
		}else{
			list = getTransferDataModelList();	
		}
		String action = executeOnGenerate ? "迁移表数据":"生成数据迁移SQL语句";
		log("开始"+action+"，对象数："+list.size());
		long start = System.nanoTime();
		transferList(srcJndi, targetJndi, list, executeOnGenerate, sqlFile, action);
		long time = System.nanoTime()-start;
		log(action+"完成，对象数："+list.size()+"，执行时间："+time/1000000000+" s,"+time/1000000+" ms,"+time+" ns");
	}
	
	/**
	 * 迁移一组数据
	 * @param srcJndi
	 * @param targetJndi
	 * @param list
	 * @param executeOnGenerate
	 * @param sqlFile
	 */
	private void transferList(final IJndi srcJndi,final IJndi targetJndi, List<ITransferDataModel> list, final boolean executeOnGenerate,final String sqlFile,final String action) {
		if(null != list && !list.isEmpty()){
			final boolean truncate = true;
			final int size = list.size();
		    String total = size+"";
			int threadCount = Math.min(getThreadCount(),size);
			if(threadCount >= 2){
				try {
					ExecutorService service = Executors.newFixedThreadPool(threadCount);
					List<List<ITransferDataModel>> aa = Tool.LANG.getSubListByCount(list, threadCount);
					List<Callable<Void>> tasks = new ArrayList<Callable<Void>>();
					int index = 0;
					for(int i = 0, s = aa.size(); i < s; i++){
						final List<ITransferDataModel> task = aa.get(i);
						final String file = Tool.FILE.getAddFilename(sqlFile, ""+(i+1));
						final int begin = index;
						index += task.size();
						final String t = total+","+(begin+1)+"-"+index+",\t"+total+"-";
						final String th = task.size()+"";
						final int taskIndex = i+1;
						tasks.add(new Callable<Void>(){
							public Void call() throws Exception {
								log("开始"+action+"第"+taskIndex+"个子任务，对象数："+task.size());
								long start = System.nanoTime();
								for(int j=0,l=task.size(); j<l; j++){
									transferObject(srcJndi, targetJndi, task.get(j), truncate, executeOnGenerate, file, j!=0, action, j+1+"", t+(begin+j+1)+",\t"+th);
								}
								long time = System.nanoTime()-start;
								log(action+"第"+taskIndex+"个子任务完成，对象数："+task.size()+"，执行时间："+time/1000000000+" s,"+time/1000000+" ms,"+time+" ns");
								return null;
							}
						});
					}
					List<Future<Void>> a = service.invokeAll(tasks);
					for(Future<Void> f: a){
						f.get();
					}
					service.shutdown();
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					e.getCause().printStackTrace();
					Throw.throwException(e.getCause());
				}
			}else{
				for(int i=0; i<size; i++){
					transferObject(srcJndi, targetJndi, list.get(i), truncate, executeOnGenerate, sqlFile, i!=0, action, i+1+"", total);
				}	
			}
		}
	}
	
	/**
	 * 迁移单个表数据
	 * @param srcJndi
	 * @param targetJndi
	 * @param model
	 * @param truncate
	 * @param executeOnGenerate
	 * @param sqlFile
	 * @param append
	 * @throws IOException 
	 */
	protected int transferObject(IJndi srcJndi, IJndi targetJndi, ITransferModel model, boolean rebuild, boolean executeOnGenerate, OutputStreamWriter writer) throws IOException{
		if(model instanceof ITransferDataModel){
			String name = model.getObjectName();
			StringBuilder sb = new StringBuilder();
			if(rebuild){
				String truncateSql = "truncate table " + name;
				writer.write(truncateSql);
				sb.append(";\n");
				if(executeOnGenerate){
					executeSql(targetJndi, truncateSql, model);
					//SpringUtil.execute(truncateSql, targetJndi);
				}
			}
			String dataSql = ((ITransferDataModel)model).getTransferDataSql(srcJndi,targetJndi);
			sb.append(dataSql).append("\n\n");
			writer.write(sb.toString());
			if(executeOnGenerate){
				return executeSql(targetJndi, dataSql, model);
				//SpringUtil.execute(dataSql, targetJndi);
			}
		}
		return -1;
	}
}
