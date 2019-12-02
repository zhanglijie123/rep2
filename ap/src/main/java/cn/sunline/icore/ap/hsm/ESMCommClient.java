package cn.sunline.icore.ap.hsm;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cn.sunline.ltts.core.api.exception.LttsBusinessException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ESMCommClient {
	private static final BizLog bizlog = BizLogUtil.getBizLog(ESMCommClient.class);
	protected Socket theSocket = null;
	protected  static String hostname = null;
	protected  static int port = 0;
	protected  static int timeout = 0;
	
	public ESMCommClient() {
		try {
			hostname = "localhost";
			port = 4444;
			timeout = 60;
			theSocket.setSoTimeout(timeout * 1000);
			theSocket = new Socket(hostname, port);
		} catch (Exception e) {
			bizlog.error(e.getMessage(), e);
		}
	}
	
	public ESMCommClient(int port, String hostname) {
		try {
//			hostname = "localhost";
//			port = 4444;
			timeout = 60;
			theSocket = new Socket(hostname, port);
			theSocket.setSoTimeout(timeout * 1000);
		} catch (Exception e) {
			bizlog.error(e.getMessage(), e);
		}
	}
	
	public boolean closeSocket()
	{
		try {
				theSocket.close();
//				System.out.println(
//					"socket closed successfully " + theSocket);
					return true;
			} catch (Exception ex) {
//				System.out.println(
//					"error closing connection : " + ex);
					return false;
			}
	}
	
    public byte[] send(byte[] baos) throws Exception  {
    	try {
    		if (theSocket == null) {
    			throw new LttsBusinessException("Could not allocated a socket not ready");
    		}
//    		TimeUnit.SECONDS.sleep(2*1000);
    		
    		// 可以捕获内存缓冲区的数据,转换成字节数组
    		ByteArrayOutputStream result = new ByteArrayOutputStream();
    		OutputStream outputToBack = theSocket.getOutputStream();

    		InputStream in = theSocket.getInputStream();
    		
    		outputToBack.write(baos);
    		outputToBack.flush();
    		byte[] buf = new byte[512];
    		int len = 0;
    		if ((len = in.read(buf, 0, buf.length)) != -1) {
            	result.write(buf, 0, len);
//            	System.out.println(" ------receving-----");
    		}

    		byte[] resultF=result.toByteArray();
//    		System.out.println("MSG received : " +resultF.toString());
//    		System.out.println("received result");

    		return resultF;

    		} catch (Exception interEx) {
    			bizlog.error(interEx.getMessage(), interEx);
    			throw interEx;
    		}
    }


//	public static void main(String[] args) 
//	{
//		ESMCommClient comm = null;
//		try {
//
//			int port = 4444; //Default
//			String inputMsg = "010100000017ee08020002009997040331918935941809101000000000";
//			//	String inputMsg="010100000017"+"EE0802"+"00"+"020001"+"010203040506070809000102"+"00060009";
////			//String inputMsg = "010100000037ee060300077ca3ba67a86a0ab1111bcc57b492ae62c7c67970dffb78d7058010023450197140200256250023450197146000000ffffff06";
//////		String inputMsg = "010100000038EE060200E2ADEDE0688198E411118E30EBEEAA3BBFCC845EE25EFE6CA573010948000000020111117974E49AD89B9A9E166EDDFD71CD1C4D";
//////		String inputMsg = "01010000015CE2256200091111111112060109081243617264204E756D62657220456E64696E670811103131313203330A3135313031360B0828435550205052455041494420434152442031202020202020202020202020202020202020202020200340063030303030310D0828202020202020202020202020202020202020202020202020202020202020202020202020202020200E082820202020202020202020202020202020202020202020202020202020202020202020202020202020101D2820202020202020202020202020202020202020202020202020202020202020202020202020202020111D2820202020202020202020202020202020202020202020202020202020202020202020202020202020";
////			//String inputMsg = "0101000000FCE2256200091111111112060103330A313531303136034006303030303031081110313131320B0828435550205052455041494420434152442031202020202020202020202020202020202020202020200D0828202020202020202020202020202020202020202020202020202020202020202020202020202020200E082820202020202020202020202020202020202020202020202020202020202020202020202020202020101D2820202020202020202020202020202020202020202020202020202020202020202020202020202020111D2820202020202020202020202020202020202020202020202020202020202020202020202020202020";
////			//String inputMsg = "010100000029EE06020055652ED2388A713A02002201094200000000011111C8BBF6801973A2065A0ED0317EE96D20";
////			//String inputMsg = "01010000001aEE0602009F504A31E0C4C0D50200220199900122920701020081";
////			//String inputMsg = "010100000017EE08020002000735648051385545261909201000000000";
////			String inputMsg = "010100000017ee08020002009997040331918935941809101000000000";
//			String ipAddress = "localhost";
//			if(args.length == 3) {
//				port = Integer.parseInt(args[1]);
//				ipAddress = args[0];
//			} else {
//				System.out.println("ESMCommClientTester usage: java -cp BosEsmTester.jar ESMCommClientTester <ip address> <port> <cmdstring>");
//				System.out.println("No ip provided, default to localhost");
//				System.out.println("No port provided, default to 4444");
//				System.out.println("No ESM Command String provided, default to 010100000017ee08020002009997040331918935941809101000000000");
//			}
//			byte[] out=EsmStringUtil.byteToBcd(inputMsg);
//			System.out.println("Message Length:"+out.length);
//			comm = new ESMCommClient(port,ipAddress);  //Instantiate the connection to ESM
//			byte[] strMsgAfterSend = comm.send(out); //Sends the message to ESM and stores the response to strMsgAfterSend
//			System.out.println("Receive Size: "+strMsgAfterSend.length);
//			String receiveMsg=EsmStringUtil.dump(strMsgAfterSend,0,strMsgAfterSend.length); //Converts to HEX with spaces every 4 char
//            System.out.println("result:["+receiveMsg+"]");
//          
//            String arrayRes = EsmStringUtil.byteArrayToHexString(strMsgAfterSend); //Converts to HEX with with no spaces
//            System.out.println("ArrayRes:["+arrayRes+"]");
////            System.out.println("Response Code:"+receiveMsg.substring(21, 23));
////            System.out.println("Response Code:"+arrayRes.substring(18,20)); 
////            String receiveOff=EsmStringUtil.dump(strMsgAfterSend,8,6);
////            System.out.println("Offset:"+receiveMsg.substring(21, 23));
//		} catch (Exception except) {
//			except.printStackTrace();
//			System.out.println("error");
//		} finally {
//			comm.closeSocket();
//		}
//	}
	
}
