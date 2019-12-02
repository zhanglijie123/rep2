package cn.sunline.icore.ap.rule;

import java.util.Map;

import cn.sunline.clwj.msap.core.util.MsBuffer;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.ltts.biz.global.CommUtil;

/**
 * <p>
 * 文件功能说明：交易缓冲区处理
 * </p>
 * 
 * @Author lid
 *         <p>
 *         <li>2016年12月12日-上午11:51:55</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 lid：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseBuffer{

	/**
	 * 
	 * @Author tsichang
	 *         <p>
	 *         <li>2019年10月9日-上午10:17:50</li>
	 *         <li>功能说明：将某个数据集加到数据缓冲区。（采用先删除再增加的方式）</li>
	 *         </p>
	 * @param dataMart 数据集ID
	 * @param commObj 数据集对象
	 * @param copy 是否拷贝数据集对象之后再加入缓存。true - 拷贝之后，将拷贝对象加入缓存； false - 直接将原对象加入缓存
	 */
	@SuppressWarnings("unchecked")
	public static void addData(String dataMart, Map<String, Object> commObj, boolean copy) {
		if (copy) {
			MsBuffer.addData(dataMart, (Map<String, Object>)BizUtil.clone(Map.class, commObj));
		}
		else {
			MsBuffer.addData(dataMart, commObj);
		}
	}
	
	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月12日-上午11:54:58</li>
	 *         <li>功能说明：将某个数据集加到数据缓冲区。（采用先删除再增加的方式）</li>
	 *         </p>
	 * @param dataMart
	 * @param commObj
	 */
	public static void addData(String dataMart, Map<String, Object> commObj) {
		addData(dataMart, commObj, true);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年03月20日-上午09:54:58</li>
	 *         <li>功能说明：往某个数据集追加数据。</li>
	 *         </p>
	 * @param dataMart
	 * @param commObj
	 */
	@SuppressWarnings("unchecked")
	public static void appendData(String dataMart, Map<String, Object> commObj) {
		MsBuffer.appendData(dataMart, (Map<String, Object>)BizUtil.clone(Map.class, commObj));
	}
	
	@SuppressWarnings("unchecked")
	public static void replaceFieldData(String dataMart, Map<String, Object> commObj) {
		MsBuffer.replaceFieldData(dataMart, (Map<String, Object>)BizUtil.clone(Map.class, commObj));
	}
	
	/**
	 * 
	 * @Author tsichang
	 *         <p>
	 *         <li>2017年4月25日-上午9:56:15</li>
	 *         <li>功能说明：获取当前的缓存区</li>
	 *         </p>
	 * @return
	 */
	public static Map<String, Object> getBuffer() {
		return MsBuffer.getBuffer();
	}	
	
	/**
	 * @Author tsichang
	 *         <p>
	 *         <li>2017年3月9日-下午4:51:31</li>
	 *         <li>功能说明：获取缓冲区某个字段的值 </li>
	 *         </p>
	 * @param dataMart
	 * @param fieldName
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public static String getFieldValue(String dataMart, String fieldName) {
		
		Map<String, Object> ruleBuffer = getBuffer();
		Object value = null;
		
		// 如果是公共运行变量，直接去公共运行变量去取
		if (ApConst.RUN_ENVS.equals(dataMart)) {
			value = BizUtil.getTrxRunEnvsValue(fieldName);
		}
		else if (ApConst.ADDITIONAL_DATA_MART.equals(dataMart)) {
			@SuppressWarnings("rawtypes")
			Map customParms = BizUtil.getTrxRunEnvs().getCustom_parm();
			
			if (customParms != null) {
				value = String.valueOf(customParms.get(fieldName));
			}
		}
		else {// 其他的就从缓冲区取
			Object dataObj = ruleBuffer.get(dataMart);// 数据集
			if (dataObj == null) {
				throw ApBaseErr.ApBase.E0034(dataMart);
			}

			if (dataObj instanceof Map) {
				Map<String, Object> data = (Map<String, Object>) dataObj;
				value = data.get(fieldName);
			}
		}
		
		if (CommUtil.isNull(value)) {
			throw ApBaseErr.ApBase.E0033(dataMart, fieldName);
		}

		return value == null ? null : value.toString();
	}	
	
}
