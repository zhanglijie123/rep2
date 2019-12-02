package cn.sunline.icore.ap.serviceimpl;

import cn.sunline.icore.ap.packet.ApPacket;

 /**
  * message brief information
  * 报文简要信息查询实现
  *
  */
@cn.sunline.ltts.frw.model.annotation.Generated
@cn.sunline.ltts.frw.model.annotation.ConfigType(value="SrvApPacketImpl", longname="message brief information", type=cn.sunline.ltts.frw.model.annotation.ConfigType.Type.service)
public class SrvApPacketImpl implements cn.sunline.icore.ap.servicetype.SrvApPacketInfo{
 /**
  * query  message brief information
  *
  */
	public cn.sunline.ltts.core.api.model.dm.Options<cn.sunline.icore.ap.type.ComApSystem.ApPacketBriefInfo> queryPacketBrief(final cn.sunline.icore.ap.type.ComApSystem.ApPacketInfoInput input){
		
		return ApPacket.queryPacketList(input);
	}

	 /**
	  * query  message detail information
	  *
	  */
	public cn.sunline.icore.ap.type.ComApSystem.ApPacketInfo queryPacketDetail( String trxn_seq){
	
		return ApPacket.queryPacketInfo(trxn_seq);
	
	}
}

