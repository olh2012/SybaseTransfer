package com.forms.task.transfer.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.spring.util.SpringUtil;
import com.forms.task.transfer.model.ITransferDataModel;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : 数据分析平台迁移项目 <br>
 * Description : 表迁移对象<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-10-30<br>
 */
public class TransferTable extends AbstractTransferModel implements ITransferDataModel{
	
	private static final Map<String, String> DB_KEYS = new HashMap<String, String>();
	static{
		DB_KEYS.put("TIME", "\"TIME\"");
	}

	public TransferTable(String name, String schema) {
		super(name, schema, "TABLE", "U");
	}

	public String getDdlSql(IJndi srcJndi) {
		Table table = new Table();
		table.schema = super.getSchema();
		table.tableName = super.getName();
		String objectname = super.getObjectName();
		Map<Integer,Column> columns =  getColumn(srcJndi, table);
		
		StringBuilder sb = new StringBuilder("CREATE TABLE ").append(objectname).append("(");
		StringBuilder colComments = new StringBuilder("");
		String key = "";
		for(Map.Entry<Integer, Column> entry : columns.entrySet()){
			Column col = entry.getValue();
			sb.append("\n\t").append(col.toDdlSql()).append(",");
			if(col.isKey){
				key += ","+col.columnName;
			}
			if(null != col.memo){
				colComments.append("\ncomment on column ").append(objectname).append(".").append(col.columnName).append(" is '").append(col.memo).append("'" + separate);
			}
		}
		sb.deleteCharAt(sb.length()-1);
		if(null != key && !"".equals(key)){
			sb.append(",\n\tCONSTRAINT PK_"+table.tableName+" PRIMARY KEY ("+key.substring(1)+")");
		}
		sb.append("\n)" + separate);
		List<Index> indexs = getIndex(srcJndi, table, columns);
		if(null != indexs && !indexs.isEmpty()){
//			sb.append("\nGO");
			for(Index index : indexs){
				sb.append("\n").append(index.toDdlSql());
			}
		}
		if(null != table.memo){
			sb.append("\ncomment on table ").append(objectname).append(" is '").append(table.memo).append("'" + separate);
		}
		sb.append(colComments);
		return sb.toString();
	}
	
	public String getTransferDataSql(IJndi srcJndi, IJndi targetJndi) {
		String objectname = super.getObjectName();
		String remote = targetJndi.getProperties().getProperty("db.remotedatebase");
		StringBuilder sb = new StringBuilder()
			.append("insert into ").append(objectname)
			.append(" location '").append(remote).append("' {select *  from ")
			.append(objectname).append("}"  + separate + "\ncommit" + separate);
		return sb.toString();
	}
	
	private Map<Integer, Column> getColumn(IJndi jndi, Table table){
		Map<Integer, Column> map = new LinkedHashMap<Integer, Column>();
		String sql = sql(table);
		List<Map<String, Object>> list = SpringUtil.getQueryListMap(sql, jndi);
		if(null != list && !list.isEmpty()){
			table.memo = (String)list.get(0).get("tableMemo");
			for(Map<String, Object> m : list){
				Column c = new Column();
				c.colId = ((Number)m.get("colId")).intValue();
				c.columnName = ((String)m.get("columnName")).trim();
				if(DB_KEYS.containsKey(c.columnName)){
					c.columnName = DB_KEYS.get(c.columnName);
				}
				c.dataType = (String)m.get("dataType");
				c.isKey = "Y".equals(m.get("pkFlg"));
				c.allowEmpty = "Y".equals(m.get("allowEmpty"));
				c.memo = (String)m.get("memo");
				map.put(c.colId, c);
			}
		}
		return map;
	}
	
