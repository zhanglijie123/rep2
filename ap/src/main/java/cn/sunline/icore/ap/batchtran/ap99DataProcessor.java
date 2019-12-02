package cn.sunline.icore.ap.batchtran;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.sunline.clwj.msap.core.parameter.MsGlobalParm;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.common.util.DateUtil;
import cn.sunline.icore.ap.api.ApDateApi;
import cn.sunline.icore.ap.namedsql.ApDayEndBaseDao;
import cn.sunline.icore.ap.tables.TabApBook.Aps_dayend_infoDao;
import cn.sunline.icore.ap.tables.TabApBook.aps_dayend_info;
import cn.sunline.icore.ap.tables.TabApDevelop.Apb_auto_eodDao;
import cn.sunline.icore.ap.tables.TabApDevelop.apb_auto_eod;
import cn.sunline.icore.ap.type.ComApSystem.ApDateInfo;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_DAYENDRESULT;
import cn.sunline.icore.sys.type.EnumType.E_DAYENDSTATUS;
import cn.sunline.icore.sys.type.EnumType.E_DAYENDSWITCH;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.CoreUtil;
import cn.sunline.ltts.biz.out.type.KBaseEnumType.E_PILJYZHT;
import cn.sunline.ltts.biz.out.type.KBaseEnumType.E_PILZXM0S;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.Tsp_flow_step_controllerDao;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.Tsp_taskDao;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.tsp_flow_step_controller;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.tsp_task;

/**
 * Trade System EOD complete.
 */

