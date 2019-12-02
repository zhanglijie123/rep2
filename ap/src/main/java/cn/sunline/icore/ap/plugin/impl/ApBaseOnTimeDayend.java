package cn.sunline.icore.ap.plugin.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.sunline.icore.ap.namedsql.ApDayEndBaseDao;
import cn.sunline.icore.ap.parm.ApBaseSystemParm;
import cn.sunline.icore.ap.tables.TabApBook.Aps_dayend_infoDao;
import cn.sunline.icore.ap.tables.TabApBook.aps_dayend_info;
import cn.sunline.icore.ap.tables.TabApSystem.App_dateDao;
import cn.sunline.icore.ap.tables.TabApSystem.app_date;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.type.EnumType.E_DAYENDSTATUS;
import cn.sunline.icore.sys.type.EnumType.E_DAYENDSWITCH;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CoreUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.biz.out.type.KBaseEnumType.E_PILJYZHT;
import cn.sunline.ltts.biz.out.type.KBaseEnumType.E_PILZXM0S;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.Tsp_flow_step_controllerDao;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.Tsp_taskDao;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.tsp_flow_step_controller;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.tsp_task;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * <p>
 * 文件功能说明：
 * </p>
 * 
 * @Author liucong
 *         <p>
 *         <li>2018年1月4日-下午2:29:41</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年1月4日-liucong：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseOnTimeDayend {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseOnTimeDayend.class);
	private static final String DAYEND_BRANCH_ID = "1234";

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2018年1月4日-下午2:30:35</li>
	 *         <li>功能说明:On time day end</li>
	 *         </p>
	 */
	public static void onTimeDayend(String flowId, String orgId) {

		bizlog.info("ApOnTimeDayendTestStart", orgId);

		// 未配置开关或者开关未开则直接跳过
		if (!ApBaseSystemParm.exists(ApConst.DAYEND_DATA, ApConst.ONTIME_DAYEND_SWITCH) 
				|| !E_DAYENDSWITCH.ON.getValue().equals(ApBaseSystemParm.getValue(ApConst.DAYEND_DATA, ApConst.ONTIME_DAYEND_SWITCH))) {
			bizlog.debug("There is no settings for schedule dayend.");
			return;
		}

		app_date dateInfo = App_dateDao.selectOne_odb1(orgId, false);

		if (dateInfo == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_date.class).getLongname(), SysDict.A.busi_org_id.getLongName(), orgId);
		}

		String trxnDate = dateInfo.getTrxn_date();

		// 查看上一日的是否有未执行成功的
		tsp_task lastDateDayendInfo = ApDayEndBaseDao.queryLastTask(flowId, orgId, SysUtil.getSystemId(), dateInfo.getLast_date(), false);

		if (lastDateDayendInfo == null) {
			aps_dayend_info lastDayend = Aps_dayend_infoDao.selectOne_odb1(orgId, flowId, dateInfo.getLast_date(), false);
			if (lastDayend != null) { // if last day end info is null, it means the first time run EOD.
				throw ApBaseErr.ApBase.E0074();
			}
		}
		else if (E_PILJYZHT.success != lastDateDayendInfo.getTran_state()) {
			// throw exception
			throw ApBaseErr.ApBase.E0075();
		}

		// 查看当日是否有执行过，如果有记录则跳过当天的记录
		tsp_task trxnDateDayendInfo = ApDayEndBaseDao.queryLastTask(flowId, orgId, SysUtil.getSystemId(), trxnDate, false);
		if (trxnDateDayendInfo != null) {
			return;
		}
		
		aps_dayend_info dayEndInfo = Aps_dayend_infoDao.selectOne_odb1(orgId, flowId, trxnDate, false);
		if (dayEndInfo == null) {
			aps_dayend_info tabDayend = BizUtil.getInstance(aps_dayend_info.class);
			tabDayend.setDayend_org_id(orgId);
			tabDayend.setDayend_flow_define(flowId);
			tabDayend.setDay_end_status(E_DAYENDSTATUS.ONPROCESS);
			tabDayend.setTrxn_date(dateInfo.getTrxn_date());
			tabDayend.setDayend_schedule("0%");
			tabDayend.setDayend_proportion("0 : " + getBatchFlowTranNum(Tsp_flow_step_controllerDao.selectAll_GetSteps(flowId, false)));
			tabDayend.setDayend_flow_define(flowId);
			tabDayend.setSystem_id_no(CoreUtil.getSystemId());

			Aps_dayend_infoDao.insert(tabDayend);
		}

		// 满足上述条件则进行
		tsp_task trxnDateInfo = BizUtil.getInstance(tsp_task.class);
		Map<String, Object> inputMap = new HashMap<String, Object>();
		Map<String, Object> commReqMap = new HashMap<String, Object>();

		long time = new Date().getTime();
		String taskId = flowId + "_" + trxnDate + "_" + orgId;
		inputMap.put("farendma", orgId);
		inputMap.put("org_id", orgId);
		inputMap.put("rzglriqi", trxnDate);
		inputMap.put("pljylcbs", flowId);
		inputMap.put("tran_flow_id", flowId);

		commReqMap = new HashMap<String, Object>();
		commReqMap.put("farendma", orgId);
		commReqMap.put("org_id", orgId);
		commReqMap.put("busi_branch_id", DAYEND_BRANCH_ID);
		commReqMap.put("channel_id", BizUtil.getTrxRunEnvs().getChannel_id());
		commReqMap.put("busi_teller_id", "S####");
		commReqMap.put("sponsor_system", CoreUtil.getSubSystemId());
		commReqMap.put("initiator_system", CoreUtil.getSubSystemId());
		commReqMap.put("jiaoyirq", trxnDate);

		// 拼接报文
		String data;
		data = "{\"input\":{" + mapToString(inputMap) + "},\"comm_req\":{" + mapToString(commReqMap) + "}}";
		trxnDateInfo.setTran_flow_id(flowId); // 批量任务批次号
		trxnDateInfo.setTask_num(taskId);
		trxnDateInfo.setTask_exe_num(time + ""); // 批量任务执行批次号
		trxnDateInfo.setTask_commit_date(trxnDate); // 批量任务提交日期
		trxnDateInfo.setTran_date(trxnDate); // 交易日期
		trxnDateInfo.setTransaction_date(trxnDate); // 当前交易日期
		trxnDateInfo.setTran_flow_id(flowId); // 批量交易流程ID
		trxnDateInfo.setTask_exe_mode(E_PILZXM0S.FLOW);
		trxnDateInfo.setTran_state(E_PILJYZHT.onprocess);
		trxnDateInfo.setFlow_step_num(0); // 流程步骤号
		trxnDateInfo.setTran_group_id(""); // 批量交易组ID
		trxnDateInfo.setTran_id(""); // 批量交易ID
		trxnDateInfo.setData_area(data); // 数据区
		trxnDateInfo.setSystem_code(CoreUtil.getSystemId()); // 系统标识号
		trxnDateInfo.setCorporate_code(orgId); // 法人代码
		trxnDateInfo.setSub_system_code(CoreUtil.getSubSystemId());
		/*bizlog.info("This is a Test1" + trxnDateInfo.getPljylcbs(), trxnDateInfo.getPljylcbs());
		if (!orgId.equals(trxnDateInfo.getPljypich())) {
			throw ApBaseErr.ApBase.E0003(trxnDateInfo.getPljypich());
		}*/
		Tsp_taskDao.insert(trxnDateInfo);

	}
	
	private static int getBatchFlowTranNum(List<tsp_flow_step_controller> batchFlowList) {
		int i = 0;
		for (tsp_flow_step_controller batchFlow : batchFlowList) {
			i += ApDayEndBaseDao.selBatchNum(batchFlow.getTran_flow_id(), false);
		}
		return i;
	} 

	/**
	 * @Author liucong map data to String data
	 * @param map
	 * @return
	 */
	private static String mapToString(Map<String, Object> map) {

		StringBuffer strBuff = new StringBuffer();

		for (Entry<String, Object> entry : map.entrySet()) {

			// 当输入报文的类型为null或类型不为String,则不用拼接""号
			if (entry.getValue() == null || !(new String().getClass().equals(entry.getValue().getClass()))) {
				strBuff.append("\"" + entry.getKey() + "\"").append(":").append(entry.getValue()).append(",");
			}
			else {
				strBuff.append("\"" + entry.getKey() + "\"").append(":").append("\"" + entry.getValue() + "\"").append(",");

			}
		}
		// 去除掉最后一个","号
		if (strBuff.length() > 0) {
			strBuff.deleteCharAt(strBuff.length() - 1);
		}
		return strBuff.toString();
	}

}
