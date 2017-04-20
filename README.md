GTL(Global Transaction Layer)
============================================================
一、需求目标:
-------------------------------------------------------
> 1. 提供数据库操作代理  
> 2. 提供可共享的持续性的事务操作  
> 3. 多数据源支持  
> 4. 数据源连接池支持  
> 5. 数据库读写分离支持  
> 6. 支持异构系统/分布式系统调用  
> 7. 长时间未提交事务, 自动超时回滚  
> 8. 支持事务嵌套使用  


二、协议说明:
-------------------------------------------------------
###交互协议:  
> HTTP(s)　　　　**备注: 后期将支持Socket协议**

###数据交换格式:  
> JSON　　　　　**二期使用MsgPack后以二进制流传输**

###请求协议:
    HOST:   http(s)://gtl.example.com
    METHOD: POST
    Entry:  /api
    请求参数: 
        call={#接口名称, 如: beginTransaction}
        args={#接口参数集合, 要求必须为JSON. 如: {dsId: 'peizi'}}
        ua={#请求方UA,用来识别用户身份. 如: ios_client_ua}
        sign={#请求签名数据,签名规则见[签名生成算法].}

###请求示例
> http(s)://gtl.example.com/api?call=beginTransaction&args={dsId: 'peizi'}&ua=ios_client_ua&sign=asdflsadjflkjsafdljlaskjfdl

###签名生成算法
> const API_UA = "ios_client_ua";  
> const API_SECKEY = API_UA."分配给子系统的签名密钥".API_UA;  
> $sign = md5( API_SECKEY . call . API_SECKEY . args . API_SECKEY  );

###响应标准:
    data            接口返回数据. 默认为 null. 根据业务不同可以为数组 或 其他任意对象
    status          接口执行状态; 默认为1: 成功;  失败则为负值; 
    message         接口返回的消息, 默认为: success; 如果接口不成功, 则消息为对应状态的错误描述


三、接口列表:
-------------------------------------------------------
###beginTransaction        开始事务

######接口参数:  
> dsId      &emsp;&emsp;       [可选参数]数据源编号, 当有txId的参数时, 此项可不传  
> timeout   &ensp;             [可选参数]指定事务超时时间, 如果为指定, 则使用全局设定  
> txId      &emsp;&emsp;       [可选参数]事务编号; 如果已经有了一个已开启的事务, 则直接把编号发过来, 这边会选中这个已开启的事务;  

######返回值:  
> 开始的事务编号, 可用于其他操作复用  

######可能的错误:  
> 01      未找到进行中的指定事务编号(txId)的事务  

***

###commitTransaction        提交事务

######接口参数:  
> txId      &emsp;&emsp;       事务编号  

######返回值:  
> 布尔型; 成功或者失败  

######可能的错误:  
> 01      未找到进行中的指定事务编号(txId)的事务  

***

###rollbackTransaction     回滚事务

######接口参数:  
> txId      &emsp;&emsp;       事务编号  

######返回值:  
> 布尔型; 成功或者失败  

######可能的错误:  
> 01      未找到进行中的指定事务编号(txId)的事务  

***

###executeQuery            执行SQL查询(事务环境内)

######接口参数:  
> txId      &emsp;&emsp;            事务编号  
> sql       &emsp;&emsp;&ensp;      要执行的SQL语句; 形如: SELECT * FROM User WHERE uname='liujng' AND upwd='123123'  

######返回值:  
> Table List Or Row Array, 形如[{rowdata...},{rowdata...}]  

######可能的错误:  
> 01      未找到进行中的指定事务编号(txId)的事务  

***

###executePreparedQuery    执行预编译的SQL查询(事务环境内)

######接口参数:  
> txId      &emsp;&emsp;            事务编号  
> sql       &emsp;&emsp;&ensp;      要执行的预编译SQL语句; 形如: SELECT * FROM User WHERE uname=? AND upwd=?  
> sqlArgs   &ensp;                  key-value paris, 预编译SQL的参数; 形如: ['liujing','123123']  

######返回值:  
> Table List Or Row Array, 形如[{rowdata...},{rowdata...}]  

######可能的错误:  
> 01      未找到进行中的指定事务编号(txId)的事务  

***

###executeNonQuery         执行非查询类SQL(事务环境内)

######接口参数:  
> txId      &emsp;&emsp;          事务编号  
> sql       &emsp;&emsp;&ensp;    要执行的SQL语句; 形如: UPDATE User SET upwd='456456' WHERE uname='liujing'  

######返回值:  
> {affectedRows: 影响的行数, autoIncrementId: 自增编号}  

######可能的错误:  
> 01      未找到进行中的指定事务编号(txId)的事务  

***

###executePreparedNonQuery    执行预编译的非查询类SQL(事务环境内)

######接口参数:  
> txId      &emsp;&emsp;            事务编号  
> sql       &emsp;&emsp;&ensp;      要执行的预编译SQL语句; 形如: UPDATE User SET upwd=? WHERE uname=?  
> sqlArgs   &ensp;                  key-value paris, 预编译SQL的参数; 形如: ['456456', 'liujing']  

######返回值:  
> {affectedRows: 影响的行数, autoIncrementId: 自增编号}  

######可能的错误:  
> 01      未找到进行中的指定事务编号(txId)的事务  
