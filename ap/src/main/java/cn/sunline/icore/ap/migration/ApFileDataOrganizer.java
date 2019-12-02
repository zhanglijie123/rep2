package cn.sunline.icore.ap.migration;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang3.SystemUtils;

import cn.sunline.icore.ap.tables.TabApDataMigration.App_ofctt_defDao;
import cn.sunline.icore.ap.tables.TabApDataMigration.App_ofile_defDao;
import cn.sunline.icore.ap.tables.TabApDataMigration.app_ofctt_def;
import cn.sunline.icore.ap.tables.TabApDataMigration.app_ofile_def;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.errors.ApPubErr;
import cn.sunline.icore.sys.type.EnumType.E_FIELDCUTTYPE;
import cn.sunline.icore.sys.type.EnumType.E_FIELDPADDINGMODE;
import cn.sunline.icore.sys.type.EnumType.E_FILECONTENTTYPE;
import cn.sunline.ltts.base.util.PropertyUtil;
import cn.sunline.ltts.base.util.PropertyUtil.PropertyAccessor;
import cn.sunline.ltts.biz.global.CommUtil;
import cn.sunline.ltts.biz.global.SysUtil;
import cn.sunline.ltts.core.api.exception.LttsServiceException;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultEnum;

public class ApFileDataOrganizer<T> {
	private static final BizLog bizlog = BizLogUtil.getBizLog(ApFileDataOrganizer.class);
	
	private static final String FIELD_FIX_LENGTH_EXP_TAG = ",";
	
	private static final String FIELD_STRING_DEF_PADDING_VALUE = " ";
	
	private static final String FIELD_DECIMAL_DEF_PADDING_VALUE = "0";
	
	private String expScheme;
	
	private app_ofile_def schemeDefinition;
	
	private List<app_ofctt_def> fields;
	
	private String sourceCoding; 
	
	private int lineCount;
	
	private Formatter dataFormater;
	
	public ApFileDataOrganizer(String expScheme) {
		this.expScheme = expScheme;
		this.lineCount = 0;
		this.sourceCoding = SysUtil.getDbEncoding();
		this.dataFormater = new Formatter();
	}
	
	private void loadExpScheme() {
		if (schemeDefinition == null) {
			schemeDefinition = App_ofile_defDao.selectOne_odb1(expScheme, true);
		}
	}
	
	private void loadExpSchemeFields() {
		if (fields == null) {
			fields = App_ofctt_defDao.selectAll_odb3(expScheme, E_FILECONTENTTYPE.DATA, true); // Data类型的配置应只有一行
		}
	}
	
	public void setSourceEncoding(String charset) {
		this.sourceCoding = charset;
	}
	
	public String getFileEncoding() {
		loadExpScheme();
		
		return schemeDefinition.getFile_encoding().getValue();
	}
	
	public void validConfiguration(Class<T> beanClz) {
		loadExpSchemeFields();
		
		for (int order = 0; order < fields.size(); order++) {
			app_ofctt_def fieldDef = fields.get(order);
			String fieldName = fieldDef.getField_name();
			
			validPropOnBean(beanClz, fieldName);
		}
	}
	
	private void validPropOnBean(Class<T> beanClz, String propName) {
		String methodTail = String.valueOf(propName.charAt(0)).toUpperCase() + propName.substring(1).toLowerCase();
		String getMethod = "get" + methodTail;
		String bulMethod = "is" + methodTail;
		
		Stack<Class<?>> stack = new Stack<Class<?>>();
		stack.push(beanClz);

		Method m = null;
		while (stack.isEmpty() == false) {
			Class<?> current = stack.pop();
			try {
				m = current.getDeclaredMethod(getMethod);
				break;
			}
			catch (Exception e) {
				bizlog.error("(1)Unable to get the [%s] property's getter on bean class[%s].", e, propName, current);
			}
			
			if (m == null) {
				try {
					m = current.getDeclaredMethod(bulMethod);
					break;
				}
				catch (Exception e) {
					bizlog.error("(2)Unable to get the [%s] property's getter on bean class[%s].", e, propName, current);
				}
			}
			
			for (Class<?> clz : current.getInterfaces()) {
				stack.push(clz);
			}
		}
		
		if (m == null) {
			throw ApPubErr.APPUB.E0037(propName, beanClz.getSimpleName());
		}
	}
	
	public int getOrganizedLines() {
		return lineCount;
	}
	
	public String genFileLine(T originalData) {
		loadExpScheme();
		loadExpSchemeFields();
		
		StringBuilder line = new StringBuilder();
		String fieldSeparator;
		PropertyAccessor util = PropertyUtil.createAccessor(originalData);
		for (int order = 0; order < fields.size(); order++) {
			fieldSeparator = CommUtil.nvl(schemeDefinition.getField_separator(), "");
			
			app_ofctt_def fieldDef = fields.get(order);
			String fieldName = fieldDef.getField_name();
			Object fieldValue = util.getNestedProperty(fieldName);
			
			line.append(fieldSeparator).append(dataFormater.formatData(fieldValue, fieldDef));
		}
		
		lineCount++;
		return line.toString();
	}
	
