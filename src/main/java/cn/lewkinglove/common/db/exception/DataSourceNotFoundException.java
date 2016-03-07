package cn.lewkinglove.common.db.exception;

/**
 * 数据源未找到异常
 * @author liujing(lewkinglove@gmail.com)
 */
public class DataSourceNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public DataSourceNotFoundException(String message){
    	super(message);
    }
}
