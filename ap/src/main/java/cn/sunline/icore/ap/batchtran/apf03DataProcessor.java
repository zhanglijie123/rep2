package cn.sunline.icore.ap.batchtran;

import cn.sunline.icore.ap.batch.ApFileReversal;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchRequest;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchRequestDao;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_FILEDEALSTATUS;
import cn.sunline.icore.sys.type.EnumType.E_FILEDETAILDEALSTATUS;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessor;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.dao.Params;

/**
 * reversal documents counteroffer
 * 
 * @author shenxy
 * @Date 20180306
 */
public class apf03DataProcessor extends
		AbstractBatchDataProcessor<cn.sunline.icore.ap.batchtran.intf.Apf03.Input, cn.sunline.icore.ap.batchtran.intf.Apf03.Property, String> {

	private static final BizLog bizlog = BizLogUtil.getBizLog(apf03DataProcessor.class);

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
	public void process(String jobId, int index, String dataItem, cn.sunline.icore.ap.batchtran.intf.Apf03.Input input,
			cn.sunline.icore.ap.batchtran.intf.Apf03.Property property) {
		
		bizlog.method("exec FileReversal  return start >>>>>>>>>>>>>>");

		ApbBatchRequest batchReq =  ApbBatchRequestDao.selectOneWithLock_odb1(dataItem, true);

		try {
			
			//一对一转账回盘处理
			ApFileReversal.prcReversalFileRet(dataItem);
		}
		catch (Exception e) {
			DBUtil.rollBack();
			batchReq.setFile_handling_status(E_FILEDEALSTATUS.FAILCHECK_OTHER);
			batchReq.setError_text(e.getMessage());  //错误信息
			
			//更新文件批量请求薄信息
			ApbBatchRequestDao.updateOne_odb1(batchReq);
			
		}

		bizlog.method("exec FileReversal  return end  <<<<<<<<<<<<<<<");
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
	public BatchDataWalker<String> getBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Apf03.Input input,
			cn.sunline.icore.ap.batchtran.intf.Apf03.Property property) {
		// 获取公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		Params parm = new Params();
		parm.add("org_id", runEnvs.getBusi_org_id());
		parm.add("file_handling_status", E_FILEDEALSTATUS.CHECKED);
		parm.add("file_detail_handling_status", E_FILEDETAILDEALSTATUS.WAIT);

		return new CursorBatchDataWalker<String>(ApFileDao.namedsql_selFileReversalResultData, parm);
	}

}
