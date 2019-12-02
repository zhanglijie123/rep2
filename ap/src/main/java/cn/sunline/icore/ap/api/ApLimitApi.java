package cn.sunline.icore.ap.api;

import java.math.BigDecimal;

import cn.sunline.icore.ap.limit.ApBaseLimit;
import cn.sunline.icore.ap.tables.TabApAttribute.app_limit;
import cn.sunline.icore.sys.type.EnumType.E_BUYORSELL;
import cn.sunline.icore.sys.type.EnumType.E_CYCLETYPE;
import cn.sunline.icore.sys.type.EnumType.E_EXCHRATETYPE;
import cn.sunline.icore.sys.type.EnumType.E_FOREXQUOTTYPE;

public class ApLimitApi {

	/**
	 * @Author wuqiang
	 *         <p>
	 *         <li>2017年3月9日-下午4:51:31</li>
	 *         <li>功能说明：限额检查及处理</li>
	 *         <li>汇率种类：默认为现汇</li>
	 *         <li>牌价种类默认：中间价</li>
	 *         </p>
	 * @param trxnEventId
	 * @param trxnCcy
	 * @param trxnAmt
	 */
	public static void process(String trxnEventId, String trxnCcy, BigDecimal trxnAmt) {
		ApBaseLimit.process(trxnEventId, trxnCcy, trxnAmt);
	}

	/**
	 * @Author tsichang
	 *         <p>
	 *         <li>2017年3月9日-下午4:51:31</li>
	 *         <li>功能说明：限额检查及处理</li>
	 *         </p>
	 * @param trxnEventId
	 * @param trxnCcy
	 * @param trxnAmt
	 * @param exchRateType
	 *            汇率种类
	 * @param forexQuotType
	 *            牌价种类
	 */
	public static void process(String trxnEventId, String trxnCcy, BigDecimal trxnAmt, E_EXCHRATETYPE exchRateType, E_FOREXQUOTTYPE forexQuotType, E_BUYORSELL buySellFlag) {
		ApBaseLimit.process(trxnEventId, trxnCcy, trxnAmt, exchRateType, forexQuotType, buySellFlag);
	}
	
	public static boolean isValid(String trxnDate, app_limit limit) {
		return ApBaseLimit.isValid(trxnDate, limit);
	}

	public static BigDecimal getCustomLimit(app_limit limit, String ownerId) {
		return ApBaseLimit.getCustomLimit(limit, ownerId);
	}

	public static String getResetDate(E_CYCLETYPE cycle, String trxnDate) {
		return ApBaseLimit.getResetDate(cycle, trxnDate);
	}
	
	public static String getResetDate(E_CYCLETYPE cycle, String trxnDate, String defDate) {
		return ApBaseLimit.getResetDate(cycle, trxnDate, defDate);
	}
	
	/**
	 * @Author Huangjj
	 *         <p>
	 *         <li>2018年4月3日-下午4:15:47</li>
	 *         <li>功能说明：限额重置</li>
	 *         </p>
	 * @param limitOwnerId
	 * @param limitStatisNo
	 */
	public static void resetLimitStatis(String limitOwnerId,String limitStatisNo) {

		ApBaseLimit.resetLimitStatis(limitOwnerId, limitStatisNo);	
	}

}
