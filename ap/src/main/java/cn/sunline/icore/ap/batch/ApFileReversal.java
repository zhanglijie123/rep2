package cn.sunline.icore.ap.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.LocalFileProcessor;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.namedsql.ApFileBaseDao;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchRequest;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchRequestDao;
import cn.sunline.icore.ap.tables.TabApFile.ApbFileReversal;
import cn.sunline.icore.ap.tables.TabApFile.AppBatch;
import cn.sunline.icore.ap.tables.TabApFile.AppBatchDao;
import cn.sunline.icore.ap.type.ComApFile.ApBatchLoadData;
import cn.sunline.icore.ap.type.ComApFile.ApFileHeadInfo;
import cn.sunline.icore.ap.type.ComApFile.ApFileRetHeadInfo;
import cn.sunline.icore.ap.type.ComApFile.ApFileReversalData;
import cn.sunline.icore.ap.type.ComApFile.ApSetRequestData;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.type.EnumType.E_FILEDEALSTATUS;
import cn.sunline.icore.sys.type.EnumType.E_FILEDETAILDEALSTATUS;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.dao.CursorHandler;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.dao.Params;

/**
 * <p>
 * 文件功能说明： 文件批量冲正处理
 * </p>
 * 
 * @Author shenxy
 *         <p>
 *         <li>2018年3月6日-上午10:11:42</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年3月6日-shenxy：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApFileReversal {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApFileReversal.class);

	/**
	 * @Author shenxy
	 *         <p>
	 *         <li>2018年3月6日-上午10:16:40</li>
	 *         <li>功能说明：批量冲正文件导入</li>
	 *         </p>
	 * @param dataItem
	 */
	public static void prcReversalFileLoad(ApBatchLoadData dataItem) {
		bizlog.method(" ApFileReversal.prcReversalFileLoad begin >>>>>>>>>>>>>>>>");

		// 本地文件名
		String localFileName = ApBaseFile.getFileFullPath(dataItem.getFile_local_path(), dataItem.getFile_name());

		localFileName = ApBaseFile.getLocalHome(localFileName);

		// 读取文件列表
		List<String> fileList = ApBaseFile.readFile(new File(localFileName));

		// 读取文件为空
		if (fileList.isEmpty()) {
			return;
		}

		// 头文件信息
		ApFileHeadInfo headInfo = SysUtil.deserialize(fileList.get(0), ApFileHeadInfo.class);

		// 文件头格式不符
		if (CommUtil.isNull(headInfo.getHead_total_count())) {

			// 更新文件请求登记薄文件处理状态
			ApBatch.setFormatErrorByImport(dataItem.getBusi_batch_code());

			return; // 头文件格式不符直接返回
		}

		// 文件体信息
		List<String> fileBody = fileList.subList(1, fileList.size());

		List<ApbFileReversal> listFileTran = new ArrayList<ApbFileReversal>();

		for (String sJson : fileBody) {

			// 反序列化对象
			ApbFileReversal fielReversal = SysUtil.deserialize(sJson, ApbFileReversal.class);

			fielReversal.setBusi_batch_code(dataItem.getBusi_batch_code());
			fielReversal.setHash_value(BizUtil.getGroupHashValue("REQUEST_HASH_VALUE", dataItem.getFile_id()));
			fielReversal.setFile_detail_handling_status(E_FILEDETAILDEALSTATUS.WAIT);

			// 非空要素赋缺省值 ，后续调交易校验
			prcFieldCover(fielReversal);

			listFileTran.add(fielReversal);

			if (listFileTran.size() == 50) {

				try {
					// 文件明细信息批量插入表
					DaoUtil.insertBatch(ApbFileReversal.class, listFileTran);
					listFileTran.clear();
				}
				catch (Exception e) {
					bizlog.debug("apb file reversal insertBatch failed!", e);
					DBUtil.rollBack();
					// 导入明细表异常、更新状态
					ApBatch.setInsertErrorByImport(dataItem.getBusi_batch_code(), e.toString());
					return;

				}

			}
		}

		// 存有数据
		if (!listFileTran.isEmpty()) {
			try {
				// 文件明细信息批量插入表
				DaoUtil.insertBatch(ApbFileReversal.class, listFileTran);
				listFileTran.clear();

			}
			catch (Exception e) {
				bizlog.debug("apb file reversal insertBatch failed!", e);
				DBUtil.rollBack();
				// 导入明细表异常、更新状态
				ApBatch.setInsertErrorByImport(dataItem.getBusi_batch_code());
				return;
			}

		}

		// 获取一对一明细汇总信息
		ApFileRetHeadInfo retHeadInfo = ApFileBaseDao.selFileReversalHeadInfo(BizUtil.getTrxRunEnvs().getBusi_org_id(), dataItem.getBusi_batch_code(), true);

		// 校验头体数据、更新状态
		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(dataItem.getBusi_batch_code(), true);
		Long head_total_count = headInfo.getHead_total_count();
		Long total_count = retHeadInfo.getTotal_count();
		
		if (head_total_count.equals(total_count)){
			
			batchReq.setFile_handling_status(E_FILEDEALSTATUS.CHECKED);
		}
		else{
			
			batchReq.setFile_handling_status(E_FILEDEALSTATUS.FAILCHECK_UNEQUAL);
		}

		batchReq.setHead_total_count(headInfo.getHead_total_count());
		batchReq.setFilebody_total_count(retHeadInfo.getTotal_count());

		ApbBatchRequestDao.updateOne_odb1(batchReq);

		bizlog.method(" ApFileReversal.prcReversalFileLoad end <<<<<<<<<<<<<<<<");
	}

	/**
	 * @Author shenxy
	 *         <p>
	 *         <li>2018年3月6日-上午10:25:36</li>
	 *         <li>功能说明：文件批量冲账明细非空字段赋缺省值</li>
	 *         </p>
	 * @param fielReversal
	 */
	private static void prcFieldCover(ApbFileReversal fielReversal) {
		bizlog.method(" ApFileReversal.prcFieldCover begin >>>>>>>>>>>>>>>>");
		// 非空字段赋缺省值

		if (CommUtil.isNull(fielReversal.getInitiator_seq())) {
			fielReversal.setInitiator_seq("NULL");
		}

		if (CommUtil.isNull(fielReversal.getBusi_seq())) {
			fielReversal.setBusi_seq("NULL");
		}

		if (CommUtil.isNull(fielReversal.getBusi_batch_code())) {
			fielReversal.setBusi_batch_code("NULL");
		}

		if (CommUtil.isNull(fielReversal.getOriginal_trxn_seq())) {
			fielReversal.setOriginal_trxn_seq("NULL");
		}

		bizlog.method(" ApFileReversal.prcFieldCover end <<<<<<<<<<<<<<<<");
	}

	/**
	 * @Author shenxy
	 *         <p>
	 *         <li>2018年3月6日-上午11:36:10</li>
	 *         <li>功能说明：批量冲正文件回盘</li>
	 *         </p>
	 * @param dataItem
	 */
	public static void prcReversalFileRet(String batchCode) {
		bizlog.method(" ApFileReversal.prcReversalFileRet begin >>>>>>>>>>>>>>>>");
		// 获取文件请求登记薄信息
		ApbBatchRequest batchReqTab = ApbBatchRequestDao.selectOne_odb1(batchCode, true);

		// 返回文件名称
		String fileName = batchReqTab.getFile_name().substring(0, batchReqTab.getFile_name().indexOf(".")) + "_result";// 拼接后缀_result

		fileName = String.format("%s.txt", fileName);

		// 获取文件批量业务定义信息
		AppBatch appBatch = AppBatchDao.selectOne_odb1(batchReqTab.getBusi_batch_id(), true);

		// 获取本地路径
		String localPath = ApBaseFile.getFullPath(appBatch.getLocal_dir_code());

		// 获取文件头信息
		ApFileRetHeadInfo retHeadInfo = ApFileBaseDao.selFileReversalHeadInfo(BizUtil.getTrxRunEnvs().getBusi_org_id(), batchCode, true);

		// 转换成json格式
		String headJson = BizUtil.toJson(retHeadInfo);

		final LocalFileProcessor processor = new LocalFileProcessor(localPath, fileName, "UTF-8");

		processor.open(true);// 打开文件

		try {

			// 文件头信息写入文件
			processor.write(headJson);

			// 执行游标处理
			bizlog.debug("Execute cursor begin >>>>>>>>>>>");

			Params para = new Params();

			para.add(SysDict.A.busi_batch_code.toString(), batchCode);
			para.add(SysDict.A.org_id.toString(), BizUtil.getTrxRunEnvs().getBusi_org_id());

			// 文件体写入文件
			DaoUtil.selectList(ApFileBaseDao.namedsql_selFileReversalRecord, para, new CursorHandler<ApFileReversalData>() {

				@Override
				public boolean handle(int index, ApFileReversalData TranStosData) {

					// 转换成json格式
					String tranStosJson = BizUtil.toJson(TranStosData);

					// 写入文件
					processor.write(tranStosJson);

					return true;
				}
			});

		}
		finally {
			// 关闭文件
			processor.close();
		}

		// 登记文件发送薄
		String fileId = ApFileSend.register(fileName, appBatch.getRemote_dir_code(), appBatch.getLocal_dir_code(), E_YESORNO.NO);

		ApSetRequestData setSucessReqData = BizUtil.getInstance(ApSetRequestData.class);

		setSucessReqData.setReturn_file_id(fileId);
		setSucessReqData.setSuccess_total_count(retHeadInfo.getSuccess_total_count());
		
		// 文件处理成功、更新请求登记薄信息
		ApBatch.setStatusByExecute(batchCode, setSucessReqData);
		bizlog.method(" ApFileReversal.prcReversalFileRet end <<<<<<<<<<<<<<<<");
	}

}
