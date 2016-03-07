package cn.lewkinglove.gtl.task;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import cn.lewkinglove.common.util.LogUtil;

/**
 * 调度启动器
 * @author liujing(lewkinglove@gmail.com)
 */
public class ScheduleBootstrap {
	private static Scheduler scheduler = null;

	public static void shutdown() {
		try {
			ScheduleBootstrap.scheduler.shutdown(true);
		} catch (Exception ex) {
			LogUtil.error(ScheduleBootstrap.class, ex.getMessage(), ex);
		}
	}

	public static void startup() {
		try {
			LogUtil.info(ScheduleBootstrap.class, "开始执行调度任务... ...");
			ScheduleBootstrap.scheduler = new StdSchedulerFactory().getScheduler();
			
			//GTLConfig dianTouConfig = GTLConfig.getInstance();
			//String txTimeoutCronStr = dianTouConfig.getProperty("config.txTimeoutCronStr", "* * */1 * * ? 2015-2020");
			
			String TXCheckAndCleanCronStr = "*/1 * * * * ? 2015-2099";
			Trigger TXCheckAndCleanTrigger = TriggerBuilder.newTrigger().withIdentity("TXCheckAndCleanTrigger", "triggerGroup")
					.withSchedule(CronScheduleBuilder.cronSchedule(TXCheckAndCleanCronStr)).build();
			JobDetail TXCheckAndCleanJob = JobBuilder.newJob(TXCheckAndCleanTask.class).withIdentity("TXCheckAndCleanJob", "jobGroup").build();
			ScheduleBootstrap.scheduler.scheduleJob(TXCheckAndCleanJob, TXCheckAndCleanTrigger);
			
			String TXIDSequenceResetCronStr = "0 */1 * * * ? 2015-2099";
			Trigger TXIDSequenceResetTrigger = TriggerBuilder.newTrigger().withIdentity("TXIDSequenceResetTrigger", "triggerGroup")
					.withSchedule(CronScheduleBuilder.cronSchedule(TXIDSequenceResetCronStr)).build();
			JobDetail TXIDSequenceResetJob = JobBuilder.newJob(TXIDSequenceResetTask.class).withIdentity("TXIDSequenceResetJob", "jobGroup").build();
			ScheduleBootstrap.scheduler.scheduleJob(TXIDSequenceResetJob, TXIDSequenceResetTrigger);
			
			ScheduleBootstrap.scheduler.start();
		} catch (Exception ex) {
			LogUtil.error(ScheduleBootstrap.class, ex.getMessage(), ex);
		}
	}
}
