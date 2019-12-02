package cn.sunline.icore.ap.batch;

import java.math.BigDecimal;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchRequest;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchRequestDao;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchSend;
import cn.sunline.icore.ap.tables.TabApFile.ApbBatchSendDao;
import cn.sunline.icore.ap.tables.TabApFile.AppBatch;
import cn.sunline.icore.ap.tables.TabApFile.AppBatchDao;
import cn.sunline.icore.ap.type.ComApFile.ApBatchResultOut;
import cn.sunline.icore.ap.type.ComApFile.ApFileIn;
import cn.sunline.icore.ap.type.ComApFile.ApSetRequestData;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_FILEDEALSTATUS;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * <p>
 * 文件功能说明： 文件批量相关
 * </p>
 * 
 * @Author yangdl
 *         <p>
 *         <li>2017年2月28日-下午5:33:53</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20160228 yangdl：文件批量相关</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */

public class ApBatch {
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBatch.class);
	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年2月28日-下午8:58:06</li>
	 *         <li>功能说明：文件批量申请</li>
	 *         </p>
	 * @param reqInfo
	 */
	public static void fileBatchApply(final ApFileIn reqInfo) {
		// 非空字段检查
		BizUtil.fieldNotNull(reqInfo.getBusi_batch_code(), SysDict.A.busi_batch_code.getId(), SysDict.A.busi_batch_code.getLongName());// 文件批量号
		BizUtil.fieldNotNull(reqInfo.getBusi_batch_id(), SysDict.A.busi_batch_id.getId(), SysDict.A.busi_batch_id.getLongName());// 文件批量业务id
		BizUtil.fieldNotNull(reqInfo.getFile_server_path(), SysDict.A.file_server_path.getId(), SysDict.A.file_server_path.getLongName());// 文件服务器路径
		BizUtil.fieldNotNull(reqInfo.getFile_name(), SysDict.A.field_name.getId(), SysDict.A.field_name.getLongName()); // 文件名称
		BizUtil.fieldNotNull(reqInfo.getTiming_process_ind(), SysDict.A.timing_process_ind.getId(), SysDict.A.timing_process_ind.getLongName()); // 定时处理标志

		String processTime = reqInfo.getTiming_process_time();

		if (reqInfo.getTiming_process_ind() == E_YESORNO.YES) {
			BizUtil.fieldNotNull(processTime, SysDict.A.timing_process_time.getId(), SysDict.A.timing_process_time.getLongName()); // 定时处理时间
			// 时间格式校验
			BizUtil.isDateString(processTime, "yyyyMMdd HH:mm:ss");
		}
		else {
			processTime = null;
		}

		// 文件批量收费标志 = Y，收费要素检查
		if (reqInfo.getBatch_charges_ind() == E_YESORNO.YES) {

			BizUtil.fieldNotNull(reqInfo.getChrg_code(), SysDict.A.chrg_code.getId(), SysDict.A.chrg_code.getLongName());
			BizUtil.fieldNotNull(reqInfo.getDeduct_chrg_acct(), SysDict.A.deduct_chrg_acct.getId(), SysDict.A.deduct_chrg_acct.getLongName());
			BizUtil.fieldNotNull(reqInfo.getDeduct_chrg_ccy(), SysDict.A.deduct_chrg_ccy.getId(), SysDict.A.deduct_chrg_ccy.getLongName());
		}

		// 获取公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		// 文件批量业务ID检查(不存在则报错)
		AppBatch appBatch = AppBatchDao.selectOne_odb1(reqInfo.getBusi_batch_id(), false);
		if (CommUtil.isNull(appBatch)) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(AppBatch.class).getLongname(), SysDict.A.busi_batch_id.getLongName(), reqInfo.getBusi_batch_id());
		}

		// 文件批量号唯一性检查
		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOne_odb1(reqInfo.getBusi_batch_code(), false);

		if (CommUtil.isNotNull(batchReq)) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(ApbBatchRequest.class).getLongname(), SysDict.A.busi_batch_code.getLongName(), reqInfo.getBusi_batch_code());
		}

