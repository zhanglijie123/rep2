package cn.sunline.icore.ap.batchtran;

import cn.sunline.icore.ap.api.ApFileApi;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_FILEPROCTYPE;
import cn.sunline.icore.sys.type.EnumType.E_FILERECVWAY;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessor;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.core.api.exception.LttsBusinessException;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.lang.Params;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
	 /**
	  * 批量同步远程目录文件
	  *
	  */

public class ap04DataProcessor extends
  AbstractBatchDataProcessor<cn.sunline.icore.ap.batchtran.intf.Ap04.Input, cn.sunline.icore.ap.batchtran.intf.Ap04.Property, cn.sunline.icore.ap.tables.TabApFile.AppBatch> {
		private static final BizLog bizlog = BizLogUtil.getBizLog(ap04DataProcessor.class);

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
		public void process(String jobId, int index, cn.sunline.icore.ap.tables.TabApFile.AppBatch dataItem, cn.sunline.icore.ap.batchtran.intf.Ap04.Input input, cn.sunline.icore.ap.batchtran.intf.Ap04.Property property) {
			RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
			runEnvs.setTemp_data(runEnvs.getTrxn_date());
			try {
				ApFileApi.syncRemoteFile2Local(dataItem.getBusi_batch_id());
				//业务异常捕获，不影响其他任务不抛错
			} catch (LttsBusinessException  | LttsServiceException e) {
			bizlog.error("Synchronization file failed! >>>>>> busiBatchId[%s]", e, dataItem.getBusi_batch_id());
				//回滚当前任务事务
				DBUtil.rollBack();
				//重启事务
				DBUtil.beginTransation();
			}

		}
		
		/**
		 * 获取数据遍历器。
		 * @param input 批量交易输入接口
		 * @param property 批量交易属性接口
		 * @return 数据遍历器
		 */
		@Override
		public BatchDataWalker<cn.sunline.icore.ap.tables.TabApFile.AppBatch> getBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Ap04.Input input, cn.sunline.icore.ap.batchtran.intf.Ap04.Property property) {
			Params params = new Params();
			params.put("file_proc_type", E_FILEPROCTYPE.RECV_REQUEST);
			params.put("file_recv_way", E_FILERECVWAY.APPOINT_DIURECTORY);
			//RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

			// 设置公共运行变量
			//runEnvs.setTemp_date(runEnvs.getTrxn_date());
			return new CursorBatchDataWalker<>(ApFileDao.namedsql_selSyscBatchIdList, params);
			 
		}

}


