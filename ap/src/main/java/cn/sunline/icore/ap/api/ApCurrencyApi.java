package cn.sunline.icore.ap.api;

import java.math.BigDecimal;
import java.util.List;

import cn.sunline.icore.ap.parm.ApBaseCurrency;
import cn.sunline.icore.ap.type.ComApBasic.ApCurrencyInfo;
import cn.sunline.icore.sys.type.EnumType.E_ROUNDRULE;

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
public class ApCurrencyApi {

	
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

		return ApBaseCurrency.exists(ccyCode);
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

		return ApBaseCurrency.getBankBase();
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
		return ApBaseCurrency.getExchBase();
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

		return ApBaseCurrency.getItem(ccyCode);

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
		return ApBaseCurrency.isBankBase(ccyCode);
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

		return ApBaseCurrency.isAmountValid(ccyCode, amount);
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

		ApBaseCurrency.chkAmountByCcy(ccyCode, amount);
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
		return ApBaseCurrency.roundAmount(ccyCode, amount);
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

		return ApBaseCurrency.roundAmount(ccyCode, amount, roundRule);
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

		return ApBaseCurrency.roundInterest(ccyCode, interest);
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
		return ApBaseCurrency.roundInterest(ccyCode, interest, roundRule);
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
		return ApBaseCurrency.all();
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
		return ApBaseCurrency.getOtherItems(ccyCode);
	}
}
