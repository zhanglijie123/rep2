
package cn.sunline.icore.ap.batchtran;

import cn.sunline.icore.ap.namedsql.ApSmsBaseDao;
import cn.sunline.icore.ap.sms.ApSmsSend;
import cn.sunline.icore.ap.type.ComApSms.ApSmsSendInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_SMSSENDSTATUS;
import cn.sunline.ltts.batch.engine.split.AbstractBatchDataProcessor;
import cn.sunline.ltts.batch.engine.split.BatchDataWalker;
import cn.sunline.ltts.batch.engine.split.impl.CursorBatchDataWalker;
import cn.sunline.ltts.dao.Params;
	 /**
	  * sms send
	  * @author 
	  * @Date 
	  */

public class ap13DataProcessor extends
  AbstractBatchDataProcessor<cn.sunline.icore.ap.batchtran.intf.Ap13.Input, cn.sunline.icore.ap.batchtran.intf.Ap13.Property, cn.sunline.icore.ap.type.ComApSms.ApSmsSendInfo> {
	  /**
		 * 批次数据项处理逻辑。
		 * 
		 * @param job 批次作业ID
		 * @param index  批次作业第几笔数据(从1开始)
		 * @param dataItem 批次数据项
		 * @param input 批量交易输入接口
		 * @param property 批量交易属性接口
		 */
		@Override
		public void process(String jobId, int index, cn.sunline.icore.ap.type.ComApSms.ApSmsSendInfo dataItem, cn.sunline.icore.ap.batchtran.intf.Ap13.Input input, cn.sunline.icore.ap.batchtran.intf.Ap13.Property property) {
			
			 ApSmsSend.sendSms(dataItem);
			
		}
		
		/**
		 * 获取数据遍历器。
		 * @param input 批量交易输入接口
		 * @param property 批量交易属性接口
		 * @return 数据遍历器
		 */
		@Override
		public BatchDataWalker<cn.sunline.icore.ap.type.ComApSms.ApSmsSendInfo> getBatchDataWalker(cn.sunline.icore.ap.batchtran.intf.Ap13.Input input, cn.sunline.icore.ap.batchtran.intf.Ap13.Property property) {
			
			Params parm = new Params();
			parm.add("org_id", BizUtil.getTrxRunEnvs().getBusi_org_id());
			parm.add("sms_send_status", E_SMSSENDSTATUS.UNSENT);

			return new CursorBatchDataWalker<ApSmsSendInfo>(ApSmsBaseDao.namedsql_selProSendSms, parm);
		}

}


