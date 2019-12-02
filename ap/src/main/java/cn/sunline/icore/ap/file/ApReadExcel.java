package cn.sunline.icore.ap.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;

public class ApReadExcel {

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApReadExcel.class);
	
	/**
	 * 读取Excel
	 */
	private static Workbook wb;
	private static Sheet sheet;
	private static Row row;

	/**
	 * 读取Excel表格文件头的内容
	 * @param headNum 文件头开始行号，excel是由下标0开始
	 * @param keys 字段名 作为返回map集合的键
	 * @param fieldCols 字段对应的列号集合，需要与keys一一对应，作为取返回集合的值
	 * @param filePath 文件路径
	 * @return
	 */
	public static Map<String,Object> readExcelFileTitle(int headNum,List<String> keys,List<Integer> fieldCols,String filePath) {
		bizlog.method(" AgReadExcel.readExcelFileTitle begin >>>>>>>>>>>>>>>>");
		bizlog.error("readExcelFileTitle>>>>headNum[%s],keys[%s],fieldCols[%s],filepath[%s]", headNum,keys,fieldCols,filePath);
		readExcel(filePath);
		if (wb == null) {
			try {
				throw new Exception("Workbook对象为空！");
			} catch (Exception e) {
				bizlog.error("wb is null>>>> ", e);
			}
		}
		sheet = wb.getSheetAt(0);
		//Excel对应的文件头所在行，Excel由下标0开始。为方便配置配置由1开始为下标
		row = sheet.getRow(headNum-1);
		Map<String,Object> headData = new HashMap<String, Object>();
		int max = 0;
		for (Integer integer : fieldCols) {
			if(max<integer){
				max = integer;
			}
		}
		for (int i = 0; i < max; i++) {
			int index = fieldCols.get(i).intValue()-1;
			bizlog.debug("index >>>>[%s]", index);
			Cell cell = row.getCell(index);
			if(cell!=null){
				Object obj = getCellFormatValue(cell);
				if(obj!=null){
					headData.put(keys.get(i), obj.toString());
				}
			}
			
		}
		bizlog.method(" AgReadExcel.readExcelFileTitle end >>>>>>>>>>>>>>>>");
		return headData;
	}

	
	/**
	 * 读取Excel文件体数据内容
	 * @param bodyNum 文件体开始行号，excel是由下标0开始
	 * @param keys 字段名 作为返回map集合的键
	 * @param fields 字段对应的列号集合，需要与keys一一对应，作为取返回集合的值
	 * @param filepath  文件路径
	 * @return
	 */
	public static List<Map<String,Object>> readExcelFileContent(int bodyNum,List<String> keys,List<Integer> fields,String filepath) {
		bizlog.method(" AgReadExcel.readExcelFileContent begin >>>>>>>>>>>>>>>>");
		bizlog.error("readExcelFileTitle>>>>headNum[%s],keys[%s],fieldCols[%s],filepath[%s]", bodyNum,keys,fields,filepath);
		readExcel(filepath);
		if (wb == null) {
			try {
				throw new Exception("Workbook对象为空！");
			} catch (Exception e) {
				bizlog.error("wb is null>>>> ", e);
			}
		}
		List<Map<String,Object>> content = new ArrayList<Map<String,Object>>();

		sheet = wb.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(bodyNum-1);
		int max = 0;
		for (Integer integer : fields) {
			if(max<integer){
				max = integer;
			}
		}
		// 正文内容应该从第二行开始,第一行为表头的标题
		for (int i = bodyNum-1; i <= rowNum; i++) {
			row = sheet.getRow(i);
			int j = 0;
			Map<String,Object> single = new HashMap<String,Object>();
			while (j < max) {
				if(row.getCell(j)!=null){
					single.put(keys.get(j), getCellFormatValue(row.getCell(fields.get(j)-1)));
					
				}
				j++;
			}
			content.add(single);
		}
		bizlog.method(" AgReadExcel.readExcelFileContent end >>>>>>>>>>>>>>>>");
		return content;
	}

	/**
	 * 
	 * 根据Cell类型设置数据
	 * 
	 * @param cell
	 * @param values
	 * @param index
	 * @return
	 */
	private static Object getCellFormatValue(Cell cell) {
		bizlog.method(" AgReadExcel.getCellFormatValue begin >>>>>>>>>>>>>>>>");
		bizlog.error("cell type:"+cell.getCellType());
		Object cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC
				// 取得当前Cell的数值
				cellvalue = cell.getNumericCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA: 
				break;
			case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING
				// 取得当前的Cell字符串
				cellvalue = cell.getRichStringCellValue().getString();
				break;
			default:// 默认的Cell值
				cellvalue = "";
			}
		}
		return cellvalue;
	}
	
	private static void readExcel(String filepath){
		bizlog.method(" AgReadExcel.readExcel begin >>>>>>>>>>>>>>>>");
		if (filepath == null) {
			return;
		}
		String ext = filepath.substring(filepath.lastIndexOf("."));
		InputStream is = null;
		try {
			is = new FileInputStream(filepath);
			if (".xls".equals(ext)) {
				wb = new HSSFWorkbook(is);
			} else if (".xlsx".equals(ext)) {
				wb = new XSSFWorkbook(is);
			} else { 
				wb = null;
			}
		}catch (IOException e) {
			try {
				wb = new XSSFWorkbook(is);
			} catch (IOException e1) {
				bizlog.error("IOException ", e);
			}
			bizlog.error("IOException", e);
		}
		bizlog.method(" AgReadExcel.readExcel end >>>>>>>>>>>>>>>>");
	} 

}
