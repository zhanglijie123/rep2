package cn.sunline.icore.ap.api;

import java.util.List;
import java.util.Map;

import cn.sunline.icore.ap.rule.ApBaseRule;
import cn.sunline.icore.ap.rule.ApBaseRule.BufferedDataVisitor;
import cn.sunline.icore.ap.type.ComApSystem.ApRuleMappingDetail;

/**
 * <p>
 * 文件功能说明： 规则引擎
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
public class ApRuleApi {
	
	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月12日-上午11:56:54</li>
	 *         <li>功能说明：判断规则是否成立</li>
	 *         <li>如果数据集不是公共运行变量，需要调用addDataToBuffer方法添加数据</li>
	 *         <li>1):如果存在例外规则，则一个一个校验，如果有一个成立，则返回false<br/>
	 *         2):如果没有例外规则，或者例外规则都不成立，则校验输入的规则，校验成功返回true,失败false<br/>
	 *         3):一个规则可以有多个组，一个组可以有多个序号，组之间是'或'的关系，序号之间是'与'的关系<br/>
	 *         一个组内所有的序号都true，则该组才为true<br/>
	 *         只要有一个组是true，则直接返回true<br/>
	 *         </li>
	 *         </p>
	 * @param ruleId
	 *            规则代码
	 * @param processor
	 * 			  规则条件匹配结果处理器，如每条规则条件匹配结果详情的记录处理等。
	 * 			             
	 * @return
	 */
	public static boolean mapping(String ruleId, ApRuleDataRetProcessor processor) {
		return ApBaseRule.mapping(ruleId, processor);
	}
	
	public static boolean mapping(String ruleId) {
		return ApBaseRule.mapping(ruleId);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月12日-下午1:09:36</li>
	 *         <li>功能说明：按规则场景代码扫描并返回首个命中回执，无命中返回null</li>
	 *         </p>
	 * @param sceneType
	 * @return
	 */
	public static String getFirstResultByScene(String sceneCode) {
		return ApBaseRule.getFirstResultByScene(sceneCode);
	}
	
	public static String getFirstResultByScene(String sceneCode, ApRuleDataRetProcessor processor) {
		return ApBaseRule.getFirstResultByScene(sceneCode, processor);
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2016年12月12日-下午1:10:38</li>
	 *         <li>功能说明：按规则场景类型扫描并返回全部命中回执，无命中返回空列表</li>
	 *         </p>
	 * @param sceneType
	 * @return
	 */
	public static List<String> getAllResultByScene(String sceneCode) {
		return ApBaseRule.getAllResultByScene(sceneCode);
	}
	
	public static List<String> getAllResultByScene(String sceneCode, ApRuleDataRetProcessor processor) {
		return ApBaseRule.getAllResultByScene(sceneCode, processor);
	}

	/**
	 * @Author shenxy
	 *         <p>
	 *         <li>2017年9月22日-下午3:50:02</li>
	 *         <li>功能说明：交易控制检查</li>
	 *         </p>
	 * @param eventId
	 *            交易事件ID
	 */
	public static void checkTrxnControl(String eventId) {

		ApBaseRule.checkTrxnControl(eventId);
	}

	/**
	 * @Author shenxy
	 *         <p>
	 *         <li>2017年9月29日-下午2:27:47</li>
	 *         <li>功能说明：弹出告警</li>
	 *         </p>
	 */
	public static void popupWarning() {

		ApBaseRule.popupWarning();
	}

	/**
	 * @Author shenxy
	 *         <p>
	 *         <li>2017年10月26日-上午9:45:41</li>
	 *         <li>功能说明：弹出场景授权窗口</li>
	 *         </p>
	 */
	public static void popupSceneAuth() {
		ApBaseRule.popupSceneAuth();
	}

	/**
	 * 内部校验单个规则id,不含例外的控制
	 */
	public static boolean checkRule(String ruleId, ApRuleDataRetProcessor processor, BufferedDataVisitor dataVisitor) {
		return ApBaseRule.checkRule(ruleId, processor, dataVisitor);
	}
	

	/**
	 * 参数按照模板替换 ${RUN_ENV.trx_date}、${INPUT.acct_no}或 ${trx_date}取默认的dataMart
	 * 
	 * @param ruleBuffer
	 * @param data_mapping_value
	 * @param dataMart
	 * @return
	 */
	public static String paramTemplate(Map<String, Object> ruleBuffer, String value, String defaultDataMart) {
		return ApBaseRule.paramTemplate(ruleBuffer, value, defaultDataMart);
	}

	/**
	 * 
	 * 
	 * @Author tsichang
	 *         <p>
	 *         <li>2018年2月8日-下午3:27:19</li>
	 *         <li>修改记录</li>
	 *         <li>-----------------------------------------------------------</li>
	 *         <li>标记：修订内容</li>
	 *         <li>2018年2月8日-tsichang：规则条件的匹配结果处理</li>
	 *         <li>-----------------------------------------------------------</li>
	 *         </p>
	 */
	public interface ApRuleDataRetProcessor {
		void process(ApRuleMappingDetail mappingDetail);
	}
	
	/**
	 * 判断规则ID是否存在
	 * 
	 * @param ruleId
	 *            规则ID
	 * @return true-存在 false-不存在
	 */
	public static boolean existsRule(String ruleId) {
		return ApBaseRule.existsRule(ruleId);
	}
	
}
