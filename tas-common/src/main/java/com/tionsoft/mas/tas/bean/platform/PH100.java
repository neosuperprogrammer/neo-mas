package com.tionsoft.mas.tas.bean.platform;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;

import com.tionsoft.mas.tas.exception.TasException;

public final class PH100 extends PlatformHeader {
	
	private static final long serialVersionUID = -5389151271905150377L;

	public PH100() {
		super();
		setValue(VERSION, "PHV100");
	}
	
	@Override
	public void decode(ByteBuffer buffer) {
		this.buffer = buffer;
		setApplicationId(true);
		setMessageId(true);
		setSessionId(true);
		setBodyType(true);
		setStatusCode(true);
		setHeaderLength(true);
		setBodyLength(true);
	}
	
	@Override
	public void decode(FileChannel fc) {
		this.fc = fc;
		setApplicationId(false);
		setMessageId(false);
		setSessionId(false);
		setBodyType(false);
		setStatusCode(false);
		setHeaderLength(false);
		setBodyLength(false);
	}
	
	private void setApplicationId(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[APPLICATION_ID_LENGTH];
			buffer.get(buf);
			setValue(APPLICATION_ID, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(APPLICATION_ID_LENGTH);
				fc.read(buf);
				setValue(APPLICATION_ID, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setMessageId(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[MESSAGE_ID_LENGTH];
			buffer.get(buf);
			setValue(MESSAGE_ID, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(MESSAGE_ID_LENGTH);
				fc.read(buf);
				setValue(MESSAGE_ID, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setSessionId(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			setValue(SESSION_ID, buffer.getLong());
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(8);
				fc.read(buf);
				buf.flip();
				setValue(SESSION_ID, buf.getLong());
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setBodyType(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			setValue(BODY_TYPE, buffer.getShort());
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(2);
				fc.read(buf);
				buf.flip();
				setValue(BODY_TYPE, buf.getShort());
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
		
	}
	
	private void setStatusCode(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			setValue(STATUS_CODE, buffer.getShort());
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(2);
				fc.read(buf);
				buf.flip();
				setValue(STATUS_CODE, buf.getShort());
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
		
	}
	
	private void setHeaderLength(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			setValue(HEADER_LENGTH, buffer.getInt());
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(4);
				fc.read(buf);
				buf.flip();
				setValue(HEADER_LENGTH, buf.getInt());
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
		
	}
	
	private void setBodyLength(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			setValue(BODY_LENGTH, buffer.getInt());
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(4);
				fc.read(buf);
				buf.flip();
				setValue(BODY_LENGTH, buf.getInt());
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	@Override
	public int getLength() {
		return APPLICATION_ID_LENGTH 
		+ MESSAGE_ID_LENGTH
		+ SESSION_ID_LENGTH
		+ BODY_TYPE_LENGTH
		+ STATUS_CODE_LENGTH
		+ HEADER_LENGTH_LENGTH 
		+ BODY_LENGTH_LENGTH;
	}
	
	/**
	 * copy an object deeply so that data store area is unique
	 * @return
	 */
	@Override
	public PlatformHeader deepCopy()
	{
		PH100 bean = new PH100();
		
		Iterator<String> itr = this.getParamNames().iterator();
		
		String key = "";
		
		while(itr.hasNext())
		{
			key = itr.next();
			bean.setValue(key, this.getValue(key));
		}
		
		return bean;
	}
	
	/**
	 * copy an empty instance
	 * @return
	 */
	@Override
	public PlatformHeader structureCopy()
	{
		PH100 bean = new PH100();
		
		return bean;
	}
	
	

}