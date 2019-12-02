package cn.sunline.icore.ap.batchtran;

import java.util.List;

import cn.sunline.clwj.msap.core.parameter.MsGlobalParm;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.ApDateApi;
import cn.sunline.icore.ap.namedsql.ApDayEndBaseDao;
import cn.sunline.icore.ap.tables.TabApDevelop.apb_auto_eod;
import cn.sunline.icore.ap.tables.TabApSystem.App_dateDao;
import cn.sunline.icore.ap.tables.TabApSystem.app_date;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;
import cn.sunline.ltts.biz.global.CommUtil;

/**
 * 系统换日操作
 */

public class ap05DataProcessor extends
		BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap05.Input, cn.sunline.icore.ap.batchtran.intf.Ap05.Property> {

	/**
	 * 批次数据项处理逻辑。
	 * 
	 * @param input
	 *            批量交易输入接口
	 * @param property
	 *            批量交易属性接口
	 */
	@Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap05.Input input, cn.sunline.icore.ap.batchtran.intf.Ap05.Property property) {
		//日切
		ApDateApi.swith();
		//获取公共运行区变量
		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		//筛选匹配计划
		List<apb_auto_eod> autoEOD = ApDayEndBaseDao.selEodPlan(runEnvs.getTrxn_date(), E_YESORNO.YES, false);
		//是否跨日
		boolean flag = false;
		
		String dayEndInd = MsGlobalParm.getValue(ApConst.DAYEND_DATA, ApConst.DAYEND_TRAN_SWITCH);

		
//		if (E_DAYENDSWITCH.valueOf(dayEndInd) == E_DAYENDSWITCH.ON){
		if(false) {
			boolean dateValidity = true;
			if( autoEOD.size()>1 ){
				//开关打开配置为空,或者超过一条匹配报错
				throw ApErr.AP.E0070();
			}
		
			//如果匹配的计划为跳跑计划,则跳跑
			if( autoEOD.size()==1 &&
				CommUtil.equals(autoEOD.get(0).getMulti_switch_ind().getValue(), E_YESORNO.YES.getValue()) &&
			    !CommUtil.equals(runEnvs.getNext_date(), autoEOD.get(0).getEnd_time()) ){
				flag = true;
				//日期检查
				dateValidity = BizUtil.isDateString(autoEOD.get(0).getBegin_time());
				if( !dateValidity ){
					throw ApPubErr.APPUB.E0011(autoEOD.get(0).getBegin_time());
				}
				dateValidity = BizUtil.isDateString(autoEOD.get(0).getEnd_time());			
				if( !dateValidity ){
					throw ApPubErr.APPUB.E0011(autoEOD.get(0).getBegin_time());
				}
			}

		}

		//如果当天在跳跑计划中，则日切后将next date设置为计划结束日期，使当日生成的下日相关数据为计划结束日期，而非下一自然日
		if( flag  ){
			app_date dateInfo = App_dateDao.selectOneWithLock_odb1(runEnvs.getBusi_org_id(), false);
			dateInfo.setNext_date(autoEOD.get(0).getEnd_time()); 
			App_dateDao.updateOne_odb1(dateInfo);
			runEnvs.setNext_date(autoEOD.get(0).getEnd_time());
		}


	}

}
