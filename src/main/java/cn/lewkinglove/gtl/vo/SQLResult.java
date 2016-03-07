package cn.lewkinglove.gtl.vo;

/**
 * 基础SQL执行结果
 * @author liujing(lewkinglove@gmail.com)
 */
public class SQLResult {
	public static final String ERROR_CODE_OK = "0";

	private String errorCode = ERROR_CODE_OK;

	private String errorMessage = "";

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
