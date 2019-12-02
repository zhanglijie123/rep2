package cn.sunline.icore.ap.parm;

import java.util.List;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.namedsql.ApBasicBaseDao;
import cn.sunline.icore.ap.tables.TabApBasic.App_summaryDao;
import cn.sunline.icore.ap.tables.TabApBasic.app_summary;
import cn.sunline.icore.ap.type.ComApBasic.ApSummaryControlInfo;
import cn.sunline.icore.ap.type.ComApBasic.ApSummaryOptionalList;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr.ApBase;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.errors.ApPubErr.APPUB;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

/**
 * <p>
 * 文件功能说明：摘要
 * </p>
 * 
 * @Author jollyja
 *         <p>
 *         <li>2016年12月5日-下午10:14:32</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 jollyja：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseSummary {

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午1:38:24</li>
	 *         <li>功能说明：判断摘要代码是否存在，不存返回false</li>
	 *         </p>
	 * @param summaryCode
	 * @return
	 */
	public static boolean exists(String summaryCode) {

		return exists(summaryCode, false);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月13日-下午12:02:40</li>
	 *         <li>功能说明：判断摘要代码是否存在</li>
	 *         </p>
	 * @param summaryCode
	 * @param flag
	 *            true 不存在报错 false 不抛错
	 * @return
	 */
	public static boolean exists(String summaryCode, boolean flag) {
		app_summary summaryInfo = App_summaryDao.selectOne_odb1(summaryCode, false);

		if (summaryInfo == null && flag) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_summary.class).getLongname(), SysDict.A.summary_code.getLongName(), summaryCode);
		}

		return (summaryInfo == null) ? false : true;
	}

	/**
	 * @Author qiuhan
	 *         <p>
	 *         <li>2016年12月6日-下午1:39:14</li>
	 *         <li>功能说明：获取摘要文字</li>
	 *         </p>
	 * @param summaryCode
	 * @return
	 */
	public static String getText(String summaryCode) {

		app_summary summaryInfo = App_summaryDao.selectOne_odb1(summaryCode, false);

		if (summaryInfo == null) {
			// 获取[${tableDesc}]失敗,无对应记录,数据键值[${keyValue}]
			throw APPUB.E0005(OdbFactory.getTable(app_summary.class).getLongname(), SysDict.A.summary_code.getLongName(), summaryCode);
		}

		return summaryInfo.getSummary_text();
	}

	/**
	 * @Author jollyja
	 *         <p>
	 *         <li>2016年12月6日-下午1:43:07</li>
	 *         <li>功能说明：获取全部摘要定义</li>
	 *         </p>
	 * @return
	 */
	public static List<app_summary> all() {
		// TODO 获取全部摘要，由于odb操作限制，暂不实现，有需要再通过namedsql实现
		throw new UnsupportedOperationException();
	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2017年11月27日-下午1:39:14</li>
	 *         <li>功能说明：获取摘要类型</li>
	 *         </p>
	 * @param summaryCode
	 * @return
	 */
	public static String getSummaryClass(String summaryCode) {

		app_summary summaryInfo = App_summaryDao.selectOne_odb1(summaryCode, false);

		if (summaryInfo == null) {
			// 获取[${tableDesc}]失敗,无对应记录,数据键值[${keyValue}]
			throw APPUB.E0005(OdbFactory.getTable(app_summary.class).getLongname(), SysDict.A.summary_code.getLongName(), summaryCode);
		}

		return summaryInfo.getSummary_class();
	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2019年9月21日-下午1:39:14</li>
	 *         <li>功能说明：获取默认摘要代码</li>
	 *         </p>
	 * @param errFlag
	 *            报错标志 true-报错， false-不报错
	 * @return 摘要代码
	 */
	public static String getDefaultSummary(boolean errFlag) {

		String trxnCode = BizUtil.getTrxRunEnvs().getTrxn_code();
		String reconCode = BizUtil.getTrxRunEnvs().getRecon_code();
		String channelId = BizUtil.getTrxRunEnvs().getChannel_id();
		String sceneCode = BizUtil.getTrxRunEnvs().getExternal_scene_code();

		// 摘要码控制信息
		List<ApSummaryControlInfo> listSummary = ApBasicBaseDao.selSummaryControlList(trxnCode, reconCode, channelId, sceneCode, false);

		// 无记录或没有默认摘要码则返回空值
		if (listSummary == null || listSummary.size() == 0 || listSummary.get(0).getDefault_ind() != E_YESORNO.YES) {

			if (errFlag) {
				throw ApBase.E0123();
			}

			// 返回空值
			return "";
		}

		// 返回默认摘要码
		return listSummary.get(0).getSummary_code();
	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2019年9月21日-下午1:39:14</li>
	 *         <li>功能说明：检查输入摘要代码与交易场景的适配性</li>
	 *         </p>
	 * @param summaryCode
	 *            摘要代码
	 */
	public static void checkTrxnSceneSummary(String summaryCode) {

		BizUtil.fieldNotNull(summaryCode, SysDict.A.summary_code.getId(), SysDict.A.summary_code.getLongName());

		String trxnCode = BizUtil.getTrxRunEnvs().getTrxn_code();
		String reconCode = BizUtil.getTrxRunEnvs().getRecon_code();
		String channelId = BizUtil.getTrxRunEnvs().getChannel_id();
		String sceneCode = BizUtil.getTrxRunEnvs().getExternal_scene_code();

		// 摘要码控制信息
		List<ApSummaryControlInfo> listSummary = ApBasicBaseDao.selSummaryControlList(trxnCode, reconCode, channelId, sceneCode, false);

		// 没有配置摘要码范围控制则不检查输入值是否合法
		if (listSummary == null || listSummary.size() == 0) {

			return;
		}

		// 摘要码匹配检查
		for (ApSummaryControlInfo summaryInfo : listSummary) {

			// 有复合要求的记录则直接退出
			if (CommUtil.equals(summaryInfo.getSummary_code(), summaryCode)) {
				return;
			}
		}

		// 摘要码不适用于本交易
		throw ApBase.E0124(summaryCode);

	}

	/**
	 * @Author zhoumy
	 *         <p>
	 *         <li>2019年9月21日-下午1:39:14</li>
	 *         <li>功能说明：查询交易摘要码可选信息</li>
	 *         </p>
	 * @param targetTrxnCode
	 *            摘要代码
	 * @param targetReconCode
	 *            对账代码
	 */
	public static Options<ApSummaryOptionalList> queryTrxnOptionalSummayList(String targetTrxnCode, String targetReconCode) {

		// 交易码必须输入
		BizUtil.fieldNotNull(targetTrxnCode, SysDict.A.trxn_code.getId(), SysDict.A.trxn_code.getLongName());

		// 渠道码和外部场景码从公共运行区获取
		String channelId = BizUtil.getTrxRunEnvs().getChannel_id();
		String sceneCode = BizUtil.getTrxRunEnvs().getExternal_scene_code();

		// 输出
		Options<ApSummaryOptionalList> listOut = new DefaultOptions<ApSummaryOptionalList>();

		// 摘要码控制信息
		List<ApSummaryControlInfo> listSummary = ApBasicBaseDao.selSummaryControlList(targetTrxnCode, targetReconCode, channelId, sceneCode, false);

		// 没有配置摘要码范围控制则不检查输入值是否合法
		if (listSummary == null || listSummary.size() == 0) {

			return listOut;
		}

		String lastSummary = "";

		// 摘要码
		for (ApSummaryControlInfo summaryInfo : listSummary) {

			if (CommUtil.equals(summaryInfo.getSummary_code(), lastSummary)) {
				continue;
			}

			ApSummaryOptionalList cplSummary = BizUtil.getInstance(ApSummaryOptionalList.class);

			cplSummary.setSummary_code(summaryInfo.getSummary_code());
			cplSummary.setSummary_text(getText(summaryInfo.getSummary_code()));

			listOut.add(cplSummary);

			// 保持上一条摘要码
			lastSummary = summaryInfo.getSummary_code();
		}

		return listOut;
	}
}
