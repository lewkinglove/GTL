package cn.lewkinglove.api.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import api.ServiceApiCacheUtil;
import cn.lewkinglove.common.ConfigPool;
import cn.lewkinglove.gtl.task.ScheduleBootstrap;

/**
 * 应用启动监听器 
 * @author liujing(lewkinglove@gmail.com)
 */
@WebListener("ServletContextListener")
public class ApplicationListener implements ServletContextListener{

	@Override
    public void contextInitialized(ServletContextEvent sce) {
	    try {
	        ConfigPool.init();
	        
	        ServiceApiCacheUtil.initAuthCache();
	        
	        ScheduleBootstrap.startup();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

	@Override
    public void contextDestroyed(ServletContextEvent sce) {
		ScheduleBootstrap.shutdown();
    }

}
