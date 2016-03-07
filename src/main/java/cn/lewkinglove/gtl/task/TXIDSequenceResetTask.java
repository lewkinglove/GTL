package cn.lewkinglove.gtl.task;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import cn.lewkinglove.gtl.vo.GTLTransaction;

/**
 * 定时任务, 每分钟重置一次GTL的事务序列号(TXID) 
 * @author liujing(lewkinglove@gmail.com)
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class TXIDSequenceResetTask implements Job {

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
		GTLTransaction.resetTXIDSequence();
    }

}
