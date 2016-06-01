package com.tionsoft.mas.tas.queue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.stereotype.Component;

import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.queue.AbstractRequestQueue;
import com.tionsoft.platform.queue.RequestQueue;
import com.tionsoft.platform.queue.bean.QueueItem;


/**
 * Tas backgroup task request queue
 * @author Administrator
 *
 */
public class TasBackgroundQueue extends AbstractRequestQueue implements RequestQueue {

	private final Queue<QueueItem> tasBackgroundRequestQueue = new ConcurrentLinkedQueue<QueueItem>();
	private static TasBackgroundQueue queue;

	private TasBackgroundQueue(int acceptableQueueSize) {
		super(acceptableQueueSize);
	}

	@Override
	public void notifyService() {
		tasBackgroundRequestQueue.notify();

	}

	@Override
	public void notifyAllService() {
		tasBackgroundRequestQueue.notifyAll();

	}

	@Override
	public void waitService() throws InterruptedException {
		tasBackgroundRequestQueue.wait();
	}

	@Override
	public long getNumberOfServiceProcessed() {
		return super.getQueueItemServiced();
	}

	@Override
	public void reset() {
		super.reset();
	}

	@Override
	public boolean checkQueueSize() {
		return super.checkQueueSize(getRemainQueueSize());
	}

	@Override
	public QueueItem getQueueItemRequest() throws PlatformException {
		try {
			return tasBackgroundRequestQueue.poll();

		} catch (IndexOutOfBoundsException e) {

			throw PlatformException.createException(ErrorCode.TION_COMMON_ERROR.QUEUEITEM, ErrorCode.TION_COMMON_CODE.QUEUE_ITEM_PUT_INDEX, e);

		} catch (Exception e) {
			throw PlatformException.createException(ErrorCode.TION_COMMON_ERROR.QUEUEITEM, ErrorCode.TION_COMMON_CODE.QUEUE_ITEM_GET, e);
		}
	}

	@Override
	public Queue<QueueItem> getQueueItemRequestQueue() throws PlatformException {

		return tasBackgroundRequestQueue;
	}

	@Override
	public void putQueueItemRequest(QueueItem message) throws PlatformException {
		try {
			tasBackgroundRequestQueue.add(message);

		} catch (Exception e) {
			throw PlatformException.createException(ErrorCode.TION_COMMON_ERROR.QUEUEITEM, ErrorCode.TION_COMMON_CODE.QUEUE_ITEM_PUT, e);
		}

	}

	@Override
	public int getRemainQueueSize() {
		return tasBackgroundRequestQueue.size();
	}
	
	
	public static synchronized TasBackgroundQueue getInstance(int acceptableQueueSize)
	{
		if(queue == null)
		{
			queue = new TasBackgroundQueue(acceptableQueueSize);
		}
		
		return queue;
		
	}
	
}
