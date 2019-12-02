package cn.sunline.icore.ap.api;

import cn.sunline.icore.ap.clean.ApBaseDataClean;

/**
 * <p>
 * 文件功能说明：分区及清理策略定义
 * </p>
 * 
 * @Author lid
 *         <p>
 *         <li>2016年12月14日-下午3:18:54</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 lid：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApDataCleanApi {

	
	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月14日-下午3:48:19</li>
	 *         <li>功能说明：分区跟清理</li>
	 *         <li>前提：表分区都是rang形式，并且分区名是"P+日期"的形式,建表分区的时候要按照约定建</li>
	 *         </p>
	 */
	@SuppressWarnings("deprecation")
	public static void paraAndClean() {
		ApBaseDataClean.paraAndClean();
	}

	/**
	 * 查询数据保留天数
	 */
	public static Integer getDataReserveDays(String tableName) {
		return ApBaseDataClean.getDataReserveDays(tableName);
	}

}
