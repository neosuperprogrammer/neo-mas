package com.tionsoft.mas.tas.taslet;

import java.io.Serializable;

import com.tionsoft.platform.queue.bean.QueueItem;
/**
 * TasRequest,TasResponse 관리함 .  
 * @version : 1.0.0
 * 
 */
public class TasMessage implements Serializable,QueueItem {
	
	private static final long serialVersionUID = 4535008563877917554L;
	
	private final TasRequest tasRequest;
	private final TasResponse tasResponse;
	private TasPushResponse tasPushResponse = null;
	private MessageDirection direction = MessageDirection.RECEIVED;
	
	public static enum MessageDirection {
		RECEIVED,SENT
	}
	
	/**
	  * TasMessage 생성자.
	  * @param tasRequest - 요청 메세지.  
	  * @param tasResponse - 응답 메세지.  
	  */
	public TasMessage(TasRequest tasRequest, TasResponse tasResponse) {
		this.tasRequest = tasRequest;
		this.tasResponse = tasResponse;
	}
	
	/**
	  * TasMessage 생성자.
	  * @param tasRequest - 요청 메세지.  
	  * @param tasResponse - 응답 메세지.  
	  */
	public TasMessage(TasRequest tasRequest, TasResponse tasResponse, MessageDirection direction) {
		this.tasRequest = tasRequest;
		this.tasResponse = tasResponse;
		this.direction = direction;
	}
	
	/**
	  * tasRequest 메세지 요청.
	  */
	public TasRequest getTasRequest() {
		return tasRequest;
	}

	/**
	  * tasResponse 메세지 요청.
	  */
	public TasResponse getTasResponse() {
		return tasResponse;
	}
	
	public void setTasPushResponse(TasPushResponse tasPushResponse) {
		this.tasPushResponse = tasPushResponse;
	}
	
	public TasPushResponse getTasPushResponse() {
		return tasPushResponse;
	}
	
	public MessageDirection getMessageDirection()
	{
		return direction;
				
	}
	
	public void setMessageDirection(MessageDirection direction)
	{
		this.direction = direction;
	}
	
}