package cn.lewkinglove.common.util;

import java.text.MessageFormat;

import cn.lewkinglove.common.ConfigPool;

public final class MessageUtil {

	private MessageUtil() {
	}
	
	public static String getMessage(String msgKey){
		//message.{$msgKey}
		return getMessageByKey(msgKey);	
	}
	
	/**
	 * 根据消息的完全限定名获取消息文本 带参数占位符
	 * @param msgKey
	 * @param formatArgs
	 * @return
	 */
	public static String getMessage(String msgKey, Object[] formatArgs){
		//message.{$msgKey}
		return getMessageByKey(msgKey,formatArgs);	
	}
	
	/**
	 * 根据业务标识符和消息键名获取消息文本
	 * @param bizIdentifier	业务标识符
	 * @param msgKey 消息键名
	 * @return
	 */
	public static String getMessage(String bizIdentifier, String msgKey){
		//message.{$bizIdentifier}.{$msgKey}
		return getMessageByKey(bizIdentifier+"."+msgKey);
	}
	
	/**
	 * 根据业务标识符和消息键名获取消息文本 带参数占位符
	 * @param bizIdentifier
	 * @param msgKey
	 * @param formatArgs
	 * @return
	 */
	public static String getMessage(String bizIdentifier, String msgKey, Object[] formatArgs){
		//message.{$bizIdentifier}.{$msgKey}
		return getMessageByKey(bizIdentifier+"."+msgKey, formatArgs);
	}
	
	/**
	 * 根据消息的完全限定名(不包括message.部分)获取消息文本 带参数占位符
	 * @param msgKey
	 * @param formatAg
	 * @return
	 */
	private static String getMessageByKey(String msgKey, Object[] formatAg){
		//messsage.{$msgKey}
		String message = getMessageByKey(msgKey);
		if(message == null)
			return message;
		return MessageFormat.format(message, formatAg);
	}
	
	/**
	 * 根据消息的完全限定名(不包括message.部分)获取消息文本
	 * @param msgKey 
	 * @return
	 */
	private static String getMessageByKey(String msgKey){
		//messsage.{$msgKey}
		return ConfigPool.getInstance().getProperty("message." + msgKey);
	}
}
