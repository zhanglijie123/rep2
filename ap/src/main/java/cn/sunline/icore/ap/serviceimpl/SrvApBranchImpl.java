
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.branch.mnt.ApBranchMnt;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchInfo;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchParmCondtion;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchRelSubWithOper;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchRelation;
import cn.sunline.ltts.core.api.model.dm.Options;


 /**
  * branch information maintenance
  * 机构信息维护
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApBranchImpl", longname="branch information maintenance", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApBranchImpl implements cn.sunline.icore.ap.servicetype.SrvApBranch{
 /**
  * added branch
  *
  */
	public void addBranch(final cn.sunline.icore.ap.type.ComApBranch.ApBranchInfo branch) {
		ApBranchMnt.addBranchDetail(branch);
	}
 /**
  * branch information changes
  *
  */
	public void modifyBranch(cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE operaterInd, final cn.sunline.icore.ap.type.ComApBranch.ApBranchInfo branch) {
		ApBranchMnt.modifyBranchDetail(operaterInd, branch);
	}
 /**
  * branch relationship maintenance
  *
  */
	public void modifyBranchRelation( String brch_relation_code,  String ccy_code,  Options<ApBranchRelSubWithOper> relations){
		ApBranchMnt.modifyBranchRelation(brch_relation_code, ccy_code, relations);
	}
 /**
  * revoke the branch
  *
  */
	public void mergeBranch(String srcBranchId, String destBranchId, String effectDate) {

	}
 /**
  * query branch list
  *
  */
	public Options<ApBranchInfo> queryBranchList(ApBranchParmCondtion branch) {
		return ApBranchMnt.queryBranchList(branch);
	}
 /**
  * query branch relationship information
  *
  */
	public Options<ApBranchRelation> queryBranchRelation(String brch_relation_code, String ccy_code) {
		return ApBranchMnt.queryBranchRelation(brch_relation_code, ccy_code);
	}

}

