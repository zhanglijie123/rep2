//package cn.sunline.icore.ap.attr;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
//import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
//import cn.sunline.icore.ap.test.UnitTest;
//import cn.sunline.icore.ap.type.ComApAttr.ApAttrCtlInfoWithInd;
//import cn.sunline.icore.ap.type.ComApAttr.ApAttrInfoWithInd;
//import cn.sunline.icore.ap.type.ComApAttr.ApAttrMutexInfoWithInd;
//import cn.sunline.ltts.core.api.logging.BizLog;
//import cn.sunline.ltts.core.api.logging.BizLogUtil;
//import cn.sunline.ltts.core.api.model.dm.Options;
//import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
//import cn.sunline.icore.ap.type.E_OWNERLEVEL;
//import cn.sunline.icore.ap.util.BizUtil;
//import cn.sunline.icore.ap.util.DBUtil;
//public class ApAttributeMntTest extends UnitTest{
//
//	private static final BizLog bizlog = BizLogUtil.getBizLog(ApAttributeMntTest.class);
//
//	@Before
//	public void init(){
//		// 公共区写入业务法人代码
//		Map<String, Object> commReq = new HashMap<String, Object>();
//		commReq.put("busi_org_id", "99");
//		newCommReq(commReq);
//	}
//	
//	@Test
//	public void testQueryAttrInfo() {
//		ApAttributeMnt.queryAttrInfo(E_OWNERLEVEL.CARD);
//	}
//
//	@Test
//	public void testModifyAttrInfo() {
//		ApAttrInfoWithInd attrInfo = BizUtil.getInstance(ApAttrInfoWithInd.class);
//		attrInfo.setOperater_ind(E_DATAOPERATE.ADD);  //操作标志
//		attrInfo.setAttr_position((long)1);  //属性位置
//		attrInfo.setAttr_desc("Test");  //属性位描述
//		attrInfo.setAttr_expiry_ind(E_YESORNO.NO);  //属性位到期标志
//		attrInfo.setRef_drop_list("test");  //引用下拉字典
//		attrInfo.setDefault_value("1");  //缺省值
//		
//		ApAttrMutexInfoWithInd attrMutexInfo = BizUtil.getInstance(ApAttrMutexInfoWithInd.class);
//		attrMutexInfo.setOperater_ind(E_DATAOPERATE.ADD);  //操作标志
//		attrMutexInfo.setAttr_mutex_id("00001");  //属性互斥ID
//		attrMutexInfo.setAttr_mutex_desc("test");  //属性互斥描述
//		attrMutexInfo.setMapping_expression("aaa");  //属性匹配表达式
//		attrMutexInfo.setShow_error_info("error");  //错误异常展示信息
//		
//		ApAttrCtlInfoWithInd attrCtlInfo = BizUtil.getInstance(ApAttrCtlInfoWithInd.class);
//		attrCtlInfo.setOperater_ind(E_DATAOPERATE.ADD);  //操作标志
//		attrCtlInfo.setAttr_ctrl_id("00002");  //属性控制ID
//		attrCtlInfo.setAttr_ctrl_desc("test");  //属性控制描述
//		attrCtlInfo.setMapping_expression("121");  //属性匹配表达式
//		attrCtlInfo.setAttr_ctrl_run_cond("111");  //属性控制运行条件
//		attrCtlInfo.setShow_error_info("error");  //错误异常展示信息
//		
//		Options<ApAttrInfoWithInd> attrList = new DefaultOptions<ApAttrInfoWithInd>();
//		Options<ApAttrMutexInfoWithInd> attrMutexList = new DefaultOptions<ApAttrMutexInfoWithInd>();
//		Options<ApAttrCtlInfoWithInd> attrCtlList = new DefaultOptions<ApAttrCtlInfoWithInd>();
//		
//		attrList.add(attrInfo);
//		attrMutexList.add(attrMutexInfo);
//		attrCtlList.add(attrCtlInfo);
//		
//	//	ApAttributeMnt.modifyAttrInfo(E_OWNERLEVEL.CARD, attrList, attrMutexList, attrCtlList);
//		DBUtil.commit();
//	}
//}
