package cn.sunline.icore.ap.rule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sunline.clwj.msap.core.util.MsBuffer;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_OPERATOR;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.ApRuleApi.ApRuleDataRetProcessor;
import cn.sunline.icore.ap.parm.ApBaseDropList;
import cn.sunline.icore.ap.tables.TabApRule.App_ruleDao;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_dataDao;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_interfaceDao;
import cn.sunline.icore.ap.tables.TabApRule.App_scene_authDao;
import cn.sunline.icore.ap.tables.TabApRule.App_trxn_controlDao;
import cn.sunline.icore.ap.tables.TabApRule.app_rule;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_data;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_interface;
import cn.sunline.icore.ap.tables.TabApRule.app_scene_auth;
import cn.sunline.icore.ap.tables.TabApRule.app_trxn_control;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleMappingDetail;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.ap.util.DBUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.AuthReason;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.parm.TrxEnvs.WarningInfo;
import cn.sunline.icore.sys.type.EnumType.E_AUTHREASONTYPE;
import cn.sunline.icore.sys.type.EnumType.E_RULEEXPTYPE;
import cn.sunline.icore.sys.type.EnumType.E_TRXNCTRLTYPE;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.exception.LttsBusinessException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.frw.model.tran.error.global.ErrorConf.ErrorType;

