
package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.batch.ApBatch;


 /**
  * file batch service implementation
  * 文件批量服务实现
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApBatchFileImp", longname="file batch service implementation", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApBatchFileImp implements cn.sunline.icore.ap.servicetype.SrvApBatchFile{
 /**
  * file batch application
  *
  */
	public void fileBatchApply(final cn.sunline.icore.ap.type.ComApFile.ApFileIn fileBatchIn){
		ApBatch.fileBatchApply(fileBatchIn);
	}
 /**
  * query file batch processing result
  *
  */
	public cn.sunline.icore.ap.type.ComApFile.ApBatchResultOut queryBatchResult(String busi_batch_code){
		return ApBatch.queryBatchResult(busi_batch_code);
	}
}

