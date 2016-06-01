package com.tionsoft.mas.tas.queue.listener;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.queue.TasBackgroundQueue;
import com.tionsoft.platform.filter.FilterChain;
import com.tionsoft.platform.filter.TionsoftCommonFilterChain;
import com.tionsoft.platform.queue.bean.QueueItem;
import com.tionsoft.platform.queue.listener.QueueItemListener;

public class TasBackgroundQueueItemListener implements QueueItemListener {

	private TasBackgroundQueue queue;
	private boolean isWaited = false;
	private boolean active = true;
	private static final Logger logger = LoggerFactory.getLogger(TasBackgroundQueueItemListener.class);
	@Resource private TionsoftCommonFilterChain tionFilterChain;
	
	public TasBackgroundQueueItemListener(int acceptableQueueSize)
	{
		queue = TasBackgroundQueue.getInstance(acceptableQueueSize);
	}
	
	@Override
	public void run() {
		
		if(logger.isDebugEnabled()){ 
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			logger.debug("TasBackgroundQueueItemListener is started");
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
		
		QueueItem item = null;
		FilterChain filterChain=null;
		while(active)
		{
			try {
				synchronized (queue.getQueueItemRequestQueue()) {
					
					if(queue.getRemainQueueSize() > 0){
						item = queue.getQueueItemRequest();
					}else {
						
						item=null;
						isWaited=true;
						queue.waitService();
						isWaited=false;
					}
				}
				
				//==========================================================
				// with filter
				//==========================================================
				if(item != null && tionFilterChain.isFilter()){
					filterChain= tionFilterChain.getFilterChain();
					filterChain.doFilter(item);
				}
				
				
				
			}catch(Exception e) 
			{
				if(logger.isDebugEnabled()) e.printStackTrace();
				logger.error(e.getMessage());
			}
			
		}
		
		
		if(logger.isDebugEnabled()){ 
			logger.debug("TasBackgroundQueueItemListener["+Thread.currentThread().getName()+"] is died");
		}
		
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
		
		if(logger.isDebugEnabled()){ 
			logger.debug("TasBackgroundQueueItemListener["+Thread.currentThread().getName()+"] is set to" + active);
			logger.debug("TasBackgroundQueueItemListener["+Thread.currentThread().getName()+"] is died gracefully");
		}
	}

	@Override
	public boolean getActive() {
		
		return this.active;
		
	}

	@Override
	public boolean getWait() {
		return this.isWaited;
	}

}
