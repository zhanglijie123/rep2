
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.parm.mnt.ApHolidayMnt;
import cn.sunline.icore.ap.type.ComApBasic.ApHolidayInfo;
import cn.sunline.icore.ap.type.ComApBasic.ApHolidayQueryOut;
import cn.sunline.icore.ap.type.ComApBasic.ApWorkdayInfo;
import cn.sunline.icore.sys.type.EnumType.E_HOLIDAYCLASS;
import cn.sunline.ltts.core.api.model.dm.Options;


 /**
  * holiday parameter maintenance
  * 节假日参数维护
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApHolidayImpl", longname="holiday parameter maintenance", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApHolidayImpl implements cn.sunline.icore.ap.servicetype.SrvApHoliday{
 /**
  * query holiday list information
  *
  */
	public Options<ApHolidayInfo> queryHolidayList(String holidayCode, String holidayYear, E_HOLIDAYCLASS holidayClass) {
		return ApHolidayMnt.queryHolidayList(holidayCode,holidayYear,holidayClass);
	}
 /**
  * holiday information maintenance
  *
  */
	public void modifyHoliday(cn.sunline.icore.ap.type.ComApBasic.ApHolidayWithInd holidayList){
		ApHolidayMnt.modifyHoliday(holidayList);
	}
 /**
  * explore holiday information
  *
  */
	public Options<ApHolidayInfo> scanHolidaty(String holidayCode, E_HOLIDAYCLASS holidayClass, String holidayYear, String holidayDate) {
		return ApHolidayMnt.scanHolidaty(holidayCode,holidayClass,holidayYear,holidayDate);

	}
	@Override
	public String getWorkDate(ApWorkdayInfo cplIn) {
		return ApHolidayMnt.getWorkDate(cplIn);
	}
	
	/**
	 * 节假日信息查询
	 */
	@Override
	public ApHolidayQueryOut queryHoliday(String holiday_date) {
		// TODO Auto-generated method stub
		return ApHolidayMnt.queryHoliday(holiday_date);
	}
}