//		// 业务渠道许可检查
//		AppBatch_channel batchChannel = AppBatch_channelDao.selectOne_odb1(reqInfo.getBusi_batch_id(), runEnvs.getChannel_id(), false);
//
//		if (CommUtil.isNull(batchChannel)) {
//			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(AppBatchChannel.class).getLongname(), SysDict.A.busi_batch_id.getLongName(), reqInfo.getBusi_batch_id());
//		}

		// 登记文件收取登记簿
		String fileId = ApFileRecv.register(reqInfo.getFile_name(), reqInfo.getFile_server_path(), appBatch.getLocal_dir_code());

		batchReq = BizUtil.getInstance(ApbBatchRequest.class);
		batchReq.setBusi_batch_code(reqInfo.getBusi_batch_code());// 文件批量号
		batchReq.setBusi_batch_id(reqInfo.getBusi_batch_id()); // 文件批量业务ID
		batchReq.setBusi_batch_type(appBatch.getBusi_batch_type()); // 文件批量类型
		batchReq.setBatch_detail_type(appBatch.getBatch_detail_type()); //批量明细类别
		batchReq.setRequest_file_id(fileId); // 请求文件ID
		batchReq.setFile_name(reqInfo.getFile_name()); // 文件名
		batchReq.setTiming_process_ind(reqInfo.getTiming_process_ind()); // 定时处理标志
		batchReq.setTiming_process_time(processTime); // 定时处理时间
		batchReq.setBatch_charges_ind(CommUtil.nvl(reqInfo.getBatch_charges_ind(), E_YESORNO.NO)); // 文件批量收费标志
		batchReq.setChrg_code(reqInfo.getChrg_code()); // 收费代码
		batchReq.setDeduct_chrg_acct(reqInfo.getDeduct_chrg_acct()); // 收费账号
		batchReq.setDeduct_chrg_ccy(reqInfo.getDeduct_chrg_ccy()); // 收费币种
		batchReq.setHash_value(BizUtil.getGroupHashValue("REQUEST_HASH_VALUE", fileId)); // 散列值
		batchReq.setFile_handling_status(E_FILEDEALSTATUS.UNCHECK); // 待校验
		batchReq.setHead_total_count(0L); // 文件头记录数
		batchReq.setHead_total_amt(BigDecimal.ZERO); // 文件头总金额
		batchReq.setFilebody_total_count(0L); // 文件体记录数
		batchReq.setFilebody_total_amt(BigDecimal.ZERO); // 文件体总金额
		batchReq.setSuccess_total_count(0L); // 成功总笔数
		batchReq.setSuccess_total_amt(BigDecimal.ZERO); // 成功总金额
		batchReq.setBranch_id(runEnvs.getTrxn_branch()); // 申请机构
		batchReq.setTeller_id(runEnvs.getTrxn_teller()); // 申请柜员
		batchReq.setTrxn_channel(runEnvs.getChannel_id()); // 申请渠道

		// 登记文件批量请求登记薄
		ApbBatchRequestDao.insert(batchReq);
	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年2月28日-下午8:58:06</li>
	 *         <li>功能说明: 文件导入成功, 更新状态</li>
	 *         </p>
	 * @param batchCode
	 * @param count
	 * @param amount
	 */
	public static void setSuccessByImport(String batchCode, long count, BigDecimal amount) {

		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(batchCode, true);

		batchReq.setFile_handling_status(E_FILEDEALSTATUS.CHECKED);

		batchReq.setHead_total_count(count);
		batchReq.setHead_total_amt(amount);
		batchReq.setFilebody_total_count(count);
		batchReq.setFilebody_total_amt(amount);

		ApbBatchRequestDao.updateOne_odb1(batchReq);

	}
	
	
	/**
	 * @Author maliang
	 *         <p>
	 *         <li>2017年6月2日-下午14:18:06</li>
	 *         <li>功能说明: 文件加工成功后, 更新状态</li>
	 *         </p>
	 * @param batchCode
	 * @param count
	 * @param amount
	 */
	public static void setSuccessBy(String batchCode) {

		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(batchCode, true);

		batchReq.setFile_handling_status(E_FILEDEALSTATUS.CHECKED);

		ApbBatchRequestDao.updateOne_odb1(batchReq);

	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年2月28日-下午8:58:06</li>
	 *         <li>功能说明: 文件格式有误, 更新状态</li>
	 *         </p>
	 * @param batchCode
	 */
	public static void setFormatErrorByImport(String batchCode) {

		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(batchCode, true);

		batchReq.setFile_handling_status(E_FILEDEALSTATUS.FAILCHECK_FORMAT);

		ApbBatchRequestDao.updateOne_odb1(batchReq);

	}
	
	public  static  void  setInsertErrorByImport(String batchCode){
		
		setInsertErrorByImport(batchCode, "null");

	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月16日-下午3:37:38</li>
	 *         <li>功能说明：文件明细导入异常，更新状态</li>
	 *         </p>
	 * @param batchCode
	 */
	public  static  void  setInsertErrorByImport(String batchCode, String e){
		
		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(batchCode, true);

		batchReq.setFile_handling_status(E_FILEDEALSTATUS.FAILCHECK_INSERT);
		int len = e.length();
		if (len>1000) 
			len = 1000;
		batchReq.setError_text(e.substring(0,len));
		ApbBatchRequestDao.updateOne_odb1(batchReq);

	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年2月28日-下午8:58:06</li>
	 *         <li>功能说明: 明细导入后, 更新状态</li>
	 *         </p>
	 * @param batchCode
	 * @param headCount
	 * @param headAmount
	 * @param bodyCount
	 * @param bodyAmount
	 */
	public static void setStatusByImport(String batchCode, long headCount, BigDecimal headAmount, long bodyCount, BigDecimal bodyAmount) {

		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(batchCode, true);

		if (headCount == bodyCount && CommUtil.equals(headAmount, bodyAmount))
			batchReq.setFile_handling_status(E_FILEDEALSTATUS.CHECKED);
		else
			batchReq.setFile_handling_status(E_FILEDEALSTATUS.FAILCHECK_UNEQUAL);

		batchReq.setHead_total_count(headCount);
		batchReq.setHead_total_amt(headAmount);
		batchReq.setFilebody_total_count(bodyCount);
		batchReq.setFilebody_total_amt(bodyAmount);

		ApbBatchRequestDao.updateOne_odb1(batchReq);

	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年2月28日-下午8:58:06</li>
	 *         <li>功能说明: 文件执行结束, 更新状态</li>
	 *         </p>
	 * @param batchCode
	 * @param successCount
	 * @param successAmount
	 */
	public static void setStatusByExecute(String batchCode, ApSetRequestData setSucessReqData) {

		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(batchCode, true);

		batchReq.setFile_handling_status(E_FILEDEALSTATUS.SUCCESS);

		batchReq.setSuccess_total_count(setSucessReqData.getSuccess_total_count()); // 成功总笔数
		batchReq.setSuccess_total_amt(setSucessReqData.getSuccess_total_amt()); // 成功总金额
		batchReq.setReturn_file_id(setSucessReqData.getReturn_file_id()); // 返回文件ID
		batchReq.setCalc_chrg_amt(setSucessReqData.getCalc_chrg_amt()); // 计费金额
		batchReq.setCalc_chrg_ccy(setSucessReqData.getCalc_chrg_ccy()); // 计费币种
		batchReq.setTrxn_seq(setSucessReqData.getTrxn_seq()); // 交易流水

		ApbBatchRequestDao.updateOne_odb1(batchReq);

	}

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年3月6日-下午4:03:20</li>
	 *         <li>功能说明：文件批量处理结果查询</li>
	 *         </p>
	 * @param batchCode
	 * @return ApBatchResultOut
	 */
	public static ApBatchResultOut queryBatchResult(String batchCode) {

		// 非空字段检查
		BizUtil.fieldNotNull(batchCode, SysDict.A.busi_batch_code.getId(), SysDict.A.busi_batch_code.getLongName());

		// 获取文件批量请求登记薄
		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOne_odb1(batchCode, false);

		if (batchReq == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(ApbBatchRequest.class).getLongname(), SysDict.A.busi_batch_code.getLongName(), batchCode);
		}

		ApBatchResultOut cplOut = BizUtil.getInstance(ApBatchResultOut.class);

		cplOut.setFile_handling_status(batchReq.getFile_handling_status()); // 文件处理状态

		if (batchReq.getFile_handling_status() == E_FILEDEALSTATUS.SUCCESS) {

			// 获取文件发送簿
			ApbBatchSend batchSend = ApbBatchSendDao.selectOne_odb1(batchReq.getReturn_file_id(), false);

			if (batchSend == null) {
				throw ApPubErr.APPUB.E0005(OdbFactory.getTable(ApbBatchSend.class).getLongname(), SysDict.A.file_id.getLongName(), batchReq.getRequest_file_id());
			}

			if (batchSend.getSend_ind() == E_YESORNO.YES) {
				cplOut.setSuccess_total_count(batchReq.getSuccess_total_count()); // 成功总笔数
				cplOut.setSuccess_total_amt(batchReq.getSuccess_total_amt()); // 成功总金额
				cplOut.setFile_name(batchSend.getFile_name()); // 返回文件名称
				cplOut.setFile_server_path(batchSend.getFile_server_path()); // 返回文件路径
				cplOut.setDeduct_chrg_acct(batchReq.getDeduct_chrg_acct()); // 扣款账号
				cplOut.setDeduct_chrg_ccy(batchReq.getDeduct_chrg_ccy()); // 扣款币种
				cplOut.setCalc_chrg_amt(batchReq.getCalc_chrg_amt()); // 实际扣款金额
				cplOut.setCalc_chrg_ccy(batchReq.getCalc_chrg_ccy()); // 实际扣款币种
			}
		}

		return cplOut;
	}
	
	/**
	 * @Author wengxt
	 *         <p>
	 *         <li>2017年2月28日-下午8:58:06</li>
	 *         <li>功能说明: 更新批量表状态</li>
	 *         </p>
	 * @param batchCode
	 * @param headCount
	 * @param headAmount
	 * @param bodyCount
	 * @param bodyAmount
	 */
	public static void setbatchReqStatus(String batchCode, E_FILEDEALSTATUS fileDealStatus) {

		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(batchCode, true);

		batchReq.setFile_handling_status(fileDealStatus);

		ApbBatchRequestDao.updateOne_odb1(batchReq);

		return;
	}
	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年2月28日-下午8:58:06</li>
	 *         <li>功能说明: 明细导入后, 更新状态</li>
	 *         </p>
	 * @param batchCode
	 * @param headCount
	 * @param headAmount
	 * @param bodyCount
	 * @param bodyAmount
	 * @return 校验成功返回true，否则返回false
	 */
	public static boolean returnStatusByImport(String batchCode, long headCount, BigDecimal headAmount, long bodyCount, BigDecimal bodyAmount) {

		boolean flag = false;
		ApbBatchRequest batchReq = ApbBatchRequestDao.selectOneWithLock_odb1(batchCode, true);

		if (headCount == bodyCount && CommUtil.equals(headAmount, bodyAmount)) {
			batchReq.setFile_handling_status(E_FILEDEALSTATUS.CHECKED);
			flag = true;
		}
		else
			batchReq.setFile_handling_status(E_FILEDEALSTATUS.FAILCHECK_UNEQUAL);

		batchReq.setHead_total_count(headCount);
		batchReq.setHead_total_amt(headAmount);
		batchReq.setFilebody_total_count(bodyCount);
		batchReq.setFilebody_total_amt(bodyAmount);

		ApbBatchRequestDao.updateOne_odb1(batchReq);

		return flag;
	}
	

}