	/**
	 * 获取索引定义
	 * @param conn
	 * @param table
	 * @param columns
	 * @return
	 */
	private List<Index> getIndex(IJndi jndi, Table table, Map<Integer,Column> columns){
		List<Index> map = new ArrayList<Index>();
		String sql = indexSql(table);
		List<Map<String, Object>> list = SpringUtil.getQueryListMap(sql, jndi);
		if(null != list && !list.isEmpty()){
			int indexId = -1;
			Index index = null;
			for(Map<String, Object> m : list){
				int id = ((Number)m.get("indexId")).intValue();
				if(id != indexId){
					index = new Index();
					indexId = id;
					map.add(index);
					index.table = table;
					index.columns = new ArrayList<Column>();
					index.indexName = ((String)m.get("indexName")).trim();
					index.indexType = (String)m.get("indexType");
				}
				index.columns.add(columns.get(((Number)m.get("columnId")).intValue()));
			}
		}
		return map;
	}
	
	private String sql(Table table){
		String sql = " SELECT d.name AS SCHEMA, 		" +
				" 		a.table_id AS TABLE_ID,		" +
				" 		a.table_name AS TABLE_NAME,		" +
				" 	    b.column_name AS COLUMN_NAME,	" +
				" 	    b.column_id AS COL_ID,			" +
				" 	    upper(c.domain_name || CASE WHEN b.domain_id = 27 THEN '(' || b.width || ',' || b.scale || ')'  " +
				" 	      	 WHEN b.domain_id = 8 OR b.domain_id = 9 THEN '(' || b.width || ')'	" +
				" 	 	END) AS DATA_TYPE,		" +
				" 	    b.pkey AS PK_FLG,		" +
				" 	    b.nulls AS ALLOW_EMPTY,	" +
				" 	    a.remarks AS TABLE_MEMO," +
				" 	 	b.remarks AS MEMO		" +
				" FROM systable a				" +
				" JOIN SYSCOLUMN b ON a.table_id = b.table_id 	" +
				" JOIN SYSDOMAIN c ON b.domain_id = c.domain_id	" +
				" JOIN sysusers d ON a.creator = d.uid			" +
				" WHERE a.table_name = '"+table.tableName+"'	" +
				"   AND d.name = '"+table.schema+"'				" +
				" ORDER BY b.column_id 	" ;
		return sql;
	}
	
	private String indexSql(Table table){
		String sql = " SELECT t.table_name                         " +
					"     , i.index_id                            " +
					"     , i.index_name                          " +
					"     , i.index_type                          " +
					"     , ic.column_id                          " +
					"     , c.column_name                         " +
					" FROM SYSTABLE t                             " +
					" JOIN sysindex i                             " +
					"   ON t.table_id = i.table_id                " +
					" JOIN SYSIXCOL ic                            " +
					"   ON t.table_id = ic.table_id               " +
					"  AND i.index_id = ic.index_id               " +
					" JOIN syscolumn c                            " +
					"   ON ic.column_id = c.column_id             " +
					"  AND t.table_id = c.table_id                " +
					" JOIN sysusers u                             " +
					"   ON t.creator = u.uid                      " +
					"WHERE t.table_name = '"+table.tableName+"'   " +
					"  AND u.name = '"+table.schema+"'            " +
					"  AND i.index_owner = 'USER'                 " ;
		return sql;
	}
	
	private static class Table{
		private String schema;
		//private int tableId;
		private String tableName;
		private String memo;
	}
	
	private static class Column{
		private int colId;
		private String columnName;
		private String dataType;
		private boolean isKey;
		private boolean allowEmpty;
		private String memo;
		private String toDdlSql(){
			return columnName.trim()+"\t"+dataType+"\t"+(allowEmpty?"":" NOT")+" NULL ";
		}
	}
	
	private static class Index{
		private String indexType;
		private String indexName;
		private Table table;
		private List<Column> columns;
		private String toDdlSql(){
			StringBuilder sb = new StringBuilder("create "+ indexType+" index "+indexName+" on "+table.schema+"."+table.tableName+"(");
			for(Column col : columns){
				sb.append(col.columnName).append(",");
			}
			sb.deleteCharAt(sb.length()-1).append(")");
			return sb.toString();
		}
	}
}
