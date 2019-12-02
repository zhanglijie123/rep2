package cn.sunline.icore.ap.batchtran;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.batch.ApFileSend;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo;
import cn.sunline.icore.ap.type.ComApFile.ApSendFileIn;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessorWithJobDataItem;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.core.api.lang.Params;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * 文件上传
 */

public class ap03DataProcessor
		extends
		AbstractBatchDataProcessorWithJobDataItem<cn.sunline.icore.ap.batchtran.intf.Ap03.Input, cn.sunline.icore.ap.batchtran.intf.Ap03.Property, cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo, cn.sunline.icore.ap.type.ComApFile.ApSendFileIn> {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ap03DataProcessor.class);

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
	public void process(String jobId, int index, cn.sunline.icore.ap.type.ComApFile.ApSendFileIn dataItem, cn.sunline.icore.ap.batchtran.intf.Ap03.Input input,
			cn.sunline.icore.ap.batchtran.intf.Ap03.Property property) {

		bizlog.method("ap03DataProcessor  begin processing.....dataItem=[%s]", dataItem);

		// 上传成功, 更新文件发送薄
		if (ApFileSend.upload(dataItem.getFile_name(), dataItem.getFile_local_path(), dataItem.getFile_server_path(), dataItem.getAppend_ok_ind())) {

			ApFileSend.modify(dataItem.getFile_id(), E_YESORNO.YES);
		}

		bizlog.method("ap03DataProcessor  end processing.....dataItem=[%s]", dataItem);

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
	public BatchDataWalker<cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo> getBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Ap03.Input input,
			cn.sunline.icore.ap.batchtran.intf.Ap03.Property property) {

		Params parm = new Params();
		parm.add("send_ind", E_YESORNO.NO);

		return new CursorBatchDataWalker<ApDataGroupNo>(ApFileDao.namedsql_selGroupIdForSend, parm);
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
	public BatchDataWalker<cn.sunline.icore.ap.type.ComApFile.ApSendFileIn> getJobBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Ap03.Input input,
			cn.sunline.icore.ap.batchtran.intf.Ap03.Property property, cn.sunline.icore.ap.type.ComApFile.ApDataGroupNo dataItem) {

		Params parm = new Params();
		parm.add("hash_value", dataItem.getHash_value());
		parm.add("send_ind", E_YESORNO.NO);

		return new CursorBatchDataWalker<ApSendFileIn>(ApFileDao.namedsql_selFileSendData, parm);
	}

}
