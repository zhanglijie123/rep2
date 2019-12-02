package cn.sunline.icore.ap.dataSync;

import java.util.List;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.namedsql.ApDataSyncBaseDao;
import cn.sunline.icore.ap.parm.ApBaseSystemParm;
import cn.sunline.icore.ap.seq.ApBaseSeq;
import cn.sunline.icore.ap.servicetype.SrvApDataSync;
import cn.sunline.icore.ap.tables.TabApDataSync.Apb_data_operateDao;
import cn.sunline.icore.ap.tables.TabApDataSync.App_data_sync_systemDao;
import cn.sunline.icore.ap.tables.TabApDataSync.Aps_data_syncDao;
import cn.sunline.icore.ap.tables.TabApDataSync.apb_data_operate;
import cn.sunline.icore.ap.tables.TabApDataSync.app_data_sync_system;
import cn.sunline.icore.ap.tables.TabApDataSync.aps_data_sync;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

/**
 * 数据同步
 * @author lid
 *
 */
public class ApBaseDataSync {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseDataSync.class);
	
	private static final String DATA_SYNC_MAINKEY = "AP_DATASYNC";
	
	private static final String SUBKEY_SYNCNO = "MAX_SYNC_NO";
	
	private static final String SUBKEY_ENABLE = "ENABLE";
	
	/**
	 * 登记数据操作日志
	 * @param execute
	 */
	public static void regDataOperateLog(Runnable excuter, E_DATAOPERATE operateType){
		bizlog.method(" ApDataSync.regDataOperateLog begin >>>>>>>>>>>>>>>>");
		
		if (!isEnable()){
			return;
		}
		

		
		List<String> sqlList = DBUtil.retSqls(excuter);
		
		String trxnSeq = BizUtil.getTrxRunEnvs().getTrxn_seq();
		
		for(String sql : sqlList){
			String operateId = ApBaseSeq.genSeq(ApConst.DATA_OPERATE_ID);// gen operate id
			// insert apb_data_operate
			apb_data_operate dataOperate = BizUtil.getInstance(apb_data_operate.class);
			dataOperate.setData_operate_id(operateId);  //data operate id
			dataOperate.setData_operate_dml(sql);  //data operate dml
			dataOperate.setData_operate_type(operateType);  //data operate type
			dataOperate.setTrxn_seq(trxnSeq);  //transaction sequence

			Apb_data_operateDao.insert(dataOperate);
		}
		
		// insert aps_data_sync
		List<app_data_sync_system> syncSystemList = App_data_sync_systemDao.selectAll_odb1(E_YESORNO.YES, false);
		for(app_data_sync_system syncSystem : syncSystemList){
			aps_data_sync dataSync = Aps_data_syncDao.selectOne_odb1(trxnSeq, syncSystem.getReceive_system_id(), false);
			if(dataSync != null) continue;
			
			// insert aps_data_sync
			dataSync = BizUtil.getInstance(aps_data_sync.class);
			dataSync.setTrxn_seq(trxnSeq);  //transaction sequence
			dataSync.setReceive_system_id(syncSystem.getReceive_system_id());  //receive system id
			dataSync.setService_node_no(syncSystem.getService_node_no());  //service node number
			dataSync.setData_sync_status(E_YESORNO.NO);  //data synchronization status
			Aps_data_syncDao.insert(dataSync);
		}
		
		bizlog.method(" ApDataSync.regDataOperateLog end   >>>>>>>>>>>>>>>>");
	}
	
	
	private static boolean isEnable() {
		boolean ret = true;
		String enable = null;
		try {
			enable = ApBaseSystemParm.getValue(DATA_SYNC_MAINKEY, SUBKEY_ENABLE);
		} catch (Exception e) {
			// ignore
		}
		if (CommUtil.isNull(enable) || !Boolean.valueOf(enable)){
			ret = false;
		}
		return ret;
	}


	/**
	 * 批量同步数据
	 * @param systemId
	 */
	public static void batchDataSync(String systemId){
		bizlog.method(" ApDataSync.batchDataSync begin >>>>>>>>>>>>>>>>");
		bizlog.debug("systemId[%s] data sync start >>>>>>>>>>>>>>",systemId);
		
		app_data_sync_system system = App_data_sync_systemDao.selectOne_odb2(systemId, false);
		if(system == null){
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_data_sync_system.class).getLongname(), SysDict.A.system_id.getLongName(), systemId);
		}
		
		if(system.getSystem_status() == E_YESORNO.NO){
			return;
		}
		
		String batchNo = ApBaseSeq.genSeq(ApConst.DATASYNC_BATCH_NO);// batch no
		bizlog.debug("batch no[%s]", batchNo);
		
		long maxSyncNo = ApBaseSystemParm.getIntValue(DATA_SYNC_MAINKEY, SUBKEY_SYNCNO); // batch sync number
		
		ApDataSyncBaseDao.updateBatchNoByLimit(batchNo, maxSyncNo, systemId);// update batch no
		
		List<String> dmlList = ApDataSyncBaseDao.selDmlByBatchNo(batchNo, false);
		
		boolean isSuccess = true;
		// 执行远程服务
		try {
			SrvApDataSync srvDataSync = SysUtil.getInstanceProxyByBind(SrvApDataSync.class, system.getService_node_no());
			srvDataSync.execDml(new DefaultOptions<String>(dmlList));
		} catch (Exception e) {
			bizlog.error("call data sync error", e);
			isSuccess = false;
		}
		
		if(isSuccess){
			ApDataSyncBaseDao.updateStatusByBatchNo(batchNo,E_YESORNO.YES);
		}else{
			ApDataSyncBaseDao.updateBatchNoEmptyByBatchNo(batchNo);
		}
		
		bizlog.method(" ApDataSync.batchDataSync end  >>>>>>>>>>>>>>>>");
	}

}
