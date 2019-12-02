package cn.sunline.icore.ap.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sunline.clwj.msap.core.tools.DateTools;
import cn.sunline.clwj.msap.core.tools.MsSystemSeq;
import cn.sunline.clwj.msap.core.util.MsBuffer;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_CASHTRXN;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.edsp.engine.online.plugin.config.OnlineEngineConfigManager;
import cn.sunline.edsp.engine.online.plugin.engine.InServiceController;
import cn.sunline.edsp.engine.online.plugin.engine.InServiceController.ServiceCategory;
import cn.sunline.edsp.engine.online.plugin.engine.handler.OETAfterHandler;
import cn.sunline.edsp.engine.online.plugin.engine.handler.OETBeforeHandler;
import cn.sunline.edsp.engine.online.plugin.engine.handler.OETExceptionHandler;
import cn.sunline.edsp.engine.online.plugin.engine.handler.OETHandlerContext;
import cn.sunline.edsp.engine.online.plugin.engine.spi.OnlineEngineExtensionPoint;
import cn.sunline.edsp.microcore.spi.ExtensionLoader;
import cn.sunline.icore.ap.api.ApSystemParmApi;
import cn.sunline.icore.ap.parm.ApBaseBusinessParm;
import cn.sunline.icore.ap.parm.ApRefDate;
import cn.sunline.icore.ap.spi.impl.ApConstants;
import cn.sunline.icore.ap.tables.TabApCommField.apb_field_normal;
import cn.sunline.icore.ap.tables.TabApSystem.App_serviceDao;
import cn.sunline.icore.ap.tables.TabApSystem.app_service;
import cn.sunline.icore.ap.type.ComApBasic.ApAccountRouteInfo;
import cn.sunline.icore.ap.type.ComApBasic.ApSplitString;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.icore.sys.parm.TrxEnvs;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_ACCOUTANALY;
import cn.sunline.icore.sys.type.EnumType.E_ACCTROUTETYPE;
import cn.sunline.icore.sys.type.EnumType.E_CYCLETYPE;
import cn.sunline.icore.sys.type.EnumType.E_INSIDEACCTTYPE;
import cn.sunline.ltts.CustomCommPluginConstant;
import cn.sunline.ltts.base.RequestData;
import cn.sunline.ltts.base.ResponseData;
import cn.sunline.ltts.base.frw.model.util.ModelPropertyDescriptor;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.base.util.DateTimeUtil_;
import cn.sunline.ltts.base.util.JsonUtil;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.CoreUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.exception.LttsBusinessException;
import cn.sunline.ltts.core.api.logging.SysLog;
import cn.sunline.ltts.core.api.logging.SysLogUtil;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultEnum;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;
import cn.sunline.ltts.core.util.LangUtil;
import cn.sunline.ltts.engine.biz.runtime.EngineContext;
import cn.sunline.ltts.frw.model.annotation.ConfigType;
import cn.sunline.ltts.frw.model.datainterface.DataInterfaceElement;
import cn.sunline.ltts.frw.model.datainterface.DataInterfaceElements;
import cn.sunline.ltts.frw.model.db.Field;

