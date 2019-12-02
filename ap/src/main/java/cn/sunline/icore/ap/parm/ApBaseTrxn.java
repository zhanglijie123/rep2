package cn.sunline.icore.ap.parm;

import cn.sunline.clwj.msap.biz.tables.MsOnlTable.MssTransaction;
import cn.sunline.clwj.msap.biz.transaction.MsTrxn;
import cn.sunline.clwj.msap.iobus.type.IoMsBizComplex.IoMsTrxnDefine;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_CASHTRXN;
import cn.sunline.icore.ap.type.ComApBook.ApTransactionInfo;
import cn.sunline.icore.ap.type.ComApSystem.ApTrxnInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * <p>
 * 文件功能说明：交易参数
 * </p>
 * 
 * @Author zhangql
 *         <p>
 *         <li>2016年12月8日-下午12:43:50</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 zhangql：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseTrxn {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseTrxn.class);

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月8日-下午8:02:24</li>
	 *         <li>功能说明：获取交易参数信息</li>
	 *         </p>
	 * @param trxnCode
	 * @return
	 */
	public static ApTrxnInfo getInfo(String trxnCode) {

		// 获取交易码定义表
		IoMsTrxnDefine trxn = MsTrxn.getTrxnDefine(trxnCode);

		ApTrxnInfo trxnInfo = BizUtil.getInstance(ApTrxnInfo.class);

		trxnInfo.setTrxn_code(trxn.getTrxn_code());
		trxnInfo.setTrxn_desc(trxn.getTrxn_desc());
		trxnInfo.setTrxn_type(trxn.getTrxn_type());
		trxnInfo.setAllow_reversal(trxn.getAllow_reversal());
		trxnInfo.setEnable_ind(trxn.getEnable_ind());
		trxnInfo.setFlow_trxn_id(trxn.getFlow_trxn_id());
		trxnInfo.setLog_level(trxn.getLog_level());
		trxnInfo.setOver_time(trxn.getOver_time());
		trxnInfo.setRegister_packet_ind(trxn.getRegister_packet_ind());
		trxnInfo.setReversal_ind(trxn.getReversal_ind());
		/*
		 * trxnInfo.setData_create_time(trxn.getData_create_time());
		 * trxnInfo.setData_create_user(trxn.getData_create_user());
		 * trxnInfo.setData_update_time(trxn.getData_update_time());
		 * trxnInfo.setData_update_time(trxn.getData_update_time());
		 * trxnInfo.setData_update_user(trxn.getData_update_user());
		 * trxnInfo.setData_version(trxn.getData_version());
		 */
		return trxnInfo;
	}

	/**
	 * @Author zhangql
	 *         <p>
	 *         <li>2016年12月9日-下午1:07:39</li>
	 *         <li>功能说明：获取交易序号，序号自增</li>
	 *         </p>
	 * @return
	 */
	public static long getSerial() {

		// 取现序号
		Long runSeq = BizUtil.getTrxRunEnvs().getRuntime_seq();

		// 若为空序号初始化为1L
		if (CommUtil.isNull(runSeq))
			runSeq = 1L;

		// 序号自增
		BizUtil.getTrxRunEnvs().setRuntime_seq(runSeq + 1);

		return runSeq;
	}

	/**
	 * @Author zyq
	 *         <p>
	 *         <li>2017年01月22日-下午1:07:39</li>
	 *         <li>功能说明：获取会计事件序号，序号自增</li>
	 *         </p>
	 * @return
	 */
	public static long getAccountingEventSerial() {

		// 取现序号
		Long runSeq = BizUtil.getTrxRunEnvs().getAccounting_event_seq();

		// 若为空序号初始化为1L
		if (CommUtil.isNull(runSeq))
			runSeq = 1L;

		// 序号自增
		BizUtil.getTrxRunEnvs().setAccounting_event_seq(runSeq + 1);

		return runSeq;
	}

	/**
	 * @Author chensy
	 *         <p>
	 *         <li>2016年12月24日-下午2:12:36</li>
	 *         <li>功能说明：</li>
	 *         </p>
	 * @param trxnInfo
	 *            交易信息
	 */
	public static void registerTrxn(ApTransactionInfo trxnInfo) {

		RunEnvs runEnv = BizUtil.getTrxRunEnvs();

		// if (runEnv.getTrxn_type() == E_TRXNTYPE.QUERY){
		// throw ApBaseErr.ApBase.E0119(runEnv.getTrxn_code());
		// }

		bizlog.method("trxnInfo1:[%s]", trxnInfo);

		runEnv.getFinance_info().setCash_trxn_ind(CommUtil.isNull(trxnInfo.getCash_trxn_ind()) ? E_CASHTRXN.OTHER : trxnInfo.getCash_trxn_ind());
		runEnv.getFinance_info().setCcy_code(trxnInfo.getTrxn_ccy());
		runEnv.getFinance_info().setTrxn_amt(trxnInfo.getTrxn_amt());
		runEnv.getFinance_info().setTrxn_acct_no(trxnInfo.getTrxn_acct_no());
		runEnv.getFinance_info().setTrxn_acct_name(trxnInfo.getTrxn_acct_name());
		runEnv.getFinance_info().setDebit_credit(trxnInfo.getDebit_credit());
		runEnv.getFinance_info().setCounterparty_acct_no(trxnInfo.getCounterparty_acct_no());
		runEnv.getFinance_info().setCounterparty_acct_na(trxnInfo.getCounterparty_acct_na());
		runEnv.getFinance_info().setTrxn_remark(trxnInfo.getTrxn_remark());
		runEnv.getFinance_info().setCustomer_remark(trxnInfo.getCustomer_remark());

		bizlog.method("trxnInfo2:[%s]", trxnInfo);

		// 如果会计事件未登记，则将现转标志强制为其他
		/*
		 * if (runEnv.getAccounting_event_seq() == 1) {
		 * runEnv.getFinance_info().setCash_trxn_ind(E_CASHTRXN.OTHER); } else {
		 * if (CommUtil.isNull(runEnv.getFinance_info().getCash_trxn_ind())) {
		 * runEnv.getFinance_info().setCash_trxn_ind(E_CASHTRXN.TRXN); } }
		 */

		bizlog.method("Finance_info:[%s]", runEnv.getFinance_info());
	}

	public static boolean isReversalTrxn(String flowTranId) {
		return ApBaseSystemParm.exists("REVERSAL_FLOWTRAN", flowTranId);
	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2019年9月25日-下午2:12:36</li>
	 *         <li>功能说明：根据调用流水带锁读取交易流水信息</li>
	 *         </p>
	 * @param initiatorSeq
	 *            调用流水
	 * @param errFlag
	 *            报错标志：true-报错 false-不报错
	 */
	public static MssTransaction getTrxn(String initiatorSeq, boolean errFlag) {

		MssTransaction trxnInfo = MsTrxn.selectFromInitiatorSeqWithLock(initiatorSeq, false);

		if (trxnInfo == null && errFlag) {
			throw APPUB.E0005(OdbFactory.getTable(MssTransaction.class).getLongname(), SysDict.A.initiator_seq.getLongName(), initiatorSeq);
		}

		return trxnInfo;
	}
}
