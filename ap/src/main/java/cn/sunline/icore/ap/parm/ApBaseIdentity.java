package cn.sunline.icore.ap.parm;

import java.util.List;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType;
import cn.sunline.icore.ap.namedsql.ApBasicBaseDao;
import cn.sunline.icore.ap.tables.TabApBasic;
import cn.sunline.icore.ap.tables.TabApBasic.App_identityDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_identity;
import cn.sunline.icore.ap.type.ComApBasic;
import cn.sunline.icore.ap.type.ComApBasic.ApIdentityInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApErr.AP;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.icore.sys.type.EnumType.E_CHECKBITTYPE;
import cn.sunline.icore.sys.type.EnumType.E_SUITABLECUSTTYPE;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;

/**
 * <p>
 * 文件功能说明：证件类型
 * </p>
 * 
 * @Author liucong
 *         <p>
 *         <li>2016年12月5日-下午10:14:15</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 liucong：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseIdentity {

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午1:58:50</li>
	 *         <li>功能说明：判断证件类型是否存在，无对应记录时返回false</li>
	 *         </p>
	 * @param idType
	 * @return 存在返回true，不存在返回false.
	 */
	public static boolean exists(String idType) {

		app_identity idInfo = App_identityDao.selectOne_odb1(idType, MsOrg.getReferenceOrgId(app_identity.class), false);

		// 没有记录返回false，否则返回true;
		return (idInfo == null) ? false : true;
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午2:02:37</li>
	 *         <li>功能说明：获取一个证件类型定义对象</li>
	 *         </p>
	 * @param idType
	 * @return 不为空返回证件类型定义对象，否则抛出异常
	 * @throws APPUB.E0005
	 *             () 获取[${tableDesc}]失敗,无对应记录,数据键值[${keyValue}]
	 */
	public static ApIdentityInfo getItem(String idType) {

		// 根据证件类型，获取证件类型定义对象
		app_identity idInfo = App_identityDao.selectOne_odb1(idType, MsOrg.getReferenceOrgId(app_identity.class), false);

		if (idInfo == null) {

			throw APPUB.E0005(OdbFactory.getTable(app_identity.class).getLongname(), SysDict.A.doc_type.getLongName(), idType);
		}

		ApIdentityInfo identityInfo = BizUtil.getInstance(ApIdentityInfo.class);
		
		identityInfo.setDoc_type(idInfo.getDoc_type());
		identityInfo.setDoc_desc(idInfo.getDoc_desc());
		identityInfo.setCheck_bit_rule(idInfo.getCheck_bit_rule());
		identityInfo.setCheck_rules1(idInfo.getCheck_rules1());
		identityInfo.setCheck_rules2(idInfo.getCheck_rules2());
		identityInfo.setCheck_rules3(idInfo.getCheck_rules3());
		identityInfo.setData_create_time(idInfo.getData_create_time());
		identityInfo.setData_create_user(idInfo.getData_create_user());
		identityInfo.setData_update_time(idInfo.getData_update_time());
		identityInfo.setData_update_user(idInfo.getData_update_user());
		identityInfo.setData_version(idInfo.getData_version());
		
		return identityInfo;
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月7日-上午11:04:43</li>
	 *         <li>功能说明：校验证件号码是否符合规则</li>
	 *         </p>
	 * @param idNo
	 * @param idInfo
	 * @return boolean
	 */
	private static boolean checkRules(String idNo, ApIdentityInfo idInfo) {

		boolean result = false;
		boolean checkFlag = false;

		// 满足三个之一就可返回true，否则返回false
		if (CommUtil.isNotNull(idInfo.getCheck_rules1())) {

			result = idNo.matches(idInfo.getCheck_rules1());
			checkFlag = true;
		}

		if (!result && CommUtil.isNotNull(idInfo.getCheck_rules2())) {

			result = idNo.matches(idInfo.getCheck_rules2());
			checkFlag = true;
		}

		if (!result && CommUtil.isNotNull(idInfo.getCheck_rules3())) {

			result = idNo.matches(idInfo.getCheck_rules3());
			checkFlag = true;
		}

		// 三个检查规则都为空，返回 true
		if (checkFlag == false) {

			return true;
		}

		return result;
	}

	/**
	 * 
	 * @Author jollyja
	 *         <p>
	 *         <li>2016年12月10日-下午12:08:25</li>
	 *         <li>功能说明：证件号校验位规则检查</li>
	 *         </p>
	 * @param idNo
	 * @param idInfo
	 * @return
	 */
	private static boolean checkBitRule(String idNo, E_CHECKBITTYPE checkBitType) {
		
		// 中国居民身份证校验位类型检查
		if (E_CHECKBITTYPE.CHNIDCARD18 == checkBitType ) {

			// 只有二代身份证才校验
			if (idNo.length() == 18)
				return idNo.substring(17).equalsIgnoreCase(BizUtil.genChinaIdCardCheckBit(idNo.substring(0, 17)));
		
		} else {
			// 其他校验规则，待补充
			;
		}
		
		return true;
	}
	
	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午2:00:08</li>
	 *         <li>检查证件种类对是否合法，不合法则报错，合法则继续检查证件号码的格式是否有效，无效则报错。</li>
	 *         </p>
	 * @param idType
	 * @param idNo
	 * @throws APPUB.E0005
	 *             () 获取[${tableDesc}]失敗,无对应记录,数据键值[${keyValue}]
	 * @throws APPUB.E0001
	 *             () 信息域不允许为空,[${fieldName}-${fieldDesc}]
	 * @throws AP.E0006
	 *             () 证件号码的校验位错误
	 * @throws AP.E0003
	 *             () 证件号码不符合规则
	 */
	public static void isValid(String idType, String idNo) {

		// 证件号码是否为空检测
		if (CommUtil.isNull(idNo)) {
			// idNo为空
			throw APPUB.E0001(SysDict.A.doc_no.getId(), SysDict.A.doc_no.getLongName());
		}
		
		// 检验证件的合法性和有效性
		ApIdentityInfo idInfo = getItem(idType);

		// 证件号规则检查，不满足则抛错
		if (!(checkRules(idNo, idInfo))) {
			// 证件号码不符合规则
			throw ApBaseErr.ApBase.E0003(idNo);
		}

		// 证件号校验位规则检查
		if (!(checkBitRule(idNo, idInfo.getCheck_bit_rule()))) {
			// 证件号码的校验码错误
			throw ApBaseErr.ApBase.E0006(idNo);
		}
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午2:04:43</li>
	 *         <li>功能说明：获取某个证件类型的名称</li>
	 *         </p>
	 * @param idType
	 * @return idDesc 返回证件描述
	 */
	public static String getName(String idType) {

		// 返回描述证件类型名称
		return getItem(idType).getDoc_desc();
	}

	/**
	 * @Author liucong
	 *         <p>
	 *         <li>2016年12月6日-下午2:03:00</li>
	 *         <li>功能说明：获取全部证件类型定义对象</li>
	 *         </p>
	 * @return
	 */
	public static List<TabApBasic.app_identity> all() {
		// TODO 获取全部证件类型，由于odb操作限制，暂不实现，有需要再通过namedsql实现
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @Author meizhian
	 *         <p>
	 *         <li>2019年4月28日-上午9:46:00</li>
	 *         <li>功能说明：获取全部证件类型定义对象</li>
	 *         </p>
	 * @param custType	客户类型
	 * @param isProvide 必输标志
	 * @return 证件类型定义
	 */
	public static List<ComApBasic.ApIdentityInfo> getIdentityList(E_SUITABLECUSTTYPE custType,MsEnumType.E_YESORNO isProvide) {
		
		// 根据适用客户类型和必输标志动态查询列表
		List<ComApBasic.ApIdentityInfo> identityInfoList = ApBasicBaseDao.selIdTypeListByProvideInd(MsOrg.getReferenceOrgId(app_identity.class), custType, isProvide, false);
		
		return identityInfoList;
		
	}
}