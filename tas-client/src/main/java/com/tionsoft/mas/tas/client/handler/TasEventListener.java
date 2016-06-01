
package com.tionsoft.mas.tas.client.handler;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.tionsoft.mas.tas.client.message.TasMessage;

public interface TasEventListener {

	public void onSessionCreated(IoSession session);
	public void onSessionOpened(IoSession session);
	public void onSessionClosed(IoSession session);
	public void onSessionIdle(IoSession session, IdleStatus status);
	public void onMessageReceived(IoSession session, TasMessage message);
	public void onMessageSent(IoSession session, TasMessage message);
	public void onExceptionCaught(IoSession session, Throwable cause);
		
}