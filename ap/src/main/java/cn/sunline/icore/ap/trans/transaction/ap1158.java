package cn.sunline.icore.ap.trans.transaction;

import cn.sunline.icore.ap.namedsql.ApSystemDao;
import cn.sunline.icore.ap.type.ComApSystem.AppDataCleanInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;


public class ap1158 {

public static void queryAppDataCleanList( final cn.sunline.icore.ap.trans.transaction.intf.Ap1158.Output output){
	RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
	Options<AppDataCleanInfo> optADCInfo = null;
	Page<AppDataCleanInfo> pageADCInfo = ApSystemDao.selAppDataCleanList(runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(), false);
	
	runEnvs.setTotal_count(pageADCInfo.getRecordCount());
	
	if(CommUtil.isNotNull(pageADCInfo)&&pageADCInfo.getRecordCount()>0){
		optADCInfo = new DefaultOptions<AppDataCleanInfo>();
		optADCInfo.addAll(pageADCInfo.getRecords());
	}
	output.setList01(optADCInfo);	
}
}
