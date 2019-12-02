package cn.sunline.icore.ap.spi;

import cn.sunline.edsp.microcore.spi.SPI;

@SPI(defaultId = "workflow")
public interface CtCheckPermisWorkflow {

	void checkPermis(String inputStr);
	
	void checkWorkflow(String inputStr);
	
	void registerTrxnTellerSeq();
	
	void chkAuthorization();
}
