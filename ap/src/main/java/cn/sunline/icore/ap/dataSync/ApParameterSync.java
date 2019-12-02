package cn.sunline.icore.ap.dataSync;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.tables.TabApDataSync.Apb_parameter_syncDao;
import cn.sunline.icore.ap.tables.TabApDataSync.App_parameter_valueDao;
import cn.sunline.icore.ap.tables.TabApDataSync.apb_parameter_sync;
import cn.sunline.icore.ap.tables.TabApDataSync.app_parameter_value;
import cn.sunline.icore.ap.type.ComApDataSync.ApParmSyncGroup;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.icore.sys.type.EnumType.E_PARAMETERTYPE;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.exception.LTTSDaoDuplicateException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * <p>
 * 文件功能说明：参数同步控制
 *       			
 * </p>
 * 
 * @Author Liubx
 *         <p>
 *         <li>2018年8月14日-下午8:01:16</li>
 *         <li>参数同步控制</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：参数同步控制</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApParameterSync {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApParameterSync.class);
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月21日-下午3:56:09</li>
	 *         <li>功能说明：登记参数同步控制表信息</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @param paraId
	 * 				参数ID
	 * @return
	 */
	public static void regParaSyncMiddlePlat(E_PARAMETERTYPE paraType, String paraId){	
		bizlog.method(" ApParameterSync.regParaSyncMiddlePlat begin >>>>>>>>>>>>>>>>");
		
		// 带锁读取
		apb_parameter_sync syncControl =  Apb_parameter_syncDao.selectOneWithLock_odb1(paraType, true);
		
		app_parameter_value parmValue = BizUtil.getInstance(app_parameter_value.class);
		
		parmValue.setPara_type(paraType);
		parmValue.setPara_id(paraId);

		try {
			App_parameter_valueDao.insert(parmValue);
		}
		catch (LTTSDaoDuplicateException e) {
			// 主键冲突，说明已登记参数同步控制表
			bizlog.error("Primary key conflict, error_code [%s] error_message [%s]", e, e.getCode(), e.getMessage());
		}
		
		// 同步状态为未同步，不做处理; 否则将已同步状态更改为未同步
		if(syncControl.getData_sync_status() == E_YESORNO.NO){
			
			return;
		}
		syncControl.setData_sync_status(E_YESORNO.NO);
		
		// 当最近更新日期被刷新时序号重置为1，否则序号加1
		if(CommUtil.compare(syncControl.getLatest_update_date(), BizUtil.getTrxRunEnvs().getTrxn_date()) < 0){
			
			syncControl.setLatest_update_date(BizUtil.getTrxRunEnvs().getTrxn_date());
			syncControl.setSerial_no(1L);
		}else{
			
			syncControl.setSerial_no(syncControl.getSerial_no() + 1L);
		}
	
		Apb_parameter_syncDao.updateOne_odb1(syncControl);
		
		bizlog.method(" ApParameterSync.regParaSyncMiddlePlat end >>>>>>>>>>>>>>>>");
	}
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月14日-下午9:20:35</li>
	 *         <li>功能说明：获取参数同步控制表信息</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @param lockFlag
	 * 				参数同步控制表信息 是否带锁查询
	 * @return ApParmSyncGroup 参数同步控制信息
	 */
	public static ApParmSyncGroup getParaSyncInfo(E_PARAMETERTYPE paraType, E_YESORNO lockFlag){
		
		ApParmSyncGroup parmSyncGroup = BizUtil.getInstance(ApParmSyncGroup.class);
		
		apb_parameter_sync syncControl = null;
		
		// 查询参数同步控制表（带锁）
		if(lockFlag == E_YESORNO.YES){
			
			syncControl =  Apb_parameter_syncDao.selectOneWithLock_odb1(paraType, false);
		}else{
			
			syncControl =  Apb_parameter_syncDao.selectOne_odb1(paraType, false);
		}
		
		if(syncControl == null){
			
			throw APPUB.E0005(OdbFactory.getTable(apb_parameter_sync.class).getLongname(), SysDict.A.para_type.getLongName(), paraType.getValue());
		}
		parmSyncGroup.setPara_type(syncControl.getPara_type());
		parmSyncGroup.setLatest_update_date(syncControl.getLatest_update_date());
		parmSyncGroup.setData_sync_status(syncControl.getData_sync_status());
		parmSyncGroup.setSerial_no(syncControl.getSerial_no());
		
		return parmSyncGroup;
	}
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月14日-下午9:18:51</li>
	 *         <li>功能说明：更新参数同步控制状态为已同步</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @return
	 */
	public static void modifyParaSyncStatus(E_PARAMETERTYPE paraType){
		
		List<app_parameter_value> valueList = App_parameter_valueDao.selectAll_odb2(paraType, false);
		
		if(CommUtil.isNotNull(valueList)){
			return;
		}
		// 不带锁读取记录
		apb_parameter_sync syncControl =  Apb_parameter_syncDao.selectOne_odb1(paraType, false);
		
		// 更新为已同步
		syncControl.setData_sync_status(E_YESORNO.YES);
		
		Apb_parameter_syncDao.updateOne_odb1(syncControl);
	}
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月28日-下午1:28:51</li>
	 *         <li>功能说明：删除待同步参数值信息表记录</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @param paraId
	 * 				参数ID
	 * @return
	 */
	public static int deleteParmValue(E_PARAMETERTYPE paraType, String paraId){
		
		return App_parameter_valueDao.deleteOne_odb1(paraType, paraId);
	}
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月28日-下午1:18:51</li>
	 *         <li>功能说明：查询待同步参数值信息</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @return List<String> 参数ID
	 */
	public static List<String> getValueList(E_PARAMETERTYPE paraType) {
		bizlog.method(" ApParameterSync.selValueList begin >>>>>>>>>>>>>>>>");

		List<app_parameter_value> tempValueList = App_parameter_valueDao.selectAll_odb2(paraType, false);
		
		if(CommUtil.isNull(tempValueList)){
			return null;
		}
		
		List<String> list = new ArrayList<String>();
		
		for (app_parameter_value parmValue : tempValueList) {
			
			list.add(parmValue.getPara_id());
		}
		
		bizlog.method(" ApParameterSync.selValueList end <<<<<<<<<<<<<<<<");
		return list;
	}
}
