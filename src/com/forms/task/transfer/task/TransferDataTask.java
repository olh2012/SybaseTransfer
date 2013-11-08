package com.forms.task.transfer.task;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.logger.CommonLogger;
import com.forms.task.transfer.model.ITransferDataModel;
import com.forms.task.transfer.model.ITransferModel;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 数据迁移任务<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class TransferDataTask extends AbstractTransferTask{
	
	@Override
	public void execute(IJndi srcJndi,IJndi targetJndi, boolean executeOnGenerate, String sqlFile) {
		List<ITransferDataModel> list = getTransferDataModelList();
		String action = executeOnGenerate ? "迁移表数据":"生成数据迁移SQL语句";
		CommonLogger.info("开始"+action+"，对象数："+list.size());
		long start = System.nanoTime();
		transferList(srcJndi, targetJndi, list, executeOnGenerate, sqlFile, action);
		long time = System.nanoTime()-start;
		CommonLogger.info(action+"完成，对象数："+list.size()+"，执行时间："+time/1000000000+" s,"+time/1000000+" ms,"+time+" ns");
	}
	
	/**
	 * 迁移一组数据
	 * @param srcJndi
	 * @param targetJndi
	 * @param list
	 * @param executeOnGenerate
	 * @param sqlFile
	 */
	private void transferList(IJndi srcJndi, IJndi targetJndi, List<ITransferDataModel> list, boolean executeOnGenerate, String sqlFile,String action) {
		if(null != list && !list.isEmpty()){
			boolean truncate = true;
			for(int i=0,s=list.size(); i<s; i++){
				transferObject(srcJndi, targetJndi, list.get(i), truncate, executeOnGenerate, sqlFile, i!=0, action, i, s);
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
