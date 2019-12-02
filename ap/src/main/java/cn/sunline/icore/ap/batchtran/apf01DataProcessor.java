package cn.sunline.icore.ap.batchtran;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.batch.ApFileReversal;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchRequest;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchRequestDao;
import cn.sunline.icore.ap.type.ComApFile.ApBatchLoadData;
import cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.type.EnumType.E_BATCHTYPE;
import cn.sunline.icore.sys.type.EnumType.E_FILEDEALSTATUS;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessorWithJobDataItem;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.dao.Params;

/**
 * reversal documents are imported
 * 
 * @author shenxy
 * @Date 20180306
 */

public class apf01DataProcessor
		extends
		AbstractBatchDataProcessorWithJobDataItem<cn.sunline.icore.ap.batchtran.intf.Apf01.Input, cn.sunline.icore.ap.batchtran.intf.Apf01.Property, cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo, cn.sunline.icore.ap.type.ComApFile.ApBatchLoadData> {

	private static final BizLog bizlog = BizLogUtil.getBizLog(apf01DataProcessor.class);

	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param job
	 *            批次作业ID
	 * @param index
	 *            批次作业第几笔数据(从1开始)
	 * @param dataItem
	 *            批次数据项
	 * @param input
	 *            批量交易输入接口
	 * @param property
	 *            批量交易属性接口
	 */
	@Override
	public void process(String jobId, int index, cn.sunline.icore.ap.type.ComApFile.ApBatchLoadData dataItem, cn.sunline.icore.ap.batchtran.intf.Apf01.Input input,
			cn.sunline.icore.ap.batchtran.intf.Apf01.Property property) {

		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(dataItem.getBusi_batch_code(), true);

		bizlog.method("exec FileReversal  downLoad start >>>>>>>>>>>>>>");

		try {

			// 批量冲正文件导入
			ApFileReversal.prcReversalFileLoad(dataItem);

		}
		catch (Exception e) {

			bizlog.error("exec FileReversal  downLoad  error >>>>>>>>>>[%s],[%s]", e.getMessage(), e);

			DBUtil.rollBack();
			batchReq.setFile_handling_status(E_FILEDEALSTATUS.FAILCHECK_OTHER);
			batchReq.setError_text(e.getMessage()); //错误信息

			//更新文件批量请求薄信息
			ApbBatchRequestDao.updateOne_odb1(batchReq);
		}
		
		bizlog.method("exec FileReversal  downLoad end <<<<<<<<<<<<<<");
	}

	/**
	 * 获取数据遍历器。
	 * 
	 * @param input
	 *            批量交易输入接口
	 * @param property
	 *            批量交易属性接口
	 * @return 数据遍历器
	 */
	@Override
	public BatchDataWalker<cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo> getBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Apf01.Input input,
			cn.sunline.icore.ap.batchtran.intf.Apf01.Property property) {

		Params parm = new Params();
		parm.add("org_id", BizUtil.getTrxRunEnvs().getBusi_org_id());
		parm.add("busi_batch_type", E_BATCHTYPE.REVERSAL);
		parm.add("file_handling_status", E_FILEDEALSTATUS.UNCHECK);
		parm.add("receive_ind", E_YESORNO.YES);

		return new CursorBatchDataWalker<ApDataGroupNo>(ApFileDao.namedsql_selGroupIdForBatchLoad, parm);
	}

	/**
	 * 获取作业数据遍历器
	 * 
	 * @param input
	 *            批量交易输入接口
	 * @param property
	 *            批量交易属性接口
	 * @param dataItem
	 *            批次数据项
	 * @return
	 */
	public BatchDataWalker<cn.sunline.icore.ap.type.ComApFile.ApBatchLoadData> getJobBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Apf01.Input input,
			cn.sunline.icore.ap.batchtran.intf.Apf01.Property property, cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo dataItem) {
		Params parm = new Params();
		parm.add("org_id", BizUtil.getTrxRunEnvs().getBusi_org_id());
		parm.add("busi_batch_type", E_BATCHTYPE.REVERSAL);
		parm.add("file_handling_status", E_FILEDEALSTATUS.UNCHECK);
		parm.add("receive_ind", E_YESORNO.YES);
		parm.add("hash_value", dataItem.getHash_value());

		return new CursorBatchDataWalker<ApBatchLoadData>(ApFileDao.namedsql_selBatchLoadData, parm);
	}

}