public class ap99DataProcessor extends
		BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap99.Input, cn.sunline.icore.ap.batchtran.intf.Ap99.Property> {

	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input
	 *            批量交易输入接口
	 * @param property
	 *            批量交易属性接口
	 */
	@Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap99.Input input, cn.sunline.icore.ap.batchtran.intf.Ap99.Property property) {
		//上一个工作日期
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		
		String orgId = runEnvs.getBusi_org_id();
		
		ApDateInfo dateInfo = ApDateApi.getInfo(orgId);

		aps_dayend_info dayEndInfo = Aps_dayend_infoDao.selectOne_odb1(orgId, "Core_SOD", dateInfo.getLast_date(), false);
		
		if (dayEndInfo != null){
			dayEndInfo.setDay_end_status(E_DAYENDSTATUS.SUCCESS);
			Aps_dayend_infoDao.updateOne_odb1(dayEndInfo);
		}

		//String flowId = input.getTran_flow_id();
		String flowId = "Core_SOD";
		//String dayEndOrgId = input.getCorporate_code();
		String dayEndOrgId = "025";

		String dayEndInd = MsGlobalParm.getValue(ApConst.DAYEND_DATA, ApConst.DAYEND_TRAN_SWITCH);
		
		if (E_DAYENDSWITCH.valueOf(dayEndInd) == E_DAYENDSWITCH.ON) {
			
			List<apb_auto_eod> listAuytoEod = ApDayEndBaseDao.selEodPlan(runEnvs.getTrxn_date(), E_YESORNO.YES, false);
			// whether to automatically run batch sign
			boolean flag = false;
			String taskId;
			String data;
			String trxnDate;
			Map<String, Object> inputMap;
			Map<String, Object> commReqMap;

			// 如果开关打开，设置多条有效信息,则抛错
			if (listAuytoEod.size() > 1) {
				throw ApErr.AP.E0070();
			}

			//无匹配结束
			if (CommUtil.isNull(listAuytoEod)) {
				return;
			}

			trxnDate = dateInfo.getTrxn_date();

			apb_auto_eod tabAuytoEod = listAuytoEod.get(0);

			if (DateUtil.compareDate(trxnDate, tabAuytoEod.getEnd_time()) == 0) {
				tabAuytoEod.setAuto_dayend_exe_result(E_DAYENDRESULT.SUCCESS);
				Apb_auto_eodDao.updateOne_odb2(tabAuytoEod);
			}

			if (E_DAYENDRESULT.STOP == tabAuytoEod.getAuto_dayend_exe_result()) {
				tabAuytoEod.setRun_status(E_YESORNO.NO);
				Apb_auto_eodDao.updateOne_odb2(tabAuytoEod);
				flag = false;
			}
			if (DateUtil.compareDate(trxnDate, tabAuytoEod.getBegin_time()) < 0) {
				flag = false;
			}
			if (DateUtil.compareDate(trxnDate, tabAuytoEod.getBegin_time()) >= 0 && DateUtil.compareDate(trxnDate, tabAuytoEod.getEnd_time()) < 0
					&& E_DAYENDRESULT.SUCCESS != tabAuytoEod.getAuto_dayend_exe_result()) {

				flag = true;
			}
			if (DateUtil.compareDate(trxnDate, tabAuytoEod.getEnd_time()) > 0) {
				flag = false;
			}
			if (flag) {
				aps_dayend_info tabDayend = BizUtil.getInstance(aps_dayend_info.class);
				tabDayend.setDayend_org_id(orgId);
				tabDayend.setDay_end_status(E_DAYENDSTATUS.ONPROCESS);
				List<tsp_flow_step_controller> batchFlowList = Tsp_flow_step_controllerDao.selectAll_GetSteps(flowId, false);
				tabDayend.setTrxn_date(dateInfo.getTrxn_date());
				tabDayend.setDayend_schedule("0%");
				tabDayend.setDayend_proportion("0 : " + getBatchFlowTranNum(batchFlowList));
				tabDayend.setDayend_flow_define(flowId);
				tabDayend.setSystem_id_no(CoreUtil.getSystemId());
				Aps_dayend_infoDao.insert(tabDayend);

				taskId = flowId + "_" + trxnDate + "_" + dayEndOrgId;
				// DataArea
				inputMap = new HashMap<String, Object>();

				inputMap.put("dcnbianh", null);
				inputMap.put("farendma", dayEndOrgId);
				inputMap.put("rzglriqi", trxnDate);
				inputMap.put("pljylcbs", flowId);

				commReqMap = new HashMap<String, Object>();
				commReqMap.put("farendma", dayEndOrgId);
				commReqMap.put("org_id", dayEndOrgId);
				commReqMap.put("trxn_branch", MsGlobalParm.getValue("DAYEND_DATA", "DAYEND_TRAN_TELLER"));
				commReqMap.put("channel_id", MsGlobalParm.getValue("DAYEND_DATA", "DAYEND_CHANNEL_ID"));
				commReqMap.put("trxn_teller", MsGlobalParm.getValue("DAYEND_DATA", "DAYEND_TRAN_TELLER"));
				commReqMap.put("sponsor_system", ApConst.SYSTEM_BATCH);
				commReqMap.put("initiator_system", ApConst.SYSTEM_BATCH);
				commReqMap.put("jiaoyirq", trxnDate);

				// 拼接报文
				data = "{\"input\":{" + mapToString(inputMap) + "},\"comm_req\":{" + mapToString(commReqMap) + "}}";
				tsp_task tabBatchTask = BizUtil.getInstance(tsp_task.class);
				tabBatchTask.setTask_num(taskId); // 批量任务批次号
				long time = new Date().getTime();
				tabBatchTask.setTask_exe_num(time + ""); // 批量任务执行批次号
				tabBatchTask.setTask_commit_date(trxnDate); // 批量任务提交日期
				tabBatchTask.setTran_date(trxnDate); // 交易日期
				tabBatchTask.setTask_exe_mode(E_PILZXM0S.FLOW);
				tabBatchTask.setTransaction_date(trxnDate); // 当前交易日期
				tabBatchTask.setTran_flow_id(flowId); // 批量交易流程ID
				tabBatchTask.setTran_state(E_PILJYZHT.onprocess);
				tabBatchTask.setFlow_step_num(0); // 流程步骤号
				tabBatchTask.setTran_group_id(""); // 批量交易组ID
				tabBatchTask.setTran_id(""); // 批量交易ID
				tabBatchTask.setData_area(data); // 数据区
				tabBatchTask.setSystem_code(CoreUtil.getSystemId()); // 系统标识号
				tabBatchTask.setCorporate_code(dayEndOrgId); // 法人代码
				tabBatchTask.setSub_system_code(CoreUtil.getSubSystemId());
				Tsp_taskDao.insert(tabBatchTask);
			}
		}
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
	 * map data to String data
	 * 
	 * @param map
	 * @return
	 */
	private String mapToString(Map<String, Object> map) {

		StringBuffer strBuff = new StringBuffer();

		for (Entry<String, Object> entry : map.entrySet()) {

			// 当输入报文的类型为null或类型不为String,则不用拼接""号
			if (entry.getValue() == null || !(entry.getValue() instanceof String)) {
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
