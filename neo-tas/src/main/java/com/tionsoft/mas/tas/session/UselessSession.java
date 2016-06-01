package com.tionsoft.mas.tas.session;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.core.session.IoSession;

public class UselessSession {

	private Queue<IoSession> uselessSessionMap = new ConcurrentLinkedQueue<IoSession>();
	
	
	public void putUselessSession(IoSession session)
	{
		uselessSessionMap.add(session);
	}
	
	
	
	public Queue<IoSession> getUselessSessions()
	{
		Queue<IoSession> temp = new ConcurrentLinkedQueue<IoSession>();
		
		IoSession session = null;
		
		while(! uselessSessionMap.isEmpty())
		{
			session = uselessSessionMap.poll();
			temp.add(session);
			
		}
		
		return temp;
		
	}
	
}
