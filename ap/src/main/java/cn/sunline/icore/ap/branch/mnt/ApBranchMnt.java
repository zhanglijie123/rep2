package cn.sunline.icore.ap.branch.mnt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.sunline.clwj.msap.core.parameter.MsOrg;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_DATAOPERATE;
import cn.sunline.clwj.msap.sys.type.MsEnumType.E_YESORNO;
import cn.sunline.icore.ap.api.ApBranchApi;
import cn.sunline.icore.ap.api.ApDataAuditApi;
import cn.sunline.icore.ap.namedsql.ApBranchDao;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branchDao;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branch_juniorDao;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branch_relationDao;
import cn.sunline.icore.ap.tables.TabApBranch.Apb_branch_seniorDao;
import cn.sunline.icore.ap.tables.TabApBranch.App_branch_relationDao;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch_junior;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch_relation;
import cn.sunline.icore.ap.tables.TabApBranch.apb_branch_senior;
import cn.sunline.icore.ap.tables.TabApBranch.app_branch_relation;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchInfo;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchLevel;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchNode;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchParmCondtion;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchRelSubWithOper;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchRelation;
import cn.sunline.icore.ap.type.ComApBranch.ApBranchTree;
import cn.sunline.icore.ap.util.ApConst;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApErr;
import cn.sunline.icore.sys.errors.ApErr.AP;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.parm.TrxEnvs.RunEnvs;
import cn.sunline.icore.sys.type.EnumType.E_BRCHSCOPE;
import cn.sunline.ltts.base.odb.OdbFactory;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.busi.sdk.util.DaoUtil;
import cn.sunline.ltts.core.api.lang.Page;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.Options;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;

