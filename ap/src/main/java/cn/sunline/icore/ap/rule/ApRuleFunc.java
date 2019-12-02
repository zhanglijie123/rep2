package cn.sunline.icore.ap.rule;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sunline.icore.ap.rule.ApBaseRule.BufferedDataVisitor;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_funcDao;
import cn.sunline.icore.ap.tables.TabApRule.App_rule_func_parmDao;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_data;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_func;
import cn.sunline.icore.ap.tables.TabApRule.app_rule_func_parm;
import cn.sunline.icore.ap.util.BizUtil;
import cn.sunline.icore.sys.dict.SysDict;
import cn.sunline.icore.sys.errors.ApBaseErr;
import cn.sunline.ltts.base.expression.enhance.OgnlCompiler;
import cn.sunline.ltts.base.util.StringUtil;
import cn.sunline.ltts.core.api.logging.BizLog;
import cn.sunline.ltts.core.api.logging.BizLogUtil;
import cn.sunline.ltts.core.api.model.dm.internal.DefaultOptions;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import ognl.OgnlContext;
import ognl.enhance.EnhancedClassLoader;

public class ApRuleFunc extends OgnlCompiler {
	Hashtable<String, Class<?>> cacheFuncClz = new Hashtable<>();

	private static final BizLog bizlog = BizLogUtil.getBizLog(ApRuleFunc.class);

	/**
	 *         <p>
	 *         <li>2018年2月26日-下午3:28:56</li>
	 *         <li>功能说明：刷新功能方法</li>
	 *         </p>
	 * @param funcDef
	 * @param funcParms
	 */
	public static void refreshFunc(app_rule_func funcDef, List<app_rule_func_parm> funcParms) {
		FuncFactory factory = FuncFactory.getInstance();
		
		String funcCode = funcDef.getFunc_code();
		factory.funcCache.get(funcCode);
		synchronized (factory.funcCache) {
			if (factory.funcCache.get(funcCode) != null) { // double check
				factory.funcCache.put(funcCode, null);
			}
		}
		
		factory.genFunc(funcDef, funcParms);
	}
	
	public static Object eval(app_rule_data ruleData, BufferedDataVisitor dataVisitor) { 
		return eval(ruleData.getFunc_code(), ruleData.getActual_parm(), dataVisitor); 
	}
	
	public static Object eval(String funcCode, String funcArgsExp, BufferedDataVisitor dataVisitor) {
		if (funcCode == null || funcArgsExp == null) {
			throw ApBaseErr.ApBase.E0086(SysDict.A.func_code.getLongName(), SysDict.A.actual_parm.getLongName());
			// SysDict.A.func_code.getLongName() + " and " + SysDict.A.actual_parm.getLongName() + " cann't be null.");
		}

		app_rule_func funcDef = App_rule_funcDao.selectOne_odb1(funcCode, true);
		List<app_rule_func_parm> funcParms = App_rule_func_parmDao.selectAll_odb3(funcDef.getFunc_code(), true);
		Func func = FuncFactory.getInstance().genFunc(funcDef, funcParms);

		Object result = func.getResult(funcArgsExp, dataVisitor);
		
		return result;
	}
	
	private static class Func extends OgnlCompiler {
		private volatile Class<?> funcContainer;
		private String signature;
		private String parameter;
		private String funcBody;
		private Class<?> funcType;

		private String funcCodeName;
		private String funcPronoun;
		private Class<?>[] paramTypes;
		private Map<String, Long> paramNames;

		public String getCode() {
			StringBuilder sb = new StringBuilder();

			sb.append(signature).append(parameter).append("{\r\n");
			if (funcBody.endsWith(";") == false) {
				sb.append(funcBody).append(";");
			} else {
				sb.append(funcBody);
			}

			sb.append("\r\n}");

			return sb.toString();
		}

		private void initContainer() {
			if (funcContainer == null) {
				synchronized (this) {
					if (funcContainer == null) {
						initFuncContainer();
					}
				}
			}
		}
		
