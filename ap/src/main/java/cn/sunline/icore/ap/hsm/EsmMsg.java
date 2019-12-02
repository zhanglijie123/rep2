package cn.sunline.icore.ap.hsm;


public class EsmMsg {

	public static String genHeader(String command) 
	{
		if( command.length() != 6 ) {
			//Error
		}
		return command; //BSE20162241
	}
	
	//SAC20162511[S]
	protected String genMsgHdr(StringBuffer sb) {
		StringBuffer msgHdr = new StringBuffer();
		int halfSize = sb.length() / 2;
		msgHdr.append("0101");
		msgHdr.append(EsmStringUtil.getPadLeft(Integer.toHexString(halfSize), 8, '0'));
		return msgHdr.toString();
	}
	//SAC20162511[E]
}
