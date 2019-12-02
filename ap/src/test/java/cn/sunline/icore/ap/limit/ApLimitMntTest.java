////package cn.sunline.icore.ap.limit;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
//import cn.sunline.icore.ap.test.UnitTest;
//import cn.sunline.icore.ap.type.ComApLimit.ApLimitBasicInfo;
//import cn.sunline.icore.ap.type.ComApLimit.ApLimitBasicInfoLstQryIn;
//import cn.sunline.icore.ap.type.ComApLimit.ApLimitDetailInfo;
//import cn.sunline.icore.ap.type.ComApLimit.ApLimitMntIn;
//import cn.sunline.ltts.base.util.Assert;
//import cn.sunline.ltts.core.api.logging.BizLog;
//import cn.sunline.ltts.core.api.logging.BizLogUtil;
//import cn.sunline.ltts.core.api.model.dm.Options;
//import cn.sunline.icore.ap.util.BizUtil;
//
//public class ApLimitMntTest extends UnitTest{
//	private static final BizLog bizlog = BizLogUtil.getBizLog(ApLimitMntTest.class);
//	
//	@Before
//	public void init(){
//		//公共区写入业务法人代码
//    	Map<String, Object> commReq = new HashMap<String, Object>();
//    	commReq.put("busi_org_id", "99");
//    	commReq.put("trxn_date", "20161225");
//    	newCommReq(commReq);
//	}
//	
//	@Test
//	public void lstLimitBasicInfoTest(){
//		ApLimitBasicInfoLstQryIn input = BizUtil.getInstance(ApLimitBasicInfoLstQryIn.class);
//		Options<ApLimitBasicInfo> lst = ApLimitMnt.lstLimitBasicInfo(input);
//		bizlog.debug("lst=%s", lst);
//		Assert.notEmpty(lst);
//	}
//	
//	@Test
//	public void getLimitDetailInfoTest(){
//		ApLimitDetailInfo detailInfo = ApLimitMnt.getLimitDetailInfo("LIMIT002", "20180101");
//		bizlog.debug("detailInfo=%s", detailInfo);
//	}
//	
//	@Test
//	public void modifyLimitInfoTest(){
//		ApLimitMntIn modifyInfo = BizUtil.getInstance(ApLimitMntIn.class);
//		modifyInfo.setLimit_no("LIMIT0003");
//		modifyInfo.setEffect_date("20171101");
//		modifyInfo.setData_version(1L);
//		modifyInfo.setOperater_ind(E_DATAOPERATE.DELETE);
//		ApLimitMnt.maintainLimitInfo(modifyInfo);
//	}
//
//}
