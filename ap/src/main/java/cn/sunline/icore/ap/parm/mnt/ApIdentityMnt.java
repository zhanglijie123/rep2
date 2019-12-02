package cn.sunline.icore.ap.parm.mnt;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.icore.ap.namedsql.ApBasicDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_identity;
import cn.sunline.icore.ap.type.ComApBasic.ApIdentityInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

/***
 * 
 * <p>
 * 文件功能说明：证件种类参数维护类
 *       			
 * </p>
 * 
 * @Author zhangjing2
 *         <p>
 *         <li>2016年12月26日-下午4:19:56</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2016年12月26日zhangjing2：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApIdentityMnt {
/**
 * 证件种类参数列表查询方法
 */
	public static Options<ApIdentityInfo> queryIdentityList(String idType, String idDesc){
		
		String orgId = MsOrg.getReferenceOrgId(app_identity.class);//法人代码
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		Page<ApIdentityInfo> page = ApBasicDao.selIdentityList(orgId, idType, idDesc, runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(), false);
		DefaultOptions<ApIdentityInfo> options = new DefaultOptions<ApIdentityInfo>();
		runEnvs.setTotal_count(page.getRecordCount());
		options.setValues(page.getRecords());
		return  options;
		
	}
}
