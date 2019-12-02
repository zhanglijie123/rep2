package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.rule.mnt.ApRuleMnt;

/**
 * rule information maintenance 规则信息维护
 */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value = "SrvApRuleImpl", longname = "rule information maintenance", type = cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApRuleImpl implements cn.sunline.icore.ap.servicetype.SrvApRule {
	/**
	 * rule information maintenance
	 */
	public void modifyRuleInfo(final cn.sunline.icore.ap.type.ComApSystem.ApRuleSimpleWithInd ruleSimple,
			final cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApRuleDataWithInd> ruleData,
			final cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApRuleDataWithInd> exceptionRuleData,
			final cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApRuleFuncInfo> funcParam) {
		ApRuleMnt.modifyRuleSimple(ruleSimple, ruleData, exceptionRuleData, funcParam);
	}

	/**
	 * query simple rule
	 */
	public cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApRuleInfo> queryRuleList(
			final cn.sunline.icore.ap.type.ComApSystem.ApRuleSimple ruleSimple) {
		return ApRuleMnt.queryRuleList(ruleSimple);
	}

	/**
	 * query rule data
	 */
	public cn.sunline.icore.ap.type.ComApSystem.ApRuleDataResult queryRuleDataList(String ruleId) {
		return ApRuleMnt.queryRuleDataList(ruleId);
	}

	/**
	 * scene rule maintenance
	 */
	public void modifyRuleScene(final cn.sunline.icore.ap.type.ComApSystem.ApRuleSceneWithInd rule,
			final cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApRuleDataWithInd> ruleData,
			final cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApRuleDataWithInd> exceptionRuleData,
			final cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApRuleFuncInfo> funcParam) {
		ApRuleMnt.modifyRuleScene(rule, ruleData, exceptionRuleData,funcParam);
	}

	/**
	 * query scene rule
	 */
	public cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApRuleInfo> queryRuleScene(
			final cn.sunline.icore.ap.type.ComApSystem.ApRuleSimple ruleSimple) {
		return ApRuleMnt.queryRuleScene(ruleSimple);
	}

	public cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApRuleFunc> queryParm(String func_name) {
		return ApRuleMnt.queryParm(func_name);
	}



}
