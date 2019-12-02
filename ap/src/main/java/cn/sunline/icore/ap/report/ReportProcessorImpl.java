package cn.sunline.icore.ap.report;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.sunline.clwj.msap.oss.model.MsTransferFileInfo;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.common.util.StringUtil;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.tables.TabApBook.Aps_reportDao;
import cn.sunline.icore.ap.tables.TabApBook.aps_report;
import cn.sunline.icore.ap.tables.TabApSystem.App_reportDao;
import cn.sunline.icore.ap.tables.TabApSystem.app_report;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.type.EnumType.E_CYCLETYPE;
import cn.sunline.ltts.base.expression.ExpressionEvaluator;
import cn.sunline.ltts.base.expression.ExpressionEvaluatorFactory;
import cn.sunline.ltts.base.logging.LogConfigManager.SystemType;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.ReportUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.report.ReportProcessor;

public class ReportProcessorImpl implements ReportProcessor {
	private static BizLog log = BizLogUtil.getBizLog(ReportProcessorImpl.class);

	@Override
	public String process(String reportId, Map<String, Object> input, String brchno, String interval, String fileName, String chineseName) {
		if (SysUtil.getCurrentSystemType() == SystemType.onl) {
			interval = StringUtil.nullable(interval, E_CYCLETYPE.DAY.getValue());
			brchno = StringUtil.nullable(brchno, BizUtil.getTrxRunEnvs().getTrxn_branch());
		}
		else if (StringUtil.isEmpty(interval))
			throw ApBaseErr.ApBase.E0047(reportId);

		app_report report = App_reportDao.selectOne_odb1(reportId, E_CYCLETYPE.get(interval), true);
		String tranDate = ApBaseReport.getReportTranDate();
		log.debug("Get the report transaction date through the ReportTools=" + tranDate);
		// 获取文件最终名称
		String realName = getRealName(fileName, chineseName, report.getReport_name(), ReportUtil.getReportLongName(reportId), report.getReport_no(), report.getReport_id());
		String relativeFineName = getRelativeFileName(input, report, chineseName, realName);
		String localefile = getLocaleFile(input, report, brchno, relativeFineName);
		String localDir = ApBaseFile.getLocalHome();
		File file = null;
		if (localDir.endsWith(String.valueOf(File.separatorChar))) {
			file = new File(localDir + localefile);
		}
		else {
			file = new File(localDir + File.separatorChar + localefile);
		}

		// 生成报表文件
		try {
			Boolean generated = ReportUtil.generate(reportId, file, input);
			if (!generated) {
				log.error("Data is not fetched so no reports are generated[" + reportId + "]");
				if (SysUtil.getCurrentSystemType() == SystemType.onl) {
					ApPubErr.APPUB.E0005(OdbFactory.getTable(app_report.class).getLongname(), SysDict.A.report_id.getLongName(), reportId);

				}
				else
					return null;
			}
			else {
				log.debug("Report absolute path=" + file.getAbsolutePath() + ",the report was generated successfully！>>>>>>>");
			}
		}
		catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		// 报表文件上传

		String remotefile = getRemoteFile(input, report, tranDate, brchno, relativeFineName);
		// 为yes才上传
		if (report.getUpload_ind() == E_YESORNO.YES) {
			log.debug("The report  is being uploaded>>>>>now upload!");
			log.debug("remotefile [%s]", remotefile);
//			ApBaseFile.createClient(report.getRemote_dir_code()).upload(localefile, remotefile);
			ApBaseFile.createClient(report.getRemote_dir_code()).upload(getUploadInfo(localefile, remotefile));
			log.debug("The report was generated successfully>>>>now success!");
		}

		// 登记流水日志
		if (report.getRegister_log_ind() == E_YESORNO.YES)
			registReportLog(reportId, localefile, report, input, remotefile, brchno, interval, relativeFineName, chineseName, realName);

		return localefile;
	}

	/**
	 * <p>Title:getUploadInfo </p>
	 * <p>Description:	</p>
	 * @author jiangyaming
	 * @date   2019年8月5日 
	 * @param localefile
	 * @param remotefile
	 * @return
	 */
	private MsTransferFileInfo getUploadInfo(String localefile, String remotefile) {
		File fileLocal = new File(localefile);
		String fileName = fileLocal.getName();
		
		String localFilePath = localefile.replace(fileName, "");
		String remoteFilePath = remotefile.replace(fileName, "");
		ApBaseFile.getMsTransferFileInfo(localFilePath, remoteFilePath, fileName, false);
		return null;
	}

	public String process(String reportId, Map<String, Object> input, String brchno, String interval, String fileName) {
		return process(reportId, input, brchno, interval, fileName, null);
	}

	/**
	 * @param obj
	 * @param report
	 * @param control
	 * @return
	 */
	private String getLocaleFile(Object input, app_report control, String brchno, String relativeFineName) {

		String tranDate = ApBaseReport.getReportTranDate();
		String localeFile = tranDate + File.separatorChar + brchno + File.separatorChar + relativeFineName;
		return localeFile;
	}

