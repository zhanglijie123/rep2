package cn.sunline.icore.ap.parm;

import java.util.List;

import cn.sunline.clwj.msap.core.parameter.MsDropList;
import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.core.type.MsCoreComplexType.MsDropListInfo;

/**
 * <p>
 * 文件功能说明：下拉列表参数
 * </p>
 * 
 * @Author duanhb
 *         <p>
 *         <li>2016年12月5日-下午10:13:41</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 duanhb：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseDropList {

	/**
	 * @Author duanhb
	 *         <p>
	 *         <li>2016年12月6日-下午2:13:27</li>
	 *         <li>功能说明：获取下拉字典的描述信息</li>
	 *         </p>
	 * @param listType
	 *            下拉字典类型
	 * @return 字典描述
	 */
	public static String getDescribe(String listType) {
		return MsDropList.getDescribe(listType);
	}

	/**
	 * @Author duanhb
	 *         <p>
	 *         <li>2016年12月6日-下午2:13:27</li>
	 *         <li>功能说明：判断下拉字典是否存在，无对应记录时按throwError判定是否抛出异常</li>
	 *         </p>
	 * @param listType
	 *            下拉字典类型
	 * @param listValue
	 *            下拉字典值
	 * @param throwError
	 *            是否抛出异常
	 * @return true:存在 false:不存在
	 */
	public static boolean exists(String listType, String listValue, boolean throwError) {
		System.out.println("下拉列表是否存在>>>类型:"+listType+",值:"+listValue+",法人代码:"+MsOrg.getDefaultOrgId());
		return MsDropList.exists(listType, listValue, throwError);
	}

	/**
	 * @Author duanhb
	 *         <p>
	 *         <li>2016年12月6日-下午2:13:27</li>
	 *         <li>功能说明：判断下拉字典是否存在，无对应记录时抛出异常</li>
	 *         </p>
	 * @param listType
	 *            下拉字典类型
	 * @param listValue
	 *            下拉字典值
	 * @return true:存在 false:不存在
	 */
	public static boolean exists(String listType, String listValue) {

		return exists(listType, listValue, true);
	}

	/**
	 * @Author duanhb
	 *         <p>
	 *         <li>2016年12月6日-下午2:16:40</li>
	 *         <li>功能说明：获取字典值对应文本，无对应记录按throwError判定是否抛出异常</li>
	 *         </p>
	 * @param listType
	 *            下拉字典类型
	 * @param listValue
	 *            下拉字典值
	 * @param throwError
	 *            是否抛出异常
	 * @return	返回下拉列表对应文本,throwError=flase时未查询到对应下拉列表，返回null
	 */
	public static String getText(String listType, String listValue, boolean throwError) {
		
		return MsDropList.getText(listType, listValue, throwError);
	}
	
	/**
	 * @Author duanhb
	 *         <p>
	 *         <li>2016年12月6日-下午2:16:40</li>
	 *         <li>功能说明：获取字典值对应文本，无对应记录抛出异常</li>
	 *         </p>
	 * @param listType
	 *            下拉字典类型
	 * @param listValue
	 *            下拉字典值
	 * @return 返回下拉列表对应文本
	 */
	public static String getText(String listType, String listValue) {

		return getText(listType, listValue, true);
	}

	/**
	 * @Author duanhb
	 *         <p>
	 *         <li>2016年12月6日-下午2:18:10</li>
	 *         <li>功能说明：获取字典列表</li>
	 *         </p>
	 * @param listType
	 *            下拉字典类型
	 * @return 下拉字典对象List集合
	 */
	public static List<MsDropListInfo> getItems(String listType) {
		
		return MsDropList.getItems(listType);
	}

}
