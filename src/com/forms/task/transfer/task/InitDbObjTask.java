package com.forms.task.transfer.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.spring.util.SpringUtil;
import com.forms.task.core.base.AbstractTask;
import com.forms.task.transfer.model.ITransferModel;
import com.forms.task.transfer.model.impl.TransferTable;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 初始化数据库对象任务<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-11-13<br>
 */
public class InitDbObjTask extends AbstractTask{
	
	private static String table = "merit.APP_DB_OBJ";

	private IJndi jndi;

	public Integer call() throws Exception {
		// 1.创建数据库对象表，如果已经存在，就不再创建
		createDbObjectTable();
		// 2.更新数据库对象信息
		int rs = updateObjectInfo();
		// 3.更新数据量
		updateDataCount();
		return rs;
	}

	public IJndi getJndi() {
		return jndi;
	}

	public void setJndi(IJndi jndi) {
		this.jndi = jndi;
	}
	
	public void setTable(String table) {
		InitDbObjTask.table = table;
	}
	
	private void updateDataCount(){
		String sql = "select DB_USER,OBJ_NAME from " +table + " where OBJ_TYPE = 'TABLE'";
		List<Map<String,Object>> list = SpringUtil.getQueryListMap(sql, getJndi());
		if(null != list && !list.isEmpty()){
			List<String> sqls = new ArrayList<String>(list.size());
			for(Map<String,Object> map : list){
				String dbUser = (String)map.get("dbUser");
				String objName = (String)map.get("objName");
				sqls.add("UPDATE " +table + " SET DATA_COUNT = (SELECT COUNT(*) FROM "+dbUser+"."+objName+") WHERE DB_USER = '"+dbUser+"' AND OBJ_NAME = '"+objName+"'");
			}
			SpringUtil.executeBatch(sqls, getJndi());
		}
	}

	private int updateObjectInfo(){
		String sql = "truncate table "+table;
		SpringUtil.execute(sql, getJndi());
		sql =" INSERT INTO "+table +
					" SELECT U.name AS DB_USER, " +
					"		 CASE WHEN S.type = 'P' THEN 'PROCEDURE' WHEN S.type = 'V' THEN 'VIEW' WHEN S.type = 'U' THEN 'TABLE' END AS OBJ_TYPE, " +
					"		 S.name AS OBJ_NAME ," +
					"		 -1," +
					"		 'Y' " +
					"   FROM sysobjects S " +
					"   JOIN sysusers U " +
					"     ON S.uid = U.uid " +
					"  WHERE U.uid > 101" +
					"    AND S.type = 'U'";//先只生成表信息
		return SpringUtil.execute(sql, getJndi());
	}
	
	private void createDbObjectTable(){
		String tableName = table;
		int index = table.lastIndexOf('.');
		if(-1 != index){
			tableName = table.substring(index+1);
		}
		String sql = "select 1 from sys.systable where table_name='"+tableName+"' and table_type in ('BASE', 'GBL TEMP')";
		boolean exists = false;
		try{
			exists = 1 == SpringUtil.getQueryBean(sql, int.class, getJndi());
		}catch(Exception e){}
		if(exists){
			return;
		}
		StringBuilder sb = new StringBuilder()
			.append("create table "+table+"(")
			.append("DB_USER	varchar(128)	not null,")
			.append("OBJ_TYPE	varchar(20)		not null,")
			.append("OBJ_NAME	varchar(128)	not null,")
			.append("DATA_COUNT	INTEGER			null,")
			.append("USE_FLAG	varchar(20)		null,")
			.append("constraint PK_"+tableName+" primary key (DB_USER, OBJ_TYPE, OBJ_NAME)")
			.append(") ");
		
		sb.append("comment on table "+table+" is '数据库对象信息' ")
		  .append("comment on column "+table+".DB_USER is '数据库用户' ")
		  .append("comment on column "+table+".OBJ_TYPE is  '对象类型 TABLE 表 VIEW 视图 PORCEDURE存储过程和自定义函数' ")
		  .append("comment on column "+table+".OBJ_NAME is  '对象名称' ")
		  .append("comment on column "+table+".DATA_COUNT is '数据量 只有表和视图有意义' ")
		  .append("comment on column "+table+".USE_FLAG is '使用标志' ");
		SpringUtil.execute(sb.toString(), getJndi());
	}
	
	/**
	 * 获取迁移模型列表
	 * @param jndi
	 * @return
	 */
	public static List<ITransferModel> getTransferModelList(IJndi jndi) {
		List<ITransferModel> list = new ArrayList<ITransferModel>();
		String sql = "SELECT DB_USER, OBJ_NAME, DATA_COUNT FROM " + table + " WHERE OBJ_TYPE = 'TABLE' AND USE_FLAG = 'Y' order by DATA_COUNT";
		List<Map<String, Object>> lm = SpringUtil.getQueryListMap(sql, jndi);
		if(null != lm && !lm.isEmpty()){
			for(Map<String, Object> map : lm){
				String name = ((String)map.get("objName")).trim();
				String schema = ((String)map.get("dbUser")).trim();
				long count = ((Number)map.get("dataCount")).longValue();
				list.add(new TransferTable(name,schema,count));	
			}
		}
		return list;
	}
}
