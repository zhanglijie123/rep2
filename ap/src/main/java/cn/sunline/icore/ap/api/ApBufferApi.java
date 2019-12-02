package cn.sunline.icore.ap.api;

import java.util.Map;

import cn.sunline.icore.ap.rule.ApBaseBuffer;

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
public class ApBufferApi{
	
	public static void addData(String dataMart, Map<String, Object> commObj, boolean copy) {
		ApBaseBuffer.addData(dataMart, commObj, copy);
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
		ApBaseBuffer.addData(dataMart, commObj);
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
	public static void appendData(String dataMart, Map<String, Object> commObj) {
		ApBaseBuffer.appendData(dataMart, commObj);
	}
	
	public static void replaceFieldData(String dataMart, Map<String, Object> commObj) {
		ApBaseBuffer.replaceFieldData(dataMart, commObj);
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
		return ApBaseBuffer.getBuffer();
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
	public static String getFieldValue(String dataMart, String fieldName) {
		
		return ApBaseBuffer.getFieldValue(dataMart, fieldName);
	}	
	
}
