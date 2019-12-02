package cn.sunline.icore.ap.api;

import cn.sunline.ltts.base.util.FileProcessor;

public class LocalFileProcessor extends FileProcessor{
	
	   public LocalFileProcessor(String path, String fileName) {
		   super(ApFileApi.getLocalHome(path),fileName);
	   }

	   public LocalFileProcessor(String path, String fileName, String encoding) {
		   super(ApFileApi.getLocalHome(path),fileName,encoding);
	   }
	   
	   public LocalFileProcessor(String path, String fileName, int flushSize) {
		   super(ApFileApi.getLocalHome(path),fileName,flushSize);
	   }

	   public LocalFileProcessor(String path, String fileName, String encoding, int flushSize) {
		   super(ApFileApi.getLocalHome(path),fileName,encoding, flushSize);
	   }
}
