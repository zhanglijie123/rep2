package cn.sunline.icore.ap.reversal;

import cn.sunline.clwj.msap.biz.spi.MsEventControlDefault;
import cn.sunline.clwj.msap.iobus.type.IoMsReverseType.IoMsInterface;
import cn.sunline.edsp.microcore.spi.SPIMeta;
import cn.sunline.icore.ap.tables.TabApAttribute.Apb_limit_statisDao;
import cn.sunline.icore.ap.tables.TabApAttribute.apb_limit_statis;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitStatisInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

@SPIMeta(id="apUpdateLimit")
public class ApBizReversal extends MsEventControlDefault {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBizReversal.class);

	/**
	 * 更新限额冲账
	 * 
	 * @param input
	 */
	@Override
	public void doReversalProcess(IoMsInterface input) {
		bizlog.debug("updateLimitReversal start>>>>>>>>>>>>>>");
		
		// 输入信息初始化
		ApLimitStatisInfo statisInfo = SysUtil.deserialize(input.getInformation_value(), ApLimitStatisInfo.class);
		String ownerId = statisInfo.getLimit_owner_id();
		String statisNo = statisInfo.getLimit_statis_no();

		String trxnDate = BizUtil.getTrxRunEnvs().getTrxn_date();
		apb_limit_statis limitStatis = Apb_limit_statisDao.selectOneWithLock_odb1(ownerId, statisNo, false);

		// 如果已经隔期，则不处理
		if (limitStatis == null || CommUtil.compare(trxnDate, limitStatis.getLimit_update_date()) > 0) {
			return;
		}

		limitStatis.setUsed_limit(limitStatis.getUsed_limit().subtract(statisInfo.getLimit_value()));
		limitStatis.setLimit_update_date(trxnDate);

		Apb_limit_statisDao.updateOne_odb1(limitStatis);
		bizlog.debug("updateLimitReversal end<<<<<<<<<<<<<<<");
	}

}
