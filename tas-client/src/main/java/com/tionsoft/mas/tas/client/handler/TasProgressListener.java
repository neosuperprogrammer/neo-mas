
package com.tionsoft.mas.tas.client.handler;

public interface TasProgressListener {

	public void onMessageReceived(int totalMessageLengthToReceive, int receivedMessageCount);
	public void onMessageSent(int totalMessageLengthToSend, int sentMessageCount);
		
}