package cn.sunline.icore.ap.spi;

import cn.sunline.edsp.microcore.spi.SPI;

@SPI(defaultId = "autoChrg")
public interface CmAutoChrg {
	
	void prcAutoChrgAccounting();
}
