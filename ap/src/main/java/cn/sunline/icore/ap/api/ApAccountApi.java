package cn.sunline.icore.ap.api;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.account.ApAccounting;
import cn.sunline.icore.ap.type.ComApAccounting.ApAccountingEvent;
import cn.sunline.icore.ap.type.ComApAccounting.ApAccountingEventCtrl;
import cn.sunline.icore.ap.type.ComApAccounting.ApAccountingEventIn;
import cn.sunline.icore.ap.type.ComApAccounting.ApGlParaCtrl;
import cn.sunline.icore.ap.type.ComApAccounting.ApRecordAccure;
import cn.sunline.icore.ap.type.ComApAccounting.ApRegLedgerBal;
import cn.sunline.ltts.core.api.model.dm.Options;

/**
 * <p>
 * 文件功能说明：会计事件处理
 * </p>
 * 
 * @Author zhangwh
 *         <p>
 *         <li>2019年7月29日-上午13:53:13</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApAccountApi {
	/**
	 * 会计事件登记
	 * 
	 * @param IoTaAccountingEventIn
	 *            会计事件登记输入
	 * @Author Administrator
	 *         <p>
	 *         <li>2019年1月18日-下午12:05:15</li>
	 *         <li>应用说明：每一笔账务交易均在此登记会计分录</li>
	 *         </p>
	 * @throws TaBase.E0024
	 *             交易金额不能为空
	 * 
	 */
	public static void regAccountingEvent(ApAccountingEventIn srvInput) {
		ApAccounting.regAccountingEvent(srvInput);
	}
	
	/**
	 * 财务平衡检查
	 * 
	 * @Author Administrator
	 *         <p>
	 *         <li>2017年1月18日-下午2:44:10</li>
	 *         <li>应用说明：每做完一笔账户交易均需调用此方法检查该交易总借总贷是否平衡</li>
	 *         </p>
	 * @throws TaBase.E0027
	 *             账务不平
	 */
	public static void checkBalance() {
		ApAccounting.checkBalance();
	}
	
	/**
	 * 登记分户账余额
	 * 
	 * @param IoTaRegLedgerBal
	 *            分户账余额登记输入
	 * @Author Administrator
	 *         <p>
	 *         <li>2017年3月23日-上午10:03:18</li>
	 *         <li>应用说明：各个模块记录分户账户余额需调用此方法</li>
	 *         </p>
	 */
	public static void regLedgerBal(ApRegLedgerBal srvInput) {
		ApAccounting.regLedgerBal(srvInput);
	}
	
	/**
	 * 计提登记
	 * 
	 * @param IoTaRecordAccure
	 *            计提登记输入
	 * @Author Administrator
	 *         <p>
	 *         <li>2017年3月22日-下午2:09:58</li>
	 *         </p>
	 */
	public static void regAccure(ApRecordAccure srvInput) {
		ApAccounting.regAccure(srvInput);
	}
	
	/**
	 * 
	 * @Title: getAliasInfos 
	 * @author zhangwh
	 * @Description: 查询核算别名信息列表
	 * @param srvInput 
	 * @return void
	 * @date 2019年9月3日 下午5:48:10 
	 * @throws
	 */
	public static Options<ApAccountingEventCtrl> getAliasInfos(String aliasCode) {
		return ApAccounting.getAccountingAliasInfo(aliasCode);
	}
	
	/**
	 * 根据交易流水查询会计事件（分页）
	 * 
	 * @param trxnSeq
	 *            交易流水
	 * @Author Administrator
	 *         <p>
	 *         <li>2017年6月8日-上午9:38:15</li>
	 *         <li>返回结果：借贷分录明细</li>
	 *         </p>
	 * 
	 * @return Options<TaAccountingEvent>
	 */
	public static Options<ApAccountingEvent> getAccountingEvent(String trxnSeq) {
		return ApAccounting.getAccountingEvent(trxnSeq);
	}
	
	
	/**
	 * 核算别名维护
	 * 
	 * @param IoTaGlParaCtrl
	 *            核算别名维护输入
	 * @Author Administrator
	 *         <p>
	 *         <li>2017年8月4日-上午9:36:22</li>
	 *         <li>应用说明：据输入操作标志可以对核算别名表新增、删除、修改操作</li>
	 *         </p>
	 */
	public static void maintainingGlParmCtrl(ApGlParaCtrl input) {
		ApAccounting.maintainingGlParmCtrl(input);
	}
	
	/**
	 * 查询核算别名信息
	 * 
	 * @param accounting_alias
	 *            核算别名
	 * @param bal_attributes
	 *            余额属性
	 * @param errer_ctrl_ind
	 *            查询为空是否报错标志
	 * @Author Administrator
	 *         <p>
	 *         <li>2019年1月23日-下午5:01:58</li>
	 *         <li>应用说明：据核算别名,余额属性、errerCtrlInd去查询核算别名</li>
	 *         <li>返回结果：某个核算别名信息</li>
	 *         </p>
	 * 
	 * @return IoTaGlParaCtrl
	 */
	public static ApGlParaCtrl qryGlParmCtrl(String accountingAlias,String balAttributes,E_YESORNO errerCtrlInd) {
		return ApAccounting.qryGlParmCtrl(accountingAlias,balAttributes,errerCtrlInd);
	}
}
