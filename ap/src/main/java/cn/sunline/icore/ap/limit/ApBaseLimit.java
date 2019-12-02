package cn.sunline.icore.ap.limit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.reversal.ApBaseReversal;
import cn.sunline.icore.ap.rule.ApBaseBuffer;
import cn.sunline.icore.ap.rule.ApBaseRule;
import cn.sunline.icore.ap.tables.TabApAttribute.Apb_custom_limitDao;
import cn.sunline.icore.ap.tables.TabApAttribute.Apb_limit_statisDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_limitDao;
import cn.sunline.icore.ap.tables.TabApAttribute.App_limit_driveDao;
import cn.sunline.icore.ap.tables.TabApAttribute.apb_custom_limit;
import cn.sunline.icore.ap.tables.TabApAttribute.apb_limit_statis;
import cn.sunline.icore.ap.tables.TabApAttribute.app_limit;
import cn.sunline.icore.ap.tables.TabApAttribute.app_limit_drive;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitStatisInfo;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.type.EnumType.E_ADDSUBTRACT;
import cn.sunline.icore.sys.type.EnumType.E_BUYORSELL;
import cn.sunline.icore.sys.type.EnumType.E_CYCLETYPE;
import cn.sunline.icore.sys.type.EnumType.E_EXCHRATETYPE;
import cn.sunline.icore.sys.type.EnumType.E_FOREXQUOTTYPE;
import cn.sunline.icore.sys.type.EnumType.E_LIMITCTRLCLASS;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.DateTimeUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApBaseLimit {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseLimit.class);

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
		process(trxnEventId, trxnCcy, trxnAmt, E_EXCHRATETYPE.EXCHANGE, E_FOREXQUOTTYPE.MIDDLE, E_BUYORSELL.BUY);
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
		bizlog.debug("transaction event id = %s, transaction currency = %s, transaction amount = %s", trxnEventId, trxnCcy, trxnAmt);
		List<app_limit_drive> limitDrives = App_limit_driveDao.selectAll_odb1(trxnEventId, false); // 获得交易事件对应的限额驱动
		if (limitDrives.size() == 0) {
			return;
		}

		String trxnDate = BizUtil.getTrxRunEnvs().getTrxn_date();
		List<ApLimitStatisInfo> limitStatisInfos = new ArrayList<ApLimitStatisInfo>();

		for (app_limit_drive drive : limitDrives) {
			if (!ApBaseRule.mapping(drive.getLimit_drive_cond())) { // 不符合驱动条件
				continue;
			}

			List<app_limit> limitList = App_limitDao.selectAll_odb1(drive.getLimit_no(), false); // 获得被驱动的限额
			for (app_limit limit : limitList) {
				if (!isValid(trxnDate, limit)) { // 理论上一个限额编号只有一条限额配置是有效的
					continue;
				}

				LimitController controller = LimitController.getInstance(limit.getLimit_ctrl_class());
				
				BigDecimal exchangedTrxnAmt = trxnAmt;
				if (controller.isTimesCtr() == false) {
					exchangedTrxnAmt = getExchangedAmt(limit.getLimit_ccy(), trxnCcy, trxnAmt, exchRateType, forexQuotType, buySellFlag);
				}
				
				String ownerId = getOwnerId(limit);
				BigDecimal changeAmt = controller.doCheck(limit, ownerId, drive.getLimit_sum_way(), exchangedTrxnAmt);
				
				// 更新金额不为零表示需要更新,添加到列表中
				if (CommUtil.compare(changeAmt, BigDecimal.ZERO) != 0) {
					ApLimitStatisInfo statisInfo = BizUtil.getInstance(ApLimitStatisInfo.class);

					statisInfo.setLimit_reset_cycle(limit.getLimit_reset_cycle());
					statisInfo.setEffect_date(limit.getEffect_date());
					statisInfo.setLimit_statis_no(limit.getLimit_statis_no());
					statisInfo.setLimit_value(changeAmt);
					statisInfo.setLimit_owner_id(ownerId);
					limitStatisInfos.add(statisInfo);

					ApBaseReversal.register(ApConst.LIMIT_REVERSAL_EVENT, statisInfo);
				}
			}
		}

		// 更新限额
		if (limitStatisInfos.size() > 0) {
			updateStatis(limitStatisInfos);
		}
	}
	
	public static boolean isValid(String trxnDate, app_limit limit) {
		if (CommUtil.compare(limit.getEffect_date(), trxnDate) > 0) {
			return false;
		}
		
		if (CommUtil.compare(trxnDate, limit.getExpiry_date()) > 0) {
			return false;
		}

		return true;
	}
	
	private static String getOwnerId(app_limit limit) {
		if (CommUtil.isNotNull(limit.getOwner_mart()) && CommUtil.isNotNull(limit.getOwner_field())) { // first
			return ApBaseBuffer.getFieldValue(limit.getOwner_mart(), limit.getOwner_field());
		}
		else if (CommUtil.isNotNull(limit.getLimit_statis_no())) { // second
			return limit.getLimit_statis_no();
		}
		else { // final
			return limit.getLimit_no();
		}

	}

	public static BigDecimal getCustomLimit(app_limit limit, String ownerId) {
		BigDecimal limitValue = limit.getLimit_value();

		if (limit.getLimit_custom_allow() == E_YESORNO.YES) {
			apb_custom_limit custLimit = Apb_custom_limitDao.selectOne_odb1(ownerId, limit.getLimit_no(), false);

			if (custLimit != null)
				limitValue = custLimit.getCustom_limit_value();
		}

		return limitValue;
	}

	private static BigDecimal getExchangedAmt(String limitCcy, String trxnCcy, BigDecimal trxnAmount, E_EXCHRATETYPE exchRateType, E_FOREXQUOTTYPE forexQuotType,
			E_BUYORSELL buySellFlag) {
		bizlog.debug("Limit currency[%s], transaction currency[%s], transaction amount[%s].", limitCcy, trxnCcy, trxnAmount);
		bizlog.debug("ExchRateType=%s, forexQuotType=%s, buySellFlag=%s.", exchRateType, forexQuotType, buySellFlag);

		BigDecimal limitAmount;
		if (CommUtil.equals(trxnCcy, limitCcy) || trxnAmount.signum() == 0) {
			limitAmount = trxnAmount;
		}
		else {
//			SrvIoCmExchBase exchRate = BizUtil.getInstance(SrvIoCmExchBase.class);
//			IoCmExchAmtCalcIn buySellIn = BizUtil.getInstance(IoCmExchAmtCalcIn.class);
//
//			buySellIn.setExch_rate_type(exchRateType);// 汇率种类
//			buySellIn.setForex_quot_type(forexQuotType);// 牌价种类
//
//			if (buySellFlag == E_BUYORSELL.BUY) {
//				buySellIn.setBuy_ccy_code(trxnCcy);// 买入币种
//				buySellIn.setBuy_amt(trxnAmount);// 买入金额
//				buySellIn.setSell_ccy_code(limitCcy);// 卖出币种
//
//				limitAmount = exchRate.calcForexAmount(buySellIn).getSell_amt();
//			}
//			else {
//				buySellIn.setBuy_ccy_code(limitCcy);// 买入币种
//				buySellIn.setSell_amt(trxnAmount);// 金额
//				buySellIn.setSell_ccy_code(trxnCcy);// 卖出币种
//
//				limitAmount = exchRate.calcForexAmount(buySellIn).getBuy_amt();
//			}
		}

//		bizlog.debug("The exchanged limit amount is [%s]", limitAmount);
//		return limitAmount;
		limitAmount = trxnAmount;
		return limitAmount;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月12日-下午4:32:47</li>
	 *         <li>功能说明：统计限额更新处理</li>
	 *         </p>
	 * @param ownerId
	 *            限额属主id
	 * @param statisNo
	 *            限额统计编号
	 * @param amount
	 *            限额值
	 */
	private static void updateStatis(List<ApLimitStatisInfo> limitStatisInfos) {
		List<ApLimitStatisInfo> statisList = limitStatisInfos;
		String trxnDate = BizUtil.getTrxRunEnvs().getTrxn_date();
		bizlog.debug("the limit number wait to update[%d]", statisList.size());

		for (ApLimitStatisInfo statis : statisList) {
			apb_limit_statis curtStatis = Apb_limit_statisDao.selectOne_odb1(statis.getLimit_owner_id(), statis.getLimit_statis_no(), false);
			String resetDate = getResetDate(statis.getLimit_reset_cycle(), trxnDate, statis.getEffect_date());
			
			if (curtStatis == null) {
				apb_limit_statis updStatis = BizUtil.getInstance(apb_limit_statis.class);

				updStatis.setLimit_owner_id(statis.getLimit_owner_id());
				updStatis.setLimit_statis_no(statis.getLimit_statis_no());
				updStatis.setLimit_update_date(trxnDate);
				updStatis.setLimit_reset_date(resetDate);
				updStatis.setUsed_limit(statis.getLimit_value());
				
				Apb_limit_statisDao.insert(updStatis);
			}
			else {
				if (CommUtil.compare(curtStatis.getLimit_reset_date(), resetDate) == 0) { // 日期一致 做累加，不一致直接覆盖
					curtStatis.setUsed_limit(statis.getLimit_value().add(curtStatis.getUsed_limit()));
				}
				else {
					curtStatis.setUsed_limit(statis.getLimit_value());
				}
				
				curtStatis.setLimit_update_date(trxnDate);
				curtStatis.setLimit_reset_date(resetDate);
				
				Apb_limit_statisDao.updateOne_odb1(curtStatis);
			}
		}
	}

	public static String getResetDate(E_CYCLETYPE cycle, String trxnDate) {
		return DateTimeUtil.firstDay(trxnDate, cycle.getValue());
	}
	
	public static String getResetDate(E_CYCLETYPE cycle, String trxnDate, String defDate) {
		if (CommUtil.isNull(cycle)) {
			return defDate;
		}
		
		return DateTimeUtil.firstDay(trxnDate, cycle.getValue());
	}
	
	private static enum LimitController {
		SING_AMT(E_LIMITCTRLCLASS.SINGLE_AMOUNT) {
			public BigDecimal doCheck(app_limit limit, String ownerId, E_ADDSUBTRACT model, BigDecimal checkAmt) {
				BigDecimal maxLimit = getCustomLimit(limit, ownerId);

				if (CommUtil.compare(checkAmt, maxLimit) > 0) {
					throw ApBaseErr.ApBase.E0010(limit.getLimit_desc(), ownerId, maxLimit, checkAmt);
				}
				
				return BigDecimal.ZERO;
			}
		},
		CAL_TIMES(E_LIMITCTRLCLASS.CUMULATIVE_NUMBER) {
			public BigDecimal doCheck(app_limit limit, String ownerId, E_ADDSUBTRACT model, BigDecimal checkAmt) {
				BigDecimal maxLimit = getCustomLimit(limit, ownerId);
				BigDecimal beforeLimit = BigDecimal.ZERO;
				BigDecimal changeLimit = getChangeValue(model, checkAmt);

				String trxnDate = BizUtil.getTrxRunEnvs().getTrxn_date();
				apb_limit_statis limitStatis = Apb_limit_statisDao.selectOneWithLock_odb1(ownerId, limit.getLimit_statis_no(), false);

				if (limitStatis != null) {
					// 在同一个统计周期
					if (CommUtil.compare(limitStatis.getLimit_reset_date(), getResetDate(limit.getLimit_reset_cycle(), trxnDate)) == 0) {
						beforeLimit = limitStatis.getUsed_limit();
					}
				}

				if (model == E_ADDSUBTRACT.ADD) {
					BigDecimal afterLimit = beforeLimit.add(changeLimit);

					if (CommUtil.compare(afterLimit, maxLimit) > 0) {
						throw ApBaseErr.ApBase.E0011(limit.getLimit_desc(), ownerId, maxLimit);
					}
				}
				
				return changeLimit;
			}

			public boolean isTimesCtr() {
				return true;
			}
		},
		CAL_AMT(E_LIMITCTRLCLASS.CUMULATIVE_AMOUNT) {
			public BigDecimal doCheck(app_limit limit, String ownerId, E_ADDSUBTRACT model, BigDecimal checkAmt) {
				BigDecimal maxLimit = getCustomLimit(limit, ownerId);
				BigDecimal beforeLimit = BigDecimal.ZERO;
				BigDecimal changeLimit = getChangeValue(model, checkAmt);

				String trxnDate = BizUtil.getTrxRunEnvs().getTrxn_date();
				apb_limit_statis limitStatis = Apb_limit_statisDao.selectOneWithLock_odb1(ownerId, limit.getLimit_statis_no(), false);

				if (limitStatis != null) {
					// 在同一个统计周期
					if (CommUtil.compare(limitStatis.getLimit_reset_date(), getResetDate(limit.getLimit_reset_cycle(), trxnDate)) == 0) {
						beforeLimit = limitStatis.getUsed_limit();
					}
				}

				if (model == E_ADDSUBTRACT.ADD) {
					BigDecimal afterLimit = beforeLimit.add(changeLimit);

					if (CommUtil.compare(afterLimit, maxLimit) > 0) {
						throw ApBaseErr.ApBase.E0012(limit.getLimit_desc(), ownerId, changeLimit, maxLimit);
					}
				}
				
				return changeLimit;
			}
		},
		AMT_COUNTER(E_LIMITCTRLCLASS.AMOUNT_COUNTER),
		TIMES_COUNTER(E_LIMITCTRLCLASS.TIMES_COUNTER) {
			public boolean isTimesCtr() {
				return true;
			}
		};

		private E_LIMITCTRLCLASS type;

		private LimitController(E_LIMITCTRLCLASS type) {
			this.type = type;
		}

		public static LimitController getInstance(E_LIMITCTRLCLASS type) {
			for (LimitController current : LimitController.values()) {
				if (current.type == type) {
					return current;
				}
			}
			throw ApPubErr.APPUB.E0017(E_LIMITCTRLCLASS.class.getSimpleName(), type.getId());
		}

		/**
		 * @Author tsichang
		 *         <p>
		 *         <li>功能说明：限额校验, 返回本次限额的更新值</li>
		 *         </p>
		 * @return 需要累积的发生数额，增加则返回正数，减少则返回负数
		 */
		public BigDecimal doCheck(app_limit limit, String ownerId, E_ADDSUBTRACT model, BigDecimal checkAmt) {
			return getChangeValue(model, checkAmt);
		}

		/**
		 * @Author tsichang
		 *         <p>
		 *         <li>function：是否是次数控制</li>
		 *         </p>
		 * @return
		 */
		public boolean isTimesCtr() {
			return false;
		}

		/**
		 * @Author tsichang
		 *         <p>
		 *         <li>function：获取变更值</li>
		 *         </p>
		 * @return 
		 */
		protected BigDecimal getChangeValue(E_ADDSUBTRACT model, BigDecimal checkAmt) {
			if (isTimesCtr()) {
				return model == E_ADDSUBTRACT.ADD ? BigDecimal.ONE : BigDecimal.ONE.negate();
			}
			else {
				return model == E_ADDSUBTRACT.ADD ? checkAmt : checkAmt.negate();
			}
		}
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

		String trxnDate = BizUtil.getTrxRunEnvs().getTrxn_date();
		List<app_limit> limitInfo = App_limitDao.selectAll_odb2(limitStatisNo, false);
		if(limitInfo.isEmpty()){
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_limit.class).getLongname(), SysDict.A.limit_statis_no.getLongName(), limitStatisNo);
		}
		
		//不是次数限制不能够使用此方法
		if(limitInfo.get(0).getLimit_ctrl_class()!=E_LIMITCTRLCLASS.CUMULATIVE_NUMBER || limitInfo.get(0).getLimit_ctrl_class()!=E_LIMITCTRLCLASS.TIMES_COUNTER ){
			throw ApBaseErr.ApBase.E0107();
		}
		
		apb_limit_statis limitStatisInfo = Apb_limit_statisDao.selectOneWithLock_odb1(limitOwnerId , limitStatisNo, false);
		if(CommUtil.isNull(limitStatisInfo)){
			throw ApPubErr.APPUB.E0024(OdbFactory.getTable(apb_limit_statis.class).getLongname(), SysDict.A.limit_owner_id.getLongName(), limitOwnerId, SysDict.A.limit_statis_no.getLongName(), limitStatisNo);
		}
		
		//统计数据重置
		limitStatisInfo.setUsed_limit(BigDecimal.ZERO);
		limitStatisInfo.setLimit_update_date(trxnDate);
		
		Apb_limit_statisDao.updateOne_odb1(limitStatisInfo);
		
	}

}
