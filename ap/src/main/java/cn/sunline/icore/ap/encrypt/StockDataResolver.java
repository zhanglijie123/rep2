package cn.sunline.icore.ap.encrypt;
//package cn.sunline.ltts.ap.encrypt;
//
//import java.lang.reflect.Method;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.crypto.Cipher;
//
//import cn.sunline.ltts.ap.namedsql.ApBasicDao;
//import cn.sunline.ltts.ap.tables.TabApDevelop.Apb_encryptDao;
//import cn.sunline.ltts.ap.tables.TabApDevelop.apb_encrypt;
//import cn.sunline.ltts.ap.util.BizUtil;
//import cn.sunline.ltts.ap.util.DBUtil;
//import cn.sunline.ltts.biz.global.CommUtil;
//import cn.sunline.ltts.busi.sdk.util.DaoUtil;
//import cn.sunline.ltts.core.api.lang.Page;
//import cn.sunline.ltts.core.api.logging.BizLog;
//import cn.sunline.ltts.core.api.logging.BizLogUtil;
//import cn.sunline.ltts.core.api.util.EncString;
//import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
//import cn.sunline.ltts.frw.model.annotation.Index;
//import cn.sunline.ltts.frw.model.annotation.Index.PrimaryKey;
//import cn.sunline.ltts.frw.model.db.Field;
//import cn.sunline.ltts.frw.model.db.Table;
//import cn.sunline.ltts.frw.model.dm.SimpleType;
//import cn.sunline.ltts.frw.model.util.ModelUtil;
//import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
//
//public class StockDataResolver {
//	
//	private static final BizLog bizlog = BizLogUtil.getBizLog(StockDataResolver.class);
//	
//	private static final String DEFAULT_ORDERBYCOLUMN = "data_create_time";
//	
//	/**
//	 * 单表处理，支持加密和解密
//	 * 处理前，setting文件中的系统参数esue=false|esfd=false|espt=false
//	 * @param table
//	 * @param mode
//	 * @throws SQLException
//	 */
//	@SuppressWarnings("unchecked")
//	public static void process(Table table, int mode){
//		
//		List<Field> encryptFields = getEncryptField(table);
//		if(encryptFields.size() > 0){
//			Class<Object> tableClass = (Class<Object>) table.getJavaClass();
//			String tableName = tableClass.getSimpleName();
//			
//			apb_encrypt encrypt = Apb_encryptDao.selectOne_odb1(tableName, false);
//			boolean isDoEncrypt = false;
//			boolean isInsert = false;
//			if(encrypt != null){
//				if(encrypt.getEncrypt_status() != E_YESORNO.YES){
//					isDoEncrypt = true;
//					encrypt.setError_text("");
//					encrypt.setProcess_number(0L);
//				}
//			}else{
//				encrypt = BizUtil.getInstance(apb_encrypt.class);
//				encrypt.setTable_name(tableName);
//				encrypt.setEncrypt_status(E_YESORNO.NO);
//				encrypt.setProcess_number(0L);
//				isInsert = true;
//				isDoEncrypt = true;
//			}
//			
//			if(isDoEncrypt){
//				DBUtil.beginTransation();
//				
//				boolean isSuccess = true;
//				Long cnt = 0L;
//				try{
//					cnt = doEncrypt(table,encryptFields,mode);
//				}catch(Exception e){
//					bizlog.error("doEncrypt error", e);
//					isSuccess = false;
//					encrypt.setError_text(e.getMessage());		
//					
//					DBUtil.rollBack();
//				}
//				
//				if(isSuccess){
//					encrypt.setEncrypt_status(E_YESORNO.YES);
//					encrypt.setProcess_number(cnt);
//				}
//				
//				if(cnt > 0 || !isSuccess){
//					if(isInsert){
//						Apb_encryptDao.insert(encrypt);
//					}else{
//						Apb_encryptDao.updateOne_odb1(encrypt);
//					}
//					DBUtil.commit();
//				}
//
//			}			
//			
//		}
//		
//	}
//	
//	@SuppressWarnings("unchecked")
//	private static Long doEncrypt(Table table, List<Field> encryptFields, int mode) throws Exception{
//		Class<Object> tableClass = (Class<Object>) table.getJavaClass();
//		
//		//如果记录数超过10000的，需要考虑其他方式
//		List<Object> datas = new ArrayList<Object>();
//		try {
//			datas = (List<Object>) DaoUtil.selectAll(tableClass);
//		} catch (Exception e) {
//			// 数据超过10000行，用分页查询处理
//			bizlog.debug("table [%s] rows more than 10000", tableClass.getSimpleName());
//			
//			return doEncryptByPaging(table,encryptFields,mode);
//		}
//		
//		if (datas.size() < 0)
//			return -1L;
//
//		
//		// 先删除所有数据
//		String sql = "delete from "+ tableClass.getSimpleName();			
//		LttsCoreBeanUtil.getDBConnectionManager().getConnection().createStatement().execute(sql);
//		
//		for (Object data : datas) {
//			Map<String, Object> dataMap =  CommUtil.toMap(data);
//			
//			doEncString(dataMap,encryptFields,mode);
//			
//			DaoUtil.insert(tableClass, dataMap);
//	    }
//		
//		return Long.valueOf(datas.size());
//	}
//	
//	
//	@SuppressWarnings({ "rawtypes" })
//	private static Long doEncryptByPaging(Table table, List<Field> encryptFields, int mode) throws Exception{
//		Class tableClass = table.getJavaClass();
//		
//		Class<? extends Index.PrimaryKey> odb = getPrimaryOdb(tableClass);		
//		String orderByColumn = getOrderByColumn(tableClass,encryptFields);
//
//		int pageSize = 1000;
//		int start = 0;
//		Long total = 0L;
//		
//		do{
//			Page<Map> page = ApBasicDao.selTableWithCount(tableClass.getSimpleName(), orderByColumn, start, pageSize, total, false);
//			total = page.getRecordCount();
//			start += pageSize;
//			
//			doProcessData(table,odb,encryptFields,page.getRecords(),mode);
//		}while(start<total);
//		
//		return total;
//	}
//
//	/**
//	 * 要先删除在插入，防止update时，主键字段是加密字段
//	 * @param table
//	 * @param odb
//	 * @param encryptFields
//	 * @param records
//	 * @param mode 
//	 */
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private static void doProcessData(Table table, Class<? extends PrimaryKey> odb, List<Field> encryptFields, List<Map> records, int mode) {
//		
//		Class<Object> tableClass = (Class<Object>)table.getJavaClass();
//		
//		for(Map data : records){
//			// 把加密字段转化为EncString类型
//			for(Field f : encryptFields){
//				Object v = data.get(f.getId());
//				if(v != null && !(v instanceof EncString)){
//					data.put(f.getId(), new EncString(v.toString()));
//				}
//			}
//			
//			// 先删除
//			DaoUtil.deleteByIndex(tableClass, odb, data);
//			
//			doEncString(data,encryptFields,mode);
//		
//			DaoUtil.insert(tableClass, data);
//		}
//	}
//
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private static void doEncString(Map data, List<Field> encryptFields, int mode) {
//		for (Field field : encryptFields) {
//			//获取原值
//			EncString value = (EncString)data.get(field.getId());
//			
//			if(value == null){
//				continue;
//			}
//			
//			Object newValue = null;
//			
//			// 如果字段已经加过密了，防止重复加密
//			if(mode == Cipher.ENCRYPT_MODE){
//				if(!isEncString(value.getValue())){
//					newValue = EncString.encrypt(value.getValue());
//				}else{
//					continue;
//				}
//			}else{
//				try{
//					newValue = EncString.decrypt(value.getValue());
//				}catch(Exception e){
//					// 解密失败，默认不处理，可能是非加密字段
//					bizlog.debug("value [%s] decrypt error[%s]",e.getMessage());
//					bizlog.error("decrypt error:",e);
//					continue;
//				}
//			}
//						
//			data.put(field.getId(), new EncString(newValue.toString()));
//		}
//		
//	}
//
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private static Class<? extends Index.PrimaryKey> getPrimaryOdb(Class tableClass) throws Exception{
//		Class<? extends Index.PrimaryKey> odb = null;;
//		
//		Class[] clazzs = tableClass.getDeclaredClasses();
//		
//		for(Class clazz : clazzs){
//			if(Index.PrimaryKey.class.isAssignableFrom(clazz)){
//				odb = clazz;
//				break;
//			}
//		}
//		
//		if(odb == null){
//			throw new Exception("table "+tableClass.getSimpleName()+" has not primaryKey odb");
//		}
//		
//		return odb;
//	}
//	
//	private static List<Field> getEncryptField(Table table){
//		List<Field> ret = new ArrayList<Field>();
//		
//		for (Field field : table.getAllElements()) {
//			
//			SimpleType st = ModelUtil.getSimpleType(field.getTypeObj());
//			if (st != null) {
//				if (SimpleType.encString == st) {
//					ret.add(field);
//				}
//			}
//		}	
//		return ret;
//	}
//	
//	/**
//	 * 获取order by 字段，默认用公共字段data_create_time
//	 * 如果没有公共字段，用其他非加密字段作为排序字段
//	 * @param tableClass
//	 * @param encryptFields
//	 * @return
//	 */
//	@SuppressWarnings("rawtypes")
//	private static String getOrderByColumn(Class tableClass, List<Field> encryptFields) {
//		boolean isDefault = false;
//		String ret = DEFAULT_ORDERBYCOLUMN;
//		
//		List<String> stringFields = new ArrayList<String>();
//		for(Field f : encryptFields){
//			stringFields.add(f.getId().toLowerCase());
//		}
//		
//		Set<String> orderBySet = new HashSet<String>();
//		
//		Method[] methods = tableClass.getDeclaredMethods();
//		
//		for(Method method : methods){
//			String methodName = method.getName();
//			if(methodName.length() <=3 || !(CommUtil.compare(methodName.substring(0, 3), "get") == 0 
//					|| CommUtil.compare(methodName.substring(0, 3), "set") == 0)){
//				continue;
//			}
//			String fieldName = methodName.substring(3).toLowerCase();
//			if(CommUtil.compare(DEFAULT_ORDERBYCOLUMN, fieldName) == 0){
//				isDefault = true;
//				break;
//			}else{
//				if(!stringFields.contains(fieldName)){
//					orderBySet.add(fieldName);
//				}
//			}
//		}
//		
//		if(!isDefault){
//			StringBuilder compositeOrderBy = new StringBuilder();
//			for(String column : orderBySet){
//				compositeOrderBy.append(column);
//				compositeOrderBy.append(",");
//			}
//			ret = compositeOrderBy.toString().substring(0,compositeOrderBy.length()-1);
//		}
//
//		return ret;
//	}
//	
//	
//	/**
//	 * 判断是否已经加过密了
//	 * 如果能否正常解密，说明已经加密
//	 * @param value
//	 * @return
//	 */
//	private static boolean isEncString(String value) {
//		boolean ret = true;
//		try{
//			EncString.decrypt(value);
//		}catch(Exception e){
//			ret = false;
//		}
//		return ret;
//	}
//
//}
