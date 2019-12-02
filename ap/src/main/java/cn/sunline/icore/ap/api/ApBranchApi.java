package cn.sunline.icore.ap.api;

import java.util.List;

import cn.sunline.icore.ap.branch.ApBaseBranch;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchInfo;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchLevel;

public class ApBranchApi {

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

		return ApBaseBranch.exists(branchId);

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
		return ApBaseBranch.exists(branchId, error);

	}
	
	/**
	 * 根据法人代码，机构号来判断，法人可能不是公共运行变量的法人
	 * @param orgId
	 * @param branchId
	 * @return
	 */
	public static boolean exists(String orgId, String branchId) {

		return ApBaseBranch.exists(orgId, branchId);

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

		return ApBaseBranch.getItem(branchId);
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

		return ApBaseBranch.getHolidayCode(branchId);
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

		return ApBaseBranch.getSwiftNo(branchId);

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

		return ApBaseBranch.convertToRelationCcy(branchRelationCode, ccy);
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

		return ApBaseBranch.getJunior(branchId, branchRelationCode, ccy);
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

		return ApBaseBranch.getAllJunior(branchId, branchRelationCode, ccy);

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

		return ApBaseBranch.getSenior(branchId, branchRelationCode, ccy);
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

		return ApBaseBranch.getAllSenior(branchId, branchRelationCode, ccy);

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

		return ApBaseBranch.isDirectlyRelation(seniorBrchId, branchRelationCode, ccy, juniorBrchId);
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

		return ApBaseBranch.isNormalRelation(seniorBrchId, branchRelationCode, ccy, juniorBrchId);
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
		return ApBaseBranch.getRoot(brchRelationCode, ccyCode);
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
		return ApBaseBranch.getSeniorLevel(branchId, branchRelationCode, ccy);
	}
}
