package cn.sunline.icore.ap.spi;

import java.util.Date;

import cn.sunline.edsp.microcore.plugin.IAdditionalExtension;
import cn.sunline.ltts.base.RequestData;
import cn.sunline.ltts.base.ResponseData;

/**
 * transaction information logger <br/>
 * @version 1.0
 */
public interface ITransLogger extends IAdditionalExtension {
	public static final String POINT = "Aplt.trans.logger";
	
	/**
	 * 
	 * Registration transaction information base on transaction state before transaction is over. <br/>
	 * 
	 * @param pckgsq    	business sequence
	 * @param pckgdt  		business date
	 * @param request  		transaction request object, contains the run environment parameters
	 * @param response  	transaction response object, contains the failed reason
	 * @param beginTime  	the time of begin to process transaction 
	 * @param cause 		this is null in normal, except when transaction process occur exception 
	 * @param autonomous	whether need to execute in independent database transaction or not 
	 * @since JDK 1.6
	 */
	void logTransInfo(String pckgsq, String pckgdt, RequestData request, ResponseData response, Date beginTime, Throwable cause, boolean autonomous);
}

