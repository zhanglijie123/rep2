package cn.sunline.icore.ap.batch;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.type.ComApFile.ApFileIn;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApBatchTest extends UnitTest {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBatchTest.class);

	@Before
	public void beforeBatch() {

		// 公共区写入业务法人代码
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);

		ApFileIn apFileIn = BizUtil.getInstance(ApFileIn.class);

		apFileIn.setBusi_batch_code("AA");
		apFileIn.setBusi_batch_id("001");
		apFileIn.setFile_name("文件批量");
		apFileIn.setFile_server_path("E:/sunline_develop/Documents");

	}

	@Test
	public void applyBatchTest() {

	}
}
