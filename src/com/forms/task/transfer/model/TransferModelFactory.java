package com.forms.task.transfer.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.forms.platform.core.database.jndi.IJndi;
import com.forms.platform.core.logger.CommonLogger;
import com.forms.platform.core.spring.util.SpringUtil;
import com.forms.platform.core.util.Tool;
import com.forms.task.transfer.filter.IModelFilter;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : Sybase数据库迁移 <br>
 * Description : 数据迁移对象工厂类<br>
 * Author : OuLinhai <br>
 * Version : 1.0.0 <br>
 * Since : 1.0 <br>
 * Date : 2013-10-30<br>
 */
public class TransferModelFactory {
	
	/**
	 * 生成迁移对象
	 * @param innerType
	 * @param name
	 * @param schema
	 * @return
	 */
	public static ITransferModel createTransferModel(String innerType, String name, String schema){
		ITransferModel model = null;
		Constructor<ITransferModel> constructor = modelMap.get(innerType);
		if(null != constructor){
			try {
				model = constructor.newInstance(name, schema);
			} catch (Exception e) {
				CommonLogger.error("创建迁移对象类型出现异常，忽略："+name);
			}
		}else{
			CommonLogger.warn("暂不支持的数据库对象类型"+innerType+"，忽略："+name);
		}
		return model;
	}
	
	/**
	 * 获取迁移模型列表
	 * @param jndi
	 * @param filter
	 * @return
	 */
	public static List<ITransferModel> getTransferModelList(IJndi jndi, IModelFilter filter){
		List<ITransferModel> list = getTransferModelList(jndi);
		return getTransferModelList(list, filter);
	}
	
	/**
	 * 过滤迁移模型
	 * @param list
	 * @param filter
	 * @return
	 */
	public static List<ITransferModel> getTransferModelList(final List<ITransferModel> list, IModelFilter filter){
		List<ITransferModel> copy = new ArrayList<ITransferModel>();
		if(null != list && !list.isEmpty()){
			for(ITransferModel model : list){
				if(null == filter || filter.accept(model)){
					copy.add(model);
				}
			}
		}
		return copy;
	}
	
	/**
	 * 获取数据迁移模型列表
	 * @param jndi
	 * @param filter
	 * @return
	 */
	public static List<ITransferDataModel> getTransferDataModelList(IJndi jndi, IModelFilter filter){
		List<ITransferModel> list = getTransferModelList(jndi);
		return getTransferDataModelList(list, filter);
	}
	
	/**
	 * 过滤数据迁移模型
	 * @param list
	 * @param filter
	 * @return
	 */
	public static List<ITransferDataModel> getTransferDataModelList(final List<ITransferModel> list, IModelFilter filter){
		List<ITransferDataModel> rs = new ArrayList<ITransferDataModel>();
		if(null != list && !list.isEmpty()){
			for(ITransferModel model : list){
				if(model instanceof ITransferDataModel && (null == filter || filter.accept(model))){
					rs.add((ITransferDataModel)model);
				}
			}
		}
		return rs;
	}
	
	/**
	 * 获取迁移模型列表
	 * @param jndi
	 * @return
	 */
	private synchronized static List<ITransferModel> getTransferModelList(IJndi jndi) {
		List<ITransferModel> list = modelListMap.get(jndi);
		if(null == list){
			list = new ArrayList<ITransferModel>();
			modelListMap.put(jndi, list);
			String sql = "SELECT u.name as schema, s.name, s.type " +
					 "  FROM SYSOBJECTS s " +
					 "  JOIN sysusers u " +
					 "    ON s.uid = u.uid " +
					 " WHERE u.uid > 100 " +
					 " order by u.uid, case when s.type = 'U' then 1 when s.type ='V' then 2 else 3 end, s.name";
			List<Map<String, Object>> lm = SpringUtil.getQueryListMap(sql, jndi);
			if(null != lm && !lm.isEmpty()){
				for(Map<String, Object> map : lm){
					String type = ((String)map.get("type")).trim();
					String name = ((String)map.get("name")).trim();
					String schema = ((String)map.get("schema")).trim();
					list.add(createTransferModel(type,name,schema));	
				}
			}
		}
		return list;
	}
	
	private static final Map<IJndi, List<ITransferModel>> modelListMap = new HashMap<IJndi, List<ITransferModel>>();
	
	/**
	 * 存储迁移对象内部类型与其构造器函数
	 */
	private static final Map<String, Constructor<ITransferModel>> modelMap = new HashMap<String, Constructor<ITransferModel>>();
	static{
		List<Class<ITransferModel>> m = Tool.LANG.scanClass("com.forms", ITransferModel.class);
		if(null != m && !m.isEmpty()){
			for(Class<ITransferModel> model : m){
				try {
					Constructor<ITransferModel> constructor = model.getConstructor(String.class,String.class);
					ITransferModel bean = constructor.newInstance("","");
					String innerType = bean.getInnerType();
					if(modelMap.containsKey(innerType)){
						CommonLogger.warn("发现多个类型为["+innerType+"]的迁移对象类，取第一个["+model+"]");
					}else{
						modelMap.put(innerType, constructor);	
					}
					innerType = bean.getType();
					if(modelMap.containsKey(innerType)){
						CommonLogger.warn("发现多个类型为["+innerType+"]的迁移对象类，取第一个["+model+"]");
					}else{
						modelMap.put(innerType, constructor);	
					}
				} catch (Exception e) {
					CommonLogger.error("读取迁移对象类["+model+"]的类型发生异常，请检查是否提供了"+model.getSimpleName()+"(String,String)的构造函数", e);
				}
			}
		}
	}
}
