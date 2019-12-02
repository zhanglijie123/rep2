package cn.sunline.icore.ap.sms;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.batch.ApFileSend;
import cn.sunline.icore.ap.file.ApBaseFile;
import cn.sunline.icore.ap.namedsql.ApSmsBaseDao;
import cn.sunline.icore.ap.seq.ApBaseSeq;
import cn.sunline.icore.ap.tables.TabApSms.Apb_sms_bookDao;
import cn.sunline.icore.ap.tables.TabApSms.App_sms_elementDao;
import cn.sunline.icore.ap.tables.TabApSms.apb_sms_book;
import cn.sunline.icore.ap.tables.TabApSms.app_sms_element;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApSmsFile {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApSmsFile.class);

	// 获取短信文件最大条数
	private static final int FILE_COUNT = 1;

	// 短信记录起始要素
	private static final String SMS_START_ELEMENT = "<";

	// 短信记录结束要素
	private static final String SMS_END_ELEMENT = ">";

	// 英文短信最大字节数
	private static final int ENGLISH_SMS_MSXCOUNTS = 160;

	// 其他短信最大字节数
	private static final int OTHER_SMS_MSXCOUNTS = 140;

	// 短信记录要素编码指向D（默认值 Alphabet）
	private static final String SMS_ENCORDING_TYPE_DEFAULT = "D";

	// 短信记录要素编码指向U（USC2）
	private static final String SMS_ENCORDING_TYPE_UNICODE = "U";

	// 短讯公司电话
	private static final String SMS_COMPANY_PHONE = "85366017337";

	// 拼接后字符串长度21
	private static final int SMS_APPEND_LENGTH_TWENTY_ONE = 21;

	// 拼接后字符串长度3
	private static final int SMS_APPEND_LENGTH_THREE = 3;

	// 拼接后字符串长度20
	private static final int SMS_APPEND_LENGTH_TWENTY = 20;

	// 短信记录要输默认值0
	private static final String SMS_CONTENT_DEFAULT_ZERO = "0";

	// 短信记录要输默认值1
	private static final String SMS_CONTENT_DEFAULT_ONE = "1";

	// 时间格式HHmmss
	private static final String TIME_FORMAT = "HHmmss";

	// 短信文件名称序号
	private static final String SMS_FILENAME_NO = "SMS_FILE_NO";

	// 获取短信文件本地目录
	private static final String SMS_UPLOAD_LOCALFILE = "AP_SMS_LOCAL";

	// 获取短信文件远程
	private static final String SMS_UPLOAD_REMOTEFILE = "AP_SMS_REMOTE";

	// ASCII编码
	private static final String ASCII_ENCORDING = "ASCII";

	// USC2编码
	private static final String UNICODE_ENCORDING = "UnicodeBigUnmarked";

	public static void smsEventUploadMain() {

		String localRootPath = ApBaseFile.getFullPath(SMS_UPLOAD_LOCALFILE); // 本地的绝对路径部分
		bizlog.debug("localRootPath>>>>[%s] ", localRootPath);
		// 获取每个文件的记录数
		int recordNum = FILE_COUNT;

		String fileId = ApFileSend.genFileId();

		smsSingleUpload(localRootPath, fileId, recordNum);
	}

	private static void smsSingleUpload(String localRootPath, String fileId, int recordNum) {

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();

		// 文件名
		String fileName = String.format("GO%s.MSG", ApBaseSeq.genSeq(SMS_FILENAME_NO));// 文件名称

		// 本地路径
		// localRootPath = ApFile.getFileFullPath(localRootPath, fileName);

		bizlog.debug("localRootPath1>>>>[%s] [%s]", localRootPath, fileName);

		// 更新写入文件的数据状态
		ApSmsBaseDao.updateSmsSendStatus(MsOrg.getReferenceOrgId(apb_sms_book.class), fileId, (long) recordNum);

		// 取出要写的文件的内容
		Collection<apb_sms_book> enventTableList = Apb_sms_bookDao.selectAll_odb4(fileId, E_YESORNO.YES, false);

		if (enventTableList.size() == 0) {

			return;

		}
		else {

			String smsFinish = SMS_END_ELEMENT; // 短信结束部分

			FileOutputStream writeOut = null;
			int maxCharBytes = ENGLISH_SMS_MSXCOUNTS; // 字符短信最大字节长度
			int maxChineseBytes = OTHER_SMS_MSXCOUNTS; // 其他短信最大字节长度
			String smsEncodingType = SMS_ENCORDING_TYPE_DEFAULT; // 编码类型，默认为ASCII编码
			E_YESORNO longSmsInd = E_YESORNO.NO; // 长短信标志，默认为N
			int maxBytes = 0;

			for (apb_sms_book tabApbSmsBook : enventTableList) {

				byte[] smsPreContent = null; // 短信前缀
				StringBuffer asciiString = new StringBuffer();

				List<byte[]> smsByteList;

				//  查询短信模板内容，确定短信语言。
				app_sms_element tabSmsElement = App_sms_elementDao.selectFirst_odb3(tabApbSmsBook.getSms_template_no(), false);

				try {

					if (CommUtil.compare(tabSmsElement.getSms_language(), "*") == 0) {

						maxBytes = maxCharBytes;
						// 英文短信是否长短信处理判断及处理
						smsByteList = getLongByte(tabApbSmsBook.getSms_content(), maxBytes, ASCII_ENCORDING, new Random().nextInt(255));

					}
					else {

						maxBytes = maxChineseBytes;
						// 其他语言短信是否长短信处理
						smsByteList = getLongByte(tabApbSmsBook.getSms_content(), maxChineseBytes, UNICODE_ENCORDING, new Random().nextInt(255));

						// 非英文时  制定编码为UnicodeBigUnmarked(USC2)
						smsEncodingType = SMS_ENCORDING_TYPE_UNICODE;
					}

					// 判断返回的sms集合大小确定是否为长短信
					if (smsByteList.size() > 1) {
						longSmsInd = E_YESORNO.YES;
					}

					asciiString.append(SMS_START_ELEMENT).append(runEnvs.getTrxn_date()).append(new SimpleDateFormat(TIME_FORMAT).format(new Date()))
							.append(appendSpace(SMS_APPEND_LENGTH_TWENTY, SMS_COMPANY_PHONE)).append(appendSpace(SMS_APPEND_LENGTH_TWENTY_ONE, tabApbSmsBook.getMobile_phone_no()))
							.append(appendSpace(SMS_APPEND_LENGTH_THREE, SMS_CONTENT_DEFAULT_ZERO)).append(smsEncodingType).append(maxCharBytes).append(longSmsInd)
							.append(E_YESORNO.YES).append(appendSpace(SMS_APPEND_LENGTH_THREE, SMS_CONTENT_DEFAULT_ONE))
							.append(appendSpace(SMS_APPEND_LENGTH_THREE, SMS_CONTENT_DEFAULT_ONE));

					smsPreContent = asciiString.toString().getBytes(ASCII_ENCORDING);

					// 确定文件地址
					//writeOut = new FileOutputStream("D:/" + fileName, true);
					writeOut = new FileOutputStream(ApBaseFile.getLocalHome() + localRootPath + fileName, true);
					bizlog.debug("file upload local >>>[%s]", ApBaseFile.getLocalHome() + localRootPath + fileName);

					for (byte[] bs : smsByteList) {
						writeOut.write(smsPreContent);
						writeOut.write(bs);
						writeOut.write(smsFinish.getBytes());

						writeOut.flush();
					}

				}
				catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				catch (IOException e) {
					e.printStackTrace();
				}

			}
			try {
				writeOut.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			// 登记文件发送部
			ApFileSend.register(fileId, fileName, SMS_UPLOAD_REMOTEFILE, SMS_UPLOAD_LOCALFILE, E_YESORNO.YES);
		}
		bizlog.debug("localRootPath[%s]", localRootPath);
	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年10月11日-上午10:16:01</li>
	 *         <li>功能说明：是否长短信判断及处理</li>
	 *         </p>
	 * @param message
	 * @param maxLength
	 * @param encoding
	 * @param longSmsHeadFour
	 * @return
	 */
	public static List<byte[]> getLongByte(String message, int maxLength, String encoding, int longSmsHeadFour) {

		List<byte[]> list = new ArrayList<byte[]>();
		byte[] messageUCS2;
		int subByte = 0;
		int addSpace = 0;

		try {
			messageUCS2 = message.getBytes(encoding);
			int messageUCS2Len = messageUCS2.length; // 短信长度

			// 长短信发送
			if (messageUCS2Len > maxLength) {

				if (maxLength == 140) {
					subByte = 8;
					addSpace = 2;
				}
				else {
					subByte = 7;
					addSpace = 1;
				}

				int messageUCS2Count = messageUCS2Len / (maxLength - 6) + 1; // 长短信分为多少条发送

				byte[] udhiHead = new byte[6]; //  长短信数据头
				udhiHead[0] = 0x05; // 表示剩余协议头的长度
				udhiHead[1] = 0x00; // 长短信标识
				udhiHead[2] = 0x03; // 剩下短信标识的长度
				udhiHead[3] = (byte) longSmsHeadFour; // 这批长短信的唯一标志
				udhiHead[4] = (byte) messageUCS2Count; // 这批短信的数量
				udhiHead[5] = 0x01;// 这批短信的第几条  默认为第一条

				for (int i = 0; i < messageUCS2Count; i++) {

					udhiHead[5] = (byte) (i + 1);

					byte[] msgContent;

					// 不为最后一条,及不用拼接占位符
					if (i != messageUCS2Count - 1) {

						msgContent = byteAdd(udhiHead, messageUCS2, i * (maxLength - subByte), (i + 1) * (maxLength - subByte));
						msgContent = byteApendSpace(msgContent, addSpace);
						list.add(msgContent);
						System.out.println("第一条" + msgContent.length);
					}
					else {

						msgContent = byteAdd(udhiHead, messageUCS2, i * (maxLength - subByte), messageUCS2Len);

						// 最后一条拼接占位符
						msgContent = byteApendSpace(msgContent, addSpace + maxLength - subByte - (messageUCS2Len - i * (maxLength - subByte)));
						list.add(msgContent);
					}
				}
			}
			// 非长短信
			else {

				// 长度不够直接拼接占位符
				messageUCS2 = byteApendSpace(messageUCS2, maxLength - messageUCS2Len);
				list.add(messageUCS2);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年10月11日-上午9:56:05</li>
	 *         <li>长短信分割短信字节</li>
	 *         </p>
	 * @param tpUdhiHead
	 * @param messageUCS2
	 * @param endIndex
	 * @param starIndex
	 * @return
	 */
	private static byte[] byteAdd(byte[] tpUdhiHead, byte[] messageUCS2, int starIndex, int endIndex) {
		byte[] msgb = new byte[endIndex - starIndex + 6];
		System.arraycopy(tpUdhiHead, 0, msgb, 0, 6);
		System.arraycopy(messageUCS2, starIndex, msgb, 6, endIndex - starIndex);

		return msgb;
	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年10月11日-下午3:41:17</li>
	 *         <li>功能说明：拼接短信内容占位符</li>
	 *         </p>
	 * @param smsContent
	 * @param apendLength
	 * @return
	 */
	private static byte[] byteApendSpace(byte[] smsContent, int apendLength) {

		byte[] msgb = new byte[smsContent.length + apendLength];
		byte[] apendByte = new byte[apendLength];

		for (int k = 0; k < apendByte.length; k++) {
			apendByte[k] = 0X00;
		}

		System.arraycopy(smsContent, 0, msgb, 0, smsContent.length);
		System.arraycopy(apendByte, 0, msgb, smsContent.length, apendLength);
		return msgb;
	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年10月12日-上午10:58:20</li>
	 *         <li>短信字节不够拼接空格</li>
	 *         </p>
	 * @param maxLength
	 * @param element
	 * @return
	 */
	private static String appendSpace(int maxLength, String element) {

		return String.format("%1$-" + maxLength + "s", element);

	}

}
