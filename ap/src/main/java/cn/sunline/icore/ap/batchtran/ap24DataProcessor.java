
package cn.sunline.icore.ap.batchtran;
import java.util.List;

import cn.sunline.ap.tables.TabApBillInfo.ApsAccrueRecord;
import cn.sunline.ap.tables.TabApBillInfo.ApsAccrueRecordDao;
import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.ApFileApi;
import cn.sunline.icore.ap.api.LocalFileProcessor;
import cn.sunline.icore.ap.batch.ApFileSend;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.type.ComApFileInfo.ApAccureDataCom;
import cn.sunline.icore.ap.type.ComApFileInfo.ApFileHead;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.dao.CursorHandler;
import cn.sunline.ltts.core.api.lang.Params;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
	 /**
	  * writing down file about accrual
	  * @author 
	  * @Date 
	  */

public class ap24DataProcessor extends
  BatchDataProcessorWithoutDataItem<cn.sunline.edsp.busi.ap.batchtran.intf.Ap24.Input, cn.sunline.edsp.busi.ap.batchtran.intf.Ap24.Property> {
	private static final BizLog bizlog = BizLogUtil.getBizLog(ap24DataProcessor.class);
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 */
	 @Override
	public void process(cn.sunline.edsp.busi.ap.batchtran.intf.Ap24.Input input, cn.sunline.edsp.busi.ap.batchtran.intf.Ap24.Property property) {
		// 生成计提数据文件
		writeAccrualGl();
			
	}
	 
		
	public static void writeAccrualGl( ) {
		// 公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		// 同步上日数据
		String lastDate = runEnvs.getLast_date();

		bizlog.debug("last_date[%s]", lastDate);

		// 计算表中最大日期
		String maxDate = ApFileDao.selMaxDateFromLedgerBal(MsOrg.getReferenceOrgId(ApsAccrueRecord.class), lastDate, false);
		if (CommUtil.isNotNull(maxDate)) {
			List<ApsAccrueRecord> lstApsAccrueRecord = ApFileDao.selsAccure
					(MsOrg.getReferenceOrgId(ApsAccrueRecord.class), lastDate, maxDate, false);
			if(lstApsAccrueRecord.size() > 0 && CommUtil.isNotNull(lstApsAccrueRecord)) {
				// 对上日有计提,今日未计提的数据补一条等于0的记录
				for(ApsAccrueRecord apsAccrueRecord : lstApsAccrueRecord) {
					ApsAccrueRecordDao.insert(apsAccrueRecord);
				}
				//ApFileDao.insAccureZero(MsOrg.getReferenceOrgId(ApsAccrueRecord.class), lastDate, maxDate);
			}
		}
		
		// 文件名
		String fileName = String.format("ACCURE_%s.txt", runEnvs.getLast_date()+"_"+SysUtil.getSubSystemId());// 文件名称

		// 获取路径
		String localPath = ApFileApi.getFullPath(ApConst.ACCURE_LOCAL_DIR_CODE);

		bizlog.debug("localPath[%s]", localPath);

		// 新建文件
		final LocalFileProcessor process = new LocalFileProcessor(localPath, fileName, "UTF-8");

		// 打开文件
		process.open(true);

		// 计算文件头
		ApFileHead fileHead = ApFileDao.selFileHeadFromAccure(MsOrg.getReferenceOrgId(ApsAccrueRecord.class), lastDate, false);
		fileHead.setSystem_id(SysUtil.getSubSystemId());
		try {

			StringBuffer files = new StringBuffer();

			// 文件头信息写入文件
			files.append(BizUtil.toJson(fileHead));

			bizlog.debug("file begin >>>>>>>>>>>[%s]", files);

			process.write(files.toString());

			// 执行游标处理
			bizlog.debug("Execute cursor begin >>>>>>>>>>>");

			Params para = new Params();

			para.add("last_date", lastDate);
			para.add("org_id", MsOrg.getReferenceOrgId(ApsAccrueRecord.class));

			// 文件体写入文件
			DaoUtil.selectList(ApFileDao.namedsql_selAccureData, para, new CursorHandler<ApAccureDataCom>() {
				
				public boolean handle(int arg0, ApAccureDataCom arg1) {

					StringBuffer files = new StringBuffer();

					// 文件体转换为josn
					files.append(BizUtil.toJson(arg1));
					// 写文件
					process.write(files.toString());

					return true;

				}
			});

		}
		finally {
			// 关闭文件
			process.close();
		}

		String fileId = ApFileSend.genFileId();
		ApFileSend.register(fileId,fileName, ApConst.ACCURE_REMOTE_DIR_CODE, ApConst.ACCURE_LOCAL_DIR_CODE, E_YESORNO.YES);
	}
		
}

