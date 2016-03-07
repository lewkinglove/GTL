package cn.lewkinglove.common.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;

/**
 * Bean操作相关工具类
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class BeanUtil {

	/**
	 * 将数据赋值给指定对象的相应属性
	 * 
	 * @param field
	 *            字段
	 * @param objInstance
	 *            要赋值的对象
	 * @param value
	 *            要赋值的数据
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NumberFormatException 
	 * @throws ParseException 
	 */
	public static void setFieldValue(Field field, Object objInstance, String value) throws NumberFormatException, IllegalArgumentException, IllegalAccessException, ParseException {
		String fieldType = field.getType().getName();
		field.setAccessible(true);
		if ("java.lang.String".equals(fieldType)) {
			field.set(objInstance, value);
			return;
		}
		if ("java.lang.Integer".equals(fieldType) || "int".equals(fieldType)) {
			field.set(objInstance, Integer.valueOf(value));
			return;
		}
		if ("java.lang.Long".equals(fieldType) || "long".equals(fieldType)) {
			field.set(objInstance, Long.valueOf(value));
			return;
		}
		if ("java.lang.Float".equals(fieldType) || "float".equals(fieldType)) {
			field.set(objInstance, Float.valueOf(value));
			return;
		}
		if ("java.lang.Double".equals(fieldType) || "double".equals(fieldType)) {
			field.set(objInstance, Double.valueOf(value));
			return;
		}
		if ("java.math.BigDecimal".equals(fieldType)) {
			field.set(objInstance, new BigDecimal(value));
			return;
		}
		if ("java.util.Date".equals(fieldType)) {
			field.set(objInstance, DateUtil.parseDate(value));
			return;
		}
		if ("java.sql.Date".equals(fieldType)) {
			field.set(objInstance, new java.sql.Date(DateUtil.parseDate(value).getTime()));
			return;
		}
		if ("java.lang.Boolean".equals(fieldType) || "boolean".equals(fieldType)) {
			field.set(objInstance, Boolean.valueOf(value));
			return;
		}
		if (fieldType.equals("java.lang.Byte") || "byte".equals(fieldType)) {
			field.set(objInstance, Byte.valueOf(value));
			return;
		}
		if ("java.lang.Short".equals(fieldType) || "short".equals(fieldType)) {
			field.set(objInstance, Short.valueOf(value));
			return;
		}
	}
}