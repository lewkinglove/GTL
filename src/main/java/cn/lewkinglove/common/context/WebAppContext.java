package cn.lewkinglove.common.context;

import java.util.HashMap;
import java.util.Map;

/**
 * web应用上下文对象
 * @author liujing(lewkinglove@gmail.com)
 */
public class WebAppContext {
	
	private static Map<Class<?>,Object> beansMap = new HashMap<Class<?>,Object>();
	
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz) {
		if(beansMap.containsKey(clazz))
			return (T) beansMap.get(clazz);
		try {
			T instance = clazz.newInstance();
			beansMap.put(clazz, instance);
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
}
