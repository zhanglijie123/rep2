package cn.sunline.icore.ap.plugin.impl;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.edsp.custom.dayend.plugin.type.CustomDayEndBatchType.DayEndFlowInfo;
import cn.sunline.edsp.custom.dayend.plugin.type.CustomDayEndBatchType.MultiCorpnoDayEndStatusOut;
import cn.sunline.edsp.plugin.custom.model.DBTranDateInfo;
import cn.sunline.edsp.plugin.custom.spi.IBatchTranCustomManager;
import cn.sunline.icore.ap.namedsql.ApDayEndBaseDao;
import cn.sunline.icore.ap.parm.ApBaseSystemParm;
import cn.sunline.icore.ap.tables.TabApBook.Aps_dayend_infoDao;
import cn.sunline.icore.ap.tables.TabApBook.aps_dayend_info;
import cn.sunline.icore.ap.tables.TabApSystem.App_dateDao;
import cn.sunline.icore.ap.tables.TabApSystem.app_date;
import cn.sunline.icore.ap.type.ComApDayEnd.ApBatchTranDef;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.icore.sys.type.EnumType.E_DAYENDSTATUS;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CoreUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.biz.out.type.KBaseEnumType.E_PILJYZHT;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.Tsp_flow_step_controllerDao;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.tsp_flow_step_controller;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.engine.data.DataArea;

/**
 * <p>
 * Title:BsapBatchTranCustomManagerImpl
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author liucong
 * @date 2017.10.17
 */
public class BsapBatchTranCustomManagerImpl implements IBatchTranCustomManager {

	public static final String ZERO_PERCENT = "0%";
	public static final String ZERO_RATIO = "0 : ";

	private static final BizLog bizlog = BizLogUtil
			.getBizLog(BsapBatchTranCustomManagerImpl.class);

	/**
	 * 根据系统编号得到流程标识
	 */
	@Override
	public List<DayEndFlowInfo> getFlowIdsForSystemId(String systcd) {

		bizlog.debug("========getFlowIdsForSystemId,[%s]==========", systcd);

		if (CoreUtil.getSystemId().equals(systcd)) {
			// to get ksys_pllcdy list
			List<ApBatchTranDef> bacthTranDefList = ApDayEndBaseDao
					.selBatchTranDef(systcd, false);

			// 遍历查到的集合查询公共参数表得到执行序号
			List<DayEndFlowInfo> dayEndFlowInfoList = new ArrayList<DayEndFlowInfo>();

			String batchTranFlowId;
			String chineseName;

			for (ApBatchTranDef bacthTranDef : bacthTranDefList) {

				// 日终流程定义对象
				DayEndFlowInfo dayEndFlowInfo = SysUtil
						.getInstance(DayEndFlowInfo.class);

				batchTranFlowId = bacthTranDef.getTran_flow_id(); // 批量交易流程ID
				chineseName = bacthTranDef.getChinese_name(); // Chinese name

				dayEndFlowInfo.setFlowid(batchTranFlowId); // 批量交易流程ID
				dayEndFlowInfo.setChinna(chineseName); // 中文名字
//				dayEndFlowInfo.setXitongbs(systcd);
				dayEndFlowInfoList.add(dayEndFlowInfo);
			}
			return dayEndFlowInfoList;
		}
		return null;
	}

