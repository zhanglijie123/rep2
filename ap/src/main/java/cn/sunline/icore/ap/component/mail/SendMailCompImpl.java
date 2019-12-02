package cn.sunline.icore.ap.component.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import cn.sunline.icore.ap.component.ApBaseComp.SendMail;
import cn.sunline.icore.ap.component.ApSendMailCompImpl.SendMailComp;
import cn.sunline.icore.ap.type.ComApMail.ApMailAttachement;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;

public class SendMailCompImpl extends SendMailComp implements SendMail {

	private static final BizLog log = BizLogUtil.getBizLog(SendMailCompImpl.class);

	@Override
	public Boolean send(String emailAddress, String mailTopic, String mailContent, Options<ApMailAttachement> mailAttList) {

		// 添加邮件发送代码
		final String userId = this.getUserId();
		final String passWord = this.getPassWord();

		//必要的参数配置加载文件对象
		Properties props = new Properties();
		props.put("mail.smtp.host", this.getSmtpServer());
		props.put("mail.smtp.port", this.getPort());
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.ssl.enable", this.getSsl());
		props.put("mail.smtp.connectiontimeout", this.getConnectionTimeOut());
		props.put("mail.smtp.timeout", this.getTimeOut());

		//发送邮件session会话  同时验证给定的账号密码信息是否正确
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userId, passWord);
			}
		});

		//打开发送邮件调试器
		session.setDebug(true);

		try {
			//邮件装载容器
			MimeMessage msg = new MimeMessage(session);
			msg.setHeader("Content-Type", "text/plain;" + "charset=" + this.getEncoding());
			msg.setFrom(new InternetAddress(this.getUserId()));

			//设置收件人
			msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));

			msg.setSubject(mailTopic, this.getEncoding());

			//装载内容的组件
			Multipart multipart = new MimeMultipart();
			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setContent(mailContent, "text/html;" + "charset=" + this.getEncoding());
			multipart.addBodyPart(textBodyPart);

			//附件处理
			ArrayList<String> attList = new ArrayList<String>();

            if (mailAttList != null && !mailAttList.isEmpty()) {

				for (ApMailAttachement attach : mailAttList) {

					String fileName = attach.getFile_name();
					String filePath = attach.getLocal_file_path();

					log.debug("filePath>>[%s]", filePath);

					//读取制定的文件的内容存入到byte[] 数组中
					byte[] document = SendMailCompImpl.getBytesFromFile(new File(filePath));

					String strFileFullPath = this.getWorkPath() + fileName;

					log.debug("strFileFullPath[%s]", strFileFullPath);

					attList.add(strFileFullPath);

					//写入重组的路径文件中
                    FileOutputStream fileOut = new FileOutputStream(strFileFullPath);
					fileOut.write(document);

					MimeBodyPart attachmentBodyPart = new MimeBodyPart();

					//设定附件数据源
					DataSource source = new FileDataSource(strFileFullPath);
					attachmentBodyPart.setDataHandler(new DataHandler(source));

					//设置附件名为原文件名
					attachmentBodyPart.setFileName(MimeUtility.encodeText(fileName));
					multipart.addBodyPart(attachmentBodyPart);
					
					fileOut.close();
				}
			}

			msg.setContent(multipart);
			log.debug(">>>>>>>>>>>>Start send email<<<<<<<<<<<<<");
			Transport.send(msg);

			return true;
		}
		catch (MessagingException e) {
			log.error("mail muilt error:" + e.getMessage(), e, new Object[0]);
			return false;
		}
		catch (IOException e) {
			log.error("file not found: " + e.getMessage(), e, new Object[0]);
			return false;
		}

	}

	/**
	 * @Author javie
	 *         <p>
	 *         <li>2017年11月30日-下午7:56:26</li>
	 *         <li>功能说明：将文件内容存入数组</li>
	 *         </p>
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private static byte[] getBytesFromFile(File file) throws IOException {

		InputStream is = new FileInputStream(file);

		// 获取文件大小
		long length = file.length();

		// 创建一个数据来保存文件数据
		byte[] bytes = new byte[(int) length];
		// 读取数据到byte数组中
		int offset = 0;
		int numRead;

		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		is.close();
		return bytes;
	}

}