/**
 * <p>
 * 文件功能说明：业务平台相关的工具类都放在这里
 * </p>
 * 
 * @Author jollyja
 *         <p>
 *         <li>2016年11月01日-下午4:42:22</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161101 jollyja：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class BizUtil {
	private static final SysLog log = SysLogUtil.getSysLog(BizUtil.class);
	private static final String PARTIAL_REVERSAL_SERVICE_LST = "SrvIoCmChrg.prcAutoChrgAccounting,SrvIoCmChrg.prcManualChrgAccounting";
	private static final Map<Class<?>, Object> srvCache = new WeakHashMap<Class<?>, Object>();

	private BizUtil() {
	}

	/**
	 * 获取当前机器时间，字符串格式（yyyyMMdd HH:mm:ss SSS）
	 */
	public static String getComputerDateTime() {
		return DateTools.getComputerDateTime();
	}

	/**
	 * 获取当前机器日期，格式：yyyyMMdd
	 */
	public static String getComputerDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(new Date());
	}

	/**
	 * 获取当前机器时间，格式：HHmmss
	 */
	public static String getComputerSimpleTime() {
		SimpleDateFormat df = new SimpleDateFormat("HHmmss");
		return df.format(new Date());
	}

	/**
	 * 获取当前机器时间，格式：HH:mm:ss SSS
	 */
	public static String getComputerTime() {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss SSS");
		return df.format(new Date());
	}

	/**
	 * 将日期字符串格式为日期对象，格式：yyyyMMdd
	 */
	public static Date toDate(String date) {
		return toDate(date, "yyyyMMdd");

	}

	/**
	 * 将日期字符串格式为日期对象
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date toDate(String date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		Date d = new Date();
		try {
			d = df.parse(date);
		}
		catch (ParseException e) {
			throw APPUB.E0011(date);
		}
		return d;
	}

	/**
	 * 将日期对象格式化为日期字符串，格式：yyyyMMdd
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(date);
	}

	/**
	 * 将日期对象格式化为日期字符串，格式：yyyyMMdd HH:mm:ss SSS
	 */
	public static String formatDateTime(Date date) {
		return DateTools.formatDateTime(date);
	}

	/**
	 * 检查是否是yyyyMMdd格式的日期字符串
	 */
	public static boolean isDateString(String date) {
		return isDateString(date, "yyyyMMdd");

	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月16日-上午10:11:48</li>
	 *         <li>功能说明：检查是否是格式的日期字符串</li>
	 *         </p>
	 * @param date
	 * @param format
	 * @return
	 */
	public static boolean isDateString(String date, String format) {
		if (CommUtil.isNull(date))
			return false;

		SimpleDateFormat df = new SimpleDateFormat(format);

		try {
			String tmp = df.format(toDate(date, format));
			if (date.equals(tmp)) {
				return true;
			}
		}
		catch (Exception e) {
			log.error("date format error:" + e.getMessage(), e, new Object[0]);
		}
		return false;
	}

	/**
	 * @Author T
	 *         <p>
	 *         <li>2014年3月4日-下午3:00:28</li>
	 *         <li>功能说明：判断周期字符串是否合法</li>
	 *         </p>
	 * @param cycle
	 *            周期 ex: 7D 1Y 30D
	 * @return true 合法 false 不合法
	 */
	public static boolean isCycleString(String cycle) {

		if (CommUtil.isNull(cycle))
			return false;

		boolean ret = false;
		int cycleLen = cycle.length();

		// 最少两位
		if (cycleLen >= 2) {

			// 周期单位
			String cycleUnit = cycle.substring(cycleLen - 1);

			// 最后一位必须在周期单位准许范围之内
			if (CommUtil.isInEnum(E_CYCLETYPE.class, cycleUnit)) {

				// 前面应该是数字部分如果转换为整数报错，则表明不是数值型字符串
				try {
					// 周期数字
					String cycleValue = cycle.substring(0, cycleLen - 1);
					Integer.parseInt(cycleValue);

					ret = true;
				}
				catch (Exception e) {
					log.error("cycle value parse error:" + e.getMessage(), e, new Object[0]);
				}
			}
		}

		return ret;
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2016年12月9日-上午9:20:49</li>
	 *         <li>功能说明：根据类型，数量，基准日期计算结果日期</li>
	 *         </p>
	 * @param type
	 *            day\dd\DD\d\D week\ww\WW\w\W month\mm\MM\m\M quarter\qq\QQ\q\Q
	 *            year\yy\YY\y\Y
	 * @param date
	 *            基准日期
	 * @param num
	 *            日期间隔
	 * @return
	 */
	public static String dateAdd(String type, String date, int num) {

		String newType = type;
		int newNum = num;
		// 将D,Y,M 转换为DD, YY, MM
		if (1 == type.length())
			newType += type;

		if ("HH".equalsIgnoreCase(newType)) {
			newType = "MM";
			newNum *= 6;
		}

		return DateTimeUtil_.dateAdd(newType.toLowerCase(), date, newNum);
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2016年12月9日-上午9:23:12</li>
	 *         <li>功能说明：根据类型、日期计算两个日期间的间隔数(不对月对日)</li>
	 *         </p>
	 * @param type
	 *            day\dd\DD\d\D week\ww\WW\w\W month\mm\MM\m\M quarter\qq\QQ\q\Q
	 *            year\yy\YY\y\Y
	 * @param firstDate
	 *            第一个日期
	 * @param secondDate
	 *            第二个日期
	 * @return 第二个日期减第一个日期的值 第一个日期大于第二个为负 第一个日期小于第二个为正 相等为0
	 */
	public static int dateDiff(String type, String firstDate, String secondDate) {

		String newType = type;
		// 将D,Y,M 转换为DD, YY, MM
		if (1 == type.length()) {
			newType += type;
		}

		return DateTimeUtil_.dateDiff(newType.toLowerCase(), firstDate, secondDate);
	}

	/**
	 * @Author T
	 *         <p>
	 *         <li>2017年1月11日-上午10:57:02</li>
	 *         <li>功能说明:通过起止日期和期限类型,计算间隔的期间数（对月对日,丢弃小数）</li>
	 *         </p>
	 * @param cycle
	 *            周期
	 * @param startDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return term 期数
	 */
	public static int dateDiffByCycle(String cycle, String startDate, String endDate) {

		if (!isCycleString(cycle))
			throw APPUB.E0012(cycle);

		if (CommUtil.compare(startDate, endDate) > 0)
			throw APPUB.E0021(startDate, endDate);

		String newCycle = cycle;
		// 去负号
		if ("-".equals(cycle.substring(0, 1))) {
			newCycle = cycle.substring(1);
		}

		// 周期值
		int cycleValue = Integer.parseInt(newCycle.substring(0, newCycle.length() - 1));

		if (cycleValue == 0)
			return 0;

		// 周期单位
		String cycleUnit = newCycle.substring(newCycle.length() - 1);

		int ret = 0;
		if ("D".equals(cycleUnit)) {

			int term = dateDiff("D", startDate, endDate);

			ret = term / cycleValue;
		}
		else {

			String nextDate = calcDateByCycle(startDate, newCycle, false);

			while (CommUtil.compare(nextDate, endDate) <= 0) {
				ret = ret + 1;
				nextDate = calcDateByCycle(nextDate, newCycle, false);
			}
		}

		return ret;
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2016年12月22日-下午12:40:57</li>
	 *         <li>功能说明：检查日期是否在指定范围</li>
	 *         </p>
	 * @param date
	 *            需要检查的日期
	 * @param effectDate
	 *            生效日期
	 * @param includeEffectDate
	 *            是否包含生效日期
	 * @param expiryDate
	 *            失效日期
	 * @param includeExpiryDate
	 *            是否包含失效日期
	 * @return 日期是否在指定范围
	 */
	public static boolean dateBetween(String date, String effectDate, boolean includeEffectDate, String expiryDate, boolean includeExpiryDate) {
		int day1 = dateDiff("dd", date, expiryDate);
		int day2 = dateDiff("dd", effectDate, date);

		boolean isExpiryDate = day1 > 0 || (includeExpiryDate && day1 == 0);
		boolean isEffectDate = day2 > 0 || (includeEffectDate && day2 == 0);
		if (isExpiryDate && isEffectDate) {
			return true;
		}

		return false;
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2016年12月9日-上午9:27:09</li>
	 *         <li>功能说明：根据基准日期、类型计算最近日期</li>
	 *         </p>
	 * @param type
	 *            M-月末 Q-季末 Y-年末 H-半年末 T-旬末 D-返回基准日期 W-返回本周的周六日期
	 * @param date
	 *            基准日期 (字符串类型yyyyMMdd)
	 * @return
	 */
	public static String lastDay(String type, String date) {

		return DateTimeUtil_.lastDay(date, type.toUpperCase());
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2016年12月9日-上午9:29:30</li>
	 *         <li>功能说明：根据基准日期、类型计算最近的额前一个日期</li>
	 *         </p>
	 * @param type
	 *            M-月初 Q-季初 Y-年初 H-半年初 T-旬初 D-基准日期 W-本周开始的周日
	 * @param date
	 *            字符串日期yyyyMMdd
	 * @return
	 */
	public static String firstDay(String type, String date) {

		return DateTimeUtil_.firstDay(date, type.toUpperCase());
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2017年1月11-上午9:29:30</li>
	 *         <li>功能说明: 判断日期是对应期间的最后一个日期</li>
	 *         </p>
	 * @param type
	 *            M-月初 Q-季初 Y-年初 H-半年初 T-旬初 D-基准日期 W-本周开始的周日
	 * @param date
	 *            字符串日期yyyyMMdd
	 * @return
	 */
	public static boolean isLastDay(String type, String date) {

		if (CommUtil.isNull(date))
			return false;
		else
			return date.equals(lastDay(type, date));
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2017年1月12日-上午9:29:30</li>
	 *         <li>功能说明: 判断日期是对应期间的第一个日期</li>
	 *         </p>
	 * @param type
	 *            M-月初 Q-季初 Y-年初 H-半年初 T-旬初 D-基准日期 W-本周开始的周日
	 * @param date
	 *            字符串日期yyyyMMdd
	 * @return
	 */
	public static boolean isFirstDay(String type, String date) {

		if (CommUtil.isNull(date))
			return false;
		else
			return date.equals(firstDay(type, date));
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2016年12月9日-上午9:30:09</li>
	 *         <li>功能说明：获取日期成分</li>
	 *         </p>
	 * @param type
	 *            day\dd\DD\d\D week\ww\WW\w\W month\mm\MM\m\M quarter\qq\QQ\q\Q
	 *            year\yy\YY\y\Y
	 * @param date
	 *            基准日期 字符串日期(yyyyMMdd)
	 * @return
	 */
	public static int datePart(String type, String date) {

		String newType = type;
		// 将D 转换为DD
		if (1 == type.length())
			newType += type;

		return DateTimeUtil_.datePart(newType.toLowerCase(), date);
	}

	/**
	 * @Author T
	 *         <p>
	 *         <li>2014年3月11日-下午5:32:02</li>
	 *         <li>功能说明：检查当前日期所年是否闰年</li>
	 *         </p>
	 * @param date
	 *            字符串日期
	 * @return true - 是闰年 false - 不是闰年
	 */
	public static boolean isLeepYear(String date) {

		boolean ret = false;

		if (isDateString(date)) {

			int dateYear = Integer.parseInt(date.substring(0, 4));

			ret = (dateYear % 4 == 0 && dateYear % 100 != 0) || (dateYear % 400 == 0);
		}

		return ret;
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月7日-下午3:24:40</li>
	 *         <li>功能说明：获取公共运行变量</li>
	 *         </p>
	 * @return TrxEnvs.RunEnvs
	 */
	public static TrxEnvs.RunEnvs getTrxRunEnvs() {
		return SysUtil.getTrxRunEnvs();
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月8日-下午18:27:53</li>
	 *         <li>功能说明：根据证件号码生成校验位</li>
	 *         </p>
	 * @param idLoseCheck
	 * @return String
	 */
	public static String genChinaIdCardCheckBit(String idLoseCheck) {

		// 先判断idInfo是否为空，不为空再进行校验位检验
		char[] charId = idLoseCheck.toCharArray();

		int sum = 0;

		// 身份证每一位对应的系数
		int[] idCoef = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

		// 每位余数在身份证上的具体表现
		String[] checkBit = new String[] { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };

		// 系数求和
		for (int i = 0; i < charId.length; i++) {

			sum += (int) (charId[i] - '0') * idCoef[i];

		}

		// 求余
		int remain = sum % 11;

		// 返回身份证上的具体表现
		return checkBit[remain];
	}

	/**
	 * @Author T
	 *         <p>
	 *         <li>2016年12月13日-上午9:57:21</li>
	 *         <li>功能说明: 通过周期计算下一日期(自动对月处理,可用于计算到期日)</li>
	 *         </p>
	 * @param date
	 *            日期
	 * @param cycle
	 *            周期
	 * @return 下一日期
	 */
	public static String calcDateByCycle(String date, String cycle) {

		// 计算到期日时，无需对到月末
		return calcDateByCycle(date, cycle, false);
	}

	/**
	 * @Author T
	 *         <p>
	 *         <li>2016年12月13日-上午9:57:21</li>
	 *         <li>功能说明: 通过周期计算下一日期</li>
	 *         </p>
	 * @param date
	 *            日期
	 * @param cycle
	 *            周期
	 * @param monthEnd
	 *            是否月末对齐
	 * @return 下一日期
	 */
	private static String calcDateByCycle(String date, String cycle, boolean monthEnd) {
		if (!isDateString(date))
			throw APPUB.E0011(date);

		if (!isCycleString(cycle))
			throw APPUB.E0012(cycle);

		// 周期值
		int val = Integer.parseInt(cycle.substring(0, cycle.length() - 1));
		// 周期单位
		String unit = cycle.substring(cycle.length() - 1);

		String ret = dateAdd(unit, date, val);

		// 对月处理
		if (monthEnd && "M,Q,H,Y".indexOf(unit) >= 0 && isLastDay("M", date))
			ret = lastDay("M", ret);

		return ret;
	}

	/**
	 * @Author DELL
	 *         <p>
	 *         <li>2018年12月28日-下午10:21:53</li>
	 *         <li>功能说明: 按星期X计算算下一日期</li>
	 *         </p>
	 * @param refDate
	 *            参考日期
	 * @param curtDate
	 *            当前日期
	 * @param cycleValue
	 *            周期值
	 * @return
	 */
	private static String calcNextWeek(String refDate, String curtDate, int cycleValue) {

		final String WEEK_DAY = ",MON,TUE,WED,THU,FRI,SAT,SUN";
		int weekPosition = -1;
		String ret = "";

		// 1 5 9 13 17 21 25 星期一到星期日
		if (CommUtil.isNotNull(refDate))
			weekPosition = WEEK_DAY.indexOf(refDate);

		if (weekPosition < 0) {
			ret = dateAdd("D", curtDate, 7);
		}
		else {

			for (int i = 0; i <= 7; i++) {

				String tempDate = dateAdd("D", curtDate, i);

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(toDate(tempDate));

				int weekNum = calendar.get(Calendar.DAY_OF_WEEK) - 1;
				if (weekNum == 0)
					weekNum = 7;

				if (weekNum * 4 - 3 == weekPosition) {
					ret = tempDate;
					break;
				}
			}
		}

		// 顺推时,计算出的返回日期比curDate小，再往后推一个周期
		if (cycleValue >= 0 && CommUtil.compare(ret, curtDate) <= 0) {
			ret = calcDateByCycle(ret, String.valueOf(1) + "W", false);
		}

		// 倒推时,计算出的返回日期比curDate大，再往前推一个周期
		if (cycleValue < 0 && CommUtil.compare(ret, curtDate) >= 0) {
			ret = calcDateByCycle(ret, String.valueOf(-1) + "W", false);
		}

		if (cycleValue > 1) {
			ret = calcDateByCycle(ret, String.valueOf(cycleValue - 1) + "W", false);
		}
		else if (cycleValue < -1) {
			ret = calcDateByCycle(ret, String.valueOf(cycleValue + 1) + "W", false);
		}

		return ret;
	}

	/**
	 * @Author DELL
	 *         <p>
	 *         <li>2018年12月29日-上午10:21:53</li>
	 *         <li>功能说明: 依据当前日期和参考日期计算周期内的第一天</li>
	 *         </p>
	 * @param curtDate
	 *            当前日期
	 * @param refDate
	 *            参考日期
	 * @param type
	 *            周期类型
	 * @param mmdd
	 *            参考日期MMDD(返回参数)
	 * @return
	 */
	private static String firstDayByReference(String curtDate, String refDate, String type, StringBuffer mmdd) {

		String firstDay = firstDay(type, curtDate);

		if ("M,Q,H,Y".indexOf(type) >= 0) {

			// 容错性处理: 参考日期有可能输入的是2位,也可能是4位,也可是8位能没输入
			if (CommUtil.isNull(refDate)) {
				mmdd.append("0101");
			}
			else if (refDate.length() == 2) {
				mmdd.append("01").append(refDate);
			}
			else if (refDate.length() == 4) {
				mmdd.append(refDate);
			}
			else if (refDate.length() == 8) {
				mmdd.append(refDate.substring(4));
			}
			else {
				mmdd.append("0101");
			}

			// 季初、半年、年初需要参考月份
			if ("Q,H,Y".indexOf(type) >= 0) {

				// 开始计算月份
				int maxMonth = 0;

				if ("Q".equals(type)) {
					maxMonth = 3;
				}
				else if ("H".equals(type)) {
					maxMonth = 6;
				}
				else if ("Y".equals(type)) {
					maxMonth = 12;
				}

				int month = Integer.parseInt(mmdd.substring(0, 2));
				int mod = month % maxMonth;

				// 将startDate推到参考日期所在月份
				if (mod == 1) {
					// 参考日期是Q,H,Y的第一个月, startDate 不需要处理
					;
				}
				else if (mod == 0) {
					// 参考日期是最后一个月，得在startDate的基础上追加到 maxMonth
					firstDay = calcDateByCycle(firstDay, String.valueOf(maxMonth - 1) + "M", false);
				}
				else {
					// 其他情况下，在startDate的基础上追加到 mod
					firstDay = calcDateByCycle(firstDay, String.valueOf(mod - 1) + "M", false);
				}
			}

		}

		return firstDay;
	}

	/**
	 * @Author DELL
	 *         <p>
	 *         <li>2016年12月13日-下午6:21:53</li>
	 *         <li>功能说明: 按周期单位计算下一日期</li>
	 *         </p>
	 * @param refDate
	 *            参考日期
	 * @param curtDate
	 *            当前日期
	 * @param cycleUnit
	 *            周期单位 M,Q,H,Y
	 * @param cycleValue
	 *            周期值
	 * @return
	 */
	private static String calcNextDate(String refDate, String curtDate, String cycleUnit, int cycleValue, boolean delay) {

		String ret = "";
		StringBuffer mmdd = new StringBuffer("");
		String startDate = firstDayByReference(curtDate, refDate, cycleUnit, mmdd);

		// 期末的处理
		if ("END".equals(refDate)) {
			// 先推一个周期
			ret = lastDay(cycleUnit, startDate);

			// 顺推日期时, 当前日>= 计算出日期, 则继续往后面推一周期
			if (CommUtil.compare(curtDate, ret) >= 0 && cycleValue >= 0) {
				ret = calcDateByCycle(ret, String.valueOf(1) + cycleUnit, true);
			}

			// 倒推日期时，当前日<= 计算出日期, 则继续往前面推一周期
			if (CommUtil.compare(curtDate, ret) <= 0 && cycleValue < 0) {
				ret = calcDateByCycle(ret, String.valueOf(-1) + cycleUnit, true);
			}

			// 推剩下的周期
			if (cycleValue > 1) {
				ret = calcDateByCycle(ret, String.valueOf(cycleValue - 1) + cycleUnit, true);
			}
			else if (cycleValue < -1) {
				ret = calcDateByCycle(ret, String.valueOf(cycleValue + 1) + cycleUnit, true);
			}
		}
		else {

			ret = startDate.substring(0, 6) + mmdd.substring(2);
			// 检查日期合法性,不合法取最后一天
			if (!isDateString(ret, "yyyyMMdd")) {
				ret = lastDay("M", startDate);
				if (delay) {
					ret = dateAdd("D", ret, 1);
				}
			}

			// 顺推日期时, 当前日>= 计算出日期, 则继续往后面推一周期
			if (CommUtil.compare(curtDate, ret) >= 0 && cycleValue >= 0) {
				ret = calcDateByCycle(ret, String.valueOf(1) + cycleUnit, false);
				ret = ret.substring(0, 6) + mmdd.substring(2);

				if (!isDateString(ret, "yyyyMMdd")) {
					ret = dateDelay(ret, delay);
				}
			}

			// 倒推日期时，当前日<= 计算出日期, 则继续往前面推一周期
			if (CommUtil.compare(curtDate, ret) <= 0 && cycleValue < 0) {
				ret = calcDateByCycle(ret, String.valueOf(-1) + cycleUnit, false);
				ret = ret.substring(0, 6) + mmdd.substring(2);

				if (!isDateString(ret, "yyyyMMdd")) {
					ret = dateDelay(ret, delay);
				}
			}

			// 推剩下的周期
			if (cycleValue > 1) {
				ret = calcDateByCycle(ret, String.valueOf(cycleValue - 1) + cycleUnit, true);
				ret = ret.substring(0, 6) + mmdd.substring(2);

				if (!isDateString(ret, "yyyyMMdd")) {
					ret = dateDelay(ret, delay);
				}
			}
			else if (cycleValue < -1) {
				ret = calcDateByCycle(ret, String.valueOf(cycleValue + 1) + cycleUnit, true);
				ret = ret.substring(0, 6) + mmdd.substring(2);

				if (!isDateString(ret, "yyyyMMdd")) {
					ret = dateDelay(ret, delay);
				}
			}
		}

		return ret;
	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2019年9月2日-下午6:21:53</li>
	 *         <li>功能说明: 按周期单位和案例日期计算下一日期</li>
	 *         </p>
	 * @param caseDate
	 *            案例日期
	 * @param curtDate
	 *            当前日期
	 * @param cycle
	 *            周期
	 * @param delay
	 *            延迟标志
	 * @return
	 */
	public static String calcNextDateByCaseDate(String caseDate, String curtDate, String cycle, boolean delay) {

		if (!isCycleString(cycle)) {
			throw APPUB.E0012(cycle);
		}

		if (!isDateString(caseDate, "yyyyMMdd")) {
			throw APPUB.E0011(caseDate);
		}

		if (!isDateString(curtDate, "yyyyMMdd")) {
			throw APPUB.E0011(curtDate);
		}

		// 周期值
		int cycleValue = Integer.parseInt(cycle.substring(0, cycle.length() - 1));

		// 周期单位
		String cycleUnit = cycle.substring(cycle.length() - 1);

		String ret = ""; // 返回日期
		int diffTerm = 0; // 日期间存期差数量
		int count = 0;

		if (CommUtil.compare(caseDate, curtDate) <= 0) {

			diffTerm = dateDiffByCycle(cycle, caseDate, curtDate);

			count = diffTerm * cycleValue;

			// 案例日期比curdate小, 则确保能往后推算日期
			if (count < 0) {
				count = -1 * count;
			}
		}
		else {

			diffTerm = dateDiffByCycle(cycle, curtDate, caseDate);

			count = diffTerm * cycleValue;

			// 案例日期比curdate大, 则确保能往前推算日期
			if (count > 0) {
				count = -1 * count;
			}
		}

		// 计算日期
		ret = BizUtil.calcDateByCycle(caseDate, String.valueOf(count).concat(cycleUnit), true);

		// 正向计算日期, 返回日期要比curdate大
		if (cycleValue >= 0) {

			if (CommUtil.compare(ret, curtDate) <= 0) {

				ret = BizUtil.calcDateByCycle(caseDate, String.valueOf(count + cycleValue).concat(cycleUnit), true);
			}
		}
		// 逆向计算日期, 返回日期要比curdate小
		else {

			if (CommUtil.compare(ret, curtDate) >= 0) {
				ret = BizUtil.calcDateByCycle(caseDate, String.valueOf(count + cycleValue).concat(cycleUnit), true);
			}
		}

		if (!isDateString(ret, "yyyyMMdd")) {
			ret = dateDelay(ret, delay);
		}

		return ret;
	}

	private static String dateDelay(String date, boolean delay) {
		return delay ? dateAdd("d", lastDay("M", date.substring(0, 6) + "01"), 1) : lastDay("M", date.substring(0, 6) + "01");
	}

	/**
	 * @Author T
	 *         <p>
	 *         <li>2016年12月13日-上午9:57:21</li>
	 *         <li>功能说明：按参考日期&指定日期计算下一日期(可用于计算结息日、还款日)</li>
	 *         <li>如遇0230情况，取当月最后一天0228</li>
	 *         <li>例如: 20160321 20160322 Q -> 20160621</li>
	 *         <li>例如: 20160221 20160701 H -> 20160721</li>
	 * @param refDate
	 *            参考日期
	 * @param curtDate
	 *            指定日期
	 * @param cycle
	 *            周期
	 * @return 下一日期
	 */
	public static String calcDateByReference(String refDate, String curtDate, String cycle) {
		return doCalcDateByReference(refDate, curtDate, cycle, false);
	}

	/**
	 * 基本功能同 calcDateByReference 遇到如 0230情况，向下个月顺延为0301
	 * 
	 * @param refDate
	 * @param curtDate
	 * @param cycle
	 * @return
	 */
	public static String calcDateByReference28(String refDate, String curtDate, String cycle) {
		return doCalcDateByReference(refDate, curtDate, cycle, true);
	}

	private static String doCalcDateByReference(String refDate, String curtDate, String cycle, boolean delay) {
		if (!isCycleString(cycle))
			throw APPUB.E0012(cycle);

		if (!isDateString(curtDate, "yyyyMMdd")) {
			throw APPUB.E0011(curtDate);
		}

		// 周期值
		int cycleValue = Integer.parseInt(cycle.substring(0, cycle.length() - 1));
		// 周期单位
		String cycleUnit = cycle.substring(cycle.length() - 1);

		String ret;

		// 日
		if ("D".equals(cycleUnit)) {

			ret = calcDateByCycle(curtDate, cycle, false);
		}
		// 周
		else if ("W".endsWith(cycleUnit)) {

			ret = calcNextWeek(refDate, curtDate, cycleValue);
		}
		// 月、季度、半年、年
		else {
			ret = calcNextDate(refDate, curtDate, cycleUnit, cycleValue, delay);
		}

		return ret;
	}

	/**
	 * @Author T
	 *         <p>
	 *         <li>2016年12月13日-上午9:57:21</li>
	 *         <li>功能说明：按参考日期&系统日期计算下一日期</li>
	 *         <li>例如: 20160321 20160322 Q -> 20160621</li>
	 *         <li>例如: 20160221 20160701 H -> 20160721</li>
	 * @param refDate
	 *            参考日期
	 * @param cycle
	 *            周期
	 * @return 下一日期
	 */
	public static String calcDateByRefernce(String refDate, String cycle) {

		return calcDateByReference(refDate, getTrxRunEnvs().getTrxn_date(), cycle);
	}

	/**
	 * @Author T
	 *         <p>
	 *         <li>2016年12月13日-上午9:57:21</li>
	 *         <li>功能说明: 获取两个日期间的储蓄天数</li>
	 *         <li>整月按照30天计算,不跨月零头天数按照实际天数计算</li>
	 * @param startDate
	 *            起始日期
	 * @param endDate
	 *            结束日期
	 * @return 天数
	 */
	public static int calcSaveDays(String startDate, String endDate) {

		if (CommUtil.compare(startDate, endDate) < 0)
			throw APPUB.E0021(startDate, endDate);

		// 计算相差月数
		int monthNum = dateDiff("M", startDate, endDate);

		// 计算零头天数, 31号等同于30号处理
		int startDay = datePart("D", startDate);
		if (startDay == 31)
			startDay = 30;

		int endDay = datePart("D", endDate);
		if (endDay == 31)
			endDay = 30;

		int ret;

		// 如结束日期是月底, 且起始零头天数大于结束零头日期的
		// 则按照整月计算，不考虑零头天数（如结束日期为2月底）
		if (startDay > endDay && isLastDay("M", startDate))
			ret = monthNum * 30;
		else
			ret = monthNum * 30 - startDay + endDay;

		return ret;
	}

	/***
	 * @Author zhangsl
	 *         <p>
	 *         <li>2017年1月19日-上午11:03:18</li>
	 *         <li>功能说明：根据周期字符串计算储蓄天数</li>
	 *         </p>
	 * @param cycle
	 * @return
	 */
	public static int calcSaveDaysByCycle(String cycle) {

		int iDays = 0;

		// 周期字符串合法性检查
		if (!isCycleString(cycle))
			throw APPUB.E0012(cycle);

		// 周期值
		int cycleValue = Integer.parseInt(cycle.substring(0, cycle.length() - 1));

		// 周期单位
		String cycleUnit = cycle.substring(cycle.length() - 1);

		// 计算储蓄天数
		if ("D".equals(cycleUnit)) {
			iDays = cycleValue;
		}
		else if ("W".equals(cycleUnit)) {
			iDays = cycleValue * 7;
		}
		else if ("M".equals(cycleUnit)) {
			iDays = cycleValue * 30;
		}
		else if ("Q".equals(cycleUnit)) {
			iDays = cycleValue * 90;
		}
		else if ("H".equals(cycleUnit)) {
			iDays = cycleValue * 180;
		}
		else if ("Y".equals(cycleUnit)) {
			iDays = cycleValue * 360;
		}

		return iDays;
	}

	/**
	 * @Author chensy
	 *         <p>
	 *         <li>2016年12月13日-上午9:57:21</li>
	 *         <li>功能说明：生成账\卡号校验位，模10隔位乘2加"校验位算法。</li>
	 *         </p>
	 * @param cardno
	 *            不含校验位的账号(卡号)
	 * @return 校验位
	 */
	public static int genCardnoCheckBit(String cardno) {

		fieldNotNull(cardno, "acct_no", "account no");

		String trimCardno = CommUtil.trim(cardno);

		if (!trimCardno.matches("^[0-9]*$")) {
			throw APPUB.E0009(trimCardno);
		}

		int sum = 0;
		int temp;
		char[] array = trimCardno.toCharArray();
		for (int i = 0; i < array.length; i++) {
			if (i % 2 == 0) {
				temp = Character.getNumericValue(array[i]) * 2;
				if (temp > 9) {
					temp = temp - 9;
				}
			}
			else {
				temp = Character.getNumericValue(array[i]);
			}
			sum += temp;
		}

		int parity = 10 - sum % 10;
		if (parity == 10) {
			parity = 0;
		}

		return parity;
	}

	/**
	 * @Author lianggx
	 *         <p>
	 *         <li>2016年12月13日-上午9:57:21</li>
	 *         <li>功能说明：生成客户号校验位，算法：[（所有偶数位的数字相加之和）* 3 +（所有奇数位的数字相加之和）] mod 23。</li>
	 *         </p>
	 * @param custno
	 *            不含校验位的客户号
	 * @return 校验位
	 */
	public static String genCustnoCheckBit(String custno) {

		custno = CommUtil.trim(custno);

		if (!custno.matches("^[0-9]*$")) {
			throw APPUB.E0009(custno);
		}

		int sum = 0;
		int iOdd = 0; // 基数
		int iEven = 0;// 偶数

		char[] array = custno.toCharArray();
		for (int i = 1; i <= array.length; i++) {
			if (i % 2 == 0) {
				iEven = (Character.getNumericValue(array[i - 1]) * 3);
				sum += iEven;
			}
			else {
				iOdd = Character.getNumericValue(array[i - 1]);
				sum += iOdd;
			}

		}

		int parity = sum % 23;

		return Integer.toString(parity);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月13日-下午3:42:26</li>
	 *         <li>功能说明：根据key获取公共运行变量的value</li>
	 *         </p>
	 * @param key
	 * @return value key对应公共运行变量的值 公共运行变量不存在，返回null
	 */
	public static Object getTrxRunEnvsValue(String key) {
		if (CommUtil.isNull(key))
			return null;

		Object value = null;
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		try {
			Method method = RunEnvs.class.getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1));
			value = method.invoke(runEnvs);
		}
		catch (Exception e) {
			// 取不到值则默认为空
			log.error("Get Trx RunEnvs Value failed. [%s]", e, e.getMessage());
		}
		return value;
	}

	/**
	 * @Author chensy
	 *         <p>
	 *         <li>2016年12月26日-下午3:56:22</li>
	 *         <li>功能说明：检查生效日期是否小于等于失效日期</li>
	 *         </p>
	 * @param effectDate
	 * @param expiryDate
	 */
	public static void checkEffectDate(String effectDate, String expiryDate) {

		if (CommUtil.isNotNull(effectDate) && CommUtil.isNotNull(expiryDate) && dateDiff("D", effectDate, expiryDate) < 0) {
			throw ApPubErr.APPUB.E0015(effectDate, expiryDate);
		}
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月9日-下午4:58:18</li>
	 *         <li>功能说明：字段不为空校验</li>
	 *         <li>为空就抛错</li>
	 *         </p>
	 * @param data
	 * @param fieldName
	 * @param fieldDesc
	 */
	public static void fieldNotNull(Object data, String fieldName, String fieldDesc) {

		if (CommUtil.isNull(data))
			throw ApPubErr.APPUB.E0001(fieldName, fieldDesc);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月10日-下午1:58:08</li>
	 *         <li>功能说明: 对象克隆</li>
	 *         </p>
	 * @param clazz
	 * @param obj
	 * @return
	 */
	public static <T> T clone(Class<T> clazz, T obj) {

		String s = SysUtil.serialize(obj);
		return (T) SysUtil.deserialize(s, clazz);
	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2017年1月19日-下午1:58:08</li>
	 *         <li>功能说明: 数据分组散列值(哈希值)计算</li>
	 *         </p>
	 * @param subKey
	 *            业务参数表中的sub_key字段值： main_key = "DATA_GROUP_HASH_VALUE"
	 * @param sourceSeq
	 *            来源流水号：如账号、冻结编号、流水号等
	 * @return 散列值(哈希值)
	 */
	public static long getGroupHashValue(String subKey, String sourceSeq) {

		// 数据拆分组数
		int groupNum = ApBaseBusinessParm.getIntValue("DATA_GROUP_HASH_VALUE", subKey);

		int len = (String.valueOf(groupNum).length()) * 2;

		// 只保留传入源流水号的2倍被除数长度
		String tempSeq = sourceSeq;

		if (sourceSeq.length() > len) {
			tempSeq = sourceSeq.substring(sourceSeq.length() - len);
		}

		// 返回的长整形值
		Long longValue = null;

		// 类型转换
		try {
			longValue = new Long(tempSeq);
		}
		catch (NumberFormatException e) {
			// 含有字符，将字符替换成数字
			char[] seqArray = tempSeq.toCharArray();

			String resultStr = "";

			int seqLen = seqArray.length;

			for (int k = 0; k < seqLen; k++) {

				if (!Character.isDigit(seqArray[k])) {

					int num = seqArray[k] - 65; // 字母在ASCII码中的值

					if (num < 0) {
						num = 0;
					}

					num = num % 10; // 只保留一位

					resultStr = resultStr.concat(String.valueOf(num));
				}
				else {
					resultStr = resultStr.concat(String.valueOf(seqArray[k]));
				}
			}

			// 得到转换后的整形数字
			longValue = new Long(resultStr);
		}

		// 整除取余得到哈希值: 哈希值从1开始
		long hashValue = longValue % groupNum + 1;

		return hashValue;
	}

	/**
	 * @Author <p>
	 *         <li>2014年3月4日-下午5:14:20<>
	 *         <li>功能说明：List多字段的正向排序。<>
	 *         </p>
	 * @param list
	 *            待排序的List对象
	 * @param isAsc
	 *            是否升序 true - 升序 false - 降序
	 * @param fields
	 *            排序字段数组，可变参数
	 */
	// @BusiComponent(name="listSort",longnme="List多字段的正向排序",type=Type.tech)
	public static <E> void listSort(List<E> list, final boolean isAsc, final String... fields) {

		Collections.sort(list, new Comparator<Object>() {
			public int compare(Object a, Object b) {

				int ret = 0;
				for (String field : fields) {

					// 获取每一个排序字段对应的值
					Object aValue = null;
					Object bValue = null;
					try {
						aValue = CommUtil.getProperty(a, field);
						bValue = CommUtil.getProperty(b, field);
					}
					catch (Exception e) {
						log.error("Get property error:" + e.getMessage(), e, new Object[0]);
					}
					// 比较
					ret = myCompare(aValue, bValue);
					if (ret != 0) {
						break;
					}
				}
				if (isAsc)
					return ret;
				else
					return -1 * ret;

			}
		});
	}

	/**
	 * @Author T
	 *         <p>
	 *         <li>2014年6月19日-下午8:11:01</li>
	 *         <li>功能说明：对象比较，仅支持String, int, BigDecimal, DefaultEnum</li>
	 *         <li>注：该方法视空与null相等</li>
	 *         </p>
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static int myCompare(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null)
			return 0;
		if (obj2 != null && obj1 == null) {
			if ("".equals(obj2)) {
				return 0;
			}
			return -1;
		}
		if (obj2 == null) {
			if ("".equals(obj1)) {
				return 0;
			}
			return 1;
		}
		if (BigDecimal.class.isAssignableFrom(obj1.getClass()) && BigDecimal.class.isAssignableFrom(obj2.getClass())) {
			return ((BigDecimal) obj1).compareTo((BigDecimal) obj2);
		}
		else if (String.class.isAssignableFrom(obj1.getClass()) && String.class.isAssignableFrom(obj2.getClass())) {
			return ((String) obj1).compareTo((String) obj2);
		}
		else if (Integer.class.isAssignableFrom(obj1.getClass()) && Integer.class.isAssignableFrom(obj2.getClass())) {
			return (Integer) obj1 - (Integer) obj2;
		}
		else if (Long.class.isAssignableFrom(obj1.getClass()) && Long.class.isAssignableFrom(obj2.getClass())) {
			return (int) ((Long) obj1 - (Long) obj2);
		}
		else if (DefaultEnum.class.isAssignableFrom(obj1.getClass()) && DefaultEnum.class.isAssignableFrom(obj2.getClass())) {
			return myCompare(((DefaultEnum) obj1).getValue(), ((DefaultEnum) obj2).getValue());
		}
		throw new IllegalArgumentException("unsupported type comparison");
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月4日-下午4:49:14</li>
	 *         <li>功能说明：判断数据是否有更改,不包含公共字段</li>
	 *         </p>
	 * @param before
	 * @param after
	 * @return true 有变动 false 无变动
	 */
	public static boolean dataIsChange(Object before, Object after) {
		Map<String, Object> beforeData = CommUtil.toMap(before);
		Map<String, Object> afterData = CommUtil.toMap(after);

		// 公共字段
		List<Field> commFields = OdbFactory.getTable(apb_field_normal.class).getFields();
		Set<String> commField = new HashSet<String>();
		for (Field field : commFields) {
			commField.add(field.getId());
		}

		for (Map.Entry<String, Object> entry : beforeData.entrySet()) {
			String key = entry.getKey();
			if (commField.contains(key)) {// 排除公共字段
				continue;
			}

			Object oldObject = entry.getValue();
			Object newObject = afterData.get(key);

			if (myCompare(oldObject, newObject) != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Author zoujh
	 *         <p>
	 *         <li>2017年2月13日-下午3:23:27</li>
	 *         <li>功能说明：以字符串最后一个字母为准，截取字符串</li>
	 * @param strParm
	 * @return CmSplitString
	 *         </p>
	 */
	public static ApSplitString spiltLastLetter(String strParm) {

		Long count = 0L;
		char splitParm[] = strParm.toCharArray();
		char temp = 'A';
		StringBuilder tempBuffer = new StringBuilder();

		// 定位最后一个字母出现的位置
		for (int i = splitParm.length - 1; i >= 0; i--) {

			if (CommUtil.compare(splitParm[i], temp) >= 0) {
				break;
			}

			count++;
			tempBuffer.append(splitParm[i]);
		}

		// 返回信息
		ApSplitString strSubInfo = SysUtil.getInstance(ApSplitString.class);

		strSubInfo.setFirst_Sub_Str(strParm.substring(0, splitParm.length - count.intValue()));// 截取前面部分
		String lastInfo = tempBuffer.reverse().toString();
		strSubInfo.setFirst_str_length((long) (splitParm.length - count.intValue()));// 前部分长度
		strSubInfo.setLast_str_length(count);// 后部分的长度
		String reg = "[0-9]" + "{" + count + "}";

		if (lastInfo.matches(reg)) {
			strSubInfo.setLast_Sub_Str(lastInfo);// 截取后面部分
		}
		else {
			throw ApBaseErr.ApBase.E0048(strParm, lastInfo);
		}

		return strSubInfo;

	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月27日-下午4:32:51</li>
	 *         <li>功能说明：获取交易输入区</li>
	 *         </p>
	 * @return
	 */
	public static Object getTrxInput() {
		return EngineContext.getRequestData().getInput();
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月27日-下午4:42:19</li>
	 *         <li>功能说明：输入转为json字符串</li>
	 *         </p>
	 * @param request
	 * @return
	 */
	public static String toJson(Object request) {
		return JsonUtil.format(request);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月2日-下午4:41:24</li>
	 *         <li>功能说明：json字符串解析为map</li>
	 *         </p>
	 * @param json
	 * @return
	 */
	public static Map<String, Object> parseJson(String json) {
		return JsonUtil.parse(json);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月2日-下午4:41:24</li>
	 *         <li>功能说明：json 字符串转换为实体对象</li>
	 *         </p>
	 * @param json
	 * @return
	 */
	public static <T> T parseEntity(String json, Class<T> type) {
		return JsonUtil.parseEntity(json, type);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月10日-下午4:41:24</li>
	 *         <li>功能说明:产生一个新的交易流水,并设置到公共运行区（仅适用于批量的单笔的、非批量调联机的process处理）</li>
	 *         </p>
	 * @param json
	 * @return
	 */
	public static void resetTrxnSequence() {

		RunEnvs runEnvs = SysUtil.getTrxRunEnvs();

		String trxnSeq = MsSystemSeq.getTrxnSeq();

		runEnvs.setTrxn_seq(trxnSeq);
		runEnvs.setBusi_seq(trxnSeq);
		runEnvs.setInitiator_seq(trxnSeq);

		// 其他数据重设
		runEnvs.setComputer_date(BizUtil.getComputerDate());
		runEnvs.setComputer_time(BizUtil.getComputerTime());
		runEnvs.setRuntime_seq(1L);// 运行期序号
		runEnvs.setAccounting_event_seq(1L); // 会计事件序号
		runEnvs.setCustom_parm(null);

		MsBuffer.getBuffer().clear();

	}

	/**
	 * @Author tsichang
	 *         <p>
	 *         <li>2017年4月24日-下午7:31:19</li>
	 *         <li>功能说明：执行服务</li>
	 *         <li>在被执行的服务(层)中使用新的rule_buffer缓存区，与调用层的rule_buffer是相对隔离的</li>
	 *         </p>
	 * @param method
	 * @param args
	 * @return
	 */
	private static Object callService(Method method, Object[] args) throws Exception {
		String srvFullId = getSrvFullId(method);

		Date starttime = new Date();
		RequestData requestData = EngineContext.getRequestData();
		ResponseData responseData = new ResponseData(null, new LinkedHashMap<String, Object>());
		InServiceController serviceController = new InServiceController(EngineContext.getEngineRuntimeContext().getInnerServiceCode(), EngineContext.getEngineRuntimeContext()
				.getInnerServiceImplCode(), ServiceCategory.S);
		OnlineEngineExtensionPoint extensionPoint = OnlineEngineConfigManager.get().getOnlineEngineExtensionPoint();

		OETHandlerContext context = new OETHandlerContext(starttime, requestData, responseData, serviceController, extensionPoint);

		/*
		 * final ServiceRequest req = new ServiceRequest(inputMap, new
		 * CustomServiceBind(IServiceControllerManager.DEFAULT_CALL_IDENTITY,
		 * srvFullId), controller); req.setNodeid(ApConst.UN_ONLINE_NODE); //
		 * 用于标注，以区分非联机交易中的服务节点
		 */
		ExtensionLoader.getExtensionLoader(OETBeforeHandler.class).getExtensionById(ApConstants.AP_SERVICE_PROCESS_BEFORE).handler(context);

		Object ret = null;
		try {
			ret = callServiceDirect(method, args);
			ExtensionLoader.getExtensionLoader(OETAfterHandler.class).getExtensionById(ApConstants.AP_SERVICE_PROCESS_AFTER).handler(context);
		}
		catch (final Exception e) {
			ExtensionLoader.getExtensionLoader(OETExceptionHandler.class).getExtensionById(ApConstants.AP_SERVICE_PROCESS_EXCEPTION).handler(context);
			log.error(CustomCommPluginConstant.AbstractMasterElector01, e, new Object[] { srvFullId });
			throw e;
		}
		return ret;
	}

	private static String getSrvFullId(Method m) {
		return m.getDeclaringClass().getSimpleName() + ApConst.PACKAGE_SEPERATE_DOT + m.getName();
	}

	/*
	 * private static Map<String, Object> genInputMap(Method method, Object[]
	 * args) { Map<String, Object> inputMap = new HashMap<String, Object>(); if
	 * (args != null && args.length > 0) { String srvFullId =
	 * getSrvFullId(method); Service srv = ServiceUtil.getByFullId(srvFullId);
	 * 
	 * Input input = srv.getDataInterface().getInput();
	 * 
	 * if (input.getPackMode() == true) { // input:packMode = true final
	 * boolean[] isInputInteface = new boolean[1]; for (Object obj : args) {
	 * Class<?> clz = obj.getClass(); isInputInteface[0] = false;
	 * 
	 * ReflectionUtil.doWithTypes(clz, new ReflectionUtil.TypeCallback() {
	 * 
	 * @Override public boolean doWith(Class<?> type) { if
	 * (CommUtil.compare("Input", type.getSimpleName()) == 0) {
	 * isInputInteface[0] = true; return false; } return true; } });
	 * 
	 * if (isInputInteface[0] == true) { dataIntfToMap(input, obj, inputMap);
	 * break; } } } else { // input:packMode = false int paramIdx = 0; for
	 * (Iterator<DataInterfaceElement> itor = input.getElements().iterator();
	 * itor.hasNext();) { DataInterfaceElement curElement = itor.next();
	 * inputMap.put(curElement.getId(), args[paramIdx++]); }
	 * 
	 * } } return inputMap; }
	 * 
	 * @SuppressWarnings("unused") private static Map<String, Object>
	 * genOutputMap(Method method, Object ret, Object... args) { Map<String,
	 * Object> outMap = new HashMap<String, Object>(); String srvFullId =
	 * getSrvFullId(method); Service srv = ServiceUtil.getByFullId(srvFullId);
	 * 
	 * Output output = srv.getDataInterface().getOutput();
	 * 
	 * if (method.getReturnType() == void.class && output.isOutputAsParm() ==
	 * false) { return outMap; }
	 * 
	 * if (output.isOutputAsParm() == false) {
	 * outMap.put(output.getElements().get(0).getId(), ret); } else if
	 * (output.getPackMode() == false) { return outMap; } else { final boolean[]
	 * isOutputInteface = new boolean[1]; for (Object obj : args) { Class<?> clz
	 * = obj.getClass(); isOutputInteface[0] = false;
	 * 
	 * ReflectionUtil.doWithTypes(clz, new ReflectionUtil.TypeCallback() {
	 * 
	 * @Override public boolean doWith(Class<?> type) { if
	 * (CommUtil.compare("Output", type.getSimpleName()) == 0) {
	 * isOutputInteface[0] = true; return false; } return true; } });
	 * 
	 * if (isOutputInteface[0] == true) { dataIntfToMap(output, obj, outMap);
	 * break; } } }
	 * 
	 * return outMap; }
	 */

	private static void dataIntfToMap(DataInterfaceElements dataInterface, Object src, Map<String, Object> dest) {
		for (Iterator<DataInterfaceElement> itor = dataInterface.getElements().iterator(); itor.hasNext();) {
			DataInterfaceElement curElement = itor.next();
			ModelPropertyDescriptor md = ModelPropertyDescriptor.get(src.getClass(), curElement.getId());

			dest.put(curElement.getId(), md.getProperty(src));
		}

	}

	/**
	 * @Author tsichang
	 *         <p>
	 *         <li>2017年4月26日-上午11:33:45</li>
	 *         <li>功能说明：直接执行服务方法</li>
	 *         </p>
	 * @param method
	 * @param args
	 * @return
	 */
	private static Object callServiceDirect(Method method, Object[] args) {
		Class<?> targetClz = method.getDeclaringClass();
		Object ret = null;
		try {
			ret = method.invoke(SysUtil.getInstance(targetClz), args);
		}
		catch (Exception e) {
			throw LangUtil.wrapThrow(e);
		}
		return ret;
	}

	/**
	 * @Author tsichang
	 *         <p>
	 *         <li>2017年4月25日-下午8:14:50</li>
	 *         <li>功能说明：判断服务类型被调用时是否需要创建新的rule_buffer缓存区</li>
	 *         </p>
	 * @param clz
	 * @return
	 */
	private static <T> boolean isServiceNeedNewBuffer(Class<T> clz) {
		List<app_service> services = App_serviceDao.selectAll_odb2(clz.getSimpleName(), E_YESORNO.YES, false);

		if (services.isEmpty()) {
			return false;
		}
		else {
			return true;
		}

	}

	/**
	 * @Author tsichang
	 *         <p>
	 *         <li>2017年4月25日-下午4:20:43</li>
	 *         <li>功能说明:获得类实例</li>
	 *         <li>对于服务，有下述处理：</li>
	 *         <li>以服务实例(服务方法)为单位，若在app_service中存在对应的服务:create_buffer_ind=Y，
	 *         则服务实例将开拓新的rule_buffer 缓存区，与调用层的rule_buffer相隔离</li>
	 *         </p>
	 * @param targetClz
	 *            类型
	 * @return T 实例
	 */
	public static <T> T getInstance(final Class<T> targetClz) {
		if (targetClz == null) {
			throw ApBaseErr.ApBase.E0043();
		}

		ConfigType ct = (ConfigType) targetClz.getAnnotation(ConfigType.class);
		if (ct == null || ConfigType.Type.service != ct.type()) { // 非服务类型
			return SysUtil.getInstance(targetClz);
		}

		if (isServiceNeedNewBuffer(targetClz) == false) { // 服务类型且不含需开辟新rule_buffer缓存区
			return SysUtil.getInstance(targetClz);
		}

		Object bizSrvObj;
		if (srvCache.get(targetClz) != null) {
			bizSrvObj = srvCache.get(targetClz);
		}
		else {
			bizSrvObj = Proxy.newProxyInstance(SysUtil.class.getClassLoader(), new Class[] { targetClz }, new InvocationHandler() {
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					app_service serviceCtrl = App_serviceDao.selectOne_odb1(targetClz.getSimpleName(), method.getName(), false);

					// 如果服务方法未配置需要开辟新的Rule_Buffer缓存区，则无需用"回调点的方式"执行
					if (serviceCtrl == null || serviceCtrl.getCreate_buffer_ind() == E_YESORNO.NO) {
						return callServiceDirect(method, args);
					}
					else {
						return callService(method, args);
					}

				}

			});

			srvCache.put(targetClz, bizSrvObj);
		}

		@SuppressWarnings("unchecked")
		T bizSrvImpl = (T) bizSrvObj;

		return (T) bizSrvImpl;
	}

	/**
	 * 对传入的list做分页处理
	 * 
	 * @param data
	 * @return
	 */
	public static <T> List<T> list2Page(List<T> data) {
		// 分页处理
		List<T> ret = new ArrayList<T>();
		long size = data.size();

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		runEnvs.setTotal_count(size);

		long start = runEnvs.getPage_start();
		long count = runEnvs.getPage_size();
		for (long i = start; i < start + count; i++) {
			if (i < size) {
				ret.add(data.get((int) i));
			}
		}
		return ret;
	}

	/**
	 * 使用变量来抛错,不支持带参数
	 * 
	 * @param packages
	 *            包路径
	 * @param method
	 *            方法名
	 * @throws LttsBusinessException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void throwBizException(String packages, String method) throws LttsBusinessException {
		Class clazz;
		try {
			clazz = Class.forName(packages);
			Method m = clazz.getMethod(method);
			m.invoke(null);
		}
		catch (InvocationTargetException e) {
			log.error("Invocation Target Failed! [%s]", e, e.getMessage());
			Throwable targetException = e.getTargetException();
			throw new LttsBusinessException(targetException.getMessage());
		}
		catch (Exception ee) {
			log.error("Throw BizException Failed! [%s]", ee, ee.getMessage());
			throw new IllegalArgumentException(ee.getMessage());
		}
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>remark：This method is to get the host ip.</li>
	 *         </p>
	 * @return
	 */
	public static String getHostIp() {

		return CoreUtil.getIp();
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>remark：This method is to get the host name.</li>
	 *         </p>
	 * @return
	 */
	public static String getHostName() {

		return CoreUtil.getHostName();
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>remark：This method is to get the application name.</li>
	 *         </p>
	 * @return
	 */
	public static String getAppName() {

		return CoreUtil.getVmid();
	}

	/**
	 * @Author liuzf@sunline.cn
	 *         <p>
	 *         <li>remark：This method is used to convert a particular format
	 *         (${xxx}) variable.</li>
	 *         </p>
	 * @return
	 */
	public static String convertParmString(String str) {
		if (CommUtil.isNotNull(str)) {
			StringBuffer sb = new StringBuffer();
			Pattern p = Pattern.compile("(\\$\\{[^\\$^\\{^\\}]+\\})");
			Matcher m = p.matcher(str);
			while (m.find()) {
				String var = m.group().replace("${", "").replace("}", "");
				String tmpVar = "";
				if (CommUtil.isNotNull(System.getenv(var)))
					tmpVar = System.getenv(var);
				if (CommUtil.isNotNull(System.getProperty(var)))
					tmpVar = System.getProperty(var);
				if (tmpVar.contains(File.separator) && File.separatorChar == '\\')
					tmpVar = tmpVar.replaceAll("\\\\", "\\\\\\\\");
				m.appendReplacement(sb, tmpVar);
			}
			m.appendTail(sb);
			return sb.toString();
		}
		return null;
	}

	/**
	 * two int type for decimal
	 * 
	 * @return String
	 */
	public static String getDecimal(int dividends, int divisor) {

		NumberFormat numberFormat = NumberFormat.getInstance();

		numberFormat.setMaximumFractionDigits(2);

		return numberFormat.format((float) dividends / (float) divisor * 100);
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         </p>
	 *         get language type
	 * @return
	 */
	public static String getlanguaheType() {
		return LttsCoreBeanUtil.getInternationalManager().getCurrentLanguage().getCountry();
	}

	/**
	 * md5 function
	 * 
	 * @param str
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String md5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str.getBytes("utf-8"));
		return CommUtil.lpad(new BigInteger(1, md.digest()).toString(16), 32, "0");
	}

	/**
	 * check reference date
	 * 
	 * @param cycle
	 * @param date
	 * @throws LttsBusinessException
	 */
	public static void checkReferenceDate(String cycle, String date) throws LttsBusinessException {
		ApRefDate.checkReferenceDate(cycle, date);
	}

	@SuppressWarnings({ "unchecked" })
	public static Stack<Map<String, Object>> getRuleBuffer() {
		Object ruleBufferObj = BizUtil.getTrxRunEnvs().getRule_buffer();

		Stack<Map<String, Object>> ruleBufferStack = null;
		if (ruleBufferObj == null) {
			ruleBufferStack = new Stack<Map<String, Object>>();
			BizUtil.getTrxRunEnvs().setRule_buffer(ruleBufferStack);
		}
		else if (ruleBufferObj instanceof Stack) {

			ruleBufferStack = (Stack<Map<String, Object>>) ruleBufferObj;

		}
		else if (ruleBufferObj instanceof List) {
			ruleBufferStack = new Stack<Map<String, Object>>();
			ruleBufferStack.addAll((List) ruleBufferObj);
			BizUtil.getTrxRunEnvs().setRule_buffer(ruleBufferStack);

		}
		else {
			throw LangUtil.wrapThrow("unknow class type :" + ruleBufferObj.getClass());
		}
		return ruleBufferStack;
	}

	public static boolean isNeedSetupNewBuffer(OETHandlerContext context) {

		String serviceOwnerName = context.getInServiceController().getServiceModel().getOwner().getId();
		String serviceMethod = context.getInServiceController().getServiceModel().getId();

		app_service serviceCtrl = App_serviceDao.selectOne_odb1(serviceOwnerName, serviceMethod, false);

		if (serviceCtrl != null && serviceCtrl.getCreate_buffer_ind() == E_YESORNO.YES) {
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean isPartialReversalBuzi(OETHandlerContext context) {
		if (CommUtil.isNotNull(context) && PARTIAL_REVERSAL_SERVICE_LST.contains(context.getInServiceController().getInnerServiceCode())) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @Author zhx
	 *         <p>
	 *         <li>2019年7月30日-下午3:56:50</li>
	 *         <li>功能说明：获取账户路由信息</li>
	 *         </p>
	 * @param sAcctNo
	 *            账号
	 * @param cashTrxnInd
	 *            现转标志
	 * @param suspenseNo
	 *            挂账编号
	 * @return 账户路由信息
	 */
	public static ApAccountRouteInfo getAccountRouteInfo(String sAcctNo, E_CASHTRXN cashTrxnInd, String suspenseNo) {

		ApAccountRouteInfo cplOut = BizUtil.getInstance(ApAccountRouteInfo.class);

		// 1.现转标志为现金
		if (cashTrxnInd == E_CASHTRXN.CASH) {
			cplOut.setAcct_analy(E_ACCOUTANALY.CASH);
			return cplOut;
		}

		// 2.挂账编号有值
		if (CommUtil.isNotNull(suspenseNo)) {
			cplOut.setAcct_analy(E_ACCOUTANALY.SUSPENSE);
			return cplOut;
		}

		if (!ApSystemParmApi.exists("ACCROUNT", String.valueOf(sAcctNo.length()))) {
			cplOut.setAcct_analy(E_ACCOUTANALY.DEPOSIT);
			return cplOut;
		}

		// 3.账号有值，根据账号长度获取账号类型
		String routeType = ApSystemParmApi.getValue("ACCROUNT", String.valueOf(sAcctNo.length()));

		if (CommUtil.compare(routeType, E_ACCTROUTETYPE.BUSINESSCODE.getValue()) == 0) {
			// 若为业务编码，判断是否为内部户或挂账户
			if (CommUtil.compare(String.valueOf(sAcctNo.subSequence(0, 1)), E_INSIDEACCTTYPE.NORMAL.getValue()) == 0) {
				cplOut.setAcct_analy(E_ACCOUTANALY.INSIDE);
			}
			else if (CommUtil.compare(String.valueOf(sAcctNo.subSequence(0, 1)), E_INSIDEACCTTYPE.SUSPENSE.getValue()) == 0) {
				cplOut.setAcct_analy(E_ACCOUTANALY.SUSPENSE);
			}

			cplOut.setAcct_no(null); // 如果是业务编码账号赋空值
			cplOut.setGl_ref_code(sAcctNo);
			return cplOut;
		}
		else if (CommUtil.compare(routeType, E_ACCTROUTETYPE.CARD.getValue()) == 0) {
			cplOut.setAcct_analy(E_ACCOUTANALY.DEPOSIT);
		}
		else if (CommUtil.compare(routeType, E_ACCTROUTETYPE.INSIDE.getValue()) == 0) {
			cplOut.setAcct_analy(E_ACCOUTANALY.INSIDE);
			cplOut.setAcct_no(sAcctNo);
		}
		else if (CommUtil.compare(routeType, E_ACCTROUTETYPE.SUSPENSE.getValue()) == 0) {
			cplOut.setAcct_analy(E_ACCOUTANALY.SUSPENSE);
			cplOut.setAcct_no(sAcctNo);
		}
		else {
			cplOut.setAcct_analy(E_ACCOUTANALY.DEPOSIT);
		}

		return cplOut;
	}
}
