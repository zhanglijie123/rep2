package cn.sunline.icore.ap.report.mnt;

import cn.sunline.icore.ap.namedsql.ApBookDao;
import cn.sunline.icore.ap.type.ComApBook.ApQueryReportOut;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

/**
 * <p>
 * 文件功能说明：报表流水查询
 * </p>
 * 
 * @Author buyunxiang
 *         <p>
 *         <li>2017年7月3日-上午9:52:08</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年7月3日-buyunxiang：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApReportMnt {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApReportMnt.class);

	/**
	 * 查询报表流水
	 */
	public static Options<ApQueryReportOut> searchReport(String trxnDate, String branchId, String reportName, String reportId) {

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		// 调用分页
		Page<ApQueryReportOut> page = ApBookDao.selReport(runEnvs.getBusi_org_id(), trxnDate, branchId, reportName, reportId, runEnvs.getPage_start(), runEnvs.getPage_size(),
				runEnvs.getTotal_count(), false);

		runEnvs.setTotal_count(page.getRecordCount());
		
		//封装报表流水复合类型成集合
		Options<ApQueryReportOut> lstReport = new DefaultOptions<>();
		lstReport.setValues(page.getRecords());
		
		return lstReport;

	}
}
