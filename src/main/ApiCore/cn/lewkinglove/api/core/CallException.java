package cn.lewkinglove.api.core;

import cn.lewkinglove.common.util.MessageUtil;

/**
 * Api通用异常类
 * 
 * @author liujing(lewkinglove@gmail.com)
 */
public class CallException extends java.lang.Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 异常状态码
	 */
	private String code = "0";

	/**
	 * 新建异常实例
	 * 
	 * @param bizIdentifier
	 *            业务标识符(需和message.properties中匹配)
	 * @param code
	 *            异常码
	 */
	public CallException(String bizIdentifier, String code) {
		super(MessageUtil.getMessage(bizIdentifier, code));
		this.code = code;
	}

	/**
	 * 新建异常实例
	 * 
	 * @param bizIdentifier
	 *            业务标识符(需和message.properties中匹配)
	 * @param code
	 *            异常码
	 * @param formatArgs
	 *            参数占位符
	 */
	public CallException(String bizIdentifier, String code, Object[] formatArgs) {
		super(MessageUtil.getMessage(bizIdentifier, code, formatArgs));
		this.code = code;
	}

	/**
	 * 新建异常实例
	 * 
	 * @param code
	 *            异常码
	 * @param message
	 *            异常消息
	 * @param cause
	 *            上级异常对象
	 */
	public CallException(String code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	/**
	 * 获取异常码
	 * 
	 * @return
	 */
	public String getCode() {
		return this.code;
	}
}
