package cn.sunline.icore.ap.parm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.sunline.edsp.midware.rpc.core.utils.StringUtils;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.exception.LttsBusinessException;

public class ApRefDate {
	
	private final static String[] WEEKS = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
	
	private final static String[] CYCLE = {"D", "W", "S", "M", "Q", "H", "Y"};
	
	private final static List<String> WEEKSLIST = new ArrayList(Arrays.asList(WEEKS));
	
	private final static List<String> CYCLELIST = new ArrayList(Arrays.asList(CYCLE));
	
	/**
	 * check reference date
	 * @param cycle
	 * @param date
	 * @throws LttsBusinessException
	 */
	public static void checkReferenceDate(String cycle, String date) throws LttsBusinessException{
		boolean result = true;
		
		if (CommUtil.isNull(cycle) || CommUtil.isNull(date)){
			throw new IllegalArgumentException();
		}
		
		if (!CYCLELIST.contains(cycle.toUpperCase())){
			result = false;
		}
		
		if (result){
			if (StringUtils.isNumeric(date) && date.length() == 4){
				if ("M".equals(cycle.toUpperCase())){
				    if (!checkDate(date, 1)){
				    	result = false;
				    }
				}
				
				if ("Q".equals(cycle.toUpperCase())){
				    if (!checkDate(date, 3)){
				    	result = false;
				    }
				}
				
				if ("H".equals(cycle.toUpperCase())){
					if (!checkDate(date, 6)){
						result = false;
					}
				}
				
				if ("Y".equals(cycle.toUpperCase())){
					if (!checkDate(date, 12)){
						result = false;
					}
				}
				
			}else {
				if ("W".equals(cycle.toUpperCase())){
					if (!WEEKSLIST.contains(date.toUpperCase())){
						result = false;
					}
				}else{
					if (!"END".equals(date.toUpperCase()) && !"CASEDATE".equals(date.toUpperCase())){
						result = false;
					}
				}				

			}
		}
		
		if (!result){
			// 参考日期不合法，周期[%s],日期[%s]
			throw ApBaseErr.ApBase.E0129(cycle, date);
		}
	}
	
	private static boolean checkDate(String date, int size){
		boolean ret = false;
	    if (checkNum(date.substring(0, 2), size) && checkNum(date.substring(2), 31)){
	    	ret = true;
	    }
	    return ret;
	}
	
	private static boolean checkNum(String num, int size){
		boolean ret = false;
		int n = Integer.valueOf(num);
		
		if (n > 0 && n <= size){
			ret = true;
		}
		
		return ret;
	}

}
