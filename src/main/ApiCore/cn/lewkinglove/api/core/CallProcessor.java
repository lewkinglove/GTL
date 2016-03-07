package cn.lewkinglove.api.core;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.google.gson.JsonSyntaxException;

import api.ServiceApiAlias;
import api.ServiceApiCacheUtil;
import api.ServiceApiProvider;
import cn.lewkinglove.common.util.JSONUtil;

/**
 * 请求处理器
 * @author liujing(lewkinglove@gmail.com)
 */
@SuppressWarnings("unchecked")
public class CallProcessor {
	/**
	 * 所有Api的根包路径
	 */
	public static final String API_ROOT_PACKAGE = "api";
	
	private PrintWriter out;
	private CallRequest callRequest;

	/**
	 * 构造器
	 * @param out		PrintWriter
	 * @param callRequest	Api请求对象
	 */
	public CallProcessor(PrintWriter out, CallRequest callRequest) {
		this.out = out;
		this.callRequest = callRequest;
	}

	public void process() {
		try {
			// -7 请求的服务不存在或已禁止外部调用，无法继续。
			String[] callArray = this.callRequest.getCall().split("\\.");
			// Api参数少于三段, 说明Api参数不正确, 调用非法
			if (callArray.length < 3){
				//别名的优先级低于本名
				//检查是否使用的是别名来进行调用
				String originalCall = ServiceApiAlias.getOriginal(this.callRequest.getCall());
				//如果根据别名, 没有找到本名, 则
				if(originalCall==null)
					throw new CallException(Consts.BIZ_IDENTIFIER, "-7");	//报错, 结束
				//否则, 对本名进行分段处理
				callArray = originalCall.split("\\.");
			}

			// 解析被调用的ServiceApi
			String methodName = callArray[callArray.length - 1];
			String className = callArray[callArray.length - 2];
			String packageName = API_ROOT_PACKAGE;
			for (int i = 0; i < callArray.length - 2; i++)
				packageName = packageName + "." + callArray[i];
			
			HashMap<String, Object> args = this.callRequest.getArgs();
			
			// 反射调用ServiceApi
			CallResponse response = this.invokeService(packageName, className, methodName, args);
			out.println(response.toString());
		} catch (CallException e) {
			out.println(new CallResponse(e.getCode(), e.getMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			// 0 出现未知的异常，请稍后再试或者联系我们的技术支持。
			out.println(new CallResponse(Consts.BIZ_IDENTIFIER, "0"));
		} finally {
			out.flush();
			out.close();
		}
	}
	
	
	/**
	 * 调用ServiceApi
	 * @param packageName	包名
	 * @param className		类名
	 * @param methodName	方法名
	 * @param args		调用参数
	 * @return
	 * @throws Exception
	 */
	private CallResponse invokeService(String packageName, String className, String methodName, HashMap<String, Object> args)  {
		className = packageName+"."+className;	//使用绝对路径标识类
		try {
			boolean isPackageAuthed = ServiceApiCacheUtil.isPackageAuthed(packageName);
			if(isPackageAuthed==false)
				throw new CallException(Consts.BIZ_IDENTIFIER, "-7");
			
			boolean isClassAuthed = ServiceApiCacheUtil.isClassAuthed(className);
			if(isClassAuthed==false)
				throw new CallException(Consts.BIZ_IDENTIFIER, "-7");
			
			boolean isMethodAuthed = ServiceApiCacheUtil.isClassMethodAuthed(className, methodName);
			if(isMethodAuthed==false)
				throw new CallException(Consts.BIZ_IDENTIFIER, "-7");
			
			Object classInstance = ServiceApiCacheUtil.getServiceApiInstance(className);
	        Method classMethodObj = ServiceApiCacheUtil.getServiceApiClazzMethod(className, methodName);
	        
	        Object invokeResult = null;
	        try{
	        	invokeResult =  classMethodObj.invoke(classInstance, new Object[]{ args });
	        	//返回调用结果
	        	return new CallResponse(Consts.DEFAULT_SUCCESS_CODE, invokeResult);
	        }catch(InvocationTargetException ex){
	        	//TODO 此处应该有Api调用异常的日志记录动作.
	        	if(ex.getCause() instanceof CallException )
	        		throw (CallException)ex.getCause();	//业务方法抛出异常
	        	
	        	ex.printStackTrace();
	        	throw new CallException(Consts.BIZ_IDENTIFIER, "0");		//代码异常或者调用异常
	        }
		} catch (CallException e) {
			return new CallResponse(e.getCode(), e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			return new CallResponse(Consts.BIZ_IDENTIFIER, "0");	// 0 出现未知异常，请稍后再试或联系我们的技术支持。
		}
	}
	
	
	
	/**
	 * 调用ServiceApi(直接反射无Cache和优化)
	 * @param packageName	包名
	 * @param className		类名
	 * @param methodName	方法名
	 * @param args		调用参数
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private CallResponse invokeServiceByReflection(String packageName, String className, String methodName, HashMap<String, Object> args)  {
		try {
			// 首先获取Package对象
			// 如果Package对象为空, 则直接反射创建当前ServiceApi类
			Package packageObj = Package.getPackage(packageName);
			
			Class<?> clazz  = null;
			if (packageObj == null) {
				try {
					clazz = Class.forName(packageName + "." + className);
					packageObj = Package.getPackage(packageName);
				} catch (ClassNotFoundException e) {
					//调用的ServiceApi 类不存在
					throw new CallException(Consts.BIZ_IDENTIFIER, "-7");
				}
			}

			// 验证当前指定的Package是否声明为包含对外提供ServiceApi服务的包
			//ServiceApiProvider sap = packageObj.getAnnotation(ServiceApiProvider.class);
			if (packageObj == null || false == packageObj.isAnnotationPresent(ServiceApiProvider.class)) {
				throw new CallException(Consts.BIZ_IDENTIFIER, "-7");	//Package不存在, 或者不是ServiceApi
			}
			
			// 验证当前指定的Class是否声明为对外提供服务的ServiceApi类
			clazz = clazz == null ? Class.forName(packageName + "." + className) : clazz;
			if(clazz == null || false == clazz.isAnnotationPresent(ServiceApiProvider.class)){
				throw new CallException(Consts.BIZ_IDENTIFIER, "-7");	//类反射失败, 或者类不存在
			}
			
			// 验证当前Method是否声明为对外提供服务的ServiceApi
	        Method method;
            try {
            	//获取方法注解  
	            method = clazz.getMethod(methodName, new Class[]{HashMap.class});
            } catch (NoSuchMethodException e) {
            	throw new CallException(Consts.BIZ_IDENTIFIER, "-7");	//调用的ServiceApi类方法不存在
            }  
	        
	        if(method == null || false == method.isAnnotationPresent(ServiceApiProvider.class)){
	        	throw new CallException(Consts.BIZ_IDENTIFIER, "-7");	//调用的ServiceApi类方法不存在, 或者不是ServiceApi
	        }
	        
	        
	        Object obj = clazz.newInstance();
	        Object invokeResult = null;
	        try{
	        	invokeResult =  method.invoke(obj, new Object[]{ args });
	        	//返回调用结果
	        	return new CallResponse(Consts.DEFAULT_SUCCESS_CODE, invokeResult);
	        }catch(Exception ex){
	        	//TODO 此处应该有Api调用异常的日志记录动作.
	        	if(ex.getCause() instanceof CallException )
	        		throw (CallException)ex.getCause();	//业务方法抛出异常
	        	throw new CallException(Consts.BIZ_IDENTIFIER, "0");		//代码异常或者调用异常
	        }
		} catch (CallException e) {
			return new CallResponse(e.getCode(), e.getMessage());
		} catch (Exception e){
			return new CallResponse(Consts.BIZ_IDENTIFIER, "0");	// 0 出现未知异常，请稍后再试或联系我们的技术支持。
		}
	}

}
