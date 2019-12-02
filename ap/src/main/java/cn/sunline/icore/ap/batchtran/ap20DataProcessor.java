package cn.sunline.icore.ap.batchtran;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.ApDataSyncApi;
import cn.sunline.icore.ap.namedsql.ApDataSyncDao;
import cn.sunline.icore.ap.type.ComApDataSync.ApDataSyncGroup;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessor;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.dao.Params;
	 /**
	  * data synchronization
	  *
	  */

public class ap20DataProcessor extends
  AbstractBatchDataProcessor<cn.sunline.icore.ap.batchtran.intf.Ap20.Input, cn.sunline.icore.ap.batchtran.intf.Ap20.Property, cn.sunline.icore.ap.type.ComApDataSync.ApDataSyncGroup> {
	  
	private static final BizLog bizlog = BizLogUtil.getBizLog(ap20DataProcessor.class);
	/**
		 * 批次数据项处理逻辑。
		 * 
		 * @param job 批次作业ID
		 * @param index  批次作业第几笔数据(从1开始)
		 * @param dataItem 批次数据项
		 * @param input 批量交易输入接口
		 * @param property 批量交易属性接口
		 */
		@Override
		public void process(String jobId, int index, cn.sunline.icore.ap.type.ComApDataSync.ApDataSyncGroup dataItem, cn.sunline.icore.ap.batchtran.intf.Ap20.Input input, cn.sunline.icore.ap.batchtran.intf.Ap20.Property property) {
			bizlog.method("ap20DataProcessor.process start >>>>>>>>>>>>>>>>>>>");
			
			String targetSystemId = dataItem.getReceive_system_id();
			ApDataSyncApi.batchDataSync(targetSystemId);
			
			bizlog.method("ap20DataProcessor.process end  >>>>>>>>>>>>>>>>>>>");
		}
		
		/**
		 * 获取数据遍历器。
		 * @param input 批量交易输入接口
		 * @param property 批量交易属性接口
		 * @return 数据遍历器
		 */
		@Override
		public BatchDataWalker<cn.sunline.icore.ap.type.ComApDataSync.ApDataSyncGroup> getBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Ap20.Input input, cn.sunline.icore.ap.batchtran.intf.Ap20.Property property) {
			Params parm = new Params();
			parm.add("data_sync_status", E_YESORNO.NO);
			
			return new CursorBatchDataWalker<ApDataSyncGroup>(ApDataSyncDao.namedsql_selDataSyncGroup,parm);
		}

}


