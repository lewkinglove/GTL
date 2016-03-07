package cn.lewkinglove.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * 数据转换相关工具类
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class DateUtil {

	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
	public static final String DEFAULT_DATETIME_STYLE = DEFAULT_DATE_PATTERN + " " + DEFAULT_TIME_PATTERN;
	private static HashMap<String, SimpleDateFormat> formaters = null;

	static {
		formaters = new HashMap<String, SimpleDateFormat>();

		SimpleDateFormat formater = new SimpleDateFormat(DEFAULT_DATETIME_STYLE);
		formater.setLenient(false);
		formaters.put("DEFAULT_STYLE", formater);
	}

	/**
	 * 设置默认的日期格式化风格
	 * 
	 * @param dateStyle
	 *            日期格式化的样式风格
	 */
	public static void setDefaultDateFormatStyle(String dateStyle) {
		SimpleDateFormat formater = new SimpleDateFormat(dateStyle);
		formater.setLenient(false);
		formaters.put("DEFAULT_STYLE", formater);
	}

	/**
	 * 使用默认风格 格式化当前时间
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String formatDate() {
		return formatDate(new Date());
	}

	/**
	 * 使用指定风格 格式化当前时间
	 * 
	 * @param dateStyle
	 *            要显示的日期风格, 类似: yyyy-MM-dd
	 * @return
	 * @throws Exception
	 */
	public static String formatDate(String dateStyle) {
		return formatDate(dateStyle, new Date());
	}

	/**
	 * 使用默认风格 格式化指定日期对象
	 * 
	 * @param date
	 *            要格式化显示的日期对象
	 * @return
	 * @throws Exception
	 */
	public static String formatDate(Date date) {
		SimpleDateFormat formater = formaters.get("DEFAULT_STYLE");
		if (formater == null)
			throw new RuntimeException("未找到默认的日期格式化对象");

		synchronized (formater) {
			return formater.format(date);
		}
	}

	/**
	 * 使用指定风格 格式化指定的日期对象
	 * 
	 * @param dateStyle
	 *            要显示的日期风格, 类似: yyyy-MM-dd
	 * @param date
	 *            要格式化的日期
	 * @return
	 */
	public static String formatDate(String dateStyle, Date date) {
		SimpleDateFormat formater = formaters.get(dateStyle);
		if (formater == null) {
			formater = new SimpleDateFormat(dateStyle);
			formaters.put(dateStyle, formater);
		}

		// TODO 此种方法对性能不友好, 有时间了改成ThreadLocal形式
		synchronized (formater) {
			return formater.format(date);
		}
	}

	/**
	 * 使用默认风格 转换指定的时间字符串为Date对象
	 * 
	 * @param dateStr
	 *            要转换的时间字符串
	 * @return
	 * @throws ParseException
	 * @throws Exception
	 */
	public static Date parseDate(String dateStr) throws ParseException {
		SimpleDateFormat formater = formaters.get("DEFAULT_STYLE");
		if (formater == null)
			throw new RuntimeException("未找到默认的日期格式化对象");

		synchronized (formater) {
			return formater.parse(dateStr);
		}
	}

	/**
	 * 使用指定的风格 转换指定的时间字符串为Date对象
	 * 
	 * @param dateStr
	 *            要转换的时间字符串
	 * @param dateStyle
	 *            要使用的转换样式格式, 例如: yyyy-MM-dd
	 * @return
	 * @throws ParseException
	 * @throws Exception
	 */
	public static Date parseDate(String dateStr, String dateStyle) throws ParseException {
		SimpleDateFormat formater = formaters.get(dateStyle);
		if (formater == null) {
			formater = new SimpleDateFormat(dateStyle);
			formater.setLenient(false);
			formaters.put(dateStyle, formater);
		}

		// TODO 此种方法对性能不友好, 有时间了改成ThreadLocal形式
		synchronized (formater) {
			return formater.parse(dateStr);
		}
	}

}
