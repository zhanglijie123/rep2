package cn.sunline.icore.ap.api;

import java.util.List;
import java.util.Map;

import cn.sunline.icore.ap.report.ApBaseReport;
import cn.sunline.icore.sys.type.EnumType.E_CYCLETYPE;

public class ApReportApi {
	
	public static List<String> genReport(String reportId, String brchno, Map<String, Object> input) {
		return ApBaseReport.genReport(reportId, brchno, input);
	}
	
	public static String genOneReport(String reportId, String brchno,  E_CYCLETYPE  interval, Map<String, Object> input ,String chineseName) {
		return ApBaseReport.genOneReport(reportId, brchno, interval, input, chineseName);
	}
	public static String genOneReport(String reportId, String brchno,  E_CYCLETYPE interval, Map<String, Object> input ) {
		return ApBaseReport.genOneReport(reportId, brchno, interval, input);
	}
	
	public static String getReportTranDate() {
		return ApBaseReport.getReportTranDate();
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
		return ApBaseReport.generateReport(reportId, brchno, input, passwd);
	}
}
