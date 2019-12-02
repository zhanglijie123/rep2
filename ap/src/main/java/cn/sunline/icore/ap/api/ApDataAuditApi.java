package cn.sunline.icore.ap.api;

import cn.sunline.icore.ap.audit.ApBaseDataAudit;

public class ApDataAuditApi {
	
	/**
	 * @Author lidi
	 *         <p>
	 *         <li>2016年12月6日-下午1:29:11</li>
	 *         <li>功能说明：新增参数审计登记</li>
	 *         </p>
	 * @param record
	 */
	public static void regLogOnInsertParameter(Object record) {
		ApBaseDataAudit.regLogOnInsertParameter(record);
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

		ApBaseDataAudit.regLogOnDeleteParameter(record);
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
		return ApBaseDataAudit.regLogOnUpdateParameter(oldRecord, newRecord);
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
		return ApBaseDataAudit.regLogOnUpdateBusiness(oldRecord, newRecord);
	}
}
