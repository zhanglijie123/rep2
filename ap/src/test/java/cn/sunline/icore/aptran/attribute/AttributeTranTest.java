//package cn.sunline.icore.aptran.attribute;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
//import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
//import cn.sunline.icore.ap.tables.TabApAttribute.App_attributeDao;
////import cn.sunline.icore.ap.tables.TabApAttribute.App_attribute_controlDao;
//import cn.sunline.icore.ap.tables.TabApAttribute.App_attribute_mutexDao;
//import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute;
////import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute_control;
//import cn.sunline.icore.ap.tables.TabApAttribute.app_attribute_mutex;
//import cn.sunline.icore.ap.test.UnitTest;
//import cn.sunline.icore.ap.type.ComApAttr.ApAttrCtlInfoWithInd;
//import cn.sunline.icore.ap.type.ComApAttr.ApAttrInfoWithInd;
//import cn.sunline.icore.ap.type.ComApAttr.ApAttrMutexInfoWithInd;
//import cn.sunline.icore.aptran.batchtran.apf02DataProcessor;
//import cn.sunline.ltts.busi.aplt.tables.TabApFile.apb_file_reversal;
//import cn.sunline.ltts.core.api.logging.BizLog;
//import cn.sunline.ltts.core.api.logging.BizLogUtil;
//import cn.sunline.ltts.core.api.model.dm.Options;
//import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
//import cn.sunline.ltts.engine.data.DataArea;
//import cn.sunline.icore.ap.type;
//import cn.sunline.icore.ap.util.BizUtil;
//
//public class AttributeTranTest extends UnitTest {
//
//	private static final BizLog bizlog = BizLogUtil.getBizLog(AttributeTranTest.class);
//
//	@Before
//	public void before() {
//		Map<String, Object> commReq = new HashMap<String, Object>();
//		commReq.put("busi_org_id", "0033");
//		newCommReq(commReq);
//	}
//
//	@Test
//	public void testFileReversal() {
//
//		apf02DataProcessor processor = BizUtil.getInstance(apf02DataProcessor.class);
//		// 获取公共运行变量
//		apb_file_reversal file_reversal = BizUtil.getInstance(apb_file_reversal.class);
//
//		processor.process(null, 0, file_reversal, null, null);
//
//	}
//
//	@Test
//	public void testAp1090() {
//		DataArea requestDataArea = newTranReq("1090");
//		// 构造公共请求
//		requestDataArea.getCommReq().putAll(genCommReq(String.valueOf(System.currentTimeMillis())));
//
//		requestDataArea.getInput().put("attr_level", "CARD");
//
//		DataArea res = super.executeTran(requestDataArea);
//		super.isTranSuccess(res);
//	}
//
//	/*
//	 * 新增
//	 */
//	@Test
//	public void testAp1091() {
//		DataArea requestDataArea = newTranReq("1091");
//		// 构造公共请求
//		requestDataArea.getCommReq().putAll(genCommReq(String.valueOf(System.currentTimeMillis())));
//
//		requestDataArea.getInput().put("attr_level", "CARD");
//
//		ApAttrInfoWithInd attrInfo = BizUtil.getInstance(ApAttrInfoWithInd.class);
//		attrInfo.setOperater_ind(E_DATAOPERATE.ADD); //操作标志
//		attrInfo.setAttr_position((long) 2); //属性位置
//		attrInfo.setAttr_desc("Test"); //属性位描述
//		attrInfo.setAttr_expiry_ind(E_YESORNO.NO); //属性位到期标志
//		attrInfo.setRef_drop_list("test"); //引用下拉字典
//		attrInfo.setDefault_value("1"); //缺省值
//
//		ApAttrMutexInfoWithInd attrMutexInfo = BizUtil.getInstance(ApAttrMutexInfoWithInd.class);
//		attrMutexInfo.setOperater_ind(E_DATAOPERATE.ADD); //操作标志
//		attrMutexInfo.setAttr_mutex_id("00002"); //属性互斥ID
//		attrMutexInfo.setAttr_mutex_desc("test"); //属性互斥描述
//		attrMutexInfo.setMapping_expression("aaa"); //属性匹配表达式
//		attrMutexInfo.setShow_error_info("error"); //错误异常展示信息
//
//		ApAttrCtlInfoWithInd attrCtlInfo = BizUtil.getInstance(ApAttrCtlInfoWithInd.class);
//		attrCtlInfo.setOperater_ind(E_DATAOPERATE.ADD); //操作标志
//		attrCtlInfo.setAttr_ctrl_id("00003"); //属性控制ID
//		attrCtlInfo.setAttr_ctrl_desc("test"); //属性控制描述
//		attrCtlInfo.setMapping_expression("121"); //属性匹配表达式
//		attrCtlInfo.setAttr_ctrl_run_cond("111"); //属性控制运行条件
//		attrCtlInfo.setShow_error_info("error"); //错误异常展示信息
//
//		Options<ApAttrInfoWithInd> attrList = new DefaultOptions<ApAttrInfoWithInd>();
//		Options<ApAttrMutexInfoWithInd> attrMutexList = new DefaultOptions<ApAttrMutexInfoWithInd>();
//		Options<ApAttrCtlInfoWithInd> attrCtlList = new DefaultOptions<ApAttrCtlInfoWithInd>();
//
//		attrList.add(attrInfo);
//		attrMutexList.add(attrMutexInfo);
//		attrCtlList.add(attrCtlInfo);
//
//		requestDataArea.getInput().put("list01", attrList);
//		requestDataArea.getInput().put("list02", attrMutexList);
//		requestDataArea.getInput().put("list03", attrCtlList);
//
//		DataArea res = super.executeTran(requestDataArea);
//		super.isTranSuccess(res);
//	}
//
//	/*
//	 * 修改
//	 */
//	@Test
//	public void testAp1091_2() {
//		/** 数据准备 **/
//		app_attribute attr = BizUtil.getInstance(app_attribute.class);
//		attr.setAttr_level(EnumType.E_OWNERLEVEL.CARD);
//		attr.setAttr_position((long) 2); //属性位置
//		attr.setAttr_desc("Test"); //属性位描述
//		attr.setAttr_expiry_ind(E_YESORNO.NO); //属性位到期标志
//		attr.setRef_drop_list("test"); //引用下拉字典
//		attr.setDefault_value("1"); //缺省值
//		App_attributeDao.insert(attr);
//
//		app_attribute_mutex attrMutex = BizUtil.getInstance(app_attribute_mutex.class);
//		attrMutex.setAttr_level(EnumType.E_OWNERLEVEL.CARD);
//		attrMutex.setAttr_mutex_id("00002"); //属性互斥ID
//		attrMutex.setAttr_mutex_desc("test"); //属性互斥描述
//		attrMutex.setMapping_expression("aaa"); //属性匹配表达式
//		attrMutex.setShow_error_info("error"); //错误异常展示信息
//		App_attribute_mutexDao.insert(attrMutex);
//
//		//		app_attribute_control attrCtl = BizUtil.getInstance(app_attribute_control.class);
//		//		attrCtl.setAttr_level(EnumType.E_OWNERLEVEL.CARD);
//		//		attrCtl.setAttr_ctrl_id("00003");  //属性控制ID
//		//		attrCtl.setAttr_ctrl_desc("test");  //属性控制描述
//		//		attrCtl.setMapping_expression("121");  //属性匹配表达式
//		//		attrCtl.setAttr_ctrl_run_cond("111");  //属性控制运行条件
//		//		attrCtl.setShow_error_info("error");  //错误异常展示信息
//		//		App_attribute_controlDao.insert(attrCtl);
//		/** 数组准备 end **/
//
//		DataArea requestDataArea = newTranReq("1091");
//		// 构造公共请求
//		requestDataArea.getCommReq().putAll(genCommReq(String.valueOf(System.currentTimeMillis())));
//
//		requestDataArea.getInput().put("attr_level", "CARD");
//
//		ApAttrInfoWithInd attrInfo = BizUtil.getInstance(ApAttrInfoWithInd.class);
//		attrInfo.setOperater_ind(E_DATAOPERATE.MODIFY); //操作标志
//		attrInfo.setAttr_position((long) 2); //属性位置
//		attrInfo.setAttr_desc("Test11"); //属性位描述
//		attrInfo.setAttr_expiry_ind(E_YESORNO.NO); //属性位到期标志
//		attrInfo.setRef_drop_list("test111"); //引用下拉字典
//		attrInfo.setDefault_value("1"); //缺省值
//		attrInfo.setData_version((long) 1);
//
//		ApAttrMutexInfoWithInd attrMutexInfo = BizUtil.getInstance(ApAttrMutexInfoWithInd.class);
//		attrMutexInfo.setOperater_ind(E_DATAOPERATE.MODIFY); //操作标志
//		attrMutexInfo.setAttr_mutex_id("00002"); //属性互斥ID
//		attrMutexInfo.setAttr_mutex_desc("test222"); //属性互斥描述
//		attrMutexInfo.setMapping_expression("aaa33"); //属性匹配表达式
//		attrMutexInfo.setShow_error_info("error"); //错误异常展示信息
//		attrMutexInfo.setData_version((long) 1);
//
//		ApAttrCtlInfoWithInd attrCtlInfo = BizUtil.getInstance(ApAttrCtlInfoWithInd.class);
//		attrCtlInfo.setOperater_ind(E_DATAOPERATE.MODIFY); //操作标志
//		attrCtlInfo.setAttr_ctrl_id("00003"); //属性控制ID
//		attrCtlInfo.setAttr_ctrl_desc("test444"); //属性控制描述
//		attrCtlInfo.setMapping_expression("12144"); //属性匹配表达式
//		attrCtlInfo.setAttr_ctrl_run_cond("11144"); //属性控制运行条件
//		attrCtlInfo.setShow_error_info("error"); //错误异常展示信息
//		attrCtlInfo.setData_version((long) 1);
//
//		Options<ApAttrInfoWithInd> attrList = new DefaultOptions<ApAttrInfoWithInd>();
//		Options<ApAttrMutexInfoWithInd> attrMutexList = new DefaultOptions<ApAttrMutexInfoWithInd>();
//		Options<ApAttrCtlInfoWithInd> attrCtlList = new DefaultOptions<ApAttrCtlInfoWithInd>();
//
//		attrList.add(attrInfo);
//		attrMutexList.add(attrMutexInfo);
//		attrCtlList.add(attrCtlInfo);
//
//		requestDataArea.getInput().put("list01", attrList);
//		requestDataArea.getInput().put("list02", attrMutexList);
//		requestDataArea.getInput().put("list03", attrCtlList);
//
//		DataArea res = super.executeTran(requestDataArea);
//		super.isTranSuccess(res);
//
//	}
//
//	private Map<String, Object> genCommReq(String trxnSeq) {
//		Map<String, Object> m = new HashMap<String, Object>();
//		m.put("trxn_seq", trxnSeq);
//		m.put("busi_teller_id", "t1");
//		m.put("busi_seq", trxnSeq);
//		m.put("busi_org_id", "99");
//		m.put("initiator_system", "LOCAL");
//		m.put("channel_id", "102");
//		m.put("sponsor_system", "COUNTER");
//		m.put("jiaoyigy", "交易柜员");
//		m.put("branch_id", "99");
//		return m;
//	}
//}
