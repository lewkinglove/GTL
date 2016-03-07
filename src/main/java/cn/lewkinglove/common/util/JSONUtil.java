package cn.lewkinglove.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * JSON工具类 为了避免重复初始化json parser造成的性能损失 特此编写此类进行包装和处理
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class JSONUtil {
	private JSONUtil() {

	}

	public static Gson instance;

	static {
		//@formatter:off
		JSONUtil.instance = new GsonBuilder()
			// 不导出实体中没有用@Expose注解的属性
			// .excludeFieldsWithoutExposeAnnotation()
			
			// 当需要序列化的值为空时，采用null映射，否则会把该字段省略
			.serializeNulls()
	        
	        // 日期格式转换
	        // .setDateFormat("yyyy-MM-dd HH:mm:ss")
	        
			// 将属性的首字母大写
	        // .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
			
			// 将结果进行格式化输出
		    // .setPrettyPrinting()

		    .create();
		//@formatter:on
	}

	public static String toJSON(Object object) {
		return instance.toJson(object);
	}

	public static <T> T fromJSON(String json, Class<T> clazz) {
		return (T) instance.fromJson(json, clazz);
	}
}
