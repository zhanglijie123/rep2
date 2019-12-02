package cn.sunline.icore.ap.parm;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cn.sunline.clwj.msap.core.parameter.MsDropList;
import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspDrop;
import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspDropDao;
import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspDropList;
import cn.sunline.clwj.msap.core.tables.MsCoreTable.MspDropListDao;
import cn.sunline.clwj.msap.core.type.MsCoreComplexType.MsDropListInfo;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.parm.ApBaseDropList;
import cn.sunline.icore.ap.test.UnitTest;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.core.api.util.LttsCoreBeanUtil;

public class ApDropListTest extends UnitTest {
	@Before
	public void insertdropList() throws SQLException {
		// 公共区写入业务法人代码
		Map<String, Object> commReq = new HashMap<String, Object>();
		commReq.put("busi_org_id", "99");
		newCommReq(commReq);
		
		// 删除数据，防止数据干扰
		Connection conn = LttsCoreBeanUtil.getDBConnectionManager().getConnection();
		Statement statement = conn.createStatement();

		// 先删除其他数据，防止干扰测试
		String sql1 = "delete from MspDropList";

		statement.executeUpdate(sql1);
		statement.close();
		
		// 测试数据插入
		MspDrop drop = BizUtil.getInstance(MspDrop.class);
		drop.setDrop_list_type("test");
		drop.setDrop_list_name("11");
		drop.setModule("ap");
		drop.setDiff_org_ind(E_YESORNO.NO);
		MspDropDao.insert(drop);
		
		drop.setDrop_list_type("test2");
		drop.setDrop_list_name("11");
		drop.setModule("ap");
		drop.setDiff_org_ind(E_YESORNO.NO);
		MspDropDao.insert(drop);
		
		MspDropList dropList = BizUtil.getInstance(MspDropList.class);
		dropList.setDict_org_id("*");
		dropList.setDrop_list_type("test");
		dropList.setDrop_list_desc("测试");
		dropList.setDrop_list_value("11");
		dropList.setData_sort(1l);
		dropList.setData_update_time("2016-12-08 13:51:23");
		MspDropListDao.insert(dropList);
		
		dropList = BizUtil.getInstance(MspDropList.class);
		dropList.setDict_org_id("*");
		dropList.setDrop_list_type("fileTest");
		dropList.setDrop_list_desc("文件测试");
		dropList.setDrop_list_value("11111");
		dropList.setData_sort(2l);
		dropList.setData_update_time("2016-12-08 13:51:23");
		MspDropListDao.insert(dropList);
	}

	/**
	 * 传入throwError=false,查询存在的下拉字典不会出现异常,并返回true
	 */
	@Test
	public void testExists() {
		// 判断下拉列表是否存在
		boolean exists = ApBaseDropList.exists("test", "11", false);
		Assert.assertTrue(exists);
	}

	/**
	 * 传入throwError=false,查询不存在的下拉字典不会出现异常,并返回false
	 */
	@Test
	public void testNotExists() {
		// 判断下拉列表是否存在
		boolean exists = ApBaseDropList.exists("test", "11030120312", false);
		assertEquals(false, exists);
	}

	/**
	 * 传入throwError=true,查询不存在的下拉字典会出现异常
	 */
	@Test
	public void testExistsException() {
		try {
			// 判断下拉列表是否存在
			ApBaseDropList.exists("test", "11030120312", true);
			Assert.assertTrue(false);
		}
		catch (Exception e) {
			Assert.assertTrue(true);
		}

	}

	/**
	 * 传入throwError=false,查询存在的下拉字典不会出现异常并且返回正确的描述
	 */
	@Test
	public void testGetText() {

		// 判断下拉列表客户级别11的名称
		String level = ApBaseDropList.getText("test", "11", false);
		assertEquals("测试", level);
	}

	/**
	 * 传入throwError=false,查询不存在的下拉字典并且获取内容为null不会出现异常
	 */
	@Test
	public void testGetTextNull() {
		// 判断下拉列表客户级别11的名称
		String desc = ApBaseDropList.getText("test", "222", false);
		assertEquals(null, desc);
	}

	/**
	 * 传入throwError=true,查询不存在的下拉字典会出现异常
	 */
	@Test
	public void testGetTextException() {
		// 判断下拉列表客户级别11的名称
		try {
			ApBaseDropList.getText("test", "111111", true);
			Assert.assertTrue(false);
		}
		catch (Exception e) {
			Assert.assertTrue(true);
		}
	}

	/**
	 * 查询test分组的数据，总数应该为1条
	 */
	@Test
	public void testGetItems() {
		// 判断下拉列表客户级别数量
		List<MsDropListInfo> levelList = MsDropList.getItems("test");
		assertEquals(1, levelList.size());
	}

}
