package cn.sunline.icore.ap.callback;

import java.util.List;

import cn.sunline.clwj.msap.biz.spi.MsBatchTransactionProcessCallBackSPIImpl;
import cn.sunline.icore.ap.namedsql.ApDayEndBaseDao;
import cn.sunline.icore.ap.tables.TabApBook.Aps_dayend_infoDao;
import cn.sunline.icore.ap.tables.TabApBook.aps_dayend_info;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_DAYENDSTATUS;
import cn.sunline.ltts.base.util.RunnableWithReturn;
import cn.sunline.ltts.batch.BatchTaskContext;
import cn.sunline.ltts.batch.engine.sequence.BatchTaskSequence;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.out.type.KBaseEnumType.E_ZUOYZXZT;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.Tsp_flow_step_controllerDao;
import cn.sunline.ltts.busi.ltts.engine.batch.plugin.tables.KSysBatchTable.tsp_flow_step_controller;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.engine.data.DataArea;

public class BatchTransactionProcessCallBackImpl extends MsBatchTransactionProcessCallBackSPIImpl{
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(BatchTransactionProcessCallBackImpl.class);
	
	@Override
	public void beforeBizEnvInTran(DataArea dataArea) {
		super.beforeBizEnvInTran(dataArea);
	}


	private int getBatchFlowTranNum(List<tsp_flow_step_controller> batchFlowList) {

		int i = 0;

		for (tsp_flow_step_controller batchFlow : batchFlowList) {
			i += ApDayEndBaseDao.selBatchNum(batchFlow.getTran_group_id(), false);
		}

		return i;
	}

	@Override
	public void afterBatchTranExecute(DataArea dataArea) {
		
		bizlog.debug("BatchTaskContext:[%s]", BatchTaskContext.get().toString());
		
		String orgId =  BatchTaskContext.get().getTenantId();
		String flowId =  BatchTaskContext.get().getCurrentFlowId();
		String trxnDate = BatchTaskContext.get().getTranDate();
		String branchId = BatchTaskContext.get().getTaskId();
		
		// 由于年结支持重复跑，所以pljypich字段会在后面加上时间戳，平台把该值set到 "gl_pljypich"里面
		if (CommUtil.equals("gl_yearend", flowId) && CommUtil.isNotNull(System.getProperty("gl_pljypich"))){
			branchId = System.getProperty("gl_pljypich");
		}
		
		final int num = ApDayEndBaseDao.selBatchSuccessNum(branchId, E_ZUOYZXZT.success,
				false);

		final int total = getBatchFlowTranNum(Tsp_flow_step_controllerDao.selectAll_GetSteps(
				flowId, false));
		
		final aps_dayend_info dayEndInfo = Aps_dayend_infoDao.selectOne_odb1(orgId,
				flowId, trxnDate, false);
		
		if (dayEndInfo != null) {
			DaoUtil.executeInNewTransation(new RunnableWithReturn<Void>() {   // TODO 某些情况下 数据更新不成功，采用独立事务强制跟新
				@Override
				public Void execute() {
					dayEndInfo.setDayend_proportion(num + " : " + total);
					dayEndInfo.setDayend_schedule(BizUtil.getDecimal(num, total) + "%");
					if(num > 0 &&  num < total) {
						dayEndInfo.setDay_end_status(E_DAYENDSTATUS.PROCESSING);
					}else if(num == total) {
						dayEndInfo.setDay_end_status(E_DAYENDSTATUS.SUCCESS);
					}
					Aps_dayend_infoDao.updateOne_odb1(dayEndInfo);
					return null;
				}
			});
		}
		super.afterBatchTranExecute(dataArea);
	}

	@Override
	public void tranExceptionProcess(DataArea dataArea, Throwable t) {

		// 批量交易失败后调用
		final String orgId = BatchTaskContext.get().getTenantId();
		final String flowId = BatchTaskContext.get().getCurrentFlowId();
		final String trxnDate = BatchTaskContext.get().getTranDate();
		DaoUtil.executeInNewTransation(new RunnableWithReturn<Integer>() {

			@Override
			public Integer execute() {
				aps_dayend_info dayEndInfo = Aps_dayend_infoDao.selectOne_odb1(
						orgId, flowId, trxnDate, false);
				int i = 0;
				if (dayEndInfo != null) {
					dayEndInfo.setDay_end_status(E_DAYENDSTATUS.FAILURE);
					i = Aps_dayend_infoDao.updateOne_odb1(dayEndInfo);
				}
				return i;
			}

		});

		super.tranExceptionProcess(dataArea, t);
	}

	@Override
	public void afterBizEnvInTran(DataArea dataArea) {
		super.afterBizEnvInTran(dataArea);
	}

	/**
	 * @Author Madong
	 *         <p>
	 *         <li>2016年1月5日-下午1:54:44</li>
	 *         <li>功能说明：作业初始化前处理</li>
	 *         </p>
	 * @param dataArea
	 *            传入的初始化的数据
	 */
	@Override
	public void beforeBizEnvInJob(DataArea dataArea) {
		super.beforeBizEnvInJob(dataArea);
	}

	@Override
	public void afterBizEnvInJob(DataArea dataArea) {
		super.afterBizEnvInJob(dataArea);

	}

	@Override
	public BatchTaskSequence initBatchTaskSequence(DataArea dataArea) {
		return super.initBatchTaskSequence(dataArea);
	}

}
