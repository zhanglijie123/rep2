package cn.sunline.icore.ap.sms;

import java.util.List;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.parm.ApBaseBusinessParm;
import cn.sunline.icore.ap.parm.ApBaseTrxn;
import cn.sunline.icore.ap.rule.ApBaseBuffer;
import cn.sunline.icore.ap.rule.ApBaseRule;
import cn.sunline.icore.ap.tables.TabApRule.app_rule;
import cn.sunline.icore.ap.tables.TabApSms.Apb_sms_bookDao;
import cn.sunline.icore.ap.tables.TabApSms.App_sms_elementDao;
import cn.sunline.icore.ap.tables.TabApSms.App_sms_languageDao;
import cn.sunline.icore.ap.tables.TabApSms.App_sms_sendDao;
import cn.sunline.icore.ap.tables.TabApSms.App_sms_templateDao;
import cn.sunline.icore.ap.tables.TabApSms.apb_sms_book;
import cn.sunline.icore.ap.tables.TabApSms.app_sms_element;
import cn.sunline.icore.ap.tables.TabApSms.app_sms_language;
import cn.sunline.icore.ap.tables.TabApSms.app_sms_send;
import cn.sunline.icore.ap.tables.TabApSms.app_sms_template;
import cn.sunline.icore.ap.type.ComApSms.ApSmsAssembleIn;
import cn.sunline.icore.ap.type.ComApSms.ApSmsSendIn;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.parm.TrxEnvs.SmsSendInfo;
import cn.sunline.icore.sys.type.EnumType.E_ONLBATCHIND;
import cn.sunline.icore.sys.type.EnumType.E_SMSELEMENTTYPE;
import cn.sunline.icore.sys.type.EnumType.E_SMSFORMAT;
import cn.sunline.icore.sys.type.EnumType.E_SMSSENDSTATUS;
import cn.sunline.icore.sys.type.EnumType.E_SMSSIGNWAY;
import cn.sunline.ltts.base.logging.LogConfigManager.SystemType;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApSms {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApSms.class);
	private static final String CUST_FIELD = "cust_no";

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年9月8日-上午9:44:15</li>
	 *         <li>功能说明：根据短信模板号发送短信</li>
	 *         </p>
	 * @param smsTemplateNo
	 */
	public static void sendSmsByTemplateNo(String smsTemplateNo) {

		// 必输项校验
		BizUtil.fieldNotNull(smsTemplateNo, SysDict.A.sms_template_no.getId(), SysDict.A.sms_template_no.getLongName());

		parseAndSendSms(smsTemplateNo);
	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年9月8日-上午9:44:49</li>
	 *         <li>功能说明：根据交易事件获取短信模板号发送短信</li>
	 *         </p>
	 * @param trxnEventId
	 */
	public static void sendSmsByTrxnEventId(String trxnEventId) {

		// 必输项校验
		BizUtil.fieldNotNull(trxnEventId, SysDict.A.trxn_event_id.getId(), SysDict.A.trxn_event_id.getLongName());

		// 获取规则情景码
		String scence = ApBaseBusinessParm.getValue("SMS_TEMPLATE_CODE", trxnEventId);

		if (CommUtil.isNull(scence)) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_rule.class).getLongname(), SysDict.A.rule_scene_code.getLongName(), scence);
		}

		// 根据规则号情景码查询规则结果
		String smsTempNo = ApBaseRule.getFirstResultByScene(scence);

		parseAndSendSms(smsTempNo);
	}

	/**
	 * @Author zoujh
	 *         <p>
	 *         <li>2017年8月24日-下午4:26:22</li>
	 *         <li>功能说明：解析并发送短信模板</li>
	 *         </p>
	 * @param smsTem
	 */
	private static void parseAndSendSms(String smsTempNo) {

		bizlog.method(" ApSms.parseSms begin >>>>>>>>>>>>>>>>");
		bizlog.debug(">>>>>smsTempNo=[%s]", smsTempNo);

		// Access to public RunEnvs parmsmsTem
		String smslanguage = ApConst.WILDCARD; // sms language
		String smsSignlanguage; // sms sign language
		String mobilePhoneNum = "";
		E_YESORNO isSeparator = E_YESORNO.NO;

		// 根据短信模板规则号查询对应的模板信息
		app_sms_template tabSmsTemplate = App_sms_templateDao.selectOne_odb1(smsTempNo, false);

		if (tabSmsTemplate == null) {
			return;
		}
		// 短信格式为分隔符时 分隔符不允许为空
		if (tabSmsTemplate.getSms_format() == E_SMSFORMAT.SEPARATOR) {
			BizUtil.fieldNotNull(tabSmsTemplate.getSms_separator(), SysDict.A.sms_separator.getId(), SysDict.A.sms_separator.getLongName());
			isSeparator = E_YESORNO.YES;
		}

		// 当短信签约方式为核心时，校验是否签约短信
		if (tabSmsTemplate.getSms_sign_way() == E_SMSSIGNWAY.CORE) {

			BizUtil.fieldNotNull(tabSmsTemplate.getAgree_data_mart(), SysDict.A.agree_data_mart.getId(), SysDict.A.agree_data_mart.getLongName());
			BizUtil.fieldNotNull(tabSmsTemplate.getPhone_field_name(), SysDict.A.phone_field_name.getId(), SysDict.A.phone_field_name.getLongName());
			BizUtil.fieldNotNull(tabSmsTemplate.getLanguage_field_name(), SysDict.A.language_field_name.getId(), SysDict.A.language_field_name.getLongName());

			try {
				mobilePhoneNum = ApBaseBuffer.getFieldValue(tabSmsTemplate.getAgree_data_mart(), tabSmsTemplate.getPhone_field_name());
			}
			catch (Exception e) {
				bizlog.error("Get Field Value [%s]", e, e.getMessage());
				mobilePhoneNum = "";
			}

			try {
				smsSignlanguage = ApBaseBuffer.getFieldValue(tabSmsTemplate.getAgree_data_mart(), tabSmsTemplate.getLanguage_field_name());
			}
			catch (Exception e) {
				bizlog.error("Get Field Value [%s]", e, e.getMessage());
				smsSignlanguage = ApConst.WILDCARD;
			}

			// 查询短信发送语言
			app_sms_language tabSmsLanguage = App_sms_languageDao.selectOne_odb1(smsSignlanguage, false);

			if (tabSmsLanguage != null) {
				smslanguage = tabSmsLanguage.getSms_language();
			}

		}

		// 拼装短信内容
		ApSmsAssembleIn assembleIn = BizUtil.getInstance(ApSmsAssembleIn.class);
		assembleIn.setSms_language(smslanguage);
		assembleIn.setSms_separator(tabSmsTemplate.getSms_separator());
		assembleIn.setSms_separator_ind(isSeparator);
		assembleIn.setSms_template_no(smsTempNo);
		String smsConten = assembleSms(assembleIn);

		// 短信发送(轮询and及时)
		ApSmsSendIn sendIn = BizUtil.getInstance(ApSmsSendIn.class);
		sendIn.setSms_template_no(smsTempNo);
		sendIn.setMobile_phone_no(mobilePhoneNum);
		sendIn.setSms_content(smsConten);
		sendSms(sendIn);
	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年9月8日-上午11:09:16</li>
	 *         <li>功能说明：拼装短信内容</li>
	 *         </p>
	 * @param assembleIn
	 * @return
	 */
	private static String assembleSms(ApSmsAssembleIn assembleIn) {

		// 短信缓冲流
		StringBuilder smsContentBuffer = new StringBuilder();

		// 查询短信模板定义
		List<app_sms_element> tabSmsElementDetails = App_sms_elementDao.selectAll_odb2(assembleIn.getSms_template_no(), assembleIn.getSms_language(), false);

		if (tabSmsElementDetails == null || tabSmsElementDetails.isEmpty()) {

			throw ApBaseErr.ApBase.E0057();
		}
		String fieldValue = null;
		for (app_sms_element tabSmsElement : tabSmsElementDetails) {

			// 当短信要素类型为字段——A 时数据集与字段名不能为空
			if (tabSmsElement.getSms_element_type() == E_SMSELEMENTTYPE.FIELD) {
				BizUtil.fieldNotNull(tabSmsElement.getData_mart(), SysDict.A.data_mart.getId(), SysDict.A.data_mart.getLongName());
				BizUtil.fieldNotNull(tabSmsElement.getField_name(), SysDict.A.field_name.getId(), SysDict.A.field_name.getLongName());

				try {
					fieldValue = ApBaseBuffer.getFieldValue(tabSmsElement.getData_mart(), tabSmsElement.getField_name());
				}
				catch (Exception e) {
					bizlog.error(tabSmsElement.getData_mart() + "---" + tabSmsElement.getField_name(), e, e.getMessage());
					fieldValue = "";
				}
				// 字段值是否截取
				if ((CommUtil.isNotNull(fieldValue)) && (CommUtil.isNotNull(tabSmsElement.getCut_length()) && CommUtil.isNotNull(tabSmsElement.getStart_position()))
						&& (tabSmsElement.getCut_length() > 0 && tabSmsElement.getStart_position() != 0)) {

					// 负数 则为倒序截取
					if (tabSmsElement.getStart_position() < 0) {
						int startPostion = fieldValue.length() + tabSmsElement.getStart_position().intValue();
						fieldValue = (String) fieldValue.substring(startPostion, startPostion + tabSmsElement.getCut_length().intValue());
					}
					else {
						int cutEndPosition = tabSmsElement.getStart_position().intValue() + tabSmsElement.getCut_length().intValue();

						if (cutEndPosition > fieldValue.length()) {
							throw ApBaseErr.ApBase.E0054();
						}

						fieldValue = (String) fieldValue.substring(tabSmsElement.getStart_position().intValue(), cutEndPosition);
					}
				}

				// 获取对应的字段值 拼接到缓冲
				smsContentBuffer.append(fieldValue);
			}
			else {

				BizUtil.fieldNotNull(tabSmsElement.getConstant_value(), SysDict.A.constant_value.getId(), SysDict.A.constant_value.getLongName());
				String constanValue = tabSmsElement.getConstant_value();

				// 字段值是否截取
				if ((CommUtil.isNotNull(tabSmsElement.getCut_length()) && CommUtil.isNotNull(tabSmsElement.getStart_position()))
						&& (tabSmsElement.getCut_length() > 0 && tabSmsElement.getStart_position() != 0)) {

					// 负数 则为倒序截取
					if (tabSmsElement.getStart_position() < 0) {
						int startPostion = constanValue.length() + tabSmsElement.getStart_position().intValue();
						constanValue = (String) constanValue.substring(startPostion, startPostion + tabSmsElement.getCut_length().intValue());
					}
					else {
						int cutEndPosition = tabSmsElement.getStart_position().intValue() + tabSmsElement.getCut_length().intValue();

						if (cutEndPosition > constanValue.length()) {
							throw ApBaseErr.ApBase.E0054();
						}

						constanValue = (String) constanValue.substring(tabSmsElement.getStart_position().intValue(), cutEndPosition);
					}
				}

				smsContentBuffer.append(constanValue);
			}

			// 短信格式为分隔符时 拼接分隔符
			if (assembleIn.getSms_separator_ind() == E_YESORNO.YES) {
				smsContentBuffer.append(assembleIn.getSms_separator());
			}
		}
		return smsContentBuffer.toString();
	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年9月8日-上午11:08:42</li>
	 *         <li>功能说明：短信发送</li>
	 *         </p>
	 * @param sendIn
	 */
	private static void sendSms(ApSmsSendIn sendIn) {

		RunEnvs runEven = BizUtil.getTrxRunEnvs();
		E_ONLBATCHIND inlBatchInd = E_ONLBATCHIND.ONLINE;

		if (SystemType.batch == SysUtil.getCurrentSystemType()) {
			inlBatchInd = E_ONLBATCHIND.BATCH;
		}
		bizlog.debug(">>>>>inlBatchInd=[%s]", inlBatchInd);
		// 查询短信的发送方式
		app_sms_send tabSmsSend = App_sms_sendDao.selectOne_odb1(sendIn.getSms_template_no(), inlBatchInd, false);

		if (tabSmsSend == null) {
			ApPubErr.APPUB.E0024(OdbFactory.getTable(app_sms_send.class).getLongname(), SysDict.A.sms_template_no.getId(), SysDict.A.sms_template_no.getLongName(),
					SysDict.A.onl_batch_ind.getId(), SysDict.A.onl_batch_ind.getLongName());
		}
		// 延迟发送写入短信发送登记簿
		else if (tabSmsSend.getSms_real_send_ind() != E_YESORNO.YES) {

			apb_sms_book tabSmsBook = BizUtil.getInstance(apb_sms_book.class);
			tabSmsBook.setTrxn_seq(runEven.getTrxn_seq()); // transaction
															// sequence
			tabSmsBook.setSerial_no(ApBaseTrxn.getSerial()); // serial no
			tabSmsBook.setTrxn_date(runEven.getTrxn_date()); // transaction date
			tabSmsBook.setSms_template_no(sendIn.getSms_template_no()); // sms  template number
			tabSmsBook.setCust_no(ApBaseBuffer.getFieldValue(ApConst.CUST_DATA_MART, CUST_FIELD)); // customer number
			tabSmsBook.setMobile_phone_no(sendIn.getMobile_phone_no()); // mobile phone number
			tabSmsBook.setSms_poll_send_way(tabSmsSend.getSms_poll_send_way()); // sms poll send way
			tabSmsBook.setSms_content(sendIn.getSms_content()); // sms content
			tabSmsBook.setSms_send_status(E_SMSSENDSTATUS.UNSENT); // sms send status
			tabSmsBook.setData_sync_file_id(null);
			tabSmsBook.setData_sync_ind(E_YESORNO.NO);
			Apb_sms_bookDao.insert(tabSmsBook);
		}
		else {

			SmsSendInfo sendInfo = BizUtil.getInstance(SmsSendInfo.class);
			sendInfo.setCust_no(ApBaseBuffer.getFieldValue(ApConst.CUST_DATA_MART, CUST_FIELD));
			sendInfo.setMobile_phone_no(sendIn.getMobile_phone_no());
			sendInfo.setSms_content(sendIn.getSms_content());

			// 实时发送将客户号，手机号，短信内容 set 到运行区
			runEven.getSms_send_info().add(sendInfo);
		}

	}

}
