package cn.lewkinglove.common.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

/**
 * 日志工具类
 * @author liujing(lewkinglove@gmail.com)
 */
public class LogUtil {

	/**
	 * 使用debug级别打印日志
	 * 
	 * @param source
	 *            打印日志的类
	 * @param msg
	 *            日志内容
	 */
	public static <T> void debug(Class<T> source, String msg) {
		LogManager.getLogger(source).debug(msg);
	}

	/**
	 * 使用debug级别打印日志
	 * 
	 * @param source
	 *            打印日志的类
	 * @param msg
	 *            日志内容
	 * @param ex
	 *            错误信息
	 */
	public static <T> void debug(Class<T> source, String msg, Throwable ex) {
		LogManager.getLogger(source).debug(msg, ex);
	}

	/**
	 * 使用info级别打印日志
	 * 
	 * @param source
	 * @param msg
	 */
	public static <T> void info(Class<T> source, String msg) {
		LogManager.getLogger(source).info(msg);
	}

	/**
	 * 使用info级别打印日志
	 * 
	 * @param source
	 * @param msg
	 * @param ex
	 */
	public static <T> void info(Class<T> source, String msg, Throwable ex) {
		LogManager.getLogger(source).info(msg, ex);
	}

	/**
	 * 使用warn级别打印日志
	 * 
	 * @param source
	 * @param msg
	 */
	public static <T> void warn(Class<T> source, String msg) {
		LogManager.getLogger(source).warn(msg);
	}

	/**
	 * 使用warn级别打印日志
	 * 
	 * @param source
	 * @param msg
	 * @param ex
	 */
	public static <T> void warn(Class<T> source, String msg, Throwable ex) {
		LogManager.getLogger(source).warn(msg, ex);
	}

	/**
	 * 使用error级别打印日志
	 * 
	 * @param source
	 * @param msg
	 */
	public static <T> void error(Class<T> source, String msg) {
		LogManager.getLogger(source).error(msg);
	}

	/**
	 * 使用error级别打印日志
	 * 
	 * @param source
	 * @param msg
	 * @param ex
	 */
	public static <T> void error(Class<T> source, String msg, Throwable ex) {
		LogManager.getLogger(source).error(msg, ex);
	}

	/**
	 * 使用fatal级别打印日志
	 * 
	 * @param source
	 * @param msg
	 */
	public static <T> void fatal(Class<T> source, String msg) {
		LogManager.getLogger(source).fatal(msg);
	}

	/**
	 * 使用fatal级别打印日志
	 * 
	 * @param source
	 * @param msg
	 * @param ex
	 */
	public static <T> void fatal(Class<T> source, String msg, Throwable ex) {
		LogManager.getLogger(source).fatal(msg, ex);
	}

	/**
	 * 自定义日志级别,打印日志
	 * 
	 * @param level
	 *            日志级别
	 * @param source
	 *            打印日志类
	 * @param message
	 *            日志内容
	 */
	public static <T> void log(Level level, Class<T> source, String message) {
		LogManager.getLogger(source).log(level, message);
	}

	/**
	 * 自定义日志级别,打印日志
	 * 
	 * @param level
	 *            日志级别
	 * @param source
	 *            打印日志类
	 * @param msg
	 *            日志内容
	 * @param ex
	 *            错误信息
	 */
	public static <T> void log(Level level, Class<T> source, String msg, Throwable ex) {
		LogManager.getLogger(source).log(level, msg, ex);
	}

}
