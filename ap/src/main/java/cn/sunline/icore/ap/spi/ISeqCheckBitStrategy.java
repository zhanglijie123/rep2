package cn.sunline.icore.ap.spi;
import cn.sunline.edsp.microcore.plugin.IAdditionalExtension;

public interface ISeqCheckBitStrategy extends IAdditionalExtension {
	public static final String POINT = "Aplt.checkbit.strategy";
	
	/**
	 * 
	 * @Author tsichang
	 *         <p>
	 *         <li>Apr 22, 2018-11:35:48 AM</li>
	 *         <li>功能说明：生成校验位</li>
	 *         </p>
	 * @param originalSeq
	 * 				原流水
	 * @return
	 */
	public String genCheckBit(String originalSeq);
}
