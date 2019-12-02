
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.limit.mnt.ApLimitMnt;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitBasicInfo;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitBasicInfoLstQryIn;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitDetailInfo;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitMntIn;
import cn.sunline.icore.ap.type.ComApLimit.ApLimitStatisResult;
import cn.sunline.ltts.core.api.model.dm.Options;


 /**
  * limit parameter information
  * 限额参数信息
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApLimitParamImpl", longname="limit parameter information", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApLimitParamImpl implements cn.sunline.icore.ap.servicetype.SrvApLimitParam{
	@Override
    public Options<ApLimitStatisResult> queryLimitStatisList(String limitOwnerId, String limitStatisNo) {
		return ApLimitMnt.queryLimitStatis(limitOwnerId,limitStatisNo);
	}
	
	@Override
	public cn.sunline.ltts.core.api.model.dm.Options<ApLimitBasicInfo> queryLimitParmList( final ApLimitBasicInfoLstQryIn qryInput){
		return ApLimitMnt.lstLimitBasicInfo(qryInput);
	}
	
	@Override
	public ApLimitDetailInfo queryLimitDetailInfo(String limit_no, String effect_date) {
		return ApLimitMnt.getLimitDetailInfo(limit_no, effect_date);
	}
    
	@Override
	public void maintainLimitInfo(ApLimitMntIn limitInfo) {
		ApLimitMnt.maintainLimitInfo(limitInfo);
	}

	@Override
	public String genLimitNo() {
		return ApLimitMnt.genLimitNO();
	}
	
}

