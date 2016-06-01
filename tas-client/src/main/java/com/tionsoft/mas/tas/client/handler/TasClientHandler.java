package com.tionsoft.mas.tas.client.handler;

import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.DefaultFileRegion;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.tionsoft.mas.tas.protocol.codec.encoder.ByteBufferEncoder;
import com.tionsoft.mas.tas.client.codec.TasClientCodecFactory;
import com.tionsoft.mas.tas.client.message.TasMessage;

public class TasClientHandler implements IoHandler {
	
	private TasEventListener eventListener = null;
	private TasProgressListener progressListener = null; // 송신 Progress Listener
	
	public TasClientHandler() {
		
	}
	
	public void setProgressListener(TasProgressListener progressListener) {
		this.progressListener = progressListener;
	}
	
	public void setEventListener(TasEventListener eventListener) {
		this.eventListener = eventListener;
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		if(eventListener != null) eventListener.onSessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) {
		if(eventListener != null) eventListener.onSessionOpened(session);
	}

	@Override
	public void sessionClosed(IoSession session) {
		closeTemporaryFileIfRequired(session);
		if(eventListener != null) eventListener.onSessionClosed(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		if(eventListener != null) eventListener.onSessionIdle(session, status);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if(eventListener != null) {
			TasMessage tasMessage = (TasMessage)message;
			eventListener.onMessageReceived(session, tasMessage);
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		int totalMessageLengthToSend = 0;
		int sentMessageCount = 0;
		
		if(session.getAttribute(ByteBufferEncoder.TOTAL_MESSAGE_SIZE) != null) {
			totalMessageLengthToSend = (Integer)session.getAttribute(ByteBufferEncoder.TOTAL_MESSAGE_SIZE);
		}
		
		if(session.getAttribute(ByteBufferEncoder.SENT_MESSAGE_COUNT) != null) {
			sentMessageCount = (Integer)session.getAttribute(ByteBufferEncoder.SENT_MESSAGE_COUNT);
		}
		
		if(message instanceof IoBuffer) {
			IoBuffer buffer = (IoBuffer)message;
			session.setAttribute(ByteBufferEncoder.SENT_MESSAGE_COUNT, sentMessageCount + buffer.limit());
			if(progressListener != null) {
				progressListener.onMessageSent(totalMessageLengthToSend, sentMessageCount + buffer.limit());
			}
		} else if (message instanceof DefaultFileRegion) {
			DefaultFileRegion fileRegion = (DefaultFileRegion)message;
			session.setAttribute(ByteBufferEncoder.SENT_MESSAGE_COUNT, sentMessageCount + (int)fileRegion.getWrittenBytes());
		} else if(message instanceof TasMessage) {
			if(eventListener != null) {
				TasMessage tasMessage = (TasMessage)message;
				eventListener.onMessageSent(session, tasMessage);
			}
		}
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		closeTemporaryFileIfRequired(session);
		if(eventListener != null) eventListener.onExceptionCaught(session, cause);
	}
	
	private final void closeTemporaryFileIfRequired(IoSession session) {
		if(session.getAttribute(TasClientCodecFactory.TEMP_FILE_CHANNEL) != null) {
			FileChannel fc = (FileChannel)session.getAttribute(TasClientCodecFactory.TEMP_FILE_CHANNEL);
			try {
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}