package cn.sunline.icore.ap.branch;

import java.util.ArrayList;
import java.util.List;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.namedsql.ApBranchBaseDao;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branchDao;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branch_juniorDao;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branch_relationDao;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branch_seniorDao;
import cn.sunline.icore.ap.tables.TabApBranch.App_branch_relationDao;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch_junior;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch_relation;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch_senior;
import cn.sunline.icore.ap.tables.TabApBranch.app_branch_relation;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchInfo;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchLevel;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;

public class ApBaseBranch {

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月15日-下午17:25/li>
	 *         <li>功能说明：判断机构号是否存在，存在返回true，不存在返回false</li>
	 *         </p>
	 * @param branchId
	 * @return
	 */
	public static boolean exists(String branchId) {

		return exists(branchId, false);

	}
	
	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月15日-下午17:25/li>
	 *         <li>功能说明：判断机构号是否存在，存在返回true，不存在返回false</li>
	 *         </p>
	 * @param branchId
	 * @return
	 */
	public static boolean exists(String branchId, boolean error) {
		apb_branch branch = Apb_branchDao.selectOne_odb1(branchId, false);
		if(branch == null && error){
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(apb_branch.class).getLongname(), SysDict.A.branch_id.getLongName(), branchId);
		}
		
		return (branch == null) ? false : true;

	}
	
	/**
	 * 根据法人代码，机构号来判断，法人可能不是公共运行变量的法人
	 * @param orgId
	 * @param branchId
	 * @return
	 */
	public static boolean exists(String orgId, String branchId) {

		return (ApBranchBaseDao.selBranchByOrgAndBrchId(orgId, branchId, false) == null) ? false : true;

	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月15日-下午17:25/li>
	 *         <li>功能说明：根据机构号返回机构信息对象，不存在则抛出错误信息</li>
	 *         </p>
	 * @param branchId
	 * @return ApBranchInfo
	 */

	public static ApBranchInfo getItem(String branchId) {

		apb_branch branch = Apb_branchDao.selectOne_odb1(branchId, false);

		if (branch == null) 
			throw APPUB.E0005(OdbFactory.getTable(apb_branch.class).getLongname(), SysDict.A.branch_id.getLongName(), branchId);

		ApBranchInfo ret = BizUtil.getInstance(ApBranchInfo.class);

		ret.setBranch_id(branch.getBranch_id());// 机构代码
		ret.setBranch_name(branch.getBranch_name());// 机构名称
		ret.setReal_branch_ind(branch.getReal_branch_ind());// 机构实体引用标志
		ret.setBranch_address(branch.getBranch_address());// 机构地址
		ret.setBranch_phone(branch.getBranch_phone());// 机构电话
		ret.setBranch_mail(branch.getBranch_mail());
		ret.setPostcode(branch.getPostcode());// 邮政编码
		ret.setContacts_name(branch.getContacts_name());// 联系人
		ret.setContacts_phone(branch.getContacts_phone());// 联系人电话
		ret.setContacts_mail(branch.getContacts_mail());
		ret.setBranch_function_class(branch.getBranch_function_class());// 机构职能分类
		ret.setSwift_no(branch.getSwift_no());// swift号
		ret.setHoliday_code(branch.getHoliday_code());// 假日代码
		ret.setData_create_time(branch.getData_create_time());
		ret.setData_create_user(branch.getData_create_user());
		ret.setData_update_time(branch.getData_update_time());
		ret.setData_update_user(branch.getData_update_user());
		ret.setData_version(branch.getData_version());

		return ret;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月16日-上午09:43/li>
	 *         <li>功能说明：通过机构号获取假日代码，假日代码为空时抛出异常</li>
	 *         </p>
	 * @param branchId
	 * @return holiday_code
	 */
	public static String getHolidayCode(String branchId) {

		ApBranchInfo branchInfo = ApBaseBranch.getItem(branchId);

		if (CommUtil.isNull(branchInfo.getHoliday_code())) {
			throw ApBaseErr.ApBase.E0019(branchId);
		}

		return branchInfo.getHoliday_code();
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月16日-上午09:43/li>
	 *         <li>功能说明：通过机构代码获取swift_no，swift_no 为空时抛出异常</li>
	 *         </p>
	 * @param branchId
	 * @return swift_id
	 */
	public static String getSwiftNo(String branchId) {

		ApBranchInfo branchInfo = ApBaseBranch.getItem(branchId);

		if (CommUtil.isNull(branchInfo.getSwift_no())) {
			throw ApBaseErr.ApBase.E0014(branchId);
		}

		return branchInfo.getSwift_no();

	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月21日-上午09:43/li>
	 *         <li>功能说明：按机构关系对币种进行转换，机构关系区分币种则原样返回，否则返回通配符</li>
	 *         </p>
	 * @param branchRelationCode
	 * @param ccy
	 * @return String
	 */
	public static String convertToRelationCcy(String branchRelationCode, String ccy) {

		app_branch_relation branchRelation = App_branch_relationDao.selectOne_odb1(branchRelationCode, false);

		if (branchRelation == null) {

			throw APPUB.E0005(OdbFactory.getTable(app_branch_relation.class).getLongname(), SysDict.A.brch_relation_code.getLongName(), branchRelationCode);
		}

		return (branchRelation.getDiff_ccy_ind() == E_YESORNO.YES) ? ccy : ApConst.WILDCARD;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月21日-上午09:43/li>
	 *         <li>功能说明：获取机构的直接下属机构</li>
	 *         </p>
	 * @param branchId
	 * @param branchRelationCode
	 * @param ccy
	 * @return List<String>
	 */
	public static List<String> getJunior(String branchId, String branchRelationCode, String ccy) {

		// 获取法人代码
		String orgId = MsOrg.getReferenceOrgId(apb_branch_junior.class);

		// 获取直接下属机构
		List<String> branchJuniorList = ApBranchBaseDao.selBranchJunior(orgId, branchId, branchRelationCode, convertToRelationCcy(branchRelationCode, ccy), false);

		return branchJuniorList;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月21日-上午09:43/li>
	 *         <li>功能说明：获取机构的所有下属机构</li>
	 *         </p>
	 * @param branchId
	 * @param branchRelationCode
	 * @param ccy
	 * @return List<ApBranchLevel>
	 */
	public static List<ApBranchLevel> getAllJunior(String branchId, String branchRelationCode, String ccy) {

		List<ApBranchLevel> branchLevelInfo = new ArrayList<ApBranchLevel>();

		List<apb_branch_junior> branchJuniorList = Apb_branch_juniorDao.selectAll_odb1(branchId, branchRelationCode, convertToRelationCcy(branchRelationCode, ccy), false);

		for (apb_branch_junior apbBranchJunior : branchJuniorList) {

			ApBranchLevel apBranchLevel = BizUtil.getInstance(ApBranchLevel.class);

			apBranchLevel.setBrch_level(apbBranchJunior.getBrch_level());
			apBranchLevel.setBranch_name(apbBranchJunior.getBranch_name());
			apBranchLevel.setTrxn_branch(apbBranchJunior.getChild_brch_id());

			branchLevelInfo.add(apBranchLevel);
		}

		return branchLevelInfo;

	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月21日-上午09:43/li>
	 *         <li>功能说明：获取机构的直属上级机构</li>
	 *         </p>
	 * @param branchId
	 * @param branchRelationCode
	 * @param ccy
	 * @return String
	 */
	public static String getSenior(String branchId, String branchRelationCode, String ccy) {

		apb_branch_relation branchRelation = Apb_branch_relationDao.selectOne_odb1(branchId, branchRelationCode, convertToRelationCcy(branchRelationCode, ccy), false);

		if (branchRelation == null) {

			throw APPUB.E0005(OdbFactory.getTable(apb_branch_relation.class).getLongname(), SysDict.A.brch_relation_code.getLongName(), branchRelationCode);
		}

		return branchRelation.getParent_brch_id();
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月21日-上午09:43/li>
	 *         <li>功能说明：获取机构的所有上级机构</li>
	 *         </p>
	 * @param branchId
	 * @param branchRelationCode
	 * @param ccy
	 * @return List<ApBranchLevel>
	 */
	public static List<ApBranchLevel> getAllSenior(String branchId, String branchRelationCode, String ccy) {

		List<ApBranchLevel> branchLevelInfo = new ArrayList<ApBranchLevel>();

		List<apb_branch_senior> branchSeniorList = Apb_branch_seniorDao.selectAll_odb1(branchId, branchRelationCode, convertToRelationCcy(branchRelationCode, ccy), false);

		for (apb_branch_senior apbBranchSenior : branchSeniorList) {

			ApBranchLevel apBranchLevel = BizUtil.getInstance(ApBranchLevel.class);

			apBranchLevel.setBrch_level(apbBranchSenior.getBrch_level());
			apBranchLevel.setBranch_name(apbBranchSenior.getBranch_name());
			apBranchLevel.setTrxn_branch(apbBranchSenior.getParent_brch_id());

			branchLevelInfo.add(apBranchLevel);
		}

		return branchLevelInfo;

	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月21日-上午16:20/li>
	 *         <li>功能说明：判断机构是否构成直属上下级关系，是返回true,否则返回false</li>
	 *         </p>
	 * @param seniorBrchId
	 * @param branchRelationCode
	 * @param ccy
	 * @param juniorBrchId
	 * @return boolean
	 */
	public static boolean isDirectlyRelation(String seniorBrchId, String branchRelationCode, String ccy, String juniorBrchId) {

		String branchid = getSenior(juniorBrchId, branchRelationCode, convertToRelationCcy(branchRelationCode, ccy));

		return CommUtil.equals(branchid, seniorBrchId);
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月21日-上午16:20/li>
	 *         <li>功能说明：判断机构是否构成一般上下级关系，是返回true,否则返回false</li>
	 *         </p>
	 * @param seniorBrchId
	 * @param branchRelationCode
	 * @param ccy
	 * @param juniorBrchId
	 * @return boolean
	 */
	public static boolean isNormalRelation(String seniorBrchId, String branchRelationCode, String ccy, String juniorBrchId) {

		apb_branch_senior branchSenior = Apb_branch_seniorDao.selectOne_odb2(juniorBrchId, branchRelationCode, convertToRelationCcy(branchRelationCode, ccy), seniorBrchId, false);

		return (branchSenior == null) ? false : true;
	}
	
	/**
	 * 
	 * @Author lid
	 *         <p>
	 *         <li>2017年3月9日-上午11:06:19</li>
	 *         <li>功能说明：获取根机构</li>
	 *         </p>
	 * @param brchRelationCode
	 * @param ccyCode
	 * @return
	 */
	public static String getRoot(String brchRelationCode, String ccyCode){
		return ApBranchBaseDao.selBranchRoot(BizUtil.getTrxRunEnvs().getBusi_org_id(), brchRelationCode, ccyCode, false);
	}
	
	/**
	 * 
	 * @Author lid
	 *         <p>
	 *         <li>May 24, 2018-5:02:48 PM</li>
	 *         <li>功能说明：获取直接上级的层级</li>
	 *         </p>
	 * @param branchId
	 * @param branchRelationCode
	 * @param ccy
	 * @return 
	 */
	public static ApBranchLevel getSeniorLevel(String branchId, String branchRelationCode, String ccy){
		List<ApBranchLevel> list = getAllSenior(branchId, branchRelationCode,ccy);
		String parentBranchId = getSenior(branchId, branchRelationCode, ccy);
		for(ApBranchLevel branchLevel : list){
			if(CommUtil.compare(branchLevel.getTrxn_branch(), parentBranchId) == 0){
				return branchLevel;
			}
		}
		return null;
	}
}
