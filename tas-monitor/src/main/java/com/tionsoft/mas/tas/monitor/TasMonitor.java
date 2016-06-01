package com.tionsoft.mas.tas.monitor;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Resource;

import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.tionsoft.mas.tas.repository.MessageStopWatchRepository;
import com.tionsoft.platform.utils.DateUtil;



@Component
public class TasMonitor {

	private static Logger logger = LoggerFactory.getLogger(TasMonitor.class);
	@Resource
	private ExecutorFilter filter;
	
	@Autowired(required=false)
	private MessageStopWatchRepository messageRepository;
	
	
	@Scheduled(fixedDelay=1000)
	public void start()
	{
		 Executor executor=filter.getExecutor();
		 
		 
		 Map<String,StopWatch> messageRepo=null;
		 if(messageRepository!=null){
			 messageRepo = messageRepository.getMessageRepo();
		 }
		
		 
		 printOut(executor);
		 if(messageRepo!=null) printOut(messageRepo);
		 
	}
	
	
	public void printOut(Executor executor)
	{
		OrderedThreadPoolExecutor orderExecutor = (OrderedThreadPoolExecutor) executor;
		
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>tas monitoring log start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>executing time:"+DateUtil.getCurrentTimeMilli("yyyy-MM-dd HH:mm:ss SSS")+">>>>>>>>>>>>>>>>>>>>");
		logger.debug("**active thread count :"+orderExecutor.getActiveCount());
		logger.debug("**completed task count :"+orderExecutor.getTaskCount());
	}
	
	public void printOut(Map<String,StopWatch> messageRepo)
	{
		Iterator<String> itr = messageRepo.keySet().iterator();
		
		String taskId;
		StopWatch watch;
		while(itr.hasNext())
		{
			taskId=itr.next();
			watch = messageRepo.get(taskId);
			if(watch!=null){
				if(watch.isRunning()) watch.stop();
				logger.debug("**task Id :"+taskId+":running time = "+watch.getTotalTimeSeconds());
				
				if(messageRepo.containsKey(taskId)){
					watch.start();
				}else {
					watch=null;
				}
			}
			
		}
		
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>tas monitoring log end>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}
}
