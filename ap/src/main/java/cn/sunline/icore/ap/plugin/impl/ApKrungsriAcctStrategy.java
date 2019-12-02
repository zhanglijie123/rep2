package cn.sunline.icore.ap.plugin.impl;
import cn.sunline.icore.ap.spi.ISeqCheckBitStrategy;
import cn.sunline.ltts.base.util.Assert;
public class ApKrungsriAcctStrategy implements ISeqCheckBitStrategy {
	/**
	 * 求和权值
	 */
	private static final int[] FACTOR = {4, 3, 2, 7, 6, 5, 4, 3, 2};
	
	/**
	 * 取余因子
	 */
	private static final int DIVISOR = 10;
	
	/**
	 * 被减数
	 */
	private static final int SUBSTRACTOR = 10;
	
	/**
	 * 最大权重
	 */
	private static final int MAX_WEIGHT = 9;

	@Override
	public String genCheckBit(String originalSeq) {
		Assert.notNull(originalSeq);
		Assert.isTrue(FACTOR.length == originalSeq.length());
		
		int sumResult = 0;
		for (int i = 0, curWeight = 0; i < originalSeq.length(); i++) {
			curWeight = (originalSeq.charAt(i) - '0') * FACTOR[i];
			if (curWeight > MAX_WEIGHT) {
				curWeight = curWeight % SUBSTRACTOR + curWeight / SUBSTRACTOR;
			}
			
			sumResult += curWeight;
		}
		
		int reminder = sumResult % DIVISOR;
		int bit = reminder == 0? reminder : SUBSTRACTOR - reminder;
		
		return String.valueOf(bit);
	}
	
	
}
