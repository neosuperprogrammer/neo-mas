package com.tionsoft.mas.tas.bean.platform;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;

import com.tionsoft.mas.tas.exception.TasException;

public final class PH105 extends PlatformHeader {
	
	
	private static final long serialVersionUID = 539096284934851847L;

	public PH105() {
		super();
		setValue(VERSION, "PHV105");
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
		setWifiMac(true);
		setMsisdn(true);
		
		setModelNo(true);
		setIspName(true);
		setOsType(true);
		setOsVersion(true);
		setUuid(true);
		
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
		setWifiMac(false);
		
		setMsisdn(false);
		
		setModelNo(false);
		setIspName(false);
		setOsType(false);
		setOsVersion(false);
		setUuid(false);
		
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

	private void setWifiMac(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[WIFI_MAC2_LENGTH];
			buffer.get(buf);
			setValue(WIFI_MAC, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(WIFI_MAC2_LENGTH);
				fc.read(buf);
				setValue(WIFI_MAC, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setMsisdn(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[MSISDN2_LENGTH];
			buffer.get(buf);
			setValue(MSISDN, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(MSISDN2_LENGTH);
				fc.read(buf);
				setValue(MSISDN, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setModelNo(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[MODEL_NO_LENGTH];
			buffer.get(buf);
			setValue(MODEL_NO, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(MODEL_NO_LENGTH);
				fc.read(buf);
				setValue(MODEL_NO, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setIspName(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[ISP_NAME_LENGTH];
			buffer.get(buf);
			setValue(ISP_NAME, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(ISP_NAME_LENGTH);
				fc.read(buf);
				setValue(ISP_NAME, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setOsType(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[OS_TYPE_LENGTH];
			buffer.get(buf);
			setValue(OS_TYPE, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(OS_TYPE_LENGTH);
				fc.read(buf);
				setValue(OS_TYPE, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}
	
	private void setOsVersion(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[OS_VERSION_LENGTH];
			buffer.get(buf);
			setValue(OS_VERSION, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(OS_VERSION_LENGTH);
				fc.read(buf);
				setValue(OS_VERSION, new String(buf.array()));
			} catch (IOException e) {
				throw new TasException(e);
			}
		}
	}	
	
	private void setUuid(boolean isByteBufferMode) {
		if(isByteBufferMode) {
			byte[] buf = new byte[UUID2_LENGTH];
			buffer.get(buf);
			setValue(UUID, new String(buf));
		} else {
			try {
				ByteBuffer buf = ByteBuffer.allocate(UUID2_LENGTH);
				fc.read(buf);
				setValue(UUID, new String(buf.array()));
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
		PH105 bean = new PH105();
		
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
		PH105 bean = new PH105();
		
		return bean;
	}
	
	@Override
	public int getLength() {  //220
		return VERSION_LENGTH 
		+ APPLICATION_ID_LENGTH 
		+ MESSAGE_ID_LENGTH
		+ SESSION_ID_LENGTH
		+ TRANSACTION_ID_LENGTH
		+ SERVICE_ID_LENGTH
		+ IMEI_LENGTH
		+ WIFI_MAC2_LENGTH
		+ MSISDN2_LENGTH
		+ MODEL_NO_LENGTH
		+ ISP_NAME_LENGTH
		+ OS_TYPE_LENGTH
		+ OS_VERSION_LENGTH
		+ UUID2_LENGTH
		+ BODY_TYPE_LENGTH
		+ STATUS_CODE_LENGTH
		+ HEADER_LENGTH_LENGTH 
		+ BODY_LENGTH_LENGTH;
	}

}