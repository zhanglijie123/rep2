package cn.sunline.icore.ap.parm.mnt;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.dict.MsDict;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.icore.ap.api.ApDataAuditApi;
import cn.sunline.icore.ap.namedsql.ApBasicBaseDao;
import cn.sunline.icore.ap.tables.TabApBasic.App_currencyDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_currency;
import cn.sunline.icore.ap.type.ComApBasic.ApCurrencyInfo;
import cn.sunline.icore.ap.type.ComApBasic.ApCurrencyInfoMnt;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApErr.AP;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

/***
 * <p>
 * 文件功能说明：货币参数维护类
 * </p>
 * 
 * @Author zhangjing2
 *         <p>
 *         <li>2016年12月26日-下午3:10:10</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2016年12月26日zhangjing2：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApCurrencyMnt {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApCurrencyMnt.class);
	/**
	 * 货币参数列表查询方法
	 */
	public static Options<ApCurrencyInfo> queryCurrencyList(String ccyCode, String ccyName, String countryCode) {
		
		String orgId = MsOrg.getReferenceOrgId(app_currency.class);//法人代码
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		Page<ApCurrencyInfo> page = ApBasicBaseDao.selCurrencyList(orgId, ccyCode, ccyName, countryCode, runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(), false);
		runEnvs.setTotal_count(page.getRecordCount());
		Options<ApCurrencyInfo> options = new DefaultOptions<ApCurrencyInfo>();
		options.setValues(page.getRecords());
		return options;
	}

	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年11月12日-下午4:48:13</li>
	 *         <li>功能说明：货币参数维护</li>
	 *         </p>
	 * @param cplIn
	 * 			货币参数信息
	 */
	public static void mntCurrencyInfo(ApCurrencyInfoMnt cplIn) {
		bizlog.method("ApCurrencyMnt.mntCurrencyInfo begin >>>>>>>>>>>>>>>>");
		bizlog.debug("ApCurrencyInfoMnt cplIn >>>>>>[%s]", cplIn);
		
		// 操作标志为空则返回
		if (CommUtil.isNull(cplIn.getOperater_ind())) {
			return;
		}

		// 必输项检查
		BizUtil.fieldNotNull(cplIn.getCcy_code(), MsDict.Comm.ccy_code.getId(), MsDict.Comm.ccy_code.getLongName());
		BizUtil.fieldNotNull(cplIn.getCcy_name(), SysDict.A.ccy_name.getId(), SysDict.A.ccy_name.getLongName());
		BizUtil.fieldNotNull(cplIn.getCcy_num_code(), SysDict.A.ccy_num_code.getId(), SysDict.A.ccy_num_code.getLongName());
		BizUtil.fieldNotNull(cplIn.getCountry_code(), SysDict.A.country_code.getId(), SysDict.A.country_code.getLongName());
		BizUtil.fieldNotNull(cplIn.getCcy_minor_unit(), SysDict.A.ccy_minor_unit.getId(), SysDict.A.ccy_minor_unit.getLongName());
		BizUtil.fieldNotNull(cplIn.getCale_interest_unit(), SysDict.A.cale_interest_unit.getId(), SysDict.A.cale_interest_unit.getLongName());
		BizUtil.fieldNotNull(cplIn.getCcy_change_unit(), SysDict.A.ccy_change_unit.getId(), SysDict.A.ccy_change_unit.getLongName());
		
		app_currency currencyTab = App_currencyDao.selectOne_odb1(cplIn.getCcy_code(),false);
		
		// 操作标志为新增
		if(E_DATAOPERATE.ADD == cplIn.getOperater_ind()){
			
			// 已存在记录
			if (currencyTab != null) {
				throw AP.E0124(OdbFactory.getTable(app_currency.class).getLongname(), cplIn.getCcy_code());
			}
			
			app_currency newCurrency = BizUtil.getInstance(app_currency.class);
			
			newCurrency.setCcy_code(cplIn.getCcy_code());
			newCurrency.setCcy_name(cplIn.getCcy_name());
			newCurrency.setCcy_num_code(cplIn.getCcy_num_code());
			newCurrency.setCountry_code(cplIn.getCountry_code());
			newCurrency.setCcy_minor_unit(cplIn.getCcy_minor_unit());
			newCurrency.setCale_interest_unit(cplIn.getCale_interest_unit());
			newCurrency.setCcy_change_unit(cplIn.getCcy_change_unit());
			newCurrency.setAccrual_base_day(cplIn.getAccrual_base_day());
			
			// 登记审计
			ApDataAuditApi.regLogOnInsertParameter(newCurrency);
			
			App_currencyDao.insert(newCurrency);
		}
		else if(E_DATAOPERATE.MODIFY == cplIn.getOperater_ind()){
			
			if (currencyTab == null) {
				throw AP.E0125(OdbFactory.getTable(app_currency.class).getLongname(), cplIn.getCcy_code());
			}
			
			// 复制旧数据
			app_currency oldCurrencyTab = BizUtil.clone(app_currency.class, currencyTab);
			
			currencyTab.setCcy_code(cplIn.getCcy_code());
			currencyTab.setCcy_name(cplIn.getCcy_name());
			currencyTab.setCcy_num_code(cplIn.getCcy_num_code());
			currencyTab.setCountry_code(cplIn.getCountry_code());
			currencyTab.setCcy_minor_unit(cplIn.getCcy_minor_unit());
			currencyTab.setCale_interest_unit(cplIn.getCale_interest_unit());
			currencyTab.setCcy_change_unit(cplIn.getCcy_change_unit());
			currencyTab.setAccrual_base_day(cplIn.getAccrual_base_day());
			
			// 登记审计日志
			ApDataAuditApi.regLogOnUpdateParameter(oldCurrencyTab, currencyTab);
			
			App_currencyDao.updateOne_odb1(currencyTab);
		}
		else if (E_DATAOPERATE.DELETE == cplIn.getOperater_ind()) {

			if (currencyTab == null) {
				throw AP.E0126(OdbFactory.getTable(app_currency.class).getLongname(), cplIn.getCcy_code());
			}

			// 登记审计
			ApDataAuditApi.regLogOnDeleteParameter(currencyTab);

			// 删除记录
			App_currencyDao.deleteOne_odb1(cplIn.getCcy_code());
		}
		bizlog.method("ApCurrencyMnt.mntCurrencyInfo end >>>>>>>>>>>>>>>>");
	}
}