	private String genFileRooter(Map<String, Object> materials, E_FILECONTENTTYPE contentType) {
		List<app_ofctt_def> fileHeadsCfg = App_ofctt_defDao.selectAll_odb3(expScheme, contentType, true);
		
		long lineSort = Long.MIN_VALUE;
		StringBuilder headContent = new StringBuilder();
		for (app_ofctt_def rooterCfg: fileHeadsCfg) {
			long tmpLineSort = rooterCfg.getContent_sort();
			if (lineSort == Long.MIN_VALUE) {
				lineSort = tmpLineSort;
			}
			else {
				if (lineSort != tmpLineSort) {
					headContent.append(SystemUtils.LINE_SEPARATOR);
					lineSort = tmpLineSort;
				}
			}
			
			Object originalValue;
			if (materials == null || CommUtil.isNull(materials.get(rooterCfg.getField_name()))) {
				originalValue = rooterCfg.getField_default_value();
			}
			else {
				originalValue = materials.get(rooterCfg.getField_name());
			}
			
			String fieldValue = dataFormater.formatData(originalValue, rooterCfg);
			
			headContent.append(fieldValue);
		}
		
		return headContent.toString();
	}
	
	public String genFileName(Map<String, Object> nameMaterails, String defName) {
		return genFileRooter(nameMaterails, E_FILECONTENTTYPE.NAME);
	}
	
	public String genFileHead(Map<String, Object> headMaterials, String defHead) {
		return genFileRooter(headMaterials, E_FILECONTENTTYPE.HEAD);
	}
	
	public String genFileTail(Map<String, Object> tailMaterials, String defTail) {
		return genFileRooter(tailMaterials, E_FILECONTENTTYPE.TAIL);
	}
	
	private String truncString(String src, String coding, int strLength, E_FIELDCUTTYPE truncType) throws UnsupportedEncodingException  {
		if (src == null) {
			return src;
		}
		byte[] srcBytes =  src.getBytes(coding);
		if (srcBytes.length <= strLength) {
			return src;
		}
		
		byte[] des = new byte[strLength];
		if (truncType == null || truncType == E_FIELDCUTTYPE.LEFT) { // default left
			System.arraycopy(srcBytes, 0, des, 0, strLength);
		}
		else {
			System.arraycopy(srcBytes, srcBytes.length - strLength, des, 0, strLength);
		}
		return new String(des, coding);
	}
	
	private String formatString(String src, app_ofctt_def format) {
		return formatString(src, format, sourceCoding, getFileEncoding());
	}
	
	private String formatString(String src, app_ofctt_def format, String srcCoding, String targetCoding) {
		String newSrc = CommUtil.nvl(src, "");
		
		String result;
		E_FIELDPADDINGMODE paddingMode = format.getField_padding_mode();
		E_FIELDCUTTYPE truncType = format.getField_trunc_type();
		try {
			result = new String(newSrc.getBytes(srcCoding), targetCoding);
			
			if (paddingMode == null || paddingMode == E_FIELDPADDINGMODE.NO) {
				if (CommUtil.isNotNull(format.getField_max_length()) && format.getField_max_length() > 0) {
					result = truncString(result, targetCoding, format.getField_max_length().intValue(), truncType);
				}
			}
			else {
				String fixLengthExp = format.getField_fix_length();  // field_fix_length must be not empty when padding mode
				Integer fixLength = Integer.valueOf(fixLengthExp.split(FIELD_FIX_LENGTH_EXP_TAG)[0]); 
				
				result = truncString(newSrc, targetCoding, fixLength.intValue(), truncType);
				
				String paddingValue = CommUtil.nvl(format.getPadding_value(), FIELD_STRING_DEF_PADDING_VALUE);
				if (paddingMode == E_FIELDPADDINGMODE.LEFT) {
					result = CommUtil.lpad(newSrc, fixLength.intValue(), paddingValue, targetCoding);
				}
				else {
					result = CommUtil.rpad(newSrc, fixLength.intValue(), paddingValue, targetCoding);
				}
			}
		} catch (UnsupportedEncodingException e) {
			bizlog.error("Unsupported Encoding.", e);
			throw new LttsServiceException("9008", "Unsupported Encoding.", e);
		}
		return result;
	}
	
	public static abstract class FieldDataFormatter<T> {
		abstract String format(T src, app_ofctt_def format);
	}
	
	private class StringFormatter extends FieldDataFormatter<String> {
		@Override
		String format(String src, app_ofctt_def format) {
			return formatString(src, format);
		}
	}
	
