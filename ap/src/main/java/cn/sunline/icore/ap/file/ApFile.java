package cn.sunline.icore.ap.file;

import java.util.Collection;
import java.util.List;

import cn.sunline.ap.tables.TabApBillInfo.ApsLedgerBal;
import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.core.parameter.MsParameter;
import cn.sunline.clwj.msap.sys.type.MsEnumType;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.ApFileApi;
import cn.sunline.icore.ap.api.ApSystemParmApi;
import cn.sunline.icore.ap.api.LocalFileProcessor;
import cn.sunline.icore.ap.batch.ApFileSend;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.tables.TabApAccounting.ApsAccountingEvent;
import cn.sunline.icore.ap.tables.TabApAccounting.ApsAccountingEventDao;
import cn.sunline.icore.ap.type.ComApFileInfo.ApFileHead;
import cn.sunline.icore.ap.type.ComApFileInfo.ApLedgerBalCom;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.DateTimeUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.dao.CursorHandler;
import cn.sunline.ltts.core.api.lang.Params;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApFile {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApFile.class);
	private static final String MAIN_KEY = "AC";
	private static final String KEY_MODE = "AC_MODE";

	public static void accountingEventUploadMain() {
		// 获取公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		// 获取每个文件的记录数
		int recordNum = ApSystemParmApi.getIntValue(ApConst.FILE_COUNT);
		bizlog.debug("gettemp_data>>>>[%s]", runEnvs.getTemp_data());
		String localRootPath = ApFileApi.getFullPath(ApConst.ACCOUNTING_EVENT_LOCAL_DIR_CODE); // 远程的绝对路径部分

		// 循环写文件，每次写文件都写最多2000条，超过2000条，重新写入
		while (true) {
			String fileId = ApFileSend.genFileId();
			if(!fileId.startsWith(runEnvs.getTemp_data())) {//上日
				fileId = runEnvs.getTemp_data() + fileId.substring(8);
			}
			int recordedCount = accountingEventSingleUpload(localRootPath, fileId, runEnvs.getTemp_data(), recordNum);
			if (recordedCount < recordNum) {
				break;
			}
		}
	}

	/**
	 * 
	 * 作者:baojk
	 * 日期:2019年3月20日 下午1:40:08
	 * 方法描述: 生成会计流水数据文件
	 * @param localRootPath
	 * @param hashValue
	 * @param fileId
	 * @param trxnDate
	 * @param recordNum
	 * @return      
	 * 返回值: int
	 */
	private static int accountingEventSingleUpload(String localRootPath, String fileId, String trxnDate, int recordNum) {
		List<ApsAccountingEvent> accountingList = ApFileDao.selApsAccountingEventNoDataSync(MsOrg.getReferenceOrgId(ApsAccountingEvent.class), (long) recordNum, trxnDate, false);
		
		// 因为CDS不支持分页下的update，故一条一条更新
		for(ApsAccountingEvent event:accountingList){
			
			event.setData_sync_ind(E_YESORNO.YES);
			event.setData_sync_file_id(fileId);
			ApsAccountingEventDao.updateOne_odb1(event);
		}

		E_YESORNO yesOrNoValue = MsParameter.getYesOrNoValue(MAIN_KEY, KEY_MODE);
		if(yesOrNoValue==E_YESORNO.YES) {//集中式模式，不需要生成文件
			return accountingList.size();
		}
		// 文件名
		String fileName = String.format("ACCOUNTING_%s.txt", fileId+"_"+SysUtil.getSubSystemId());// 文件名称

		bizlog.debug("localRootPath>>>>[%s] [%s]", localRootPath, fileName);
		
		// 计算文件头
		ApFileHead fileHead = ApFileDao.selFileHeadFromAccountingEvent(fileId, false);
		fileHead.setSystem_id(SysUtil.getSubSystemId());// 设置一个系统编号，用于区分每个分片

		// 取出要写的文件的内容
		Collection<ApsAccountingEvent> enventTableList = ApFileDao.selAccountingEvent(fileId, false);

		// 总笔数
		int totalCount = enventTableList.size();

		// 创建文件处理对象
		LocalFileProcessor processor = new LocalFileProcessor(localRootPath, fileName, "UTF-8");
		// 打开文件
		processor.open(true);
		// 写文件头
		processor.write(BizUtil.toJson(fileHead));
		for (ApsAccountingEvent enventTable : enventTableList) {
			processor.write(SysUtil.serialize(enventTable));// 用json格式，写文件
		}

		processor.close();// 关闭文件
		
		ApFileSend.register(fileId, fileName, ApConst.ACCOUNTING_EVENT_REMOTE_DIR_CODE, ApConst.ACCOUNTING_EVENT_LOCAL_DIR_CODE, MsEnumType.E_YESORNO.YES);
		bizlog.debug("localRootPath[%s]", localRootPath);

		bizlog.debug("uplaod files end <<<<<<<<");

		return totalCount;
	}
	
	/**
	 * 
	 * @author:baojk
	 * <p>
	 * <li>2019年3月28日 下午5:20:44</li>
	 * <li>功能说明：生成余额文件</li>
	 * </p>      
	 * 返回值: void
	 */
	public static void writeLedgerBalDataMain() {

		// 公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		// 同步上日数据
		String lastDate = runEnvs.getLast_date();

		bizlog.debug("last_date[%s]", lastDate);

		// 计算表中最大日期
		String maxDate = ApFileDao.selMaxDateFromLedgerBal(MsOrg.getReferenceOrgId(ApsLedgerBal.class), lastDate, false);
		if (CommUtil.isNotNull(maxDate)) {
			String createTime= DateTimeUtil.getNow("yy-MM-dd hh:mm:ss");
			// 对上日有计提,今日未计提的数据补一条等于0的记录
			//ApFileDao.insLedgerBalZero(MsOrg.getReferenceOrgId(ApsLedgerBal.class), lastDate, maxDate,createTime);//hotdb不支持此sql 修改如下
			List<ApsLedgerBal> zeroBalList = ApFileDao.selLedgerBalZero(MsOrg.getReferenceOrgId(ApsLedgerBal.class), lastDate, maxDate,createTime, false);
			if(zeroBalList != null && zeroBalList.size() > 0) {
				DaoUtil.insertBatch(ApsLedgerBal.class, zeroBalList);
			}
		}

		// 文件名
		String fileName = String.format("%s.txt", "LEDGERBAL_" + lastDate + "_" + SysUtil.getSubSystemId());

		// 获取路径
		String localPath = ApFileApi.getFullPath(ApConst.CORE_LEDGER_BAL_REMOTE_DIR_CODE);
		bizlog.debug("localPath[%s]", localPath);

		// 新建文件
		final LocalFileProcessor process = new LocalFileProcessor(localPath, fileName, "UTF-8");

		// 打开文件
		process.open(true);

		// 计算文件头
		ApFileHead fileHead = ApFileDao.selFileHeadFromLedgerBal(MsOrg.getReferenceOrgId(ApsLedgerBal.class), lastDate, false);
		fileHead.setSystem_id(SysUtil.getSubSystemId());
		
		
		String okFileName = String.format("%s.txt.ok", "LEDGERBAL" + lastDate);
		// 创建Ok文件
//		LocalFileProcessor processorOk = new LocalFileProcessor(localPath, okFileName, "UTF-8");

		try {

			StringBuffer files = new StringBuffer();

			// 文件头信息写入文件
			files.append(BizUtil.toJson(fileHead));

			// 写文件头
			process.write(files.toString());

			// 执行游标处理
			bizlog.debug("Execute cursor begin >>>>>>>>>>>");

			Params para = new Params();

			para.add("last_date", lastDate);
			para.add("org_id", MsOrg.getReferenceOrgId(ApsLedgerBal.class));

			// 文件体写入文件
			DaoUtil.selectList(ApFileDao.namedsql_selLedgerBal, para, new CursorHandler<ApLedgerBalCom>() {

				@Override
				public boolean handle(int arg0, ApLedgerBalCom arg1) {

					StringBuffer files = new StringBuffer();

					// 文件体转换为josn
					files.append(BizUtil.toJson(arg1));
					// 写文件
					process.write(files.toString());

					return true;

				}
			});
			
			
			
			// 打开文件
//			processorOk.open(true);
			

		}
		finally {
			// 关闭文件
			process.close();
//			processorOk.close();// 关闭文件
		}
		String fileId = ApFileSend.genFileId();
//		ApFileSendJrss.register(fileId, fileName, ApConst.CORE_LEDGER_BAL_REMOTE_DIR_CODE, ApConst.CORE_LEDGER_BAL_LOCAL_DIR_CODE, MsEnumType.E_YESORNO.NO);
		ApFileSend.register(fileId, fileName, ApConst.CORE_LEDGER_BAL_REMOTE_DIR_CODE, ApConst.CORE_LEDGER_BAL_LOCAL_DIR_CODE, MsEnumType.E_YESORNO.YES);

	}


}
