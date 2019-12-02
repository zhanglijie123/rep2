package cn.sunline.icore.ap.api;

import java.util.List;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.dataSync.ApParameterSync;
import cn.sunline.icore.ap.type.ComApDataSync.ApParmSyncGroup;
import cn.sunline.icore.sys.type.EnumType.E_PARAMETERTYPE;

/**
 * <p>
 * 文件功能说明：
 * 			参数同步抽象类      			
 * </p>
 * 
 * @Author sunshaoyu
 *         <p>
 *         <li>2019年10月8日-下午1:53:55</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2019年10月8日-sunshaoyu：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApParameterSyncApi {
	
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月21日-下午3:56:09</li>
	 *         <li>功能说明：登记参数同步控制表信息</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @param paraId
	 * 				参数ID
	 * @return
	 */
	public static void regParaSyncMiddlePlat(E_PARAMETERTYPE paraType, String paraId){
		ApParameterSync.regParaSyncMiddlePlat(paraType, paraId);
	}
	
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月14日-下午9:20:35</li>
	 *         <li>功能说明：获取参数同步控制表信息</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @param lockFlag
	 * 				参数同步控制表信息 是否带锁查询
	 * @return ApParmSyncGroup 参数同步控制信息
	 */
	public static ApParmSyncGroup getParaSyncInfo(E_PARAMETERTYPE paraType, E_YESORNO lockFlag){
		return ApParameterSync.getParaSyncInfo(paraType, lockFlag);
	}
	
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月14日-下午9:18:51</li>
	 *         <li>功能说明：更新参数同步控制状态为已同步</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @return
	 */
	public static void modifyParaSyncStatus(E_PARAMETERTYPE paraType){
		ApParameterSync.modifyParaSyncStatus(paraType);
	}
	
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月28日-下午1:28:51</li>
	 *         <li>功能说明：删除待同步参数值信息表记录</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @param paraId
	 * 				参数ID
	 * @return
	 */
	public static int deleteParmValue(E_PARAMETERTYPE paraType, String paraId){
		return ApParameterSync.deleteParmValue(paraType, paraId);
	}
	
	
	/**
	 * @Author Liubx
	 *         <p>
	 *         <li>2018年8月28日-下午1:18:51</li>
	 *         <li>功能说明：查询待同步参数值信息</li>
	 *         </p>
	 * @param paraType
	 * 				参数类型
	 * @return List<String> 参数ID
	 */
	public static List<String> getValueList(E_PARAMETERTYPE paraType) {
		return ApParameterSync.getValueList(paraType);
	}
}
