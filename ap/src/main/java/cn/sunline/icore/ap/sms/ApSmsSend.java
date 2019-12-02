package cn.sunline.icore.ap.sms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;

import cn.sunline.icore.ap.parm.ApBaseBusinessParm;
import cn.sunline.icore.ap.tables.TabApSms.Apb_sms_bookDao;
import cn.sunline.icore.ap.tables.TabApSms.apb_sms_book;
import cn.sunline.icore.ap.type.ComApSms.ApSmsSendInfo;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.type.EnumType.E_SMSSENDSTATUS;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;


/**
 * <p>
 * 文件功能说明：
 * </p>
 * 
 * @Author javie
 *         <p>
 *         <li>2018年3月21日-下午4:34:06</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2018年3月21日-javie：创建注释模板</li>
 *         <li>--------------------短信发送------------------------------</li>
 *         </p>
 */
public class ApSmsSend {
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApSmsSend.class);

	public static Map<String, Object> sendSms(ApSmsSendInfo sendInfo) {

		bizlog.method(" ApSmsSend.sendSms begin >>>>>>>>>>>>>>>>[%s]", sendInfo);

		// 查询短信登记簿信息
		apb_sms_book tabSmsBook = Apb_sms_bookDao.selectOne_odb1(sendInfo.getTrxn_seq(), sendInfo.getSerial_no(), true);

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("phoneNumber", sendInfo.getMobile_phone_no());
		body.put("message", sendInfo.getSms_content());
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Map<String, Object> request = new HashMap<String, Object>();
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("userid", "user1");
		header.put("datetime", format.format(new Date()));
		header.put("uuid", UUID.randomUUID());
		header.put("workstation", BizUtil.getHostIp());
		header.put("timeout", "30000");
		header.put("channel", BizUtil.getTrxRunEnvs().getChannel_id());
		header.put("className", "SendSms");
		header.put("module", "sms");
		header.put("device", BizUtil.getHostName());
		request.put("header", header);
		request.put("body", body);
		
		String requestParam = JSON.toJSONString(request);

		// 发起请求
		String strResponse = ApSmsHttpReq.sendPost(ApBaseBusinessParm.getValue(ApConst.SMS_OTP_ADDRESS), requestParam);
		
		// 解析返还报文
		Map<String, Object> response = JSON.parseObject(strResponse, Map.class);
		Map<String, Object> responseHeader = (Map<String, Object>) response.get("header");

		// 返回码为1 则发送成功
		if (CommUtil.compare(responseHeader.get("errorCode").toString(), "1") == 0) {

			// 发送失败 登记错误信息
			String errorInfo = responseHeader.get("errorCode").toString() + ":" + responseHeader.get("errorMessage").toString();
			tabSmsBook.setRemark(errorInfo);
			// 更新发送状态
			tabSmsBook.setSms_send_status(E_SMSSENDSTATUS.SENT);
			Apb_sms_bookDao.updateOne_odb1(tabSmsBook);
		}
		else {
			// 发送失败 登记错误信息
			String errorInfo = responseHeader.get("errorCode").toString() + ":" + responseHeader.get("errorMessage").toString();
			// 更新发送状态
			tabSmsBook.setSms_send_status(E_SMSSENDSTATUS.SEND_FAILED);
			tabSmsBook.setRemark(errorInfo);
			Apb_sms_bookDao.updateOne_odb1(tabSmsBook);
		}

		bizlog.method(" ApSmsSend.sendSms end <<<<<<<<<<<<<<<<");
		return response;
	}

}
