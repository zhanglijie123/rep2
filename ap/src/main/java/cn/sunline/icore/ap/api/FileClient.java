package cn.sunline.icore.ap.api;

import cn.sunline.icore.ap.type.ComApFile.ApRemoteFileList;

public interface FileClient {

	/**
	 * @Author liuzf@sunline.cn
	 *         <p>
	 *         <li>2017年10月17日-下午5:35:53</li>
	 *         <li>功能说明：使用list的循环</li>
	 *         </p>
	 * @return
	 */
	public String getRemoteHome();

	public ApRemoteFileList getRemoteFileList(String remoteDir);

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午3:56:35</li>
	 *         <li>功能说明：文件下载</li>
	 *         </p>
	 * @param localFileName
	 * @param remoteFileName
	 * @return
	 */
	public String download(String localFileName, String remoteFileName);

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午3:56:51</li>
	 *         <li>功能说明：文件上传，无OK文件</li>
	 *         </p>
	 * @param localFileName
	 *            相对路径
	 * @param remoteFileName
	 *            相对路径
	 * @return
	 */
	public void upload(String localFileName, String remoteFileName);

	/**
	 * @Author lid
	 *         <p>
	 *         <li>2017年2月9日-下午3:56:51</li>
	 *         <li>功能说明：文件上传</li>
	 *         </p>
	 * @param localFileName
	 *            相对路径
	 * @param remoteFileName
	 *            相对路径
	 * @return
	 */
	public void upload(String localFileName, String remoteFileName, boolean uploadOk);
}
