package cn.sunline.icore.ap.api;

import cn.sunline.icore.ap.mail.ApBaseMail;
import cn.sunline.icore.ap.tables.TabApMail.apb_mail_book;
import cn.sunline.icore.ap.type.ComApMail.ApAssembleEmailInfo;

/**
 * <p>
 * 文件功能说明：邮件相关功能
 * </p>
 * 
 * @Author zhangsl
 *         <p>
 *         <li>2017年11月30日-下午4:19:21</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年11月30日-zhangsl：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApMailApi {

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年12月1日-上午11:11:07</li>
	 *         <li>功能说明：邮件发送</li>
	 *         </p>
	 * @param trxnSeq
	 * @return
	 */
	public static Boolean sendMail(String trxnSeq) {

		return ApBaseMail.sendMail(trxnSeq);
	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年12月1日-上午11:13:07</li>
	 *         <li>功能说明：登记邮件发送簿</li>
	 *         </p>
	 * @param mailInfo
	 * @return
	 */

	public static void registerMailInfo(ApAssembleEmailInfo mailInfo) {
		ApBaseMail.registerMailInfo(mailInfo);
	}

	
	/**
	 * 
	 * @Author javie
	 *         <p>
	 *         <li>2017年12月15日-下午4:35:14</li>
	 *         <li>功能说明：批量发送邮件</li>
	 *         </p>
	 */
	public static void sendMail(apb_mail_book tabMailBook) {
		
		ApBaseMail.sendMail(tabMailBook);		
	}

}
