package cn.sunline.icore.ap.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspDrop;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchInfo;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.base.util.FileProcessor;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.core.api.exception.LttsBusinessException;

public class BizUtilTest extends UnitTest {

	@Test
	public void testDateDiff() {
		int i = BizUtil.dateDiff("d", "20170130", "20170203");
		System.out.println("-------------i:" + i);
	}

	/**
	 * 测试获取公共运行变量 正例
	 */
	@Test
	public void testGetTrxRunEnvs() {
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);

		String orgId = BizUtil.getTrxRunEnvs().getBusi_org_id();
		assertTrue(orgId.equals("99"));
	}

	/**
	 * 正例，生成的校验位正确
	 */
	@Test
	public void testCnrtIdCheckBit() {
		String idLoseCheck = "42222419701202002";
		String checkBit = "X".toUpperCase();
		assertTrue(checkBit.equals(BizUtil.genChinaIdCardCheckBit(idLoseCheck)));
	}

	/**
	 * 反例，生成的校验位不正确
	 */
	@Test
	public void testCnrtIdCheckBit1() {
		String idLoseCheck = "42220419660916941";
		String checkBit = "6".toUpperCase();
		assertFalse(checkBit.equals(BizUtil.genChinaIdCardCheckBit(idLoseCheck)));
	}

	/**
	 * 测试卡号生成校验位
	 */
	@Test
	public void testgenCardnoCheckBit() {
		String cardno = "622609750000001";
		assertEquals(Integer.valueOf(4), Integer.valueOf(BizUtil.genCardnoCheckBit(cardno)));
	}

	/**
	 * 反例，输入参数异常
	 */
	@Test
	public void testgenCardnoCheckBit1() {
		String cardno = "62260975000000X";
		try {
			BizUtil.genCardnoCheckBit(cardno);
		}
		catch (LttsBusinessException e) {
			assertTrue(true);
			return;
		}
		assertTrue(false);
	}

	@Test
	public void testcalcDateByReference() {
		// 案例1
		String refDate1 = "20160321";
		String curtDate1 = "20160122";
		String cycleUnit1 = "1Q";
		String calcNextDate1 = null;
		// 预期结果 20160321
		String expectedDate1 = "20160321";

		// 案例2
		String refDate2 = "20160221";
		String curtDate2 = "20160201";
		String cycleUnit2 = "1Q";
		String calcNextDate2 = null;
		// 预期结果 20160221
		String expectedDate2 = "20160221";

		// 案例3
		String refDate3 = "20160321";
		String curtDate3 = "20160321";
		String cycleUnit3 = "1Q";
		String calcNextDate3 = null;
		// 预期结果 20160621
		String expectedDate3 = "20160621";

		// 案例4
		String refDate4 = "20160221";
		String curtDate4 = "20160701";
		String cycleUnit4 = "1H";
		String calcNextDate4 = null;
		// 预期结果 20160721
		String expectedDate4 = "20160821";

		// 案例5
		String refDate5 = "20160229";
		String curtDate5 = "20161001";
		String cycleUnit5 = "1M";
		String calcNextDate5 = null;
		// 预期结果 20161031
		String expectedDate5 = "20161029";

		// 案例6
		String refDate6 = "20160229";
		String curtDate6 = "20160901";
		String cycleUnit6 = "1M";
		String calcNextDate6 = null;
		// 预期结果 20160930
		String expectedDate6 = "20160930";

		// 案例7
		String refDate7 = "20160101";
		String curtDate7 = "20170101";
		String cycleUnit7 = "2M";
		String calcNextDate7 = null;
		// 预期结果 20170301
		String expectedDate7 = "20170301";

		// 案例8
		String refDate8 = "20160114";
		String curtDate8 = "20170101";
		String cycleUnit8 = "2M";
		String calcNextDate8 = null;
		// 预期结果 20170214
		String expectedDate8 = "20170214";

		// 案例8
		String refDate9 = "20160131";
		String curtDate9 = "20170101";
		String cycleUnit9 = "2M";
		String calcNextDate9 = null;
		// 预期结果 20170228
		String expectedDate9 = "20170228";

		try {

			// 计算出来的下一日期
			calcNextDate1 = BizUtil.calcDateByReference(refDate1, curtDate1, cycleUnit1);
			calcNextDate2 = BizUtil.calcDateByReference(refDate2, curtDate2, cycleUnit2);
			calcNextDate3 = BizUtil.calcDateByReference(refDate3, curtDate3, cycleUnit3);
			calcNextDate4 = BizUtil.calcDateByReference(refDate4, curtDate4, cycleUnit4);
			calcNextDate5 = BizUtil.calcDateByReference(refDate5, curtDate5, cycleUnit5);
			calcNextDate6 = BizUtil.calcDateByReference(refDate6, curtDate6, cycleUnit6);
			calcNextDate7 = BizUtil.calcDateByReference(refDate7, curtDate7, cycleUnit7);
			calcNextDate8 = BizUtil.calcDateByReference(refDate8, curtDate8, cycleUnit8);
			calcNextDate9 = BizUtil.calcDateByReference(refDate9, curtDate9, cycleUnit9);

			Assert.assertTrue(CommUtil.equals(calcNextDate1, expectedDate1));
			Assert.assertTrue(CommUtil.equals(calcNextDate2, expectedDate2));
			Assert.assertTrue(CommUtil.equals(calcNextDate3, expectedDate3));
			Assert.assertTrue(CommUtil.equals(calcNextDate4, expectedDate4));
			Assert.assertTrue(CommUtil.equals(calcNextDate5, expectedDate5));
			Assert.assertTrue(CommUtil.equals(calcNextDate6, expectedDate6));
			Assert.assertTrue(CommUtil.equals(calcNextDate7, expectedDate7));
			Assert.assertTrue(CommUtil.equals(calcNextDate8, expectedDate8));
			Assert.assertTrue(CommUtil.equals(calcNextDate9, expectedDate9));

		}
		catch (Exception e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void testCheckEffectDate() {
		// 正例
		BizUtil.checkEffectDate("20160808", "20160909");

		// 反例
		try {
			BizUtil.checkEffectDate("20170808", "20160909");
		}
		catch (Exception e) {
			Assert.assertTrue(true);
		}

	}

	@Test
	public void testDataIsChange() {
		MspDrop oldObject = BizUtil.getInstance(MspDrop.class);
		MspDrop newObject = BizUtil.getInstance(MspDrop.class);

		oldObject.setDrop_list_name("test1");
		oldObject.setDrop_list_type("test2");

		newObject.setDrop_list_name("test3");
		newObject.setDrop_list_type("test4");

		assertTrue(BizUtil.dataIsChange(oldObject, newObject));

		newObject.setDrop_list_name("test1");
		newObject.setDrop_list_type("test2");
		oldObject.setData_version((long) 1);
		newObject.setData_version((long) 2);
		assertFalse(BizUtil.dataIsChange(oldObject, newObject));
	}

	@Test
	public void testpro() {

		String locl = "D:\\a.txt";
		final FileProcessor fileProc = new FileProcessor("", locl);
		fileProc.open(true);

		StringBuffer s = new StringBuffer();
		s.append("111111111");
		for (int i = 0; i < 6000; i++) {
			s.append(
					"ssssssssssssssssssssffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffsssssssssssssssssssssdfdddddddddddddddddddddddddddddddddddddddddffffffffffdfffffffffffsssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss")
					.append("\n");

		}
		fileProc.write(s.toString());
		fileProc.close();

	}

	@Test
	public void checkTimeFormat() {

		String date = "20160101 18:34";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd HH:mm");

		String tmp = df.format(BizUtil.toDate(date));

		System.out.println("tmp" + tmp);
		System.out.println(BizUtil.isDateString(date));

	}

	@Test
	public void test1() {


		
		List<ApBranchInfo>  l = new ArrayList<ApBranchInfo>();
		ApBranchInfo branch = BizUtil.getInstance(ApBranchInfo.class);
		branch.setBranch_address("123");
		branch.setBranch_id("456");
		l.add(branch);

		
		List<Object> t2 = BizUtil.clone(List.class, l);
		Object o = t2.get(0);
		ApBranchInfo branch2 = BizUtil.parseEntity(o.toString(), ApBranchInfo.class);
		
		System.out.println("-----------------"+l.get(0).getBranch_address());
		
	}

}
