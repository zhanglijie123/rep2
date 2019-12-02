package cn.sunline.icore.ap.api;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.icore.ap.dataSync.ApBaseDataSync;

/**
 * 数据同步
 * @author lid
 *
 */
public class ApDataSyncApi {
	
	/**
	 * 登记数据操作日志
	 * @param execute
	 */
	public static void regDataOperateLog(Runnable excuter, E_DATAOPERATE operateType){
		ApBaseDataSync.regDataOperateLog(excuter, operateType);
	}


	/**
	 * 批量同步数据
	 * @param systemId
	 */
	public static void batchDataSync(String systemId){
		ApBaseDataSync.batchDataSync(systemId);
	}

}
