package cn.sunline.icore.ap.batchtran;
import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType;
import cn.sunline.icore.ap.api.ApFileApi;
import cn.sunline.icore.ap.api.LocalFileProcessor;
import cn.sunline.icore.ap.batch.ApFileSend;
import cn.sunline.icore.ap.namedsql.ApFileDao;
import cn.sunline.icore.ap.tables.TabApAccounting.ApsAccountingEvent;
import cn.sunline.icore.ap.type.ComApFileInfo.ApCheckRecord;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_BATCHTYPE;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

	 /**
	  * writing down file about accounting event number
	  * @author 
	  * @Date 
	  */
public class ap22DataProcessor extends
  BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap22.Input, cn.sunline.icore.ap.batchtran.intf.Ap22.Property> {
  
	private static final BizLog bizlog = BizLogUtil.getBizLog(ap21DataProcessor.class);
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 */
	 public void process(cn.sunline.icore.ap.batchtran.intf.Ap22.Input input, cn.sunline.icore.ap.batchtran.intf.Ap22.Property property) {
		// 获取公共运行变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		
		String system_id = SysUtil.getSubSystemId();

		String localRootPath = ApFileApi.getFullPath(ApConst.CORE_CHECK_RECORD_REMOTE); // 远程的绝对路径部分
		
		// 文件名
		String fileName = String.format("CHECK_RECORD_%s.txt", runEnvs.getLast_date()+"_"+system_id);// 文件名称

		// 本地路径
		// localRootPath = ApFileApi.getFileFullPath(localRootPath, fileName);

		bizlog.debug("localRootPath>>>>[%s] [%s]", localRootPath, fileName);
		
		String org_id = MsOrg.getReferenceOrgId(ApsAccountingEvent.class) ;

		// 计算文件头
		ApCheckRecord fileHead = ApFileDao.selFileHeadNumFromAccountingEvent(org_id, runEnvs.getLast_date(),false);
		fileHead.setSystem_id(system_id);
		fileHead.setFile_type(E_BATCHTYPE.ACCOUNTINGEVENT_DOWN);
		fileHead.setTrxn_date(runEnvs.getLast_date());
//		fileHead.setAccounting_subject(E_ACCOUNTINGSUBJECT.);

		// 创建文件处理对象
		LocalFileProcessor processor = new LocalFileProcessor(localRootPath, fileName, "UTF-8");
		try {
			// 打开文件
			processor.open(true);

			// 写文件头
			processor.write(BizUtil.toJson(fileHead));

			processor.close();// 关闭文件
			
		} catch (Exception e) {
			
		} 

		String fileId = ApFileSend.genFileId();
//		ApFileSendJrss.register(fileId, fileName, ApAcConst.CORE_CHECK_RECORD_REMOTE, ApAcConst.CORE_CHECK_RECORD_LOCAL, MsEnumType.E_YESORNO.NO);
		ApFileSend.register(fileId, fileName, ApConst.CORE_CHECK_RECORD_REMOTE, ApConst.CORE_CHECK_RECORD_LOCAL, MsEnumType.E_YESORNO.YES);


	}

}


