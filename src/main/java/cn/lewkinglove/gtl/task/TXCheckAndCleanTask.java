package cn.lewkinglove.gtl.task;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import cn.lewkinglove.gtl.GTLTransactionPool;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class TXCheckAndCleanTask implements Job {

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		GTLTransactionPool.checkTXPoolAndClean();		
    }

}