	private String getRelativeFileName(Object input, app_report report, String chineseName, String realName) {
		ExpressionEvaluator ee = ExpressionEvaluatorFactory.getInstance();
		String filenamepattern = ReportUtil.getPathPattern(report.getReport_id());

		String fileSuffix = "";

		if (CommUtil.isNotNull(report.getReport_file_type())) {
			fileSuffix = report.getReport_file_type().getValue();
			if (CommUtil.equals(fileSuffix, "excel")) {
				fileSuffix = "xls";
			}
		}

		Map<String, Object> content = new HashMap<String, Object>();
		if (!CommUtil.equals("", fileSuffix)){
			if(realName.indexOf(".")<0){
				realName = realName + "." + fileSuffix;
			}			
		}
			

		String relativePath = "";
		if (StringUtil.isNotEmpty(filenamepattern)) {
			Object _relativePath = ee.eval(filenamepattern, input, content);
			log.debug("Relative path[" + filenamepattern + "]result[" + _relativePath + "]，the parameter is [" + input + "]");
			if (StringUtil.isNotEmpty(_relativePath))
				relativePath = ((String) _relativePath).replace('/', File.separatorChar).replace('\\', '/');
			if (StringUtil.isNotEmpty(relativePath)) {
				if (relativePath.endsWith("/"))
					realName = relativePath + realName;
				else
					realName = relativePath + '/' + realName;
			}
		}
		return realName;
	}

	private void registReportLog(String reportId, String localefile, app_report control, Map<String, Object> input, String remoteFile, String brchno, String interval,
			String relativeFineName, String chineseName, String realName) {
		aps_report logInfo = BizUtil.getInstance(aps_report.class);

		logInfo.setReport_id(reportId);

		// 此处应改为登记dir_code
		logInfo.setRemote_file_path(remoteFile);
		logInfo.setLocal_file_path(localefile);
		logInfo.setReport_name(realName);

		logInfo.setTrxn_date(ApBaseReport.getReportTranDate());
		brchno = StringUtil.nullable(brchno, BizUtil.getTrxRunEnvs().getTrxn_branch());
		logInfo.setBranch_id(brchno);
		String tranDate = logInfo.getTrxn_date();
		String relativePath = tranDate + "/" + brchno + "/" + relativeFineName;
		if (SysUtil.getCurrentSystemType() == SystemType.batch)
			relativePath = tranDate + "/" + brchno + "/" + relativeFineName;// 跟上传路径保持一致
		logInfo.setRelative_path(relativePath);
		logInfo.setGen_frequency(E_CYCLETYPE.get(StringUtil.nullable(interval, E_CYCLETYPE.DAY.getValue())));

		aps_report tblKsys_bblius = Aps_reportDao.selectOne_odb1(logInfo.getTrxn_date(), logInfo.getBranch_id(), logInfo.getReport_id(), false);

		if (CommUtil.isNotNull(tblKsys_bblius)) {
		    logInfo.setData_create_user(tblKsys_bblius.getData_create_user());
		    logInfo.setData_create_time(tblKsys_bblius.getData_create_time());
		    logInfo.setData_version(tblKsys_bblius.getData_version());
			Aps_reportDao.updateOne_odb1(logInfo);
		}
		else {
			Aps_reportDao.insert(logInfo);
		}

	}

	public String getRemoteFile(Object input, app_report control, String tranDate, String brchno, String relativeFineName) {
		String file = null;
		if (CommUtil.isNotNull(control.getRemote_dir_code())) {
			file = ApBaseFile.getFullPath(control.getRemote_dir_code(), relativeFineName);
		}
		else {
			file = "reports/" + tranDate + "/" + brchno + "/" + relativeFineName;
		}
		return file;
	}

	private String getRealName(String filenamepattern, String chineseName, String reportName, String longname, String reportNo, String reportId) {

		if (CommUtil.isNotNull(filenamepattern)) {
			return filenamepattern;
		}
		if (CommUtil.isNotNull(chineseName)) {
			return chineseName;
		}
		if (CommUtil.isNotNull(reportName)) {
			return reportName;
		}
		if (CommUtil.isNotNull(longname)) {
			return longname;
		}
		if (CommUtil.isNotNull(reportNo)) {
			return reportNo;
		}
		return reportId;

	}
	
	// 拼接远程目录
	public static String remotefile(app_report control , String brchno,String localName){
		
		String relativeFineName = localName.substring(localName.lastIndexOf(File.separatorChar)+1);
		String tranDate = ApBaseReport.getReportTranDate();
		String file = null;
		if (CommUtil.isNotNull(control.getRemote_dir_code())) {
			file = ApBaseFile.getFullPath(control.getRemote_dir_code(), relativeFineName);
		}
		else if (CommUtil.isNotNull(ReportUtil.getFileNamePattern(control.getReport_id()))) {
			file = ReportUtil.getFileNamePattern(control.getReport_id());
		}
		else {
			file = "/reports/" + tranDate + "/" + brchno + "/" + relativeFineName;
		}
		return file;
		
	}
	
}
