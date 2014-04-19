package com.forms.task.transfer.task;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.task.transfer.model.ITransferModel;
import com.forms.task.transfer.model.TransferModelFactory;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 清除对象任务<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-6<br>
 */
public class ClearObjectTask extends AbstractTransferTask{

	protected void execute(IJndi srcJndi, IJndi targetJndi,
			boolean executeOnGenerate, String sqlFile) {
		List<ITransferModel> list = TransferModelFactory.getTransferModelList(getTargetJndi(), getFilter());
		String action = executeOnGenerate ? "drop数据库对象":"生成drop数据库对象的DDL语句";
		log("开始"+action+"，对象数："+list.size());
		long start = System.nanoTime();
		transferList(srcJndi, targetJndi, list, executeOnGenerate, sqlFile, action);
		long time = System.nanoTime()-start;
		log(action+"完成，对象数："+list.size()+"，执行时间："+time/1000000000+" s,"+time/1000000+" ms,"+time+" ns");
	}
	
	/**
	 * 迁移一组对象
	 * @param srcJndi
	 * @param targetJndi
	 * @param list
	 * @param executeOnGenerate
	 * @param sqlFile
	 */
	private void transferList(IJndi srcJndi, IJndi targetJndi, List<ITransferModel> list, boolean executeOnGenerate, String sqlFile, String action) {
		if(null != list && !list.isEmpty()){
			boolean rebuild = true;
			int s = list.size();
			String total = s+"";
			for(int i=0; i<s; i++){
				transferObject(srcJndi, targetJndi, list.get(i), rebuild, executeOnGenerate, sqlFile, i!=0, action, i+1+"", total);
			}
		}
	}

	@Override
	protected int transferObject(IJndi srcJndi, IJndi targetJndi,
			ITransferModel model, boolean rebuild, boolean executeOnGenerate,
			OutputStreamWriter writer) throws IOException {
		String name = model.getObjectName();
		if(!model.exists(targetJndi)){
			error("目标源数据库中已经不存在  "+name + "，忽略 "+name+"...");
			return -1;
		}
		StringBuilder sb = new StringBuilder();
		String drop = model.getDropSql(srcJndi);
		writer.write(drop);
		sb.append("\n");
		if(executeOnGenerate){
			executeSql(targetJndi, drop, model);
			//SpringUtil.execute(drop, targetJndi);
		}
		writer.write(sb.toString());
		return -1;
	}

}
