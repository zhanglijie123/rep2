package cn.sunline.icore.ap.mail;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.component.AbstractComponent;
import cn.sunline.icore.ap.component.ApBaseComp;
import cn.sunline.icore.ap.namedsql.ApMailBaseDao;
import cn.sunline.icore.ap.parm.ApBaseBusinessParm;
import cn.sunline.icore.ap.parm.ApBaseTrxn;
import cn.sunline.icore.ap.rule.ApBaseRule;
import cn.sunline.icore.ap.tables.TabApMail.Apb_mail_bookDao;
import cn.sunline.icore.ap.tables.TabApMail.App_mail_templateDao;
import cn.sunline.icore.ap.tables.TabApMail.apb_mail_book;
import cn.sunline.icore.ap.tables.TabApMail.app_mail_template;
import cn.sunline.icore.ap.tables.TabApRule.app_rule;
import cn.sunline.icore.ap.type.ComApMail.ApAssembleEmailInfo;
import cn.sunline.icore.ap.type.ComApMail.ApMailAttachement;
import cn.sunline.icore.ap.type.ComApMail.ApMailInfoToSend;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_MAILSENDSTATUS;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

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
public class ApBaseMail {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBaseMail.class);

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

		// 实例化邮件发送组件
		ApBaseComp.SendMail tran = SysUtil.getInstance(ApBaseComp.SendMail.class, AbstractComponent.AbsSendMail);

		// 查询待发送的邮件
		List<ApMailInfoToSend> mailList = ApMailBaseDao.selSendMailInfo(MsOrg.getReferenceOrgId(apb_mail_book.class), trxnSeq, E_MAILSENDSTATUS.UNSENT, false);

		Boolean flag = false;

		// 循环发送
		for (ApMailInfoToSend apMailInfoToSend : mailList) {

			Options<ApMailAttachement> attachementList = new DefaultOptions<ApMailAttachement>();

			if (CommUtil.isNotNull(apMailInfoToSend.getAttachment_name())) {
				// 解析附件列表
				List<ApMailAttachement> attachementList2 = JSONArray.parseArray(apMailInfoToSend.getAttachment_name(), ApMailAttachement.class);

				for (ApMailAttachement apMailAttachement : attachementList2) {
					attachementList.add(apMailAttachement);
				}
			}

			flag = tran.send(apMailInfoToSend.getE_mail(), apMailInfoToSend.getMail_topic(), apMailInfoToSend.getMail_content(), attachementList);

			if (flag) {
				bizlog.debug("success");
				ApMailBaseDao.updateMailSendStatus(MsOrg.getReferenceOrgId(apb_mail_book.class), E_MAILSENDSTATUS.SENT, trxnSeq, apMailInfoToSend.getSerial_no());
			}
			else {
				bizlog.debug("failed");
				ApMailBaseDao.updateMailSendStatus(MsOrg.getReferenceOrgId(apb_mail_book.class), E_MAILSENDSTATUS.SENT_FAILED, trxnSeq, apMailInfoToSend.getSerial_no());
			}
		}

		return flag;
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
		bizlog.method(" ApMail.registerMailInfo begin >>>>>>>>>>>>>>>>");
		bizlog.debug(" begin registerMailInfo:>>>>>>[%s]", mailInfo);

		// 获取公共运行区对象
		RunEnvs runEvens = BizUtil.getTrxRunEnvs();

		String mailContent = null;// 邮件通知内容

		app_mail_template tabMialTemp=null;
		// 根据邮件模板号查询，如果无记录，则不发送邮件
		tabMialTemp = App_mail_templateDao.selectOne_odb1(mailInfo.getMail_template_no(), false);

		if (tabMialTemp == null) {
			
			if(!ApBaseBusinessParm.exists(ApConst.RULE_MAIL_TEMPLATE, mailInfo.getTrxn_event_id())){
				
				bizlog.debug("RULE MAIL TEMPLATE NOT EXISTS [%s][%s]", ApConst.RULE_MAIL_TEMPLATE, mailInfo.getTrxn_event_id());
				bizlog.method(" ApMail.registerMailInfo end <<<<<<<<<<<<<<<<");
				return;
			}
			
			// 为空则通过交易事件匹配规则
			String scence = ApBaseBusinessParm.getValue(ApConst.RULE_MAIL_TEMPLATE, mailInfo.getTrxn_event_id());

			if (CommUtil.isNull(scence)) {
				throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_rule.class).getLongname(), SysDict.A.rule_scene_code.getLongName(), scence);
			}

			// 根据规则号情景码查询规则结果
			String mailTempNo = ApBaseRule.getFirstResultByScene(scence);
			
			tabMialTemp = App_mail_templateDao.selectOne_odb1(mailTempNo, false);
			
			if(tabMialTemp==null){
				
				bizlog.method(" ApMail.registerMailInfo end <<<<<<<<<<<<<<<<");
				return;
			}
		}

		// 邮件模板格式验证，分别截取邮件模板中“{”、“}”数量，如果数量不相等，则模板格式配置错误
		int lnum = splitNum(tabMialTemp.getMail_content(), "{");
		int rnum = splitNum(tabMialTemp.getMail_content(), "}");

		if (lnum == 0 && rnum==0) {
			mailContent = tabMialTemp.getMail_content();
		}
		else {
			if (lnum != rnum) {
				throw ApBaseErr.ApBase.E0072();
			}

			String mailModel = tabMialTemp.getMail_content();
			
			// 邮件模板要素替换
			if ( mailInfo.getMailData().size() > 0) {
				Map<String, String> dataMap = mailInfo.getMailData();
				Object keyValue;
				for (String key : dataMap.keySet()) {
					keyValue=dataMap.get(key);
					if(keyValue==null){
						keyValue="";
					}
					mailModel = mailModel.replaceAll("\\{" + key + "\\}", keyValue.toString());
				}
				mailContent = mailModel;
			}
		}

		bizlog.debug("mailContent[%s]", mailContent);

		// 登记邮件登记簿
		apb_mail_book tabMailBook = BizUtil.getInstance(apb_mail_book.class);
		tabMailBook.setTrxn_seq(runEvens.getTrxn_seq()); //transaction sequence
		tabMailBook.setSerial_no(ApBaseTrxn.getSerial()); //serial no
		tabMailBook.setTrxn_date(runEvens.getTrxn_date()); //transaction date
		tabMailBook.setMail_template_no(tabMialTemp.getMail_template_no()); //mail template number
		tabMailBook.setMail_content(mailContent); //mail content

		// 从集合中取出附件对象转为Json保存
		if (tabMialTemp.getAttachment_ind() == E_YESORNO.YES && mailInfo.getAttachListInfo().size() > 0) {
			String attacheName = BizUtil.toJson(mailInfo.getAttachListInfo());
			tabMailBook.setAttachment_name(attacheName.toString()); //attachment name
		}

		tabMailBook.setCust_no(mailInfo.getCust_no()); //customer number
		tabMailBook.setE_mail(mailInfo.getE_mail()); //E-mail
		tabMailBook.setMail_send_status(E_MAILSENDSTATUS.UNSENT); //mail send status
		Apb_mail_bookDao.insert(tabMailBook);
		
		bizlog.method(" ApMail.registerMailInfo end <<<<<<<<<<<<<<<<");
	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年12月1日-上午11:11:25</li>
	 *         <li>功能说明：统计邮件待替换符号个数</li>
	 *         </p>
	 * @param content
	 * @param symbol
	 * @return
	 */
	private static int splitNum(String content, String symbol) {

		int num = 0;
		int index = content.indexOf(symbol);
		String contentTemp = content;

		while (index >= 0) {
			num++;
			contentTemp = contentTemp.substring(index + symbol.length());
			index = contentTemp.indexOf(symbol);
		}

		return num;
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
		
		bizlog.method(" ApMail.sendMail begin <<<<<<<<<<<<<<<<");
		bizlog.debug("tabMailBook==>>[%s]", tabMailBook);
		
		// 实例化邮件发送组件
		ApBaseComp.SendMail tran = SysUtil.getInstance(ApBaseComp.SendMail.class, AbstractComponent.AbsSendMail);
		
		boolean flag = false;
		
		Options<ApMailAttachement> attachementList = new DefaultOptions<ApMailAttachement>();

		if (CommUtil.isNotNull(tabMailBook.getAttachment_name())) {
			
			// 解析附件列表
			List<ApMailAttachement> attachementList2 = JSONArray.parseArray(tabMailBook.getAttachment_name(), ApMailAttachement.class);

			for (ApMailAttachement apMailAttachement : attachementList2) {
				attachementList.add(apMailAttachement);
			}
			
		}
		
		// 根据短信模板号查询模板信息
		app_mail_template  tabMailTemp = App_mail_templateDao.selectOne_odb1(tabMailBook.getMail_template_no(), false);
		
		// 邮件发送
		flag = tran.send(tabMailBook.getE_mail(), tabMailTemp.getMail_topic(), tabMailBook.getMail_content(), attachementList);

		bizlog.debug("flag==>>[%s]", flag);
		
		if (flag) {
			bizlog.debug("success");
			ApMailBaseDao.updateMailSendStatus(MsOrg.getReferenceOrgId(apb_mail_book.class), E_MAILSENDSTATUS.SENT, tabMailBook.getTrxn_seq(), tabMailBook.getSerial_no());
		}
		else {
			bizlog.debug("failed");
			ApMailBaseDao.updateMailSendStatus(MsOrg.getReferenceOrgId(apb_mail_book.class), E_MAILSENDSTATUS.SENT_FAILED, tabMailBook.getTrxn_seq(), tabMailBook.getSerial_no());
		}
		
	}

}
