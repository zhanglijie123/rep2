package cn.sunline.icore.ap.parm.mnt;

import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.ApBusinessParmApi;
import cn.sunline.icore.ap.api.ApChannelApi;
import cn.sunline.icore.ap.api.ApFileApi;
import cn.sunline.icore.ap.api.LocalFileProcessor;
import cn.sunline.icore.ap.batch.ApFileSend;
import cn.sunline.icore.ap.namedsql.ApSystemDao;
import cn.sunline.icore.ap.type.ComApBook.ApTransReconIn;
import cn.sunline.icore.ap.type.ComApBook.ApTransReconInfo;
import cn.sunline.icore.ap.type.ComApBook.ApTransReconOut;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.dao.CursorHandler;
import cn.sunline.ltts.core.api.lang.Params;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

/**
 * <p>
 * 文件功能说明： 交易流水表相关操作
 * </p>
 * 
 * @Author lid
 *         <p>
 *         <li>2017年4月13日-上午9:31:01</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>2017年4月13日-lid：创建注释模板</li>
 *        <li> 2018年8月20日-lid：部分查詢上交到MSAP</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */

public class ApTrxnMnt {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApTrxnMnt.class);

	/**
	 * @Author yangdl
	 *         <p>
	 *         <li>2017年9月19日-上午11:27:33</li>
	 *         <li>功能说明：生成交易流水对账文件</li>
	 *         </p>
	 * @param trxnIn
	 */
	public static ApTransReconOut genTransReconFile(ApTransReconIn trxnIn) {

		bizlog.method("ApTrxnMnt.genTransReconFile begin >>>>>>>>>>>>>");

		// 非空要素检查
		BizUtil.fieldNotNull(trxnIn.getInitiator_start_date(), SysDict.A.initiator_start_date.getId(), SysDict.A.initiator_start_date.getLongName());
		BizUtil.fieldNotNull(trxnIn.getInitiator_end_date(), SysDict.A.initiator_end_date.getId(), SysDict.A.initiator_end_date.getLongName());

		// 交易渠道合法性检查
		if (CommUtil.isNotNull(trxnIn.getChannel_id())) {
			ApChannelApi.exists(trxnIn.getChannel_id());
		}

		// 起始日期不能大于结束日期
		if (CommUtil.compare(trxnIn.getInitiator_start_date(), trxnIn.getInitiator_end_date()) > 0) {
			throw ApErr.AP.E0127(trxnIn.getInitiator_start_date(), trxnIn.getInitiator_end_date());
		}

		// 查询间隔周期检查
		if (BizUtil.dateDiff("dd", trxnIn.getInitiator_start_date(), trxnIn.getInitiator_end_date()) > ApBusinessParmApi.getIntValue("FILE_RECON_INTERVAL_DAYS")) {

			ApErr.AP.E0056(ApBusinessParmApi.getValue("FILE_RECON_INTERVAL_DAYS"));
		}

		String fileId = ApFileSend.genFileId();

		// 文件名
		String fileName = String.format("RECONFILE%s.txt", fileId);// 文件名称

		String localPath = ApFileApi.getFullPath(ApConst.RECON_UPLOAD_LOCALFILE); // 获取本地路径
		String serverPath = ApFileApi.getFullPath(ApConst.RECON_UPLOAD_REMOTEFILE); // 获取远程路径

		final LocalFileProcessor processor = new LocalFileProcessor(localPath, fileName, "UTF-8");

		processor.open(true);// 打开文件

		Long count = 0L; // 总笔数
		try {

			// 执行游标处理
			bizlog.debug("Execute cursor begin >>>>>>>>>>>");

			Params para = new Params();

			para.add(SysDict.A.initiator_start_date.toString(), trxnIn.getInitiator_start_date()); // 起始日期
			para.add(SysDict.A.initiator_end_date.toString(), trxnIn.getInitiator_end_date()); // 结束日期
			para.add(SysDict.A.recon_code.toString(), trxnIn.getRecon_code()); // 对账代码
			para.add(SysDict.A.channel_id.toString(), trxnIn.getChannel_id());// 渠道ID
			para.add(SysDict.A.org_id.toString(), BizUtil.getTrxRunEnvs().getBusi_org_id()); // 法人代码

			// 获取总笔数
			count = DaoUtil.selectOne(ApSystemDao.namedsql_selTrxnReconListCount, para);

			// 文件头信息写入文件
			processor.write(String.valueOf(count)); // 零条记录也要写文件

			// 文件体写入文件
			DaoUtil.selectList(ApSystemDao.namedsql_selTrxnReconList, para, new CursorHandler<ApTransReconInfo>() {

				@Override
				public boolean handle(int index, ApTransReconInfo fileRecondData) {

					// 转换成json格式
					String recondDataJson = BizUtil.toJson(fileRecondData);
					// 写入文件
					processor.write(recondDataJson);
					return true;
				}
			});

		}
		finally {
			// 关闭文件
			processor.close();
		}

		// 登记文件发送簿
		ApFileSend.register(fileId, fileName, ApConst.RECON_UPLOAD_REMOTEFILE, ApConst.RECON_UPLOAD_LOCALFILE, E_YESORNO.NO);

		ApTransReconOut cplOut = BizUtil.getInstance(ApTransReconOut.class);

		cplOut.setFile_local_path(localPath);
		cplOut.setFile_server_path(serverPath);
		cplOut.setReturn_file_id(fileId);
		cplOut.setFile_name(fileName);
		cplOut.setTotal_count(count);

		bizlog.method("ApTrxnMnt.genTransReconFile end <<<<<<<<<<<<<");

		return cplOut;

	}

}
