package cn.sunline.icore.ap.packet;

import cn.sunline.icore.ap.namedsql.ApSystemDao;
import cn.sunline.icore.ap.type.ComApSystem.ApPacketBriefInfo;
import cn.sunline.icore.ap.type.ComApSystem.ApPacketInfo;
import cn.sunline.icore.ap.type.ComApSystem.ApPacketInfoInput;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
public class ApPacket {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApPacket.class);
	
	/**
	 * @Author jss
	 *         <p>
	 *         <li>2018年11月5日-下午2:27:12</li>
	 *         <li>功能说明：报文简要信息查询</li>
	 *         </p>
	 * @param packetInfo
	 * @return
	 */
	
	public static Options<ApPacketBriefInfo> queryPacketList(ApPacketInfoInput packetInfo) {
		bizlog.method(" ApPacket.queryPacketList begin >>>>>>>>>>>>>>>>");
		// 运行环境
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		
		// 非空要素检查
		BizUtil.fieldNotNull(packetInfo.getTrxn_date(), SysDict.A.trxn_date.getId(), SysDict.A.trxn_date.getLongName());

		//开始时间
		if(CommUtil.isNotNull(packetInfo.getBegin_time())) {
			
			packetInfo.setBegin_time(packetInfo.getTrxn_date() + " " + packetInfo.getBegin_time());
		}
		
		//结束时间
		if(CommUtil.isNotNull(packetInfo.getEnd_time())) {
			
			packetInfo.setEnd_time(packetInfo.getTrxn_date() + " " + packetInfo.getEnd_time());
		
		}
		
		// 起始日期不能大于结束日期
		if (CommUtil.compare(packetInfo.getBegin_time(), packetInfo.getEnd_time()) > 0) {
			
			throw ApErr.AP.E0127(packetInfo.getBegin_time(), packetInfo.getEnd_time());
		}
		
		
		// 得到带翻页的总记录查询
		Page<ApPacketBriefInfo> page = ApSystemDao.selPacketList(packetInfo.getTrxn_date(), packetInfo.getTrxn_code(), packetInfo.getChannel_id(), packetInfo.getTrxn_teller(), 
				packetInfo.getBegin_time(), packetInfo.getEnd_time(), packetInfo.getTrxn_seq(), packetInfo.getInitiator_seq(), packetInfo.getInitiator_system(), packetInfo.getReturn_code(),
				packetInfo.getTrxn_type(), packetInfo.getTrxn_desc(), packetInfo.getCounterparty_acct_no(), packetInfo.getCounterparty_acct_na(),
				packetInfo.getTrxn_acct_no(), packetInfo.getTrxn_acct_name(),
				runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(), false);
		
		runEnvs.setTotal_count(page.getRecordCount());
		Options<ApPacketBriefInfo> packetList = new DefaultOptions<>();
		packetList.setValues(page.getRecords());
		bizlog.method(" ApPacket.queryPacketList end <<<<<<<<<<<<<<<<");
		return packetList;
	}
	
	/**
	 * @Author jss
	 *         <p>
	 *         <li>2018年11月5日-下午2:27:12</li>
	 *         <li>功能说明：报文简要信息查询</li>
	 *         </p>
	 * @param trxn_seq
	 * @return
	 */
	public static ApPacketInfo queryPacketInfo(String trxn_seq) {
		bizlog.method(" ApPacket.ApPacketInfo begin >>>>>>>>>>>>>>>>");
		
		// 非空要素检查
		BizUtil.fieldNotNull(trxn_seq, SysDict.A.trxn_seq.getId(), SysDict.A.trxn_seq.getLongName());

		//查询报文明细
		ApPacketInfo packetInfo = ApSystemDao.selPacketDetail(trxn_seq, false);
		
		bizlog.method(" ApPacket.ApPacketInfo end <<<<<<<<<<<<<<<<");
		return packetInfo;
	}

}
