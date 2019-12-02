package cn.sunline.icore.ap.hsm;

public class EsmRetrivePubKey extends EsmMsg {

	public static String GenPublicKey(String pksIndex){
		StringBuffer sb = new StringBuffer();
		sb.append(genHeader("EE3000")); //CVV-GENERATE (EE3000)   FunctionCode
		sb.append("00"); //FunctionModifier(FM=00)
		sb.append("02"); //FORMAT
		sb.append("00"); //CVK
		sb.append(pksIndex); //PKSIDX
				
		String pubKeyStr = sb.toString();
		return pubKeyStr;
	}
}
