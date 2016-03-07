package api;

//@formatter:off
/**
 * 所有对外提供服务的ServiceApi必须全部置于当前package下面.
 * 目录结构为:
 * 	api			#ServiceApi根目录
 * 		module			#Api模块目录, 根据功能和面向的客户端来细分模块
 * 			class		#具体的Api服务类 
 * 			……
 * 		……
 * 所有包含ServiceApi服务的Package必须
 * 		1.	包含package-info.java的包描述文件
 * 		2.	package必须使用@ServiceApiProvider来进行标记
 * 
 * 所有包含ServiceApi服务的Class必须
 * 		1. 类声明的时候必须使用@ServiceApiProvider来进行标记
 * 
 * 所有提供ServiceApi服务的Method必须
 * 		1. 方法声明的时候必须使用@ServiceApiProvider来进行标记
 * 
 * 以上package/class/method如果不使用@ServiceApiProvider进行标记, 则
 * 客户端在进行调用对应或其下相关ServiceApi时会被阻止.
 * 
 **/
// @formatter:on