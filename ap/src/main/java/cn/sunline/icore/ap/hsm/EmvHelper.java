package cn.sunline.icore.ap.hsm;

import java.util.HashMap;

public class EmvHelper {

	private HashMap<String,String> emvData = new HashMap<String,String>();

	public EmvHelper(HashMap<String,String> emvData){
		this.emvData = emvData;
	}
	
	public HashMap<String, String> getEmvData() {
		return emvData;
	}

	public void setEmvData(HashMap<String, String> emvData) {
		this.emvData = emvData;
	}
	
	
	
}
