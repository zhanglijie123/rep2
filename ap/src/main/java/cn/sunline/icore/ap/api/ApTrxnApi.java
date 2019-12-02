package cn.sunline.icore.ap.api;

import cn.sunline.clwj.msap.biz.tables.MsOnlTable.MssTransaction;
import cn.sunline.icore.ap.parm.ApBaseTrxn;
import cn.sunline.icore.ap.type.ComApBook.ApTransactionInfo;
import cn.sunline.icore.ap.type.ComApSystem.ApTrxnInfo;

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
public class ApTrxnApi {

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
		return ApBaseTrxn.getInfo(trxnCode);
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

		return ApBaseTrxn.getSerial();
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

		return ApBaseTrxn.getAccountingEventSerial();
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

		ApBaseTrxn.registerTrxn(trxnInfo);
	}

	public static boolean isReversalTrxn(String flowTranId) {
		return ApBaseTrxn.isReversalTrxn(flowTranId);
	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2016年12月24日-下午2:12:36</li>
	 *         <li>功能说明：获取交易流水信息</li>
	 *         </p>
	 * @param callSeq
	 *            调用流水
	 * @param errFlag
	 *            找不到记录是否报错
	 * @return 交易流水信息
	 */
	public static MssTransaction getTrxn(String callSeq, boolean errFlag) {

		return ApBaseTrxn.getTrxn(callSeq, errFlag);
	}
}