/**
 * <p>
 * 文件功能说明： 规则引擎
 * </p>
 * 
 * @Author lid
 *         <p>
 *         <li>2016年12月12日-上午11:51:55</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 lid：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseRule {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseRule.class);

	// 参数模板正则表达式
	private static final String paramPattern = "[$]\\{([A-Za-z0-9_\\.]+)\\}";
	// 分割符
	private static final String split = "\\.";
	
	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月12日-上午11:56:54</li>
	 *         <li>功能说明：判断规则是否成立</li>
	 *         <li>如果数据集不是公共运行变量，需要调用addDataToBuffer方法添加数据</li>
	 *         <li>1):如果存在例外规则，则一个一个校验，如果有一个成立，则返回false<br/>
	 *         2):如果没有例外规则，或者例外规则都不成立，则校验输入的规则，校验成功返回true,失败false<br/>
	 *         3):一个规则可以有多个组，一个组可以有多个序号，组之间是'或'的关系，序号之间是'与'的关系<br/>
	 *         一个组内所有的序号都true，则该组才为true<br/>
	 *         只要有一个组是true，则直接返回true<br/>
	 *         </li>
	 *         </p>
	 * @param ruleId
	 *            规则代码
	 * @param processor
	 * 			  规则条件匹配结果处理器，如每条规则条件匹配结果详情的记录处理等。
	 * 			             
	 * @return
	 */
	public static boolean mapping(String ruleId, ApRuleDataRetProcessor processor) {
		bizlog.method(" ApRule.mapping begin >>>>>>>>>>>>>>>>");
		bizlog.debug("rule id is [%s]", ruleId);

		// 先查看规则代码是否定义
		app_rule ruleInfo = App_ruleDao.selectOne_odb2(ruleId, false);

		if (ruleInfo == null) {
			// throw 规则代码[ruleId]没有定义
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_rule.class).getLongname(), SysDict.A.rule_id.getLongName(), ruleId);
		}
		
		BufferedDataVisitor dataVisitor = new BufferedDataVisitor(ruleInfo.getRule_scene_code());

		// 先判断是否有例外id
		String exceptionRuleId = ruleInfo.getException_rule_id();
		if (CommUtil.isNotNull(exceptionRuleId) && checkRule(exceptionRuleId, processor, dataVisitor)) {
			// 例外规则成立，则返回失败
			return false;
		}

		// 检查规则
		boolean isSuccess = checkRule(ruleId, processor, dataVisitor);
		bizlog.debug("mapping result [%s]", isSuccess);
		bizlog.method(" ApRule.mapping end <<<<<<<<<<<<<<<<");

		return isSuccess;
	}
	
	public static boolean mapping(String ruleId) {
		return mapping(ruleId, bizlog.isDebugEnabled() ? DefaultRuleDataRetProcessor.getInstance() : null);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月12日-下午1:09:36</li>
	 *         <li>功能说明：按规则场景代码扫描并返回首个命中回执，无命中返回null</li>
	 *         </p>
	 * @param sceneType
	 * @return
	 */
	public static String getFirstResultByScene(String sceneCode) {
		return getFirstResultByScene(sceneCode, bizlog.isDebugEnabled() ? DefaultRuleDataRetProcessor.getInstance() : null);
	}
	
	public static String getFirstResultByScene(String sceneCode, ApRuleDataRetProcessor processor) {
		List<String> rule = getAllResult(sceneCode, processor, true);
		return (!rule.isEmpty()) ? rule.get(0) : null;
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月12日-下午1:10:38</li>
	 *         <li>功能说明：按规则场景类型扫描并返回全部命中回执，无命中返回空列表</li>
	 *         </p>
	 * @param sceneType
	 * @return
	 */
	public static List<String> getAllResultByScene(String sceneCode) {
		return getAllResultByScene(sceneCode, bizlog.isDebugEnabled() ? DefaultRuleDataRetProcessor.getInstance() : null);
	}
	
	public static List<String> getAllResultByScene(String sceneCode, ApRuleDataRetProcessor processor) {
		return getAllResult(sceneCode, processor, false);
	}

	/**
	 * @Author shenxy
	 *         <p>
	 *         <li>2017年9月22日-下午3:50:02</li>
	 *         <li>功能说明：交易控制检查</li>
	 *         </p>
	 * @param eventId
	 *            交易事件ID
	 */
	public static void checkTrxnControl(String eventId) {

		checkError(eventId);

		checkWarning(eventId);

		checkSceneAuthor(eventId);
	}

	/**
	 * 场景授权检查
	 */
	private static void checkSceneAuthor(String eventId) {
		bizlog.method(" ApRule.checkSceneAuthor begin >>>>>>>>>>>>>>>>");

		// 获取公共运行区
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		if (runEnvs.getAuth_ind() == E_YESORNO.YES)
			return;

		// 查询数据
		List<app_trxn_control> trxnCtrlList = App_trxn_controlDao.selectAll_odb3(eventId, E_TRXNCTRLTYPE.SCENE_AUTHOR, false);

		Options<AuthReason> listReason = runEnvs.getAuth_reason();

		// 不为空进行交易控制检查
		for (app_trxn_control trxnCtrl : trxnCtrlList) {

			if (mapping(trxnCtrl.getTrxn_ctrl_run_cond())) {

				// 判断场景授权码是否存在
				ApBaseDropList.exists(ApConst.SCENE_AUTH_ID, trxnCtrl.getScene_auth_id(), true);

				// 实例化授权原因
				AuthReason authReason = BizUtil.getInstance(AuthReason.class);

				authReason.setAuth_reason_type(E_AUTHREASONTYPE.SENCE);
				authReason.setAuth_reason_id(trxnCtrl.getScene_auth_id());
				authReason.setAuth_reason_desc(getHintText(trxnCtrl));

				// 添加角色id
				List<app_scene_auth> sceneAuthList = App_scene_authDao.selectAll_odb2(trxnCtrl.getScene_auth_id(), false);

				String temp = "";
				for (app_scene_auth sceneAuth : sceneAuthList) {
					temp = temp.concat(sceneAuth.getRole_id()).concat(",");
				}

				authReason.setAuth_role_list(temp);

				listReason.add(authReason);
			}
		}

		runEnvs.setAuth_reason(listReason);

		bizlog.method(" ApRule.checkSceneAuthor end <<<<<<<<<<<<<<<<");

	}

	/**
	 * 检查并收集错误，中断交易返回给调用方
	 */
	private static void checkWarning(String eventId) {
		bizlog.method(" ApRule.checkWarning begin >>>>>>>>>>>>>>>>");

		// 获取公共运行区
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		if (runEnvs.getWarning_confirm_ind() == E_YESORNO.YES)
			return;

		// 获取交易控制数据
		List<app_trxn_control> trxnCtrlList = App_trxn_controlDao.selectAll_odb3(eventId, E_TRXNCTRLTYPE.WARNING, false);

		// 不为空进行交易控制检查
		for (app_trxn_control trxnCtrl : trxnCtrlList) {

			// app_rule做判断，为空或判断成功抛出异常
			if (mapping(trxnCtrl.getTrxn_ctrl_run_cond())) {

				// 获得提示文本
				String hintText = getHintText(trxnCtrl);
				
				// 判断是否有相同客户号的数据
				for(WarningInfo warningInfo : runEnvs.getWarning_info()){
					
					if(CommUtil.equals(warningInfo.getShow_error_info(), hintText))
						
						return;
				}
				
				// 初始化警告信息
				WarningInfo warningInfo = BizUtil.getInstance(WarningInfo.class);

				warningInfo.setTrxn_event_id(trxnCtrl.getTrxn_event_id());// 交易事件id

				warningInfo.setTrxn_ctrl_desc(trxnCtrl.getTrxn_ctrl_desc());// 交易控制描述

				warningInfo.setShow_error_info(hintText);// 错误异常展示信息

				runEnvs.getWarning_info().add(warningInfo);

			}

		}

		bizlog.method(" ApRule.checkWarning end <<<<<<<<<<<<<<<<");
	}

	/**
	 * 获得提示文本
	 */
	private static String getHintText(app_trxn_control trxnCtrl) {
		bizlog.method(" ApRule.getHintText begin >>>>>>>>>>>>>>>>");

		String hintText = null;

		// 优先去数据集取值

		if (CommUtil.isNotNull(trxnCtrl.getHint_data_mart()) && CommUtil.isNotNull(trxnCtrl.getHint_field_name())) {

			try {

				hintText = ApBaseBuffer.getFieldValue(trxnCtrl.getHint_data_mart(), trxnCtrl.getHint_field_name());
			}
			catch (LttsBusinessException e) {
				bizlog.info("errorCode:[%s],errorMessage:[%s]", e, e.getCode(), e.getMessage());
			
			}

		}

		if (CommUtil.isNull(hintText)) {

			hintText = trxnCtrl.getHint_text();
		}

		// 获得处理后的文本
		String text = paramTemplate(MsBuffer.getBuffer(), hintText, null);

		bizlog.method(" ApRule.getHintText end <<<<<<<<<<<<<<<<");

		return text;
	}

	/**
	 * 检查若遇到一个立即抛出异常
	 */
	private static void checkError(String eventId) {
		bizlog.method(" ApRule.checkError begin >>>>>>>>>>>>>>>>");

		// 获取交易控制数据
		List<app_trxn_control> trxnCtrlList = App_trxn_controlDao.selectAll_odb3(eventId, E_TRXNCTRLTYPE.ERROR, false);

		// 不为空进行交易控制检查
		for (app_trxn_control trxnCtrl : trxnCtrlList) {

			// app_rule做判断，为空或判断成功抛出异常
			if (mapping(trxnCtrl.getTrxn_ctrl_run_cond())) {

				String hintText = getHintText(trxnCtrl);

				if (CommUtil.isNotNull(trxnCtrl.getReturn_code())) {
					throw new LttsBusinessException(ErrorType.error, trxnCtrl.getReturn_code(), hintText);
				} 
				else {
					throw ApBaseErr.ApBase.E0068(hintText);
				}
			}

		}

		bizlog.method(" ApRule.checkError end <<<<<<<<<<<<<<<<");

	}

	/**
	 * @Author shenxy
	 *         <p>
	 *         <li>2017年9月29日-下午2:27:47</li>
	 *         <li>功能说明：弹出告警</li>
	 *         </p>
	 */
	public static void popupWarning() {

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		if (runEnvs.getWarning_confirm_ind() == E_YESORNO.YES)
			return;

		if (!runEnvs.getWarning_info().isEmpty()) {

			throw ApBaseErr.ApBase.E0059();
		}
	}

	/**
	 * @Author shenxy
	 *         <p>
	 *         <li>2017年10月26日-上午9:45:41</li>
	 *         <li>功能说明：弹出场景授权窗口</li>
	 *         </p>
	 */
	public static void popupSceneAuth() {
		bizlog.method(" ApRule.popupSceneAuth begin >>>>>>>>>>>>>>>>");

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		if (runEnvs.getAuth_ind() == E_YESORNO.YES)
			return;

		Options<AuthReason> authReasonList = runEnvs.getAuth_reason();
		if (!authReasonList.isEmpty()) {

			for (AuthReason authReason : authReasonList) {
				if (CommUtil.isNull(authReason.getAuth_role_list())) {

					throw ApBaseErr.ApBase.E0063();
				}
			}
/**			授权检查，需要授权则此交易回滚
			xiang
			2019年9月24日
*/
			DBUtil.rollBack();
			throw ApBaseErr.ApBase.E0062();
		}

		bizlog.method(" ApRule.popupSceneAuth end <<<<<<<<<<<<<<<<");
	}

	/**
	 * 通用获取result
	 * 
	 * @param sceneType
	 *            场景类型
	 * @param sceneCode
	 *            场景代码
	 * @param type
	 *            是否根据场景类型
	 * @param firstFlag
	 *            是否只获取第一行数据
	 */
	private static List<String> getAllResult(String sceneCode, ApRuleDataRetProcessor processor, boolean firstFlag) {
		List<String> rulesResult = new ArrayList<String>();// 规则代码结果集

		List<app_rule> rulesList = App_ruleDao.selectAll_odb1(sceneCode, false);

		BizUtil.listSort(rulesList, true, SysDict.A.mapping_sort.getId());
		
		for (app_rule rules : rulesList) {
			if (CommUtil.compare(rules.getEffect_date(), BizUtil.getTrxRunEnvs().getTrxn_date()) > 0 
					|| CommUtil.compare(rules.getExpiry_date(), BizUtil.getTrxRunEnvs().getTrxn_date()) < 0) {
				bizlog.debug("Rule[%s] of scene[%s] is not effective.", rules.getRule_id(), sceneCode);
				continue;
			}
			
			if (mapping(rules.getRule_id(), processor)) {
				rulesResult.add(rules.getMapping_result());
			}
			else {
				continue;
			}

			if (firstFlag)
				break;
		}

		return rulesResult;
	}

	/**
	 * 内部校验单个规则id,不含例外的控制
	 */
	public static boolean checkRule(String ruleId, ApRuleDataRetProcessor processor, BufferedDataVisitor dataVisitor) {
		// 规则数据定义，数据已按组和序号排序
		List<app_rule_data> ruleData = App_rule_dataDao.selectAll_odb1(ruleId, false);
		bizlog.debug("rule data size of rule[%s] is %s", ruleId, ruleData.size());
		
		BizUtil.listSort(ruleData, true, SysDict.A.rule_group_no.getId(), SysDict.A.rule_sort.getId());
		
		// Map<String, Object> ruleBuffer = ApBuffer.getBuffer();// 规则缓冲区
		String groupId = "";// 初始组
		boolean isSuccess = true;
		for (app_rule_data rule : ruleData) {
			String tmpGroupId = String.valueOf(rule.getRule_group_no());
			if ("".equals(groupId)) {
				groupId = tmpGroupId;
			}
			else {
				if (!groupId.equals(tmpGroupId) && isSuccess)// 如果不是同一个组，但是上个组成功，则跳出
					break;

				if (groupId.equals(tmpGroupId) && !isSuccess)// 如果是同一个组，但是组内已经有一个校验失败，则跳过
					continue;

				if (!groupId.equals(tmpGroupId))// 如果不是同一个组，但是上个组失败，则继续
					groupId = tmpGroupId;
			}

			bizlog.debug("group id [%s]  rule sort [%s]", groupId, rule.getRule_sort());
			isSuccess = checkRuleSingle(rule, processor, dataVisitor);
		}

		return isSuccess;
	}
	
	/**
	 * 单条记录的检查
	 * @param processor 
	 */
	private static boolean checkRuleSingle(app_rule_data rule, ApRuleDataRetProcessor processor, BufferedDataVisitor dataVisitor) {
		// String dataMart = rule.getData_mart();

		Object value;
		if (rule.getRule_expr_type() == E_RULEEXPTYPE.COMMON) {
			value = dataVisitor.getFieldValue(rule.getField_name());
		}
		else {
			value = ApRuleFunc.eval(rule, dataVisitor);
			value = value == null? "" : value;
		}
		
		Object mappingValue = paramTemplate(rule.getData_mapping_value(), dataVisitor);

		return check(value, mappingValue, rule, processor);
	}
	
	/**
	 * 
	 *         <p>
	 *         <li>2018年2月26日-上午10:50:57</li>
	 *         <li>功能说明： 参数按照模板替换 ${column_name}</li>
	 *         </p>
	 * @param value
	 * @param dataVisitor
	 * @return
	 */
	protected static String paramTemplate(String value, BufferedDataVisitor dataVisitor) {
		if (value == null || value.length() == 0)
			return value;

		Pattern pat = Pattern.compile(paramPattern);
		Matcher mather = pat.matcher(value);

		String ret = value;
		StringBuilder sb = new StringBuilder(value);

		int offset = 0;
		
		while (mather.find()) {
			String[] keys = mather.group(1).split(split);
			
			String v;
			if (keys.length > 1) {
				v = dataVisitor.getFieldValue(keys[0], keys[1]).toString();
			}
			else {
				v = dataVisitor.getFieldValue(keys[0]).toString();
			}

			ret = sb.replace(mather.start() - offset, mather.end() - offset, v).toString();
			
			offset = mather.group().length() - v.length()+offset;
		}

		return ret;
	}

	/**
	 * 参数按照模板替换 ${RUN_ENV.trx_date}、${INPUT.acct_no}或 ${trx_date}取默认的dataMart
	 * 
	 * @param ruleBuffer
	 * @param data_mapping_value
	 * @param dataMart
	 * @return
	 */
	public static String paramTemplate(Map<String, Object> ruleBuffer, String value, String defaultDataMart) {
		if (value == null || value.length() == 0)
			return value;

		Pattern pat = Pattern.compile(paramPattern);
		Matcher mather = pat.matcher(value);

		String ret = value;
		StringBuilder sb = new StringBuilder(value);

		int offset = 0;
		
		while (mather.find()) {
			String[] keys = mather.group(1).split(split);
			String dataMart = keys.length > 1 ? keys[0] : defaultDataMart;
			String key = keys.length > 1 ? keys[1] : keys[0];

			String v = getValueByBuffer(ruleBuffer, dataMart, key).toString();

			ret = sb.replace(mather.start()-offset, mather.end()-offset, v).toString();
			
			offset = mather.group().length() - v.length()+offset;
		}

		return ret;
	}

	/*
	 * 从buffer区取值
	 */
	private static Object getValueByBuffer(Map<String, Object> ruleBuffer, String dataMart, String key) {
		return getValueByBuffer(ruleBuffer, dataMart, key, false);
	}
	
	@SuppressWarnings("unchecked")
	private static Object getValueByBuffer(Map<String, Object> ruleBuffer, String dataMart, String key, boolean nullable) {
		Object ret = null;

		if (ApConst.RUN_ENVS.equals(dataMart)) {
			ret = BizUtil.getTrxRunEnvsValue(key);
		}
		else {
			Object dataObj = ruleBuffer.get(dataMart);// 数据集			
			if (dataObj != null && dataObj instanceof Map) {
				Map<String, Object> data = (Map<String, Object>) dataObj;
				ret = data.get(key);
			}
		}

		return ret == null ? (nullable ? null : "") : ret;
	}

	/**
	 * 校验控制
	 * @param processor 
	 */
	private static boolean check(Object value, Object mappingValue, app_rule_data rule, ApRuleDataRetProcessor processor) {
		Logic logicOperator = Logic.getLogicOperator(rule.getData_mapping_operator());
		boolean isSuccess = logicOperator.check(value, mappingValue);
		
		if (CommUtil.isNotNull(processor)) {
			ApRuleMappingDetail mappingDetail = BizUtil.getInstance(ApRuleMappingDetail.class);
			mappingDetail.setRule_id(rule.getRule_id());  //rule id
			mappingDetail.setRule_group_no(rule.getRule_group_no());  //rule group no
			mappingDetail.setRule_sort(rule.getRule_sort());  //rule sort
			mappingDetail.setData_mapping_operator(rule.getData_mapping_operator());  //data mapping operator
			mappingDetail.setChecking_value(value == null ? "" : value.toString());
			mappingDetail.setData_mapping_value(mappingValue == null ? "" : mappingValue.toString());
			mappingDetail.setSucc_mapping_flag(isSuccess? E_YESORNO.YES : E_YESORNO.NO);
			mappingDetail.setField_name(rule.getField_name());
		
			processor.process(mappingDetail);
		}
		
		return isSuccess;
	}


	/**
	 * equal校验方法
	 */
	private static boolean checkEq(Object value, Object mappingValue) {
		BigDecimal v, mapV;
		try {
			v = new BigDecimal(value.toString());
			mapV = new BigDecimal(mappingValue.toString());
		}
		catch (NumberFormatException e) {// 如果其中有一个不能转成bigDecimal，则判断对应的字符串
			return (CommUtil.compare(value.toString(), mappingValue.toString()) == 0) ? true : false;
		}

		return (CommUtil.compare(v, mapV) == 0) ? true : false;
	}

	/**
	 * not equal校验
	 */
	private static boolean checkNe(Object value, Object mappingValue) {
		return checkEq(value, mappingValue) ? false : true;
	}

	/**
	 * in校验
	 */
	private static boolean checkIn(Object value, Object mappingValue) {
		if ("".equals(value.toString())) // in校验，如果是空字符串，直接返回失败
			return false;

		int i = mappingValue.toString().indexOf(value.toString());
		return (i != -1) ? true : false;
	}

	/**
	 * not in校验
	 */
	private static boolean checkNi(Object value, Object mappingValue) {
		return checkIn(value, mappingValue) ? false : true;
	}

	/**
	 * greater-than校验
	 */
	private static boolean checkGt(Object value, Object mappingValue) {
		Object newValue = "".equals(value.toString()) ? 0 : value; // 初始化0处理

		BigDecimal v, mapV;
		try {
			v = new BigDecimal(newValue.toString());
			mapV = new BigDecimal(mappingValue.toString());
		}
		catch (NumberFormatException e) {
			return false;// 转换错误，返回false
		}
		return (CommUtil.compare(v, mapV) == 1) ? true : false;
	}

	/**
	 * less-than校验
	 */
	private static boolean checkLt(Object value, Object mappingValue) {
		Object newValue = "".equals(value.toString()) ? 0 : value; // 初始化0处理

		BigDecimal v, mapV;
		try {
			v = new BigDecimal(newValue.toString());
			mapV = new BigDecimal(mappingValue.toString());
		}
		catch (NumberFormatException e) {
			return false;// 转换错误，返回false
		}
		return (CommUtil.compare(v, mapV) == -1) ? true : false;
	}

	/**
	 * less-than校验
	 */
	private static boolean checkGteq(Object value, Object mappingValue) {
		return checkLt(value, mappingValue) ? false : true;
	}

	/**
	 * less-then or equal校验
	 */
	private static boolean checkLteq(Object value, Object mappingValue) {
		return checkGt(value, mappingValue) ? false : true;
	}

	/**
	 * regular-expression校验
	 */
	private static boolean checkExp(Object value, Object mappingValue) {
		return value.toString().matches(mappingValue.toString());
	}

	/**
	 * 
	 */
	private static boolean checkContain(Object value, Object mappingValue) {
		return checkIn(mappingValue, value);
	}

	/**
	 * 
	 */
	private static boolean checkNotContain(Object value, Object mappingValue) {
		return !checkContain(value, mappingValue);
	}
	
	private static class DefaultRuleDataRetProcessor implements ApRuleDataRetProcessor {
		private static final DefaultRuleDataRetProcessor inst = new DefaultRuleDataRetProcessor();
		
		public static ApRuleDataRetProcessor getInstance() {
			return inst;
		}
		
		@Override
		public void process(ApRuleMappingDetail mappingDetail) {
			bizlog.debug("check operator is [%s]", mappingDetail.getData_mapping_operator());
			bizlog.debug("check value    is [%s]", mappingDetail.getChecking_value());
			bizlog.debug("mappingValue   is [%s]", mappingDetail.getData_mapping_value());
			bizlog.debug("check result   is [%s]", mappingDetail.getSucc_mapping_flag());
		}
	}
	
	public static class BufferedDataVisitor {
		private String ruleScene;
		
		protected BufferedDataVisitor(String ruleScene) {
			this.ruleScene = ruleScene;
		}
		
		protected Object getFieldValue(String columnName) {
			return getFieldValue(columnName, false);
		}
		
		protected Object getFieldValue(String dataMart, String columnName) {
			return getFieldValue(dataMart, columnName, false);
		}
		
		protected Object getFieldValue(String dataMart, String columnName, boolean nullable) {
			return getValueByBuffer(MsBuffer.getBuffer(), dataMart, columnName, nullable);
		}
		
		protected Object getFieldValue(String columnName, boolean nullable) {
			app_rule_interface ruleFace = App_rule_interfaceDao.selectOne_odb1(ruleScene, columnName, false);
			if (ruleFace == null) {
				throw ApPubErr.APPUB.E0024(OdbFactory.getTable(app_rule_interface.class).getLongname(), 
						SysDict.A.rule_scene_code.getLongName(), ruleScene, SysDict.A.field_name.getLongName(), columnName);
			}
			
			return getValueByBuffer(MsBuffer.getBuffer(), ruleFace.getData_mart(), ruleFace.getColumn_name(), nullable);
		}
		
	}
	
	/**
	 * 判断规则ID是否存在
	 * 
	 * @param ruleId
	 *            规则ID
	 * @return true-存在 false-不存在
	 */
	public static boolean existsRule(String ruleId) {

		app_rule ruleInfo = App_ruleDao.selectOne_odb2(ruleId, false);

		return ruleInfo == null ? false : true;
	}
	
	private static enum Logic {
		EQUAL {
			@Override
			protected boolean check(Object value, Object mappingValue) {
				if (value == null || mappingValue == null) {
					return value == null && mappingValue == null;
				}
				else {
					return _check(value, mappingValue);
				}
			}
			
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkEq(value, mappingValue);
			}
			
		},NO_EQUAL {
			boolean check(Object value, Object mappingValue) {
				if (value == null || mappingValue == null) {
					return !(value == null && mappingValue == null);
				}
				else {
					return _check(value, mappingValue);
				}
			}
			
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkNe(value, mappingValue);
			}
			
		},IN {
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkIn(value, mappingValue);
			}
		},NO_IN {
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkNi(value, mappingValue);
			}
		},GREATER_THAN {
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkGt(value, mappingValue);
			}
		},LESS_THAN {
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkLt(value, mappingValue);
			}
		},GREATER_THAN_OR_EQUAL {
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkGteq(value, mappingValue);
			}
		},LESS_THAN_OR_EQUAL {
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkLteq(value, mappingValue);
			}
		},REQULAR_EXPRESSION {
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkExp(value, mappingValue);
			}
		},CONTAIN {
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkContain(value, mappingValue);
			}
		},NOT_CONTAIN {
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return checkNotContain(value, mappingValue);
			}
		},NONE { // default empty logic
			@Override
			protected boolean _check(Object value, Object mappingValue) {
				return false;
			}
		};
		
		protected abstract boolean _check(Object value, Object mappingValue);
		
		boolean check(Object value, Object mappingValue) {
			if (value == null || mappingValue == null) {
				return false;
			}
			else {
				return _check(value, mappingValue);
			}
		}
		
		static Logic getLogicOperator(E_OPERATOR orgOperator) {
			try {
				return Logic.valueOf(orgOperator.getId());
			}
			catch (Exception e) {
				return Logic.NONE;
			}
		}
	}
	
}
