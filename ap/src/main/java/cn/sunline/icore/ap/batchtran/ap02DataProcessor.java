package cn.sunline.icore.ap.batchtran;

import java.io.File;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.ApFileApi;
import cn.sunline.icore.ap.api.ApSystemParmApi;
import cn.sunline.icore.ap.batch.ApFileRecv;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo;
import cn.sunline.icore.ap.type.ComApFile.ApRecvFileIn;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessorWithJobDataItem;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.core.api.lang.Params;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * 文件接收
 */

public class ap02DataProcessor extends
		AbstractBatchDataProcessorWithJobDataItem<cn.sunline.icore.ap.batchtran.intf.Ap02.Input, cn.sunline.icore.ap.batchtran.intf.Ap02.Property, cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo, cn.sunline.icore.ap.type.ComApFile.ApRecvFileIn> {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ap02DataProcessor.class);
	private static final Object lock = new Object();

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
	public void process(String jobId, int index, cn.sunline.icore.ap.type.ComApFile.ApRecvFileIn dataItem,
			cn.sunline.icore.ap.batchtran.intf.Ap02.Input input,
			cn.sunline.icore.ap.batchtran.intf.Ap02.Property property) {

		bizlog.method("ap02DataProcessor  begin processing.....dataItem=[%s]", dataItem);

		// 获取服务器文件路径
		String remoteFileName = ApFileApi.getFileFullPath(dataItem.getFile_server_path(), dataItem.getFile_name());

		// 获取本地文件路径
		String localFileName = ApFileApi.getFileFullPath(dataItem.getFile_local_path(), dataItem.getFile_name());

		synchronized (lock) {
			File file = new File(localFileName);
			if (file.exists() && file.isFile()) {
				if (bizlog.isDebugEnabled())
					bizlog.debug("To download the file[%s] already exists", localFileName);
				return;
			}
		}

		boolean sucessFlag = true;

		try {
			// 返回本地路径
//			ApFileApi.createClient(null).download(localFileName, remoteFileName);
			ApFileApi.createClient(null).download(ApBaseFile.getMsTransferFileInfo(dataItem.getFile_local_path(),
					dataItem.getFile_server_path(), dataItem.getFile_name(), false));
		} catch (Exception e) {
			sucessFlag = false;
			bizlog.error("File Download fail  >>>>>>>>>>>>>>>");
		}

		// 接收成功、更新文件接收登记薄
		if (sucessFlag) {
			ApFileRecv.modify(dataItem.getFile_id(), E_YESORNO.YES);
		} else {
			ApFileRecv.modify(dataItem.getFile_id(), E_YESORNO.NO);
		}

		bizlog.method("ap02DataProcessor  end processing.....dataItem=[%s]", dataItem);

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
	public BatchDataWalker<cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo> getBatchDataWalker(
			cn.sunline.icore.ap.batchtran.intf.Ap02.Input input,
			cn.sunline.icore.ap.batchtran.intf.Ap02.Property property) {

		Params parm = new Params();
		parm.add("receive_ind", E_YESORNO.NO);

		return new CursorBatchDataWalker<ApDataGroupNo>(ApFileDao.namedsql_selGroupIdForRecv, parm);
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
	public BatchDataWalker<cn.sunline.icore.ap.type.ComApFile.ApRecvFileIn> getJobBatchDataWalker(
			cn.sunline.icore.ap.batchtran.intf.Ap02.Input input,
			cn.sunline.icore.ap.batchtran.intf.Ap02.Property property,
			cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo dataItem) {

		Params parm = new Params();
		parm.add("hash_value", dataItem.getHash_value());
		parm.add("receive_ind", E_YESORNO.NO);
		parm.add("fail_total_count", ApSystemParmApi.getValue(ApConst.UPGRADE_THRESHOILD_COUNT, ApConst.WILDCARD));

		return new CursorBatchDataWalker<ApRecvFileIn>(ApFileDao.namedsql_selFileReceiveData, parm);
	}
}
