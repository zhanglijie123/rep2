package cn.sunline.icore.ap.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApSmsHttpReq {
	
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApSmsHttpReq.class);
	
	public static String sendPost(String url, String param) {
		
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		
		try {
			
			URL realUrl = new URL(url);
			
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setReadTimeout(3000);

			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			
			// 发送请求参数
			out.print(param);
			
			// flush输出流的缓冲
			out.flush();
			
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			
			while ((line = in.readLine()) != null) {
				result += line;
			}
			
		} catch (Exception e) {
			bizlog.info("Send POST request exception [%s]", e, e.getMessage());
			throw new LttsServiceException("9008", "Send POST request exception!");
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
			    bizlog.info("close PrintWriter or BufferedReader failed [%s]", ex, ex.getMessage());
			}
		}
		
		return result;
	}

}
