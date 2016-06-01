package com.tionsoft.mas.tas.bean.platform;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;

import com.tionsoft.mas.tas.exception.TasException;

public final class PH101 extends PlatformHeader {
	
	private static final long serialVersionUID = 5260292568002918321L;
	
	public PH101() {
		super();
		setValue(VERSION, "PHV101");
	}
	
	@Override
	public void decode(ByteBuffer buffer) {
		this.buffer = buffer;
		setVersion(true);
		setApplicationId(true);
		setMessageId(true);
		setSessionId(true);
		setTransactionId(true);
		setServiceId(true);
		setImei(true);
		setMsisdn(true);
		setBodyType(true);
		setStatusCode(true);
		setHeaderLength(true);
		setBodyLength(true);
	}
	
	@Override
	public void decode(FileChannel fc) {
		this.fc = fc;
		setVersion(false);
		setApplicationId(false);
		setMessageId(false);
		setSessionId(false);
		setTransactionId(false);
		setServiceId(false);
		setImei(false);
		setMsisdn(false);
		setBodyType(false);
		setStatusCode(false);
		setHeaderLength(false);
		setBodyLength(false);
	}
	
	private void setVersion(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[VERSION_LENGTH];
			buffer.get(buf);
			setValue(VERSION, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(VERSION_LENGTH);
				fc.read(buf);
				setValue(VERSION, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
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
	
	private void setTransactionId(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			setValue(TRANSACTION_ID, buffer.getLong());
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(8);
				fc.read(buf);
				buf.flip();
				setValue(TRANSACTION_ID, buf.getLong());
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setServiceId(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			setValue(SERVICE_ID, buffer.getInt());
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(4);
				fc.read(buf);
				buf.flip();
				setValue(SERVICE_ID, buf.getInt());
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setImei(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[IMEI_LENGTH];
			buffer.get(buf);
			setValue(IMEI, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(IMEI_LENGTH);
				fc.read(buf);
				setValue(IMEI, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setMsisdn(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[MSISDN_LENGTH];
			buffer.get(buf);
			setValue(MSISDN, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(MSISDN_LENGTH);
				fc.read(buf);
				setValue(MSISDN, new String(buf.array()));
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
	
	/**
	 * copy an object deeply so that data store area is unique
	 * @return
	 */
	@Override
	public PlatformHeader deepCopy()
	{
		PH101 bean = new PH101();
		
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
		PH101 bean = new PH101();
		
		return bean;
	}
	
	@Override
	public int getLength() {//82
		return VERSION_LENGTH 
		+ APPLICATION_ID_LENGTH 
		+ MESSAGE_ID_LENGTH
		+ SESSION_ID_LENGTH
		+ TRANSACTION_ID_LENGTH
		+ SERVICE_ID_LENGTH
		+ IMEI_LENGTH
		+ MSISDN_LENGTH
		+ BODY_TYPE_LENGTH
		+ STATUS_CODE_LENGTH
		+ HEADER_LENGTH_LENGTH 
		+ BODY_LENGTH_LENGTH;
	}

}