package com.tionsoft.mas.tas.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.StopWatch;



public class MessageStopWatchRepository {
	
	private final Map<String,StopWatch> messageRepository =  new ConcurrentHashMap<String, StopWatch>();
	
	
	public void removeRequestMessageInfo(String taskId)
	{
		messageRepository.remove(taskId);
	}
	
	public Map<String,StopWatch> getMessageRepo()
	{
		return messageRepository;
	}
	
	
	public void requestMessageInfo(String taskId)
	{
		StopWatch watch = new StopWatch();
		watch.start();
		
		if(!messageRepository.containsKey(taskId))	messageRepository.put(taskId, watch);
		
	}

}
