package cn.sunline.icore.ap.batchtran;

import java.util.HashMap;
import java.util.Map;

import cn.sunline.clwj.msap.sys.dict.MsDict;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.edsp.plugin.error.code.api.ErrorCodeMapUtils;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.tables.TabApFile.ApbFileReversal;
import cn.sunline.icore.ap.tables.TabApFile.ApbFileReversalDao;
import cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_FILEDEALSTATUS;
import cn.sunline.icore.sys.type.EnumType.E_FILEDETAILDEALSTATUS;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessorWithJobDataItem;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.dao.Params;

/**
 * reversal documents accounting process
 * 
 * @author shenxy
 * @Date 20180306
 */
public class apf02DataProcessor
		extends
		AbstractBatchDataProcessorWithJobDataItem<cn.sunline.icore.ap.batchtran.intf.Apf02.Input, cn.sunline.icore.ap.batchtran.intf.Apf02.Property, cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo, cn.sunline.icore.ap.tables.TabApFile.ApbFileReversal> {
	private static final String TRAN_1000 = "1000";

	private static final BizLog bizlog = BizLogUtil.getBizLog(apf02DataProcessor.class);

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
	public void process(String jobId, int index, cn.sunline.icore.ap.tables.TabApFile.ApbFileReversal dataItem,
			cn.sunline.icore.ap.batchtran.intf.Apf02.Input input, cn.sunline.icore.ap.batchtran.intf.Apf02.Property property) {

		bizlog.debug("apf02DataProcessor.process dataItem = [%s]", dataItem);

		Map<String, Object> reversalInput = new HashMap<String, Object>();

		reversalInput.put(MsDict.Comm.orig_initiator_seq.toString(), dataItem.getOriginal_trxn_seq());
		reversalInput.put(MsDict.Comm.reversal_type.toString(), dataItem.getReversal_type());
		reversalInput.put(MsDict.Comm.chrg_reversal_ind.toString(), dataItem.getChrg_reversal_ind());
		reversalInput.put(MsDict.Comm.voch_reversal_ind.toString(), dataItem.getVoch_reversal_ind());
		reversalInput.put(MsDict.Comm.unfroze_reversal_ind.toString(), dataItem.getUnfroze_reversal_ind());
		// reversalInput.put(SysDict.A.busi_batch_code.toString(), dataItem.getBusi_batch_code());
    
		// 外围系统传进来的流水，记账时将其写入交易环境变量
		BizUtil.getTrxRunEnvs().setInitiator_seq(dataItem.getInitiator_seq());
		BizUtil.getTrxRunEnvs().setBusi_seq(dataItem.getBusi_seq());
		BizUtil.getTrxRunEnvs().setSet_seq_ind(E_YESORNO.YES);

		try {
			// 调用一对一转账flowtran
			SysUtil.callFlowTran(TRAN_1000, reversalInput);

			dataItem.setFile_detail_handling_status(E_FILEDETAILDEALSTATUS.SUCESS);
		}
		catch (Exception e) {

			DBUtil.rollBack();
			bizlog.error("callFlowTran(TRAN_1000) error >>>>>>>>>>", e);
			dataItem.setFile_detail_handling_status(E_FILEDETAILDEALSTATUS.FAIL);
			
			// 错误信息
			dataItem.setError_text(e.getMessage());
			// 错误ID
			dataItem.setError_id(ErrorCodeMapUtils.getErrorCodeModel(e).getInnerErrorCode());
			// 错误码
			dataItem.setError_code(ErrorCodeMapUtils.getErrorCodeModel(e).getOuterErrorCode());
		
		}

		// 更新一对一转账明细登记薄
		ApbFileReversalDao.updateOne_odb1(dataItem);
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
	public BatchDataWalker<cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo> getBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Apf02.Input input,
			cn.sunline.icore.ap.batchtran.intf.Apf02.Property property) {
		// 获取公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		Params parm = new Params();
		parm.add("org_id", runEnvs.getBusi_org_id());
		parm.add("trxn_time", runEnvs.getComputer_time());
		parm.add("file_handling_status", E_FILEDEALSTATUS.CHECKED);
		parm.add("file_detail_handling_status", E_FILEDETAILDEALSTATUS.WAIT);

		return new CursorBatchDataWalker<ApDataGroupNo>(ApFileDao.namedsql_selGroupIdForFileReversal, parm);
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
	public BatchDataWalker<cn.sunline.icore.ap.tables.TabApFile.ApbFileReversal> getJobBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Apf02.Input input,
			cn.sunline.icore.ap.batchtran.intf.Apf02.Property property, cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo dataItem) {
		// 获取公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		Params parm = new Params();
		parm.add("org_id", runEnvs.getBusi_org_id());
		parm.add("trxn_time", runEnvs.getComputer_time());
		parm.add("file_handling_status", E_FILEDEALSTATUS.CHECKED);
		parm.add("hash_value", dataItem.getHash_value());
		parm.add("file_detail_handling_status", E_FILEDETAILDEALSTATUS.WAIT);

		return new CursorBatchDataWalker<ApbFileReversal>(ApFileDao.namedsql_selFileReversalData, parm);
	}

}
