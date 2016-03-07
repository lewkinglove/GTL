package cn.lewkinglove.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于PO层,用来指明实体类及其字段对应的数据库映射名
 * @author liujing(lewkinglove@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.FIELD, ElementType.TYPE })
public @interface TableColumn {
	/**
	 * 代表数据库中的表名或者列名,必须为该注解赋值
	 * 
	 * @return 数据库表名或列名
	 */
	String value();
}
