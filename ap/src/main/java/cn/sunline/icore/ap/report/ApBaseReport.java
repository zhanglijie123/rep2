package cn.sunline.icore.ap.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.sunline.common.util.StringUtil;
import cn.sunline.icore.ap.tables.TabApSystem.App_reportDao;
import cn.sunline.icore.ap.tables.TabApSystem.app_report;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_CYCLETYPE;
import cn.sunline.ltts.base.logging.LogConfigManager.SystemType;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.DateTimeUtil;
import cn.sunline.ltts.biz.global.ReportUtil;
import cn.sunline.ltts.biz.global.SysUtil;

public class ApBaseReport {
	
	public static List<String> genReport(String reportId, String brchno, Map<String, Object> input) {
		String tranDate = getReportTranDate();
		
		List<app_report> reports = App_reportDao.selectAll_obd2(reportId, false);
		List<String> rpts = new ArrayList<String>();
		for (app_report report : reports) {
			String rpt = genOneReport(reportId, brchno, report.getGen_frequency(), input,null);
			rpts.add(rpt);
		}
		//年结日特殊处理
		if (tranDate.equals(BizUtil.getTrxRunEnvs().getBal_sheet_date())) {
			String rpt = ReportUtil.getReportById(reportId, input, brchno,  E_CYCLETYPE.DAY.getValue());
			if(StringUtil.isNotEmpty(rpt))
				rpts.add(rpt);
		}
		return rpts;
	}
	
	public static String genOneReport(String reportId, String brchno,  E_CYCLETYPE  interval, Map<String, Object> input ,String chineseName) {
		String tranDate = getReportTranDate();
		app_report report = App_reportDao.selectOne_odb1(reportId, interval, true);
			String tmpDate = DateTimeUtil.lastDay(tranDate, report.getGen_frequency().getValue());
			if(tranDate.equals(tmpDate)) {
				return ReportUtil.getReportById(reportId, input, brchno, report.getGen_frequency().getValue(), chineseName);
			}
		//年结日特殊处理
		if (tranDate.equals(BizUtil.getTrxRunEnvs().getBal_sheet_date())) {
			return ReportUtil.getReportById(reportId, input, brchno,  E_CYCLETYPE.DAY.getValue(),chineseName);
		}
		return null;
	}
	public static String genOneReport(String reportId, String brchno,  E_CYCLETYPE interval, Map<String, Object> input ) {
		String tranDate = getReportTranDate();
		app_report report = App_reportDao.selectOne_odb1(reportId, interval, true);
		String tmpDate = DateTimeUtil.lastDay(tranDate, report.getGen_frequency().getValue());
		if(tranDate.equals(tmpDate)) {
			return ReportUtil.getReportById(reportId, input, brchno, report.getGen_frequency().getValue());
		}
	    //年结日特殊处理
	    if (tranDate.equals(BizUtil.getTrxRunEnvs().getBal_sheet_date())) {
		   return ReportUtil.getReportById(reportId, input, brchno,  E_CYCLETYPE.DAY.getValue());
	    }
		return null;
	}
	
	public static String getReportTranDate() {
		String tranDate = BizUtil.getTrxRunEnvs().getTrxn_date();
		String endDate = BizUtil.getTrxRunEnvs().getBal_sheet_date();
		if(SysUtil.getCurrentSystemType() == SystemType.batch) {

			if(CommUtil.compare("830", BizUtil.getTrxRunEnvs().getBatch_group()) == 0
					|| CommUtil.compare("930", BizUtil.getTrxRunEnvs().getBatch_group()) == 0){
				tranDate = BizUtil.getTrxRunEnvs().getLast_date();
			}
  
		}
		
		return tranDate;
	}
	
	/**
	 * @Author liuzf@sunline.cn
	 *         <p>
	 *         <li>2016年11月2日-上午9:18:57</li>
	 *         <li>功能说明：生成报表并加密</li>
	 *         </p>
	 * @param reportId
	 * @param brchno
	 * @param input
	 * @param passwd
	 * @return
	 */
	public static List<String> generateReport(String reportId, String brchno, Map<String, Object> input, String passwd) {
		ReportUtil.getContext().put(ReportUtil.USER_PASSWORD, passwd);
		return genReport(reportId, brchno, input);
	}
}
