package api;

import java.lang.reflect.Method;
import java.util.HashMap;

import api.GTL.Gate;
import cn.lewkinglove.api.core.CallProcessor;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public class ServiceApiCacheUtil {
	private static HashMap<String, Boolean> pkgesAuthMap = new HashMap<String, Boolean>();
	private static HashMap<String, Boolean> classAuthMap = new HashMap<String, Boolean>();
	private static HashMap<String, Boolean> methodAuthMap = new HashMap<String, Boolean>();
	
	/**
	 * 所有经过认证的ServiceApi的Class对象缓存
	 */
	private static HashMap<String, Class<?>> serviceApiClassCache = new HashMap<String, Class<?>>();
	
	/**
	 * 所有经过认证的ServiceApi的Method对象缓存
	 */
	private static HashMap<String, Method> serviceApiClzMethodCache = new HashMap<String, Method>();
	
	/**
	 * 所有经过认证的ServiceApi的实例化对象缓存
	 */
	private static HashMap<String, Object> serviceApiInstanceCache = new HashMap<String, Object>();
	
	public static void initAuthCache() {
		discovery(CallProcessor.API_ROOT_PACKAGE);
	}
	
	private static void discovery(String rootPkgPath){
		try {
			//扫描Api包下所有对外开放和提供服务的Api类和Package
	        ClassPath cp = ClassPath.from(Gate.class.getClassLoader());
	        ImmutableSet<ClassInfo> list = cp.getTopLevelClassesRecursive(rootPkgPath);
	        for(ClassInfo info : list){
	        	String className = info.getName();
	        	String pkgName = info.getPackageName();
	        	
	        	authAndCacheInstance(pkgName, className);
	        	//System.out.println("Package: " + pkgName + "\tClass: " + className);
	        }
        } catch (Exception e) {
	        e.printStackTrace();
        }
	}
	
	private static void authAndCacheInstance(String packageName, String className)throws ClassNotFoundException, IllegalAccessException{
		Boolean isClassAuthed = classAuthMap.get(className);
		if(isClassAuthed!=null){		//如果Class已经Auth过了, 则
			return ;	//直接跳过
		}
		//否则, 任务Class还没有Auth, 继续进行下面流程
		
		Boolean isPkgAuthed = pkgesAuthMap.get(packageName);
		//如果所在包还尚未认证过, 则执行认证流程
		if(isPkgAuthed==null){
			Package packageObj = Package.getPackage(packageName);
			if (packageObj == null) {
				//有可能Package还尚未载入虚拟机, 故而
				//先载入当前包里面的类来使虚拟机载入包
				Class.forName(className);
				packageObj = Package.getPackage(packageName);
			}
			// 验证当前的Package是否声明为包含对外提供ServiceApi服务的包
			isPkgAuthed = packageObj != null && packageObj.isAnnotationPresent(ServiceApiProvider.class)==true;
			pkgesAuthMap.put(packageName, isPkgAuthed);
		}
		
		//如果所在包不是Api服务包, 则
		if(isPkgAuthed==false){	
			classAuthMap.put(className, false);				//直接设定当前类也Auth失败
			return ;
		}
		
		// 验证当前指定的Class是否声明为对外提供服务的ServiceApi类
		Class<?> clazz = Class.forName(className);
		isClassAuthed = clazz != null && clazz.isAnnotationPresent(ServiceApiProvider.class)==true;
		classAuthMap.put(className, isClassAuthed);			//设定当前类的Auth结果
		
		//如果当前类是已Auth过的, 则
		if(isClassAuthed==true){
			//缓存当前的Class对象
			serviceApiClassCache.put(className, clazz);
			Object clazzInstance;
            try {
            	//缓存当前的Class对象的实例
	            clazzInstance = clazz.newInstance();
	            serviceApiInstanceCache.put(className, clazzInstance);
            } catch (InstantiationException e) {
            }
		}
	}
	
	public static boolean isPackageAuthed(String pkgName){
		Boolean isPkgAuthed = pkgesAuthMap.get(pkgName);
		
		isPkgAuthed = isPkgAuthed==null ? false : isPkgAuthed;
		
		return isPkgAuthed;
	}
	
	public static boolean isClassAuthed(String className){
		Boolean isClassAuthed = classAuthMap.get(className);
		
		isClassAuthed = isClassAuthed==null ? false : isClassAuthed;
		
		return isClassAuthed;
	}
	
	public static boolean isClassMethodAuthed(String className, String methodName){
		//查找是否已经auth过了, 如果是
		Boolean isMethodAuthed = methodAuthMap.get(className+':'+methodName);
		if(isMethodAuthed!=null){
			return isMethodAuthed;	//则直接返回结果
		}
		
		//先验证所在Class是否Auth通过
		boolean isClassAuthed = isClassAuthed(className);
		if(isClassAuthed==false){
			//设置当前class的method auth失败
			methodAuthMap.put(className+':'+methodName, false);
			return false;
		}

		
		Class<?> clazz = getServiceApiClazz(className);
		// 验证当前Method是否声明为对外提供服务的ServiceApi
        Method method;
        try {
        	//获取方法注解  
            method = clazz.getMethod(methodName, new Class[]{HashMap.class});
        } catch (NoSuchMethodException e) {
        	//类方法不存在, 设置当前class的method auth失败
			methodAuthMap.put(className+':'+methodName, false);
			return false;
        }
        
        isMethodAuthed = method != null && method.isAnnotationPresent(ServiceApiProvider.class)==true;
        methodAuthMap.put(className+':'+methodName, isMethodAuthed);
        
        //如果Auth通过, 则缓存Method对象
        if(isMethodAuthed==true)
        	serviceApiClzMethodCache.put(className+':'+methodName, method);
        
        return isMethodAuthed;
	}
	
	public static Class<?> getServiceApiClazz(String className){
		return serviceApiClassCache.get(className);
	}
	
	public static Method getServiceApiClazzMethod(String className, String methodName){
		return serviceApiClzMethodCache.get(className+':'+methodName);
	}
	
	public static Object getServiceApiInstance(String className){
		return serviceApiInstanceCache.get(className);
	}

}