		/**
		 * 
		 * @Author tsichang
		 *         <p>
		 *         <li>2018年2月26日-上午11:11:13</li>
		 *         </p>
		 * @param funcArgsExp ${column_name} or ${mart.column_name} or arg1=>${column_name} or arg1=>${mart.column_name}
		 * @param dataVisitor
		 * @return
		 */
		public Object getResult(String funcArgsExp, BufferedDataVisitor dataVisitor) {
			bizlog.debug("execute func[%s]>>>>>>>>>>>>>>>>>>>", funcPronoun);
			String[] argExps = funcArgsExp.split(",");
			if (argExps.length != paramTypes.length) {
				throw ApBaseErr.ApBase.E0090();
			}
			
			Object[] args = new Object[argExps.length];
			boolean oraStyle = false;
			boolean simStyle = false;
			Pattern pat = Pattern.compile(PARAMETER_TOKEN);
			for (int i = 0; i < argExps.length; i++) {
				String[] expParts = argExps[i].split(ASSIGNMENT_FLAG);
				
				if (expParts.length == 2) {
					oraStyle = true;
				}
				else if (expParts.length < 2) {
					simStyle = true;
				}
				
				if (simStyle ^ oraStyle == false) {
					throw ApBaseErr.ApBase.E0091();
				}
				
				int argIndex;
				if (simStyle) { //simple style: ${column_name} or ${mart.column_name}
					argIndex = i;
				}
				else { // oracle style：arg1=>${column_name} or arg1=>${mart.column_name}
					String paramName = expParts[0].trim();
					if (paramNames.get(paramName) == null) {
						throw ApBaseErr.ApBase.E0092(paramName, funcCodeName);
					}
					argIndex = paramNames.get(paramName).intValue() - 1;
				}
				
				Matcher mather = pat.matcher(simStyle? expParts[0] : expParts[1]);
				String exp;
				if (mather.find()) {
					exp = mather.group(1);
				} else {
					throw ApBaseErr.ApBase.E0093(argExps[i]);
				}
				
				String[] keys = exp.split("\\.");
				Object argValue;
				if (keys.length > 1) {
					argValue = dataVisitor.getFieldValue(keys[0], keys[1], true);
				}
				else {
					argValue = dataVisitor.getFieldValue(keys[0], true);
				}
				
				args[argIndex] = argValue;
			}
			
			bizlog.debug("func args = %s", Arrays.toString(args));
			
			return _getResult(args);
		}

		private static final String ASSIGNMENT_FLAG = "=>"; // eg:arg1=>${CUSTOMER.cust_type}
		private static final String PARAMETER_TOKEN = "[$]\\{([A-Za-z0-9_\\.]+)\\}";
		private Object _getResult(Object... args) {
			initContainer();

			try {
				Method m = funcContainer.getDeclaredMethod(funcCodeName, paramTypes);
				Object result = m.invoke(null, args);

				bizlog.debug("execute func[%s] result is [%s]", funcPronoun, result);
				return result;
			} catch (Exception e) {
				throw ApBaseErr.ApBase.E0089(e.getMessage(), e);
			}
		}

		private void initFuncContainer() {
			OgnlContext context = new OgnlContext();
			
			EnhancedClassLoader loader = getClassLoader(context);
			ClassPool pool = getClassPool(context, loader);
			CtClass newClass = pool.makeClass(this.getClass().getName() + StringUtil.capitalFirst(funcCodeName));
			
			impPackage(pool);
			try {
				CtMethod cm = CtMethod.make(getCode(), newClass);
				newClass.addMethod(cm);

				funcContainer = newClass.toClass();
			} catch (CannotCompileException e) {
				throw ApBaseErr.ApBase.E0088(funcBody, e.getMessage());
				// "function body non-compliant." + e.getMessage() + ".\r\n[" + funcBody + "]");
			}
		}

		private void impPackage(ClassPool pool) {
			impBasicPackage(pool);
			impFuncTypePackage(pool);
		}
		
		private void impFuncTypePackage(ClassPool pool) {
			pool.importPackage(getPackage(funcType));
			for (Class<?> parmType : paramTypes) {
				pool.importPackage(getPackage(parmType));
			}
		}

		private void impBasicPackage(ClassPool pool) {
			pool.importPackage(BizUtil.class.getPackage().getName());
			pool.importPackage(DefaultOptions.class.getPackage().getName());
			pool.importPackage(String.class.getPackage().getName());
			pool.importPackage(Date.class.getPackage().getName());
			pool.importPackage(BigDecimal.class.getPackage().getName());
		}

		private String getPackage(Class<?> clz) {
			String clzName = clz.getName();
			String packageName = clzName.substring(0, clzName.lastIndexOf("."));
			return packageName;
		}

		private static class FuncBuilder {
			private Func inst = new Func();

			public FuncBuilder wSignature(String signature) {
				inst.signature = signature;
				return this;
			}

