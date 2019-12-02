package cn.sunline.icore.ap.parm;

import java.math.BigDecimal;
import java.util.List;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.icore.ap.namedsql.ApBasicBaseDao;
import cn.sunline.icore.ap.tables.TabApBasic.App_currencyDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_currency;
import cn.sunline.icore.ap.type.ComApBasic.ApCurrencyInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.icore.sys.type.EnumType.E_ROUNDRULE;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;

/**
 * <p>
 * 文件功能说明：币种
 * </p>
 * 
 * @Author jollyja
 *         <p>
 *         <li>2016年12月5日-下午10:14:23</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 jollyja：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseCurrency {

	
	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:34:24</li>
	 *         <li>功能说明：判断货币代码是否存在，存在返回true，不存在返回false</li>
	 *         </p>
	 * @param ccy_code
	 * @return
	 */
	public static boolean exists(String ccyCode) {

		app_currency ccyInfo = App_currencyDao.selectOne_odb1(ccyCode, false);

		return (ccyInfo == null) ? false : true;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:45:20</li>
	 *         <li>功能说明：获取银行基础货币</li>
	 *         </p>
	 * @param
	 * @return
	 */
	public static String getBankBase() {

		return ApBaseBusinessParm.getValue("BANK_BASE_CCY");
	}
	
	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:45:20</li>
	 *         <li>功能说明：获取外币兑换基础货币</li>
	 *         </p>
	 * @param
	 * @return
	 */
	public static String getExchBase() {
		return ApBaseBusinessParm.getValue("EXCH_BASE_CCY");
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:35:36</li>
	 *         <li>功能说明：获取指定货币定义对象</li>
	 *         </p>
	 * @param ccyCode
	 * @return
	 */
	public static ApCurrencyInfo getItem(String ccyCode) {

		app_currency ccyInfo = App_currencyDao.selectOne_odb1(ccyCode, false);

		if (ccyInfo == null) {

			// 查询[${tableName}]失败,未找到相关信息。
			throw APPUB.E0005(OdbFactory.getTable(app_currency.class).getLongname(), SysDict.A.ccy_code.getLongName(), ccyCode);
		}
		
		return getCurrencyInfo(ccyInfo);

	}

	private static ApCurrencyInfo getCurrencyInfo(app_currency ccy) {
		ApCurrencyInfo currencyInfo = BizUtil.getInstance(ApCurrencyInfo.class);

		currencyInfo.setCale_interest_unit(ccy.getCale_interest_unit());
		currencyInfo.setCcy_change_unit(ccy.getCcy_change_unit());
		currencyInfo.setCcy_code(ccy.getCcy_code());
		currencyInfo.setCcy_minor_unit(ccy.getCcy_minor_unit());
		currencyInfo.setCcy_name(ccy.getCcy_name());
		currencyInfo.setCcy_num_code(ccy.getCcy_num_code());
		currencyInfo.setCountry_code(ccy.getCountry_code());
		currencyInfo.setAccrual_base_day(ccy.getAccrual_base_day());
		currencyInfo.setData_create_time(ccy.getData_create_time());
		currencyInfo.setData_create_user(ccy.getData_create_user());
		currencyInfo.setData_update_time(ccy.getData_update_time());
		currencyInfo.setData_update_user(ccy.getData_update_user());
		currencyInfo.setData_version(ccy.getData_version());

		return currencyInfo;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:44:00</li>
	 *         <li>功能说明：判断货币是否银行基础货币</li>
	 *         </p>
	 * @param ccyCode
	 * @return
	 */
	public static boolean isBankBase(String ccyCode) {

		if (CommUtil.isNotNull(ccyCode)) {

			String bankBaseCode = getBankBase();

			return CommUtil.equals(bankBaseCode, ccyCode);
		}

		return false;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:48:47</li>
	 *         <li>功能说明：按币种精度位检查金额是否合法</li>
	 *         </p>
	 * @param ccyCode
	 * @param amount
	 * @return
	 */
	public static boolean isAmountValid(String ccyCode, BigDecimal amount) {

		Boolean ret = false;

		if (amount != null) {
			BigDecimal bigAmountNew = roundAmount(ccyCode, amount);

			if (CommUtil.compare(bigAmountNew, amount) == 0) {

				ret = true;
			}

		}
		return ret;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:48:47</li>
	 *         <li>功能说明：输入金额精度不合法时，抛出异常</li>
	 *         </p>
	 * @param ccyCode
	 * @param amount
	 * @return
	 */
	public static void chkAmountByCcy(String ccyCode, BigDecimal amount) {

		if (amount != null && !isAmountValid(ccyCode, amount)) {
            // 信息域[${fieldValue}]格式不合法,[${fieldName}-${fieldDesc}]
            throw APPUB.E0002(amount.toString(), SysDict.A.trxn_amt.getId(), SysDict.A.trxn_amt.getLongName());
        }
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:56:03</li>
	 *         <li>功能说明：按记账精度位做四舍五入处理</li>
	 *         </p>
	 * @param ccyCode
	 * @param amount
	 * @return
	 */
	public static BigDecimal roundAmount(String ccyCode, BigDecimal amount) {
		return roundAmount(ccyCode, amount, E_ROUNDRULE.ROUND);
	}

	/**
	 * 
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月24日-上午10:54:18</li>
	 *         <li>功能说明：按记账精度为自定义处理</li>
	 *         </p>
	 * @param ccyCode
	 * @param amount
	 * @param roundRule
	 * @return
	 */
	public static BigDecimal roundAmount(String ccyCode, BigDecimal amount, E_ROUNDRULE roundRule) {

		ApCurrencyInfo ccyInfo = getItem(ccyCode);

		return round(amount, roundRule, ccyInfo.getCcy_minor_unit().intValue());
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:56:03</li>
	 *         <li>功能说明：按计息精度为做四舍五入处理</li>
	 *         </p>
	 * @param ccyCode
	 * @param amount
	 * @return
	 */
	public static BigDecimal roundInterest(String ccyCode, BigDecimal interest) {

		return roundInterest(ccyCode, interest, E_ROUNDRULE.ROUND);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月24日-上午10:19:29</li>
	 *         <li>功能说明：按计息精度自定义处理</li>
	 *         </p>
	 * @param ccyCode
	 * @param interest
	 * @param roundRule
	 * @return
	 */
	public static BigDecimal roundInterest(String ccyCode, BigDecimal interest, E_ROUNDRULE roundRule) {
		ApCurrencyInfo ccyInfo = getItem(ccyCode);

		return round(interest, roundRule, ccyInfo.getCale_interest_unit().intValue());
	}

	private static BigDecimal round(BigDecimal amt, E_ROUNDRULE roundRule, int scale) {

		BigDecimal amtOut = amt;
		// 四舍五入
		if (roundRule == E_ROUNDRULE.ROUND) {

			amtOut = amtOut.setScale(scale, BigDecimal.ROUND_HALF_UP);
		}
		// 无条件进位
		else if (roundRule == E_ROUNDRULE.UP) {
			amtOut = amtOut.setScale(scale, BigDecimal.ROUND_UP);
		}
		// 无条件舍去
		else if (roundRule == E_ROUNDRULE.DOWN) {
			amtOut = amtOut.setScale(scale, BigDecimal.ROUND_FLOOR);
		}

		return amtOut;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:36:22</li>
	 *         <li>功能说明：获取所有货币定义对象</li>
	 *         </p>
	 * @return
	 */
	public static List<ApCurrencyInfo> all() {
		// 获取全部币种，由于odb操作限制，暂不实现，有需要再通过namedsql实现
		// 用命名sql查询
		List<ApCurrencyInfo> currentcyInfo = ApBasicBaseDao.selCurrencyList(MsOrg.getReferenceOrgId(app_currency.class), null, null, null, false);
		
		return currentcyInfo;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午2:43:05</li>
	 *         <li>功能说明：获得指定币种以外的货币对象</li>
	 *         </p>
	 * @param ccy_code
	 * @return
	 */
	public static List<ApCurrencyInfo> getOtherItems(String ccyCode) {

		List<ApCurrencyInfo> currencyInfo = all();
		if(CommUtil.isNotNull(ccyCode) && !currencyInfo.isEmpty()){
            for(int i=0 ; i < currencyInfo.size() ; i++){
                if(CommUtil.equals(currencyInfo.get(i).getCcy_code(), ccyCode)){
                    currencyInfo.remove(i);
                    break;
                }
            }
        }
		return currencyInfo;
	}
}