	private class BigDecimalFormatter extends FieldDataFormatter<BigDecimal> {
		@Override
		String format(BigDecimal src, app_ofctt_def format) {
			E_FIELDPADDINGMODE paddingMode = format.getField_padding_mode();
			if (paddingMode == null || paddingMode == E_FIELDPADDINGMODE.NO) {
				return src.toPlainString();
			}
			
			String targetCoding = getFileEncoding();
			String fixLengthExp = format.getField_fix_length().trim();
			String paddingVal = CommUtil.nvl(format.getPadding_value(), FIELD_DECIMAL_DEF_PADDING_VALUE);
			String[] fixs = fixLengthExp.split(FIELD_FIX_LENGTH_EXP_TAG); // like: 20,10 -> eg: 1.03 => 00000000010300000000
			if (fixs.length > 2) {
				throw new LttsServiceException("9008", "Unsupported format string [" + format+ "] for decimal");
			}
			
			int integerBit = Integer.parseInt(fixs[0]);
			int decimalBit = 0;
			if (fixs.length == 2) {
				decimalBit = Integer.parseInt(fixs[1]);
				integerBit -= decimalBit;
			}
			if (integerBit <= 0 || decimalBit < 0) {
				throw new LttsServiceException("9008", "Unsupported format string [" + format+ "] for decimal");
			}
			
			app_ofctt_def intFormat = BizUtil.getInstance(app_ofctt_def.class);
			intFormat.setField_padding_mode(CommUtil.nvl(format.getField_padding_mode(), E_FIELDPADDINGMODE.LEFT));
			intFormat.setPadding_value(paddingVal);
			intFormat.setField_fix_length(Integer.toString(integerBit));
			intFormat.setField_trunc_type(null);
			intFormat.setField_max_length((long)integerBit);
			
			String intStr = formatString(src.toBigInteger().toString(), intFormat, targetCoding, targetCoding);
			
			String decStr = "";
			if (decimalBit > 0) {
				if (new BigDecimal(src.toBigInteger()).compareTo(src) == 0) {
					decStr = CommUtil.rpad("", decimalBit, paddingVal, targetCoding);
				}
				else {
					decStr = composeDecimalStr(src, decimalBit, paddingVal);
					decStr = CommUtil.rpad(decStr, decimalBit, paddingVal, targetCoding);
				}
			}
			
			return intStr + decStr;
		}
	}
	
	private String composeDecimalStr(BigDecimal src, int decimalBit, String paddingValue) {
		BigInteger intValue = src.toBigInteger();
		if (intValue.signum() == 0) {
			src = BigDecimal.ONE.add(src);
		}
		String srcZoomStr = src.multiply(BigDecimal.TEN.pow(Byte.MAX_VALUE)).toPlainString();
		String intValueStr = intValue.toString();
		
		String decStr = srcZoomStr.substring(intValueStr.length(), intValueStr.length() + decimalBit);
		
		if (CommUtil.compare(paddingValue, FIELD_DECIMAL_DEF_PADDING_VALUE) != 0) {
			for (int idx = decStr.length() - 1; idx >= 0; idx--) {
				char bit = decStr.charAt(idx);
				if (bit != '0') {
					decStr = decStr.substring(0, idx + 1);
					break;
				}
			}
		}
		return decStr;
	}
	
	private class EnumEleFormatter extends FieldDataFormatter<DefaultEnum<?>> {
		@Override
		String format(DefaultEnum<?> src, app_ofctt_def format) {
			if (src == null) {
				return formatString("", format);
			}
			return formatString(src.getValue().toString(), format);
		}
	}
	
	private class NumberFormatter extends FieldDataFormatter<Number> {
		@Override
		String format(Number src, app_ofctt_def format) {
			if (src == null) {
				return formatString("", format);
			}
			
			@SuppressWarnings("unchecked")
			FieldDataFormatter<BigDecimal> decFormatter = (FieldDataFormatter<BigDecimal>)dataFormater.getFieldFormatter(BigDecimal.class);
			
			return  decFormatter.format(new BigDecimal(src.toString()), format);
		}
	}
	
	private class Formatter {
		private Map<Class<?>, FieldDataFormatter<?>> formatters;
		
		{
			formatters = new HashMap<>();
			formatters.put(String.class, new StringFormatter());
			formatters.put(BigDecimal.class, new BigDecimalFormatter());
			formatters.put(DefaultEnum.class, new EnumEleFormatter());
			formatters.put(Number.class, new NumberFormatter());
		}
		
		private FieldDataFormatter<?> getFieldFormatter(Class<?> type) {
			Class<?> key;
			if (BigDecimal.class == type || String.class == type) {
				key = type;
			}
			else if (DefaultEnum.class.isAssignableFrom(type)) {
				key = DefaultEnum.class;
			}
			else if (Number.class.isAssignableFrom(type)) {
				key = Number.class;
			}
			else {
				throw ApPubErr.APPUB.E0038(type.getSimpleName());
			}
			
			return formatters.get(key);
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		String formatData(Object obj, app_ofctt_def format) {
			FieldDataFormatter formatter;
			if (obj == null) {
				String defValue = "";
				if (CommUtil.isNotNull(format.getField_default_value())) {
					defValue = format.getField_default_value();
				}
				formatter = getFieldFormatter(String.class);
				
				return formatter.format(defValue, format);
			}
			else {
				formatter = getFieldFormatter(obj.getClass());
				return formatter.format(obj, format);
			}
		}
	}
}