/**
 * <p>
 * 文件功能说明：机构信息参数维护类
 * </p>
 * 
 * @Author lanlf
 *         <p>
 *         <li>2016年12月26日-上午9:58:28</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>20140228 lanlf：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ApBranchMnt {
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApBranchMnt.class);

	/**
	 * @Author lidi
	 *         <p>
	 *         <li>2016年12月26日-下午3:02:05</li>
	 *         <li>功能说明：查询机构列表</li>
	 *         </p>
	 * @param branch
	 * @return
	 */
	public static Options<ApBranchInfo> queryBranchList(ApBranchParmCondtion branch) {
		bizlog.method(" queryBranchList begin >>>>>>>>>>>>>>>>");

		String branchQueryScope = branch.getBranch_query_scope();
		if (CommUtil.isNotNull(branchQueryScope)) {
			if (branchQueryScope.equals(E_BRCHSCOPE.ENTITY.getValue())) {
				branch.setReal_branch_ind(E_YESORNO.YES);
			}
			else if (branchQueryScope.equals(E_BRCHSCOPE.VIRTUAL.getValue())) {
				branch.setReal_branch_ind(E_YESORNO.NO);
			}
		}

		branch.setOrg_id(MsOrg.getReferenceOrgId(apb_branch.class));// 法人代码

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		Page<ApBranchInfo> page = ApBranchDao.selBranchList(branch, runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(), false);
		runEnvs.setTotal_count(page.getRecordCount());

		Options<ApBranchInfo> branchList = new DefaultOptions<ApBranchInfo>();
		branchList.setValues(page.getRecords());

		bizlog.method("queryBranchList end <<<<<<<<<<<<<<<<<<<<");
		return branchList;
	}

	/**
	 * @Author lanlf
	 *         <p>
	 *         <li>2016年12月27日-上午10:12:50</li>
	 *         <li>功能说明：增加机构</li>
	 *         </p>
	 * @param branch
	 */
	public static void addBranchDetail(ApBranchInfo branch) {
		bizlog.method("addBranchDetail begin >>>>>>>>>>>>>>>>>>>>");

		addBranchSingle(branch);

		bizlog.method("addBranchDetail end <<<<<<<<<<<<<<<<<<<<");
	}

	/**
	 * @param operaterInd 
	 * @Author lidi
	 *         <p>
	 *         <li>2016年12月27日-上午10:13:11</li>
	 *         <li>功能说明：修改机构信息</li>
	 *         </p>
	 * @param branch
	 */
	public static void modifyBranchDetail(E_DATAOPERATE operaterInd, ApBranchInfo branch) {
		bizlog.method("modifyBranchDetail begin >>>>>>>>>>>>>>>>>>>>");
		
		BizUtil.fieldNotNull(operaterInd, SysDict.A.operater_ind.getId(), SysDict.A.operater_ind.getLongName());
		
		if(operaterInd == E_DATAOPERATE.MODIFY){
			modifyBranchSingle(branch);
		}else if(operaterInd == E_DATAOPERATE.DELETE){
			deleteBranchSingle(branch);
		}else if(operaterInd == E_DATAOPERATE.ADD){
			addBranchSingle(branch);
		}
		
		
		bizlog.method("modifyBranchDetail end <<<<<<<<<<<<<<<<<<<<");
	}

	/**
	 * 单个机构信息新增
	 * @param branch
	 */
	private static void addBranchSingle(ApBranchInfo branch) {
		validBranchInfo(branch);// 校验字段

		apb_branch oldBranch = Apb_branchDao.selectOne_odb1(branch.getBranch_id(), false);
		if (oldBranch != null) {// 新增机构时，判断机构号是否已存在
			ApPubErr.APPUB.E0019(OdbFactory.getTable(apb_branch.class).getLongname(), branch.getBranch_id());
		}

		apb_branch newBranch = getBranchObject(branch, BizUtil.getInstance(apb_branch.class));

		Apb_branchDao.insert(newBranch);
		// 登记审计
		ApDataAuditApi.regLogOnInsertParameter(newBranch);
	}

	/**
	 * 机构信息单个修改
	 * @param branch
	 */
	private static void modifyBranchSingle(ApBranchInfo branch) {
		validBranchInfo(branch);// 校验字段

		// 调用 getBranchObject（只适合新增，考虑去掉现有方法）
		// 期会导致公共字段的丢失 例如数据创建时间、数据创建用户
		// 1.查询结果保存到 newBranch 2.getInstanceByCopy 到 oldBranch 3.对newBranch做赋值
		// 4.更新db 5.审计处理
		apb_branch oldBranch = Apb_branchDao.selectOneWithLock_odb1(branch.getBranch_id(), false);
		if (oldBranch == null) {// 机构信息为空
			// throw
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(apb_branch.class).getLongname(), SysDict.A.branch_id.getLongName(), branch.getBranch_id());
		}

		bizlog.debug("oldBranch [%s]", oldBranch);
		if (CommUtil.compare(branch.getData_version().longValue(), oldBranch.getData_version().longValue()) != 0) {
			// throw 数据表[xxxx]在维护时出现丢失更新，需获取最新数据再做维护
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(apb_branch.class).getLongname());
		}
		// 修改后的机构信息
		apb_branch newBranch = BizUtil.clone(apb_branch.class, oldBranch);// 克隆OldObject，防止公共字段丢失
		newBranch = getBranchObject(branch, newBranch);

		// 先登记审计,如果没有字段修改，需要抛错
		int i = ApDataAuditApi.regLogOnUpdateParameter(oldBranch, newBranch);
		if (i == 0) {
			// throw
			throw ApPubErr.APPUB.E0023(OdbFactory.getTable(apb_branch.class).getLongname());
		}
		Apb_branchDao.updateOne_odb1(newBranch);

	}

	/**
	 * 机构信息单个删除
	 * @param branch
	 */
	private static void deleteBranchSingle(ApBranchInfo branch) {
		validBranchInfo(branch);// 校验字段
		
		apb_branch oldBranch = Apb_branchDao.selectOneWithLock_odb1(branch.getBranch_id(), false);
		if (oldBranch == null) {// 机构信息为空
			// throw
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(apb_branch.class).getLongname(), SysDict.A.branch_id.getLongName(), branch.getBranch_id());
		}
		
		if (CommUtil.compare(branch.getData_version().longValue(), oldBranch.getData_version().longValue()) != 0) {
			// throw 数据表[xxxx]在维护时出现丢失更新，需获取最新数据再做维护
			throw ApPubErr.APPUB.E0018(OdbFactory.getTable(apb_branch.class).getLongname());
		}
		
		Apb_branchDao.deleteOne_odb1(branch.getBranch_id());
		
		// 审计
		ApDataAuditApi.regLogOnDeleteParameter(oldBranch);
		
		// 对应的机构关系也要删除
		Apb_branch_relationDao.delete_odb3(branch.getBranch_id());
	}

	/*
	 * 产生新的机构实体类
	 */
	private static apb_branch getBranchObject(ApBranchInfo branch, apb_branch newBranch) {

		newBranch.setBranch_id(branch.getBranch_id());// 机构代码
		newBranch.setBranch_name(branch.getBranch_name());// 机构名称
		newBranch.setReal_branch_ind(branch.getReal_branch_ind());// 机构实体引用标志
		newBranch.setBranch_address(branch.getBranch_address());// 机构地址
		newBranch.setBranch_phone(branch.getBranch_phone());// 机构电话
		newBranch.setBranch_mail(branch.getBranch_mail());
		newBranch.setPostcode(branch.getPostcode());// 邮政编码
		newBranch.setContacts_name(branch.getContacts_name());// 联系人
		newBranch.setContacts_phone(branch.getContacts_phone());// 联系人电话
		newBranch.setContacts_mail(branch.getContacts_mail());
		newBranch.setBranch_function_class(branch.getBranch_function_class());// 机构职能分类
		newBranch.setSwift_no(branch.getSwift_no());// swift号
		newBranch.setHoliday_code(branch.getHoliday_code());// 假日代码

		return newBranch;
	}

	/*
	 * 机构信息输入的相关校验
	 */
	private static void validBranchInfo(ApBranchInfo branch) {
		// 机构号不能为空
		BizUtil.fieldNotNull(branch.getBranch_id(), SysDict.A.branch_id.getId(), SysDict.A.branch_id.getLongName());
		// 机构名称不能为空
		BizUtil.fieldNotNull(branch.getBranch_name(), SysDict.A.branch_name.getId(), SysDict.A.branch_name.getLongName());
		// 机构实体引用标志不能为空
		BizUtil.fieldNotNull(branch.getReal_branch_ind(), SysDict.A.real_branch_ind.getId(), SysDict.A.real_branch_ind.getLongName());

		if (branch.getReal_branch_ind() == E_YESORNO.YES) {// 实体机构时，swift号，holidayCode为必输
			// swift_no不能为空
			BizUtil.fieldNotNull(branch.getSwift_no(), SysDict.A.swift_no.getId(), SysDict.A.swift_no.getLongName());
			// 假日代码不能为空
			BizUtil.fieldNotNull(branch.getHoliday_code(), SysDict.A.holiday_code.getId(), SysDict.A.holiday_code.getLongName());
		}
	}

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月9日-上午11:07:34</li>
	 *         <li>功能说明：查询机构关系信息</li>
	 *         </p>
	 * @param branchId
	 * @param ccyCode
	 */
	public static Options<ApBranchRelation> queryBranchRelation(String brchRelationCode, String ccyCode) {
		bizlog.method("queryBranchRelation begin >>>>>>>>>>>>>>>>>>>>");

		BizUtil.fieldNotNull(brchRelationCode, SysDict.A.brch_relation_code.getId(), SysDict.A.brch_relation_code.getLongName());

		RunEnvs runEnvs = BizUtil.getTrxRunEnvs();
		
		// 检查机构关系是否存在
		app_branch_relation branchRelation = App_branch_relationDao.selectOne_odb1(brchRelationCode, false);
		if (branchRelation == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_branch_relation.class).getLongname(), SysDict.A.brch_relation_code.getLongName(), brchRelationCode);
		}

		if (branchRelation.getDiff_ccy_ind() == E_YESORNO.YES) {
			// 货币代号不能为空
			BizUtil.fieldNotNull(ccyCode, SysDict.A.ccy_code.getId(), SysDict.A.ccy_code.getLongName());
		}
		else {// 不区分时，忽略货币代号
			ccyCode = ApConst.WILDCARD;
		}

		String orgId = MsOrg.getReferenceOrgId(apb_branch.class);
		Page<ApBranchRelation> brchRelationPage = ApBranchDao.selBranchRelation(brchRelationCode, orgId, null, ccyCode, 
				runEnvs.getPage_start(), runEnvs.getPage_size(), runEnvs.getTotal_count(), false);
		
		runEnvs.setTotal_count(brchRelationPage.getRecordCount());

		bizlog.method("queryBranchRelation end <<<<<<<<<<<<<<<<<<<<");
		return new DefaultOptions<ApBranchRelation>(brchRelationPage.getRecords());
	}

	/**
	 * @param operaterInd 
	 * @Author lid
	 *         <p>
	 *         <li>2017年1月9日-下午1:19:49</li>
	 *         <li>功能说明：机构关系信息维护</li>
	 *         </p>
	 * @param brch_relation_code
	 * @param ccy_code 
	 * @param relations
	 */
	public static void modifyBranchRelation(String brchRelationCode, String ccyCode, Options<ApBranchRelSubWithOper> relations) {
		bizlog.method("modifyBranchRelation begin >>>>>>>>>>>>>>>>>>>>");

		List<ApBranchRelSubWithOper> branchRelationList = relations.getValues();
		// 检查机构关系是否存在
		app_branch_relation branchRelation = App_branch_relationDao.selectOne_odb1(brchRelationCode, false);
		if (branchRelation == null) {
			throw ApPubErr.APPUB.E0005(OdbFactory.getTable(app_branch_relation.class).getLongname(), SysDict.A.brch_relation_code.getLongName(), brchRelationCode);
		}

		if (branchRelation.getDiff_ccy_ind() == E_YESORNO.NO)// 如果不区分别种，货币代号省略
			ccyCode = ApConst.WILDCARD;// 不区分用*

		// 关系信息维护
		for (ApBranchRelSubWithOper brchRelation : branchRelationList) {
			E_DATAOPERATE operaterInd = brchRelation.getOperater_ind();
			
			//BizUtil.fieldNotNull(operaterInd, SysDict.A.operater_ind.getId(), SysDict.A.operater_ind.getLongName());
			if(operaterInd==null){
				continue;
			}else if(operaterInd == E_DATAOPERATE.ADD){
				addBranchRelationSingle(brchRelationCode, ccyCode, brchRelation);
			}else if(operaterInd == E_DATAOPERATE.DELETE){
				delBranchRelationSingle(brchRelationCode, ccyCode, brchRelation);
			}else if(operaterInd == E_DATAOPERATE.MODIFY){
				modifyBranchRelationSingle(brchRelationCode, ccyCode, brchRelation);
			}
			
		}

		// 生成上下级数据
		genBranchRelations(brchRelationCode, ccyCode, branchRelation.getRelation_ref_self(), branchRelation.getBrch_refer_scope());

		bizlog.method("modifyBranchRelation end <<<<<<<<<<<<<<<<<<<<");
	}

	/**
	 * 单个机构关系删除操作
	 * @param brchRelationCode
	 * @param ccyCode
	 * @param brchRelation
	 */
	private static void delBranchRelationSingle(String brchRelationCode, String ccyCode, ApBranchRelSubWithOper brchRelation) {
		String branchId = brchRelation.getBranch_id();

		// 先检查机构关系表是否存在
		apb_branch_relation oldRelation = Apb_branch_relationDao.selectOne_odb1(branchId, brchRelationCode, ccyCode, false);
		if (oldRelation == null) {
			// throw
			throw AP.E0023(branchId, brchRelationCode, ccyCode);
		}
		
		if(CommUtil.isNotNull(brchRelation.getData_version())){
			// 数据版本好不一致，抛错
			if (CommUtil.compare(oldRelation.getData_version(), brchRelation.getData_version()) != 0) {
				throw ApPubErr.APPUB.E0018(apb_branch_relation.class.getName());
			}
		}
		
		Apb_branch_relationDao.deleteOne_odb1(branchId, brchRelationCode, ccyCode);
		
		// 登记审计
		ApDataAuditApi.regLogOnDeleteParameter(oldRelation);
	}

	/*
	 * 单个机构关系维护操作
	 */
	private static void modifyBranchRelationSingle(String brchRelationCode, String ccyCode, ApBranchRelSubWithOper brchRelation) {
		String branchId = brchRelation.getBranch_id();
		String seniorBrchId = brchRelation.getParent_brch_id();

		// 先检查机构关系表是否存在
		apb_branch_relation oldRelation = Apb_branch_relationDao.selectOne_odb1(branchId, brchRelationCode, ccyCode, false);
		if (oldRelation == null) {
			// throw
			throw AP.E0023(branchId, brchRelationCode, ccyCode);
		}

		// 如果数据没有变更直接返回
		if (CommUtil.equals(oldRelation.getParent_brch_id(), seniorBrchId)) {
			return;
		}

		if(CommUtil.isNotNull(brchRelation.getData_version())){
			// 数据版本好不一致，抛错
			if (CommUtil.compare(oldRelation.getData_version(), brchRelation.getData_version()) != 0) {
				throw ApPubErr.APPUB.E0018(apb_branch_relation.class.getName());
			}
		}

		// 检查机构号是否存在
		ApBranchApi.getItem(branchId);
		//上级机构为非空才进行校验
		if(CommUtil.isNotNull(seniorBrchId)){
			ApBranchApi.getItem(seniorBrchId);
		}

		// 同样会丢失部分公共字段
		apb_branch_relation newRelation = BizUtil.clone(apb_branch_relation.class, oldRelation);// 克隆OldObject
		newRelation.setParent_brch_id(seniorBrchId);// 上级机构

		// 更新操作
		Apb_branch_relationDao.updateOne_odb1(newRelation);

		// 登记审计
		ApDataAuditApi.regLogOnUpdateParameter(oldRelation, newRelation);
	}

	/*
	 * 单个机构关系新增操作
	 */
	private static void addBranchRelationSingle(String brchRelationCode, String ccyCode, ApBranchRelSubWithOper brchRelation) {
		String branchId = brchRelation.getBranch_id();
		String seniorBrchId = brchRelation.getParent_brch_id();

		// 检查机构关系是否存在
		apb_branch_relation oldRelation = Apb_branch_relationDao.selectOne_odb1(branchId, brchRelationCode, ccyCode, false);
		if (oldRelation != null) {
			throw AP.E0025(branchId, brchRelationCode, ccyCode);
		}
		// 检查机构号是否存在
		ApBranchApi.getItem(branchId);
		//上级机构不为空才进行检查
		if(CommUtil.isNotNull(seniorBrchId)){
			ApBranchApi.getItem(seniorBrchId);
		}

		apb_branch_relation newRelation = BizUtil.getInstance(apb_branch_relation.class);
		newRelation.setBranch_id(branchId);// 机构代码
		newRelation.setBrch_relation_code(brchRelationCode);// 机构关系代码
		newRelation.setCcy_code(ccyCode);// 货币代号
		newRelation.setParent_brch_id(seniorBrchId);// 上级机构
		// 插入操作
		Apb_branch_relationDao.insert(newRelation);

		// 登记审计
		ApDataAuditApi.regLogOnInsertParameter(newRelation);

	}

	/*
	 * 生成上下级数据
	 */
	private static void genBranchRelations(String relationCode, String ccyCode, E_YESORNO isRefSelf, E_BRCHSCOPE brchScope) {

		List<apb_branch_senior> branchSenior = new ArrayList<apb_branch_senior>();
		List<apb_branch_junior> branchJunior = new ArrayList<apb_branch_junior>();

		E_YESORNO realBranchInd = (brchScope == E_BRCHSCOPE.ENTITY) ? E_YESORNO.YES : null;// 引用范围

		// 生成上下级数据
		genBranchRelationData(relationCode, ccyCode, isRefSelf, branchSenior, branchJunior, realBranchInd);

		// 删除数据,要用命名sql根据范围过滤
//		ApBranchDao.delBranchJunior(MsOrg.getReferenceOrgId(apb_branch_junior.class),relationCode, realBranchInd);
		List<apb_branch_junior> juniorList = ApBranchDao.selBranchJunior(MsOrg.getReferenceOrgId(apb_branch_junior.class),
				relationCode, realBranchInd, false);
		for(apb_branch_junior entity : juniorList) {
			Apb_branch_juniorDao.delete_odb3(entity.getBranch_id(), entity.getBrch_relation_code(), entity.getCcy_code());
		}
//		ApBranchDao.delBranchSenior(MsOrg.getReferenceOrgId(apb_branch_senior.class),relationCode, realBranchInd);
		List<apb_branch_senior> seniorList = ApBranchDao.selBranchSenior(MsOrg.getReferenceOrgId(apb_branch_senior.class),
				relationCode, realBranchInd, false);
		for(apb_branch_senior entity : seniorList) {
			Apb_branch_seniorDao.delete_odb4(entity.getBranch_id(), entity.getBrch_relation_code(), entity.getCcy_code());
		}
		// 新增数据
		DaoUtil.insertBatch(apb_branch_senior.class, branchSenior);
		DaoUtil.insertBatch(apb_branch_junior.class, branchJunior);
	}

	/*
	 * 生成上下级数据
	 */
	private static void genBranchRelationData(String relationCode, String ccyCode, E_YESORNO isRefSelf, List<apb_branch_senior> branchSenior, List<apb_branch_junior> branchJunior,
			E_YESORNO realBranchInd) {

		String orgId = MsOrg.getReferenceOrgId(apb_branch.class);

		List<ApBranchRelation> branchRelation = ApBranchDao.selBranchRelation(relationCode, orgId, realBranchInd, ccyCode, false);

		boolean isSelf = (isRefSelf == E_YESORNO.YES) ? true : false;

		Map<String, ApBranchNode> nodes = getNodes(branchRelation);// 获取每个节点信息

		for (Map.Entry<String, ApBranchNode> entry : nodes.entrySet()) {
			ApBranchNode node = entry.getValue();

			addBranchSenior(branchSenior, node, isSelf);// 上级处理
			addBranchJunior(branchJunior, node, isSelf);// 下级处理

		}
	}

	/*
	 * 获取节点信息，包括每个要处理的机构 每个机构里面包含它对应的上级和下级
	 */
	@SuppressWarnings({ "rawtypes" })
	private static Map<String, ApBranchNode> getNodes(List<ApBranchRelation> branchRelation) {
		Map<String, ApBranchTree> map = new HashMap<String, ApBranchTree>(); // 树结构信息
		Map<String, ApBranchNode> nodes = new HashMap<String, ApBranchNode>(); // 单个节点信息

		for (ApBranchRelation brchRelation : branchRelation) {
			String branchId = brchRelation.getBranch_id();
			String ccyCode = brchRelation.getCcy_code();

			ApBranchTree tree = BizUtil.getInstance(ApBranchTree.class);
			tree.setBranch_id(branchId);
			tree.setParent_brch_id(brchRelation.getParent_brch_id());
			tree.setCcy_code(ccyCode);
			tree.setChilds(new HashMap());
			tree.setBrch_relation_code(brchRelation.getBrch_relation_code());
			tree.setBranch_name(brchRelation.getBranch_name());

			map.put(branchId, tree);
		}

		// 树结构
		ApBranchTree branchTree = getTree(map);

		// 构建节点
		genNode(branchTree, nodes, 1);

		return nodes;
	}

	/*
	 * 获取树结构
	 */
	@SuppressWarnings({ "unchecked" })
	private static ApBranchTree getTree(Map<String, ApBranchTree> map) {
		ApBranchTree branchTree = null;
		// 生成树结构
		for (Map.Entry<String, ApBranchTree> entry : map.entrySet()) {
			ApBranchTree tree = entry.getValue();
			String branchId = tree.getBranch_id();
			String parentId = tree.getParent_brch_id();

			if (CommUtil.isNull(parentId)) {
				branchTree = tree;
			}
			else {
				ApBranchTree parent = map.get(parentId);
				if (parent == null) {// 如果取不到，则没有根节点，虚拟一个节点
					// 抛错，机构[parentId]未维护机构关系
					throw ApErr.AP.E0026(parentId);
				}
				parent.getChilds().put(branchId, tree);
			}
		}

		if (branchTree == null) {// 能进到这里，说明配置的机构关系是个死循环
			// throw 机构关系未配置上级机构为空的机构
			throw ApErr.AP.E0027();
		}
		return branchTree;
	}

	/*
	 * 通过树构造节点
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void genNode(ApBranchTree tree, Map<String, ApBranchNode> nodes, long i) {
		String parentId = tree.getParent_brch_id();
		String branchId = tree.getBranch_id();
		ApBranchNode node = getNewNode(tree);
		nodes.put(branchId, node);
		Map child = tree.getChilds();// 直接子节点
		List juniorInfo = (List) node.getJunior_info();// 下级
		List seniorInfo = (List) node.getSenior_info();// 上级

		node.setBrch_level(i);
		node.setBrch_relation_code(tree.getBrch_relation_code());

		if (CommUtil.isNull(parentId)) {// 根节点处理,上级不用处理，直接处理下级

			genJunior(child, nodes, juniorInfo, i);// 处理下级

		}
		else {// 非根节点处理
				// 上级处理
			ApBranchLevel branchLevel = BizUtil.getInstance(ApBranchLevel.class);
			branchLevel.setTrxn_branch(parentId);
			branchLevel.setBrch_level(i - 1);// 当前层级减1
			seniorInfo.add(branchLevel);// 先添加直接上级

			List treeSenior = (List) nodes.get(parentId).getSenior_info();// 上级的上级
			seniorInfo.addAll(treeSenior);// 添加上级的上级

			genJunior(child, nodes, juniorInfo, i);// 处理下级

		}
	}

	/*
	 * 构建节点里面的下级信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void genJunior(Map child, Map<String, ApBranchNode> nodes, List juniorInfo, long i) {
		Set keys = child.keySet();
		for (Object key : keys) {
			long j=i;
			ApBranchTree childTree = (ApBranchTree) child.get(key);
			genNode(childTree, nodes, ++j); // 下级处理完之后

			ApBranchLevel branchLevel = BizUtil.getInstance(ApBranchLevel.class);
			branchLevel.setTrxn_branch(key.toString());
			branchLevel.setBrch_level(j);
			juniorInfo.add(branchLevel);// 先添加直接下属

			List treeJunior = (List) nodes.get(key).getJunior_info();

			juniorInfo.addAll(treeJunior);// 在添加下属的下属
		}
	}

	/*
	 * 添加下级数据
	 */
	@SuppressWarnings("rawtypes")
	private static void addBranchJunior(List<apb_branch_junior> branchJuniorList, ApBranchNode node, boolean isRefSelf) {
		String branchId = node.getBranch_id();
		String branchName = node.getBranch_name();
		long level = node.getBrch_level();
		List juniorInfo = (List) node.getJunior_info();

		if (isRefSelf) {
			apb_branch_junior branchJunior = BizUtil.getInstance(apb_branch_junior.class);
			branchJunior.setBranch_id(branchId);
			branchJunior.setBrch_relation_code(node.getBrch_relation_code());
			branchJunior.setCcy_code(node.getCcy_code());
			branchJunior.setChild_brch_id(branchId);
			branchJunior.setBrch_level((long) 0);
			branchJunior.setBranch_name(branchName);

			branchJuniorList.add(branchJunior);
		}

		for (Object junior : juniorInfo) {
			ApBranchLevel branchLevel = (ApBranchLevel) junior;
			long juniorLevel = branchLevel.getBrch_level();
			long diffLevel = Math.abs(level - juniorLevel);// 层级是两个机构的层级差的绝对值

			apb_branch_junior branchJunior = BizUtil.getInstance(apb_branch_junior.class);
			branchJunior.setBranch_id(branchId);
			branchJunior.setBrch_relation_code(node.getBrch_relation_code());
			branchJunior.setCcy_code(node.getCcy_code());
			branchJunior.setBranch_name(branchName);

			branchJunior.setChild_brch_id(branchLevel.getTrxn_branch());
			branchJunior.setBrch_level(diffLevel);

			branchJuniorList.add(branchJunior);
		}

	}

	/*
	 * 添加上级数据
	 */
	@SuppressWarnings("rawtypes")
	private static void addBranchSenior(List<apb_branch_senior> branchSeniorList, ApBranchNode node, boolean isRefSelf) {

		String branchId = node.getBranch_id();
		String branchName = node.getBranch_name();
		long level = node.getBrch_level();
		List seniorInfo = (List) node.getSenior_info();

		if (isRefSelf) {
			apb_branch_senior branchSenior = BizUtil.getInstance(apb_branch_senior.class);
			branchSenior.setBranch_id(branchId);
			branchSenior.setBrch_relation_code(node.getBrch_relation_code());
			branchSenior.setCcy_code(node.getCcy_code());
			branchSenior.setParent_brch_id(branchId);
			branchSenior.setBrch_level((long) 0);
			branchSenior.setBranch_name(branchName);

			branchSeniorList.add(branchSenior);
		}

		for (Object junior : seniorInfo) {
			ApBranchLevel branchLevel = (ApBranchLevel) junior;
			long juniorLevel = branchLevel.getBrch_level();
			long diffLevel = Math.abs(level - juniorLevel);// 层级是两个机构的层级差的绝对值

			apb_branch_senior branchSenior = BizUtil.getInstance(apb_branch_senior.class);
			branchSenior.setBranch_id(branchId);
			branchSenior.setBrch_relation_code(node.getBrch_relation_code());
			branchSenior.setCcy_code(node.getCcy_code());
			branchSenior.setBranch_name(branchName);

			branchSenior.setParent_brch_id(branchLevel.getTrxn_branch());
			branchSenior.setBrch_level(diffLevel);

			branchSeniorList.add(branchSenior);
		}
	}

	private static ApBranchNode getNewNode(ApBranchTree branchTree) {
		ApBranchNode node = BizUtil.getInstance(ApBranchNode.class);
		node.setBranch_id(branchTree.getBranch_id());// 机构号
		node.setCcy_code(branchTree.getCcy_code());// 货币代号
		node.setJunior_info(new ArrayList<ApBranchLevel>());//
		node.setSenior_info(new ArrayList<ApBranchLevel>());
		node.setBranch_name(branchTree.getBranch_name());
		node.setBrch_relation_code(branchTree.getBrch_relation_code());

		return node;
	}

}
