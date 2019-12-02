package cn.sunline.icore.ap.rule.mnt;

import java.util.List;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.icore.ap.api.ApDataAuditApi;
import cn.sunline.icore.ap.api.ApDropListApi;
import cn.sunline.icore.ap.api.ApRuleApi;
import cn.sunline.icore.ap.api.ApSeqApi;
import cn.sunline.icore.ap.namedsql.ApRuleDao;
import cn.sunline.icore.ap.tables.TabApRule.App_ruleDao;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_dataDao;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_funcDao;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_func_parmDao;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_interfaceDao;
import cn.sunline.icore.ap.tables.TabApRule.app_rule;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_data;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_func;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_func_parm;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_interface;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleDataInfo;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleDataResult;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleDataWithInd;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleFunc;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleFuncInfo;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleInfo;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleSceneWithInd;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleSimple;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleSimpleWithInd;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_RULEEXPTYPE;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

public class ApRuleMnt {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApRuleApi.class);

	private static final String exceptionRule = "_E";
	private static final String simpleDropList = "RULE_SIMPLE";
	private static final String sceneDropList = "RULE_SCENE";
	private static final String effect_date = "19990101";
	private static final String expiry_date = "20991231";

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月17日-下午3:41:38</li>
	 *         <li>功能说明：简单规则信息维护</li>
	 *         </p>
	 * @param operater_ind
	 * @param ruleSimple
	 * @param ruleData
	 * @param exceptionRuleData
	 * @param secondExceptionData
	 */
	public static void modifyRuleSimple(ApRuleSimpleWithInd ruleSimple, Options<ApRuleDataWithInd> ruleData, Options<ApRuleDataWithInd> exceptionRuleData,
			Options<ApRuleFuncInfo> funcParam) {
		bizlog.method(" ApRuleMnt.modifyRuleSimple begin >>>>>>>>>>>>>>>>");
		bizlog.method(" ruleSimple>>>>>>>>>>>>>>>>" + ruleSimple + "ruleData>>>>>>>>>" + ruleData + "exceptionRuleData>>>>>>>>>" + exceptionRuleData + "funcParam>>>>>>>>>"
				+ funcParam);
		// 必输项检查
		checkRuleSimple(ruleSimple);
		// 下拉字典校验
		ApDropListApi.exists(simpleDropList, ruleSimple.getRule_scene_code(), true);
		String ruleId = ruleSimple.getRule_id();

		if (ruleSimple.getOperater_ind() == E_DATAOPERATE.ADD) {
			//新增规则信息表
			addRule(ruleSimple);
			//新增基本规则信息
			for (ApRuleDataWithInd data : ruleData) {
				checkRuleData(ruleSimple.getRule_scene_code(), data, funcParam);
				addRuleDataSingle(ruleId, data, funcParam);
			}
			//新增例外规则信息
			for (ApRuleDataWithInd data : exceptionRuleData) {
				checkRuleData(ruleSimple.getRule_scene_code(), data, funcParam);
				addRuleDataSingle(ruleId + exceptionRule, data, funcParam);
				ruleSimple.setException_rule_id(ruleId + exceptionRule);
			}

		}
		else if (ruleSimple.getOperater_ind() == E_DATAOPERATE.MODIFY) {
			//先删除明细
			clearRuleDatas(ruleId, ruleData, exceptionRuleData);

			//插入明细
			for (ApRuleDataWithInd data : ruleData) {
				checkRuleData(ruleSimple.getRule_scene_code(), data, funcParam);
				addRuleDataSingle(ruleId, data, funcParam);
			}
			for (ApRuleDataWithInd data : exceptionRuleData) {
				checkRuleData(ruleSimple.getRule_scene_code(), data, funcParam);
				addRuleDataSingle(ruleId + exceptionRule, data, funcParam);
			}

			if (exceptionRuleData.size() > 0) {
				ruleSimple.setException_rule_id(ruleId + exceptionRule);
			}
			else {
				ruleSimple.setException_rule_id(null);
			}

			modifyRule(ruleSimple);
		}
		else if (ruleSimple.getOperater_ind() == E_DATAOPERATE.DELETE) {
			//删除明细表			
			clearRuleDatas(ruleId, ruleData, exceptionRuleData);
			//删除主表
			App_ruleDao.deleteOne_odb2(ruleId);

		}

		bizlog.method(" ApRuleMnt.modifyRuleSimple end <<<<<<<<<<<<<<<<");
	}

	//删除规则明细	
	private static void clearRuleDatas(String ruleId, Options<ApRuleDataWithInd> ruleData, Options<ApRuleDataWithInd> exceptionRuleData) {
		List<app_rule_data> ruleDatas = App_rule_dataDao.selectAll_odb1(ruleId, false);
		for (app_rule_data currRule : ruleDatas) {
			App_rule_dataDao.deleteOne_odb2(currRule.getRule_id(), currRule.getRule_group_no(), currRule.getRule_sort());
		}
		List<app_rule_data> ruleExcpDatas = App_rule_dataDao.selectAll_odb1(ruleId + exceptionRule, false);
		for (app_rule_data currRule : ruleExcpDatas) {
			App_rule_dataDao.deleteOne_odb2(currRule.getRule_id(), currRule.getRule_group_no(), currRule.getRule_sort());
		}

	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月17日-下午4:01:41</li>
	 *         <li>功能说明：规则数据查询</li>
	 *         </p>
	 * @param ruleId
	 * @return
	 */
	public static ApRuleDataResult queryRuleDataList(String ruleId) {
		bizlog.method(" ApRuleMnt.queryRuleDataList begin >>>>>>>>>>>>>>>>");

		String orgId = MsOrg.getReferenceOrgId(app_rule_data.class);

		List<ApRuleDataInfo> ruleList = ApRuleDao.selRuleDataList(orgId, ruleId, false);
		List<ApRuleDataInfo> exceptionList = ApRuleDao.selRuleDataList(orgId, ruleId + exceptionRule, false);
		if (ruleList.size() > 0) {
			for (int i = 0; i < ruleList.size(); i++) {
				app_rule_func funcInfo = App_rule_funcDao.selectOne_odb1(ruleList.get(i).getFunc_code(), false);
				if (funcInfo != null) {
					ruleList.get(i).setFunc_name(funcInfo.getFunc_name());
				}
			}
		}
		if (exceptionList.size() > 0) {
			for (int i = 0; i < exceptionList.size(); i++) {
				app_rule_func funcInfo = App_rule_funcDao.selectOne_odb1(exceptionList.get(i).getFunc_code(), false);
				if (funcInfo != null) {
					exceptionList.get(i).setFunc_name(funcInfo.getFunc_name());
				}
			}
		}

		ApRuleDataResult ruleDataResult = BizUtil.getInstance(ApRuleDataResult.class);
		ruleDataResult.setRuleData(new DefaultOptions<ApRuleDataInfo>(ruleList));
		ruleDataResult.setExceptionRule(new DefaultOptions<ApRuleDataInfo>(exceptionList));
		bizlog.method(" ApRuleMnt.queryRuleDataList end <<<<<<<<<<<<<<<<");
		return ruleDataResult;
	}

	public static Options<ApRuleFunc> queryInformation(String func_name) {
		bizlog.method(" ApRuleMnt.queryRuleSimpleList begin >>>>>>>>>>>>>>>>");
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		Page<ApRuleFunc> page = ApRuleDao.selFuncAndParm(func_name, runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(), false);

		runEnvs.setTotal_count(page.getRecordCount());

		Options<ApRuleFunc> ruleList = new DefaultOptions<ApRuleFunc>();
		ruleList.setValues(page.getRecords());
		bizlog.method(" ApRuleMnt.queryRuleSimpleList end <<<<<<<<<<<<<<<<");
		return ruleList;
	}

	public static Options<ApRuleFunc> queryParm(String func_name) {
		bizlog.method(" ApRuleMnt.queryRuleSimpleList begin >>>>>>>>>>>>>>>>");
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		Page<ApRuleFunc> selParm = ApRuleDao.selParm(func_name, runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(), false);
		runEnvs.setTotal_count(selParm.getRecordCount());
		Options<ApRuleFunc> ruleList = new DefaultOptions<ApRuleFunc>();
		ruleList.setValues(selParm.getRecords());
		return ruleList;

	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月17日-下午4:25:43</li>
	 *         <li>功能说明：简单规则查询</li>
	 *         </p>
	 * @param ruleSimple
	 * @return
	 */
	public static Options<ApRuleInfo> queryRuleList(ApRuleSimple ruleSimple) {
		bizlog.method(" ApRuleMnt.queryRuleSimpleList begin >>>>>>>>>>>>>>>>");

		Options<ApRuleInfo> ruleList = queryRuleInfo(ruleSimple);

		bizlog.method(" ApRuleMnt.queryRuleSimpleList end <<<<<<<<<<<<<<<<");
		return ruleList;
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月18日-上午9:50:40</li>
	 *         <li>功能说明：场景规则维护</li>
	 *         </p>
	 * @param rule
	 * @param ruleData
	 * @param exceptionRuleData
	 */
	public static void modifyRuleScene(ApRuleSceneWithInd rule, Options<ApRuleDataWithInd> ruleData, Options<ApRuleDataWithInd> exceptionRuleData, Options<ApRuleFuncInfo> funcParam) {
		bizlog.method(" ApRuleMnt.modifyRuleScene begin >>>>>>>>>>>>>>>>");
		bizlog.method(" rule>>>>>>>>>>>>>>>>" + rule + "ruleData>>>>>>>>>" + ruleData + "exceptionRuleData>>>>>>>>>" + exceptionRuleData + "funcParam>>>>>>>>>" + funcParam);
		// 必输项检查
		checkRule(rule);

		// 下拉字典校验
		ApDropListApi.exists(sceneDropList, rule.getRule_scene_code(), true);

		String ruleId = rule.getRule_id();// 规则id
		if (rule.getOperater_ind() == E_DATAOPERATE.ADD) {
			for (ApRuleDataWithInd data : ruleData) {
				checkRuleData(rule.getRule_scene_code(), data, funcParam);
				addRuleDataSingle(ruleId, data, funcParam);
			}
			for (ApRuleDataWithInd data : exceptionRuleData) {
				checkRuleData(rule.getRule_scene_code(), data, funcParam);
				addRuleDataSingle(ruleId + exceptionRule, data, funcParam);

				rule.setException_rule_id(ruleId + exceptionRule);
			}

			ruleId = addRuleScene(rule);
		}
		else if (rule.getOperater_ind() == E_DATAOPERATE.MODIFY) {
			//先删除明细
			clearRuleDatas(ruleId, ruleData, exceptionRuleData);

			//插入明细
			for (ApRuleDataWithInd data : ruleData) {
				checkRuleData(rule.getRule_scene_code(), data, funcParam);
				addRuleDataSingle(ruleId, data, funcParam);
			}
			for (ApRuleDataWithInd data : exceptionRuleData) {
				checkRuleData(rule.getRule_scene_code(), data, funcParam);
				addRuleDataSingle(ruleId + exceptionRule, data, funcParam);
			}

			if (exceptionRuleData.size() > 0) {
				rule.setException_rule_id(ruleId + exceptionRule);
			}
			else {
				rule.setException_rule_id(null);
			}
			modRuleScene(rule);

		}
		else if (rule.getOperater_ind() == E_DATAOPERATE.DELETE) {
			//删除明细表		
			clearRuleDatas(ruleId, ruleData, exceptionRuleData);
			//删除主表
			deleteRuleScene(rule);
		}
		else {
			return;
		}

		bizlog.method(" ApRuleMnt.modifyRuleScene end <<<<<<<<<<<<<<<<");
	}

	/**
	 * @Author lid 删除场景规则
	 * @param rule
	 */
	private static void deleteRuleScene(ApRuleSceneWithInd rule) {
		bizlog.method(" ApRuleMnt.deleteRuleScene begin >>>>>>>>>>>>>>>>");

		app_rule oldRule = App_ruleDao.selectOne_odb2(rule.getRule_id(), false);
		if (oldRule == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_rule.class).getLongname(), SysDict.A.rule_id.getLongName(), rule.getRule_id());
		}

		if (CommUtil.compare(rule.getData_version().longValue(), oldRule.getData_version().longValue()) != 0) {
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(app_rule.class).getLongname());
		}

		App_ruleDao.deleteOne_odb2(rule.getRule_id());

		// 审计
		ApDataAuditApi.regLogOnDeleteParameter(oldRule);

		bizlog.method(" ApRuleMnt.deleteRuleScene end <<<<<<<<<<<<<<<<");
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月18日-上午9:51:04</li>
	 *         <li>功能说明：场景规则查询</li>
	 *         </p>
	 * @param ruleSenceType
	 * @param module
	 * @return
	 */
	public static Options<ApRuleInfo> queryRuleScene(ApRuleSimple ruleSimple) {
		bizlog.method(" ApRuleMnt.queryRuleScene begin >>>>>>>>>>>>>>>>");

		Options<ApRuleInfo> ruleList = queryRuleInfo(ruleSimple);

		bizlog.method(" ApRuleMnt.queryRuleScene end <<<<<<<<<<<<<<<<");
		return ruleList;
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月18日-上午9:51:04</li>
	 *         <li>功能说明：规则信息查询</li>
	 *         </p>
	 * @param ruleSenceType
	 * @param module
	 * @return
	 */
	private static Options<ApRuleInfo> queryRuleInfo(ApRuleSimple ruleSimple) {
		String orgId = MsOrg.getReferenceOrgId(app_rule.class);
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		Page<ApRuleInfo> page = ApRuleDao.selRuleList(orgId, ruleSimple.getRule_id(), ruleSimple.getRule_desc(), ruleSimple.getRule_scene_code(), runEnvs.getPage_start(),
				runEnvs.getPage_size(), runEnvs.getTotal_count(), false);
		runEnvs.setTotal_count(page.getRecordCount());

		Options<ApRuleInfo> ruleList = new DefaultOptions<ApRuleInfo>();
		ruleList.setValues(page.getRecords());

		return ruleList;
	}

	/*
	 * 简单规则信息必输项检查
	 */
	private static void checkRuleSimple(ApRuleSimpleWithInd ruleSimple) {
		BizUtil.fieldNotNull(ruleSimple.getRule_id(), SysDict.A.rule_id.getId(), SysDict.A.rule_id.getLongName());
		BizUtil.fieldNotNull(ruleSimple.getRule_desc(), SysDict.A.rule_desc.getId(), SysDict.A.rule_desc.getLongName());
		BizUtil.fieldNotNull(ruleSimple.getOperater_ind(), SysDict.A.operater_ind.getId(), SysDict.A.operater_ind.getLongName());
		BizUtil.fieldNotNull(ruleSimple.getRule_scene_code(), SysDict.A.rule_scene_code.getId(), SysDict.A.rule_scene_code.getLongName());
	}

	/*
	 * 规则数据必输项检查
	 */
	private static void checkRuleData(String ruleScene, ApRuleDataWithInd ruleData, Options<ApRuleFuncInfo> funcParam) {
		BizUtil.fieldNotNull(ruleData.getRule_group_no(), SysDict.A.rule_group_no.getId(), SysDict.A.rule_group_no.getLongName());
		BizUtil.fieldNotNull(ruleData.getRule_sort(), SysDict.A.rule_sort.getId(), SysDict.A.rule_sort.getLongName());
		BizUtil.fieldNotNull(ruleData.getRule_expr_type(), SysDict.A.rule_expr_type.getId(), SysDict.A.rule_expr_type.getLongName());
		BizUtil.fieldNotNull(ruleData.getData_mapping_operator(), SysDict.A.data_mapping_operator.getId(), SysDict.A.data_mapping_operator.getLongName());
		BizUtil.fieldNotNull(ruleData.getConstant_variable(), SysDict.A.constant_variable.getId(), SysDict.A.constant_variable.getLongName());

		if (ruleData.getRule_expr_type() == E_RULEEXPTYPE.COMMON) {
			BizUtil.fieldNotNull(ruleData.getField_name(), SysDict.A.field_name.getId(), SysDict.A.field_name.getLongName());

			// 验证app_rule_interface中对应的场景有这个字段
			app_rule_interface tabRuleInterface = App_rule_interfaceDao.selectOne_odb1(ruleScene, ruleData.getField_name(), false);

			if (tabRuleInterface == null) {
				throw ApErr.AP.E0105(ruleScene, ruleData.getField_name());
			}
		}
		else {
			BizUtil.fieldNotNull(ruleData.getFunc_code(), SysDict.A.func_code.getId(), SysDict.A.func_code.getLongName());
			BizUtil.fieldNotNull(ruleData.getFunc_name(), SysDict.A.func_name.getId(), SysDict.A.func_name.getLongName());
			//方法参数必须设置
			for (ApRuleFuncInfo ruleFuncParmInfo : funcParam) {
				BizUtil.fieldNotNull(ruleFuncParmInfo.getParm_desc(), SysDict.A.parm_desc.getId(), SysDict.A.parm_desc.getLongName());
			}

			//验证app_rule_func中有这个方法，以及参数个数是否与方法定义的一致
			/*
			 * app_rule_func tabRuleFunc =
			 * App_rule_funcDao.selectOne_odb1(ruleData.getFunc_code(), false);
			 * 
			 * if(tabRuleFunc == null){ throw
			 * ApErr.AP.E0104(ruleData.getFunc_code()); }
			 */

			List<app_rule_func_parm> ruleFuncParmList = App_rule_func_parmDao.selectAll_odb3(ruleData.getFunc_code(), false);

			if (ruleFuncParmList == null) {
				throw ApErr.AP.E0106(ruleData.getFunc_code());
			}

		}

	}

	/*
	 * 场景规则信息必输项检查
	 */
	private static void checkRule(ApRuleSceneWithInd rule) {
		BizUtil.fieldNotNull(rule.getRule_id(), SysDict.A.rule_id.getId(), SysDict.A.rule_id.getLongName());
		BizUtil.fieldNotNull(rule.getRule_desc(), SysDict.A.rule_desc.getId(), SysDict.A.rule_desc.getLongName());
		BizUtil.fieldNotNull(rule.getOperater_ind(), SysDict.A.operater_ind.getId(), SysDict.A.operater_ind.getLongName());
		BizUtil.fieldNotNull(rule.getRule_scene_code(), SysDict.A.rule_scene_code.getId(), SysDict.A.rule_scene_code.getLongName());
		//BizUtil.fieldNotNull(rule.getMapping_sort(), SysDict.A.mapping_sort.getId(), SysDict.A.mapping_sort.getLongName());
		BizUtil.fieldNotNull(rule.getMapping_result(), SysDict.A.mapping_result.getId(), SysDict.A.mapping_result.getLongName());

		if (CommUtil.compare(rule.getEffect_date(), rule.getExpiry_date()) > 0) {
			throw ApPubErr.APPUB.E0015(rule.getEffect_date(), rule.getExpiry_date());
		}
	}

	/*
	 * 填加规则信息
	 */
	private static void addRule(ApRuleSimpleWithInd ruleSimple) {
		String ruleId = ruleSimple.getRule_id();

		app_rule oldRule = App_ruleDao.selectOne_odb2(ruleId, false);
		if (oldRule != null) {
			throw ApPubErr.APPUB.E0019(OdbFactory.getTable(app_rule.class).getLongname(), ruleId);
		}

		app_rule newRule = BizUtil.getInstance(app_rule.class);
		newRule.setRule_id(ruleId);
		newRule.setRule_desc(ruleSimple.getRule_desc());
		newRule.setRule_scene_code(ruleSimple.getRule_scene_code());
		newRule.setException_rule_id(ruleSimple.getException_rule_id());
		newRule.setEffect_date(effect_date);
		newRule.setExpiry_date(expiry_date);

		App_ruleDao.insert(newRule);

		ApDataAuditApi.regLogOnInsertParameter(newRule);
	}

	/*
	 * 修改规则信息
	 */
	private static int modifyRule(ApRuleSimpleWithInd ruleSimple) {
		String ruleId = ruleSimple.getRule_id();

		app_rule oldRule = App_ruleDao.selectOne_odb2(ruleId, false);

		if (oldRule == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_rule.class).getLongname(), SysDict.A.rule_id.getLongName(), ruleId);
		}

		if (CommUtil.compare(ruleSimple.getData_version(), oldRule.getData_version()) != 0) {
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(app_rule.class).getLongname());
		}
		// 修改后的机构信息
		app_rule newRule = BizUtil.clone(app_rule.class, oldRule);// 克隆OldObject，防止公共字段丢失
		newRule.setRule_desc(ruleSimple.getRule_desc());
		newRule.setRule_scene_code(ruleSimple.getRule_scene_code());
		newRule.setException_rule_id(ruleSimple.getException_rule_id());

		// 先登记审计,如果没有字段修改，需要抛错
		int i = ApDataAuditApi.regLogOnUpdateParameter(oldRule, newRule);
		//		if (i == 0) {
		//			// throw
		//			throw ApPubErr.APPUB.E0023(OdbFactory.getTable(app_rule.class).getLongname());
		//		}
		App_ruleDao.updateOne_odb2(newRule);

		return i;

	}

	/*
	 * 单个新增规则数据
	 */
	private static void addRuleDataSingle(String ruleId, ApRuleDataWithInd data, Options<ApRuleFuncInfo> funcParam) {
		Long ruleGroup = data.getRule_group_no();
		Long ruleSort = data.getRule_sort();

		String actuparm = "";
		app_rule_data ruleData = App_rule_dataDao.selectOne_odb2(ruleId, ruleGroup, ruleSort, false);
		if (ruleData != null) {
			throw ApPubErr.APPUB.E0019(OdbFactory.getTable(app_rule_data.class).getLongname(), ruleId + ApConst.KEY_CONNECTOR + ruleGroup.toString() + ApConst.KEY_CONNECTOR
					+ ruleSort.toString());
		}
			
		
		// 如果为function,进行function的新增处理
		if( data.getRule_expr_type() ==E_RULEEXPTYPE.FUNCTION){
			 
			//方法信息又后台定义不做新增或者修改，只进行方法的参数设置
			for(ApRuleFuncInfo ruleFuncParmInfo : funcParam){
				app_rule_func_parm ruleFuncParm = BizUtil.getInstance(app_rule_func_parm.class);
					
				ruleFuncParm.setFunc_code(data.getFunc_code());  //function code
				ruleFuncParm.setParm_order(ruleFuncParmInfo.getParm_order());  //parameter order
				ruleFuncParm.setParm_code(ruleFuncParmInfo.getParm_code());  //parameter code
				ruleFuncParm.setParm_desc(ruleFuncParmInfo.getParm_desc());  //parameter describe
				ruleFuncParm.setParm_type(ruleFuncParmInfo.getParm_type());  //parameter type
				 
				App_rule_func_parmDao.updateOne_odb1(ruleFuncParm);
				
				if(ruleFuncParmInfo.getParm_desc() != null){
					actuparm = actuparm+ruleFuncParmInfo.getParm_desc()+",";
				}
			}
			
			if(CommUtil.isNotNull(actuparm)){
				actuparm = actuparm.substring(0,actuparm.length()-1);
			}
			 
		}
		
		app_rule_data newRuleData = BizUtil.getInstance(app_rule_data.class);
		newRuleData.setRule_id(ruleId);
		newRuleData.setRule_group_no(data.getRule_group_no());
		newRuleData.setRule_sort(data.getRule_sort());
		newRuleData.setRule_expr_type(data.getRule_expr_type());
		newRuleData.setField_name(data.getField_name());
		newRuleData.setFunc_code(data.getFunc_code());
		newRuleData.setActual_parm(actuparm);  //拼接parm_desc
		newRuleData.setConstant_variable(data.getConstant_variable());
		newRuleData.setData_mapping_operator(data.getData_mapping_operator());
		newRuleData.setData_mapping_value(data.getData_mapping_value());
		
		//插入数据库
		App_rule_dataDao.insert(newRuleData);

	}

	/*
	 * 场景规则新增
	 */
	private static String addRuleScene(ApRuleSceneWithInd rule) {
		String ruleId = rule.getRule_id();// 生成规则id
		String ruleSenceCode = rule.getRule_scene_code(); // 规则场景代码
		Long ruleSort = rule.getMapping_sort(); // 规则次序
		app_rule RuleId = App_ruleDao.selectOne_odb2(ruleId, false);
		if (RuleId != null) {
			throw ApPubErr.APPUB.E0019(OdbFactory.getTable(app_rule.class).getLongname(), ruleId);
		}
		app_rule oldRule = App_ruleDao.selectFirst_odb4(ruleSenceCode, ruleId, false);
		if (oldRule != null) {
			throw ApPubErr.APPUB.E0019(OdbFactory.getTable(app_rule.class).getLongname(), ruleSenceCode + ApConst.KEY_CONNECTOR + ruleSort);
		}

		app_rule newRule = BizUtil.getInstance(app_rule.class);
		newRule.setRule_id(ruleId); // 规则ID
		newRule.setRule_desc(rule.getRule_desc()); // 规则描述
		newRule.setRule_scene_code(ruleSenceCode); // 规则场景代码
		newRule.setMapping_sort(ruleSort); // 规则次序
		newRule.setMapping_result(rule.getMapping_result()); // 命中结果
		newRule.setException_rule_id(rule.getException_rule_id());
		newRule.setEffect_date(effect_date);//默认生效日期给定，后期可根据需要修改获取方式
		newRule.setExpiry_date(expiry_date);//默认失效日期给定，后期可根据需要修改获取方式

		App_ruleDao.insert(newRule);

		ApDataAuditApi.regLogOnInsertParameter(newRule);

		return ruleId;
	}

	/*
	 * 场景规则维护
	 */
	private static int modRuleScene(ApRuleSceneWithInd rule) {
		String ruleId = rule.getRule_id();

		app_rule oldRule = App_ruleDao.selectOne_odb2(ruleId, false);

		if (oldRule == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_rule.class).getLongname(), SysDict.A.rule_id.getLongName(), ruleId);
		}

		if (CommUtil.compare(rule.getData_version(), oldRule.getData_version()) != 0) {
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(app_rule.class).getLongname());
		}
		app_rule dbRule = App_ruleDao.selectFirst_odb4(rule.getRule_scene_code(), ruleId, false);
		if (dbRule == null) {
			throw ApPubErr.APPUB.E0024(OdbFactory.getTable(app_rule.class).getLongname(), SysDict.A.rule_scene_code.getLongName(), rule.getRule_scene_code(),
					SysDict.A.mapping_sort.getLongName(), rule.getMapping_sort().toString());
		}

		// 修改后的规则信息
		app_rule newRule = BizUtil.clone(app_rule.class, oldRule);// 克隆OldObject，防止公共字段丢失
		newRule.setRule_desc(rule.getRule_desc());
		newRule.setRule_scene_code(rule.getRule_scene_code());
		newRule.setMapping_sort(rule.getMapping_sort());
		newRule.setMapping_result(rule.getMapping_result());
		newRule.setException_rule_id(rule.getException_rule_id());
		newRule.setEffect_date(rule.getEffect_date());
		newRule.setExpiry_date(rule.getExpiry_date());

		// 先登记审计,如果没有字段修改，需要抛错
		int i = ApDataAuditApi.regLogOnUpdateParameter(newRule, oldRule);

		App_ruleDao.updateOne_odb2(newRule);

		return i;

	}

	/*
	 * 获取新的规则id
	 */
	public static String getRuleId() {
		return ApSeqApi.genSeq("RULE_NO");
	}
}
