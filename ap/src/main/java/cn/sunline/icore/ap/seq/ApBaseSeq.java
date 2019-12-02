package cn.sunline.icore.ap.seq;

import cn.sunline.clwj.msap.util.sequ.MsSeqUtil;

/**
 * <p>
 * 文件功能说明：流水号
 * 
 * </p>
 * 
 * @Author chensy
 *         <p>
 *         <li>2016年12月5日-上午9:09:09</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20161205 chensy：创建</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBaseSeq {
	

	/**
	 * 
	 * @Author chensy
	 *         <p>
	 *         <li>2016年12月8日-下午2:08:43</li>
	 *         <li>功能说明：获取一个字符型流水号</li>
	 *         <li>当流水组建标志为Y时，调用此函数前需要调用addDataToBuffer方法，往runEnvs中设置app_sequence_builder对于的rule_buffer</li>
	 *         </p>
	 * @param seqCode
	 * @return
	 */
	public static String genSeq(String seqCode) {
		
		return MsSeqUtil.genSeq(seqCode);
	}
	
}