	/**
	 * 根据流程标识和系统标识获取日期
	 */
	@Override
	public List<String> getDayEndDatesByFlowID(String systcd, String flowId) {

		List<String> dateList = new ArrayList<String>();

		// get organization list
		String busi_org_id = MsOrg.getDefaultOrgId();

		app_date dateInfo;
		dateInfo = App_dateDao.selectOne_odb1(busi_org_id,
				false);
		if (dateInfo == null) {
			throw ApBaseErr.ApBase.E0067(busi_org_id);
		}
		aps_dayend_info dayEndInfo = Aps_dayend_infoDao.selectOne_odb1(
				busi_org_id, flowId, dateInfo.getTrxn_date(),
				false);
		if (dayEndInfo == null) {

			// get last date info
			aps_dayend_info lastDayEndInfo = Aps_dayend_infoDao.selectOne_odb1(
					busi_org_id, flowId,
					dateInfo.getLast_date(), false);
			// 如果上日信息都没有就初始化一条数据
			if (lastDayEndInfo == null) {
				dayEndInfo = BizUtil.getInstance(aps_dayend_info.class);
				dayEndInfo.setDayend_org_id(busi_org_id);
				dayEndInfo.setDay_end_status(E_DAYENDSTATUS.ONPROCESS);
				List<tsp_flow_step_controller> batchFlowList = Tsp_flow_step_controllerDao
						.selectAll_GetSteps(flowId, false);
				dayEndInfo.setTrxn_date(dateInfo.getTrxn_date());
				dayEndInfo.setDayend_schedule(ZERO_PERCENT);
				dayEndInfo.setDayend_proportion(ZERO_RATIO
						+ getBatchFlowTranNum(batchFlowList));
				dayEndInfo.setDayend_flow_define(flowId);
				dayEndInfo.setSystem_id_no(systcd);
				Aps_dayend_infoDao.insert(dayEndInfo);
				dateList.add(dateInfo.getTrxn_date());

			} else {
				// 上日信息不为空判断状态
				if (lastDayEndInfo.getDay_end_status() != E_DAYENDSTATUS.SUCCESS) {
					dateList.add(dateInfo.getLast_date());
				}
				else {
					dayEndInfo = BizUtil.getInstance(aps_dayend_info.class);
					dayEndInfo.setDayend_org_id(busi_org_id);
					dayEndInfo.setDay_end_status(E_DAYENDSTATUS.ONPROCESS);
					List<tsp_flow_step_controller> batchFlowList = Tsp_flow_step_controllerDao
							.selectAll_GetSteps(flowId, false);
					dayEndInfo.setTrxn_date(dateInfo.getTrxn_date());
					dayEndInfo.setDayend_schedule(ZERO_PERCENT);
					dayEndInfo.setDayend_proportion(ZERO_RATIO
							+ getBatchFlowTranNum(batchFlowList));
					dayEndInfo.setDayend_flow_define(flowId);
					dayEndInfo.setSystem_id_no(systcd);
					Aps_dayend_infoDao.insert(dayEndInfo);
					dateList.add(dateInfo.getTrxn_date());
				}
			}
		} else
			dateList.add(dayEndInfo.getTrxn_date());

		// remove the repetition indateList
		List<String> dateListRemoveRepetion = new ArrayList<String>();

		for (String date : dateList) {

			if (!dateListRemoveRepetion.contains(date)) {
				dateListRemoveRepetion.add(date);
			}
		}

		return dateListRemoveRepetion;

	}

	/**
	 * 根据法人代码、流程标识给数据区设置日期时间
	 */
	@Override
	public DBTranDateInfo getDBTranDateInfo(String flowid ,String orgId, String systcd,
			DBTranDateInfo dateInfo) {
		// 根据法人代码和系统编号查询查询日终批量日期管理表得到交易日期 查询交易系统日期参数表
		app_date queryDateInfo = App_dateDao.selectOne_odb1(orgId, false);

		if (queryDateInfo == null) {
			throw APPUB.E0005(
					OdbFactory.getTable(app_date.class).getLongname(),
					SysDict.A.org_id.getLongName(), orgId);
		}

		dateInfo.setLastTranDate(queryDateInfo.getLast_date()); // 上次交易日期
		dateInfo.setTranDate(queryDateInfo.getTrxn_date()); // 系统日期
		dateInfo.setNextTranDate(queryDateInfo.getNext_date()); // 下次交易日期
		return dateInfo;
	}

