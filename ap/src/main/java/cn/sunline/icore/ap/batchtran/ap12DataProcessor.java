/**************************************************************************
 * CopyRight (c) 2002-2018 未授权
 **************************************************************************/
package cn.sunline.icore.ap.batchtran;

import cn.sunline.icore.ap.api.ApOnTimeDayendApi;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
/**
 * auto dayend
 * @author liucong
 * @Date 20180102
 */

/**
 * @author liucong
 * @date 20180102
 **/
public class ap12DataProcessor extends
		BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap12.Input, cn.sunline.icore.ap.batchtran.intf.Ap12.Property> {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ap12DataProcessor.class);
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input
	 *            批量交易输入接口
	 * @param property
	 *            批量交易属性接口
	 */
	@Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap12.Input input, cn.sunline.icore.ap.batchtran.intf.Ap12.Property property) {
		// TODO:
		bizlog.info("ap12DataProcessorStart", "");
		System.out.println("This is a Test>>>>>>>>>>>.");
		String flowId = input.getTran_flow_id();

		String orgId = BizUtil.getTrxRunEnvs().getBusi_org_id();
		
//		try {
			ApOnTimeDayendApi.onTimeDayend(flowId, orgId);
//		}
//		catch (LttsBusinessException e) {
//			throw e;
//		}

	}

}
