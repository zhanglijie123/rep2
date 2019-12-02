package cn.sunline.icore.ap.batchtran;

import java.util.List;
import java.util.Map;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.icore.ap.namedsql.ApBranchDao;
import cn.sunline.icore.ap.servicetype.SrvApBranch;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch_relation;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.ltts.batch.engine.split.BatchDataProcessorWithoutDataItem;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
	 /**
	  * 上下级机构关系数据生成
	  *
	  */

public class ap01DataProcessor extends
  BatchDataProcessorWithoutDataItem<cn.sunline.icore.ap.batchtran.intf.Ap01.Input, cn.sunline.icore.ap.batchtran.intf.Ap01.Property> {
  
	private static final BizLog bizlog = BizLogUtil.getBizLog(ap01DataProcessor.class);
	/**
	 * 批次数据项处理逻辑。
	 * 
	 * 上下级机构关系数据生成
	 * @param input 批量交易输入接口
	 * @param property 批量交易属性接口
	 */
	 @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void process(cn.sunline.icore.ap.batchtran.intf.Ap01.Input input, cn.sunline.icore.ap.batchtran.intf.Ap01.Property property) {
		//所有的机构关系
		String orgId = MsOrg.getReferenceOrgId(apb_branch_relation.class);
		List<Map<String,Object>> relationList = ApBranchDao.selBranchRelationByGroup(orgId,false); 
		
		for(Map<String,Object> relation : relationList){
			String brchRelationCode = relation.get("brch_relation_code").toString();
			String ccyCode = relation.get("ccy_code").toString();
						
			bizlog.debug("org_id [%s] brch_relation_code [%s] ccy_code [%s] start >>>>>>>>>",orgId,brchRelationCode,ccyCode);
			
			SrvApBranch apBranch = BizUtil.getInstance(SrvApBranch.class);
			apBranch.modifyBranchRelation(brchRelationCode, ccyCode, new DefaultOptions());
			
			bizlog.debug("org_id [%s] brch_relation_code [%s] ccy_code [%s] end   >>>>>>>>>",orgId,brchRelationCode,ccyCode);
		}
	}

}


