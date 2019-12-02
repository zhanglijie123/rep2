package cn.sunline.icore.ap.audit;

import cn.sunline.clwj.msap.util.audit.MsAuditUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * <p>
 * 文件功能说明：参数和业务数据审计
 * </p>
 * 
 * @Author lidi
 *         <p>
 *         <li>2016年12月5日-下午10:04:29</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 jollyja：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseDataAudit {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseDataAudit.class);

	private ApBaseDataAudit(){}
	
	/**
	 * @Author lidi
	 *         <p>
	 *         <li>2016年12月6日-下午1:29:11</li>
	 *         <li>功能说明：新增参数审计登记</li>
	 *         </p>
	 * @param record
	 */
	public static void regLogOnInsertParameter(Object record) {

		bizlog.method(" ApDataAudit.regLogOnInsertParameter begin >>>>>>>>>>>>>>>>");

		MsAuditUtil.regLogOnInsertParameter(record);

		bizlog.method(" ApDataAudit.regLogOnInsertParameter end <<<<<<<<<<<<<<<<");
	}

	/**
	 * @Author lidi
	 *         <p>
	 *         <li>2016年12月6日-下午1:29:11</li>
	 *         <li>功能说明：删除参数审计登记</li>
	 *         </p>
	 * @param record
	 */
	public static void regLogOnDeleteParameter(Object record) {

		bizlog.method(" ApDataAudit.regLogOnDeleteParameter begin >>>>>>>>>>>>>>>>");

		MsAuditUtil.regLogOnDeleteParameter(record);

		bizlog.method(" ApDataAudit.regLogOnDeleteParameter end <<<<<<<<<<<<<<<<");
	}

	/**
	 * @Author lidi
	 *         <p>
	 *         <li>2016年12月6日-下午3:48:32</li>
	 *         <li>功能说明：修改参数审计登记，返回本次修改的字段数，为0表示没有维护</li>
	 *         </p>
	 * @param oldRecord
	 * @param newRecord
	 * @return int 登记记录数
	 */
	public static int regLogOnUpdateParameter(Object oldRecord, Object newRecord) {
		return MsAuditUtil.regLogOnUpdateParameter(oldRecord, newRecord);
	}

	/**
	 * @Author lidi
	 *         <p>
	 *         <li>2016年12月8日-下午3:52:52</li>
	 *         <li>功能说明：修改业务审计登记，返回本次修改的字段数，为0表示没有维护</li>
	 *         </p>
	 * @param oldRecord
	 * @param newRecord
	 * @param tableType
	 * @return
	 */
	public static int regLogOnUpdateBusiness(Object oldRecord, Object newRecord) {
		return MsAuditUtil.regLogOnUpdateBusiness(oldRecord, newRecord);
	}

}