			public FuncBuilder wParameter(String parameter) {
				inst.parameter = parameter;
				return this;
			}

			public FuncBuilder wBody(String funcBody) {
				inst.funcBody = funcBody;
				return this;
			}

			public FuncBuilder wFuncName(String funcName) {
				inst.funcCodeName = funcName;
				return this;
			}

			public FuncBuilder wParmTypes(Class<?>[] types) {
				inst.paramTypes = types;
				return this;
			}

			public FuncBuilder wFuncPronoun(String funcPronoun) {
				inst.funcPronoun = funcPronoun;
				return this;
			}

			public FuncBuilder wFuncType(Class<?> type) {
				inst.funcType = type;
				return this;
			}
			
			public FuncBuilder wParamNames(Map<String, Long> names) {
				inst.paramNames = names;
				return this;
			}

			public Func build() {
				return inst;
			}
		}
	}

	private static class FuncFactory {
		private static final String MODIFIER = "public static ";
		private static final String BLANK = " ";
		private static final String COMMA = ",";
		private static final String LEFT_BRACKET = "(";
		private static final String RIGHT_BLACKET = ")";
		
		private static FuncFactory inst = new FuncFactory();
		private Map<String, Func> funcCache = new HashMap<>();

		public static FuncFactory getInstance() {
			return inst;
		}

		public Func genFunc(app_rule_func funcDef,
				List<app_rule_func_parm> funcParms) {
			synchronized (funcCache) {
				if (funcCache.get(funcDef.getFunc_code()) == null) {
					sort(funcParms);
					Func.FuncBuilder builder = new Func.FuncBuilder();
					builder.wSignature(genSignature(funcDef))
							.wParameter(genParameter(funcParms)).wParamNames(genParamNames(funcParms))
							.wBody(genBody(funcDef))
							.wFuncPronoun(funcDef.getFunc_name())
							.wFuncName(funcDef.getFunc_code())
							.wParmTypes(getParmTypes(funcParms))
							.wFuncType(loadClass(funcDef.getFunc_return_type().getValue()));

					funcCache.put(funcDef.getFunc_code(), builder.build());
				}
			}
			return funcCache.get(funcDef.getFunc_code());
		}

		private void sort(List<app_rule_func_parm> funcParms) {
			BizUtil.listSort(funcParms, true, SysDict.A.parm_order.getId());
		}

		private Map<String, Long> genParamNames(List<app_rule_func_parm> funcParms) {
			Map<String, Long> paramNames = new HashMap<String, Long>();
			for (int i = 0; i < funcParms.size(); i++) {
				app_rule_func_parm paramDef = funcParms.get(i);

				paramNames.put(paramDef.getParm_code(), paramDef.getParm_order());
			}
			return paramNames;
		}

		private Class<?> loadClass(String fulClzName) {
			try {
				return Class.forName(fulClzName);
			} catch (ClassNotFoundException e) {
				throw ApBaseErr.ApBase.E0087(fulClzName);
				// "unable resolve the type of [" + fulClzName + "]");
			}
		}

		private Class<?>[] getParmTypes(List<app_rule_func_parm> funcParms) {
			Class<?>[] parmTypes = new Class<?>[funcParms.size()];
			app_rule_func_parm paramDef = null;
			for (int i = 0; i < funcParms.size(); i++) {
				paramDef = funcParms.get(i);
				parmTypes[i] = loadClass(paramDef.getParm_type().getValue());
			}

			return parmTypes;
		}

		private String genBody(app_rule_func funcDef) {
			return funcDef.getFunc_body().replace("\\r", "\r").replace("\\n", "\n").replace("\\t", "\t");
		}

		private String genSignature(app_rule_func funcDef) {
			return MODIFIER + funcDef.getFunc_return_type().getValue() + BLANK + funcDef.getFunc_code();
		}

		private String genParameter(List<app_rule_func_parm> funcParms) {
			StringBuilder parameter = new StringBuilder();
			parameter.append(LEFT_BRACKET);
			String seperator = COMMA;
			for (int i = 0; i < funcParms.size(); i++) {
				app_rule_func_parm paramDef = funcParms.get(i);
				String paramType = paramDef.getParm_type().getValue();
				String paramName = paramDef.getParm_code();

				if (i == funcParms.size() - 1) {
					seperator = RIGHT_BLACKET;
				}
				parameter.append(paramType).append(BLANK).append(paramName).append(seperator);
			}
			return parameter.toString();
		}
	}
}
