package cn.sunline.icore.ap.hsm;

public class EsmGenerateCVV extends EsmMsg {
	public static String createMsg(String cardNo, String expiry, String keyIndex, String serviceCode ) {
		StringBuffer sb = new StringBuffer();
		sb.append(genHeader("EE0802")); //CVV-GENERATE (EE0802)   FunctionCode
		sb.append("00"); //FunctionModifier(FM=00)
		sb.append("02"); //FORMAT
		sb.append("00"); //CVK
		sb.append(keyIndex); //CVKIDX
		sb.append(cardNo); //PAN
		sb.append(expiry); // YYMM
		sb.append(serviceCode); //   ServerCode
		sb.append("000000000"); //NUL5
				
		String cmdStr = sb.toString();
		return cmdStr;
	}
}