	/**
	 * 给数据区设置交易机构、渠道号和交易柜员
	 */
	@Override
	public DataArea getDayEndDataArea(DataArea dataArea) {

		String orgId = (String) dataArea.getCommReq().get("farendma");

		String tranBranch = ApBaseSystemParm.getValue(ApConst.DAYEND_DATA,
				ApConst.DAYEND_TRAN_BRANCH);
		String channelID = ApBaseSystemParm.getValue(ApConst.DAYEND_DATA,
				ApConst.DAYEND_CHANNEL_ID);
		String tranTeller = ApBaseSystemParm.getValue(ApConst.DAYEND_DATA,
				ApConst.DAYEND_TRAN_TELLER);
		dataArea.getCommReq().put("org_id", orgId); // 交易法人
		dataArea.getCommReq().put("trxn_branch", tranBranch); // 交易机构
		dataArea.getCommReq().put("channel_id", channelID); // 渠道号
		dataArea.getCommReq().put("trxn_teller", tranTeller); // 交易柜员
		dataArea.getCommReq().put("initiator_system", "999");
		dataArea.getCommReq().put("sponsor_system", "999");
		return dataArea;
	}

	
	
	/**
	 * 查询日终明细信息
	 */
	@Override
	public List<MultiCorpnoDayEndStatusOut> getMultiCorpnoDayEndStu(
			String systcd, String flowId, String tranDate) {

		List<MultiCorpnoDayEndStatusOut> dayEndInfoList = new ArrayList<MultiCorpnoDayEndStatusOut>();

		List<aps_dayend_info> dayEndList = Aps_dayend_infoDao.selectAll_odb3(flowId, tranDate, false);
		
		MultiCorpnoDayEndStatusOut dayEndInfo = null;
		

		for (aps_dayend_info tabDayEnd : dayEndList) {

			dayEndInfo = SysUtil.getInstance(MultiCorpnoDayEndStatusOut.class);

			dayEndInfo.setTrandt(tabDayEnd.getTrxn_date());
			dayEndInfo.setCorpno(tabDayEnd.getDayend_org_id());
			dayEndInfo.setTranst(E_PILJYZHT.valueOf(tabDayEnd
					.getDay_end_status().getValue()));
			dayEndInfo.setProces(tabDayEnd.getDayend_schedule());
			dayEndInfo.setFlowid(tabDayEnd.getDayend_flow_define());
			dayEndInfo.setSucpro(tabDayEnd.getDayend_proportion());
			dayEndInfo.setProdid(tabDayEnd.getSystem_id_no());
			dayEndInfo.setCorpno(tabDayEnd.getDayend_org_id());
			dayEndInfoList.add(dayEndInfo);
		}

		return dayEndInfoList;
	}

	/**
	 * get batch flow number
	 * 
	 * @param batchTranGroup
	 * @return
	 */
	private int getBatchFlowTranNum(List<tsp_flow_step_controller> batchFlowList) {

		int i = 0;

		for (tsp_flow_step_controller batchFlow : batchFlowList) {
			i += ApDayEndBaseDao.selBatchNum(batchFlow.getTran_flow_id(), false);
		}

		return i;
	}

	/**
	 * 根据模式选择执行的法人列表
	 */
	@Override
	public List<String> getAllExecCorpnos(String execMode, String systcd,
			String flowId, String tranDate, Options<String> corpnos) {

		return corpnos;
	}

	/**
	 * 批量提交前的校验
	 * <p>
	 * Title:checkCorpnos
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @author XX
	 * @date 2017年9月22日
	 * @param systcd
	 * @param flowId
	 * @param tranDate
	 * @param corpnos
	 * @return
	 */

	public List<String> check(String systcd, String flowId, String tranDate,
			Options<String> corpnos) {
		return corpnos;
	}

	@Override
	public List<String> getAllExeTaskCorpnos(String systcd, String flowId,
			String tranDate, Options<String> arg3) {
		List<String> list = new ArrayList<String>();

		for (String orgId : arg3) {
			aps_dayend_info tabDayendInfo = Aps_dayend_infoDao.selectOne_odb1(
					orgId, flowId, tranDate, false);
			if (E_DAYENDSTATUS.SUCCESS != tabDayendInfo.getDay_end_status()) {

				list.add(tabDayendInfo.getDayend_org_id());
			}

		}
		return list;
	}


}