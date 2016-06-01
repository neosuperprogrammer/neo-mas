package com.tionsoft.mas.tas.protocol.codec.encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.exception.ErrorType;
import com.tionsoft.mas.tas.exception.TasException;
import com.tionsoft.mas.tas.protocol.definition.BodyDefinition;
import com.tionsoft.mas.tas.protocol.definition.DefinitionFinder;
import com.tionsoft.mas.tas.protocol.definition.FieldDefinition;
import com.tionsoft.mas.tas.protocol.definition.SectionDefinition;

public class ByteBufferEncoder {
	
	public static final String TOTAL_MESSAGE_SIZE = "TOTAL_MESSAGE_SIZE";
	public static final String SENT_MESSAGE_COUNT = "SENT_MESSAGE_COUNT";
	public static final String FILE_CHANNEL = "FILE_CHANNEL";
	public static final String DEL_FILEPATH = "DEL_FILEPATH";
	
	private final IoSession session;
	private final PlatformHeader platformHeader;
	private final TasBean header;
	private final TasBean body;
	private final DefinitionFinder definitionFinder;
	private final boolean isServer;
	
	private BodyEncoder bodyEncoder = null;
	
	private String version = null;
	private short statusCode = 0;  //PHV103 에서 status_code 사용함
	
	public ByteBufferEncoder(IoSession session, PlatformHeader platformHeader, TasBean header, TasBean body, DefinitionFinder definitionFinder, boolean isServer) {
		this.session = session;
		this.platformHeader = platformHeader;
		this.header = header;
		this.body = body;
		this.definitionFinder = definitionFinder;
		this.isServer = isServer;
	}
	
	public int encode() {
		int platformHeaderLength = encodePlatformHeader();
		int headerLength = encodeHeader();
		int bodyLength = encodeBody();
		
		int messageLength = platformHeaderLength + headerLength + bodyLength;
		return messageLength;
	}
	
	private final int encodePlatformHeader() {
		
		version = platformHeader.getValue(PlatformHeader.VERSION, String.class);
		
		short bodyType = platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class);
		statusCode = platformHeader.getValue(PlatformHeader.STATUS_CODE, Short.class);
		
		int headerLength = 0;
		if(header != null && header.getParamNames().size() > 0) {
			headerLength = getEncodedSectionLength(header, definitionFinder.getActiveDefinition().getHeaderDefinition());
		}
		
		BodyDefinition bodyDefinition = definitionFinder.getActiveDefinition().getBodyDefinition();
		int bodyLength = 0;
		
		if(body != null  && body.getParamNames().size() > 0) {
			if(isServer) {
				if(bodyEncoder != null) {
					bodyLength = bodyEncoder.encode(body, bodyDefinition.getResponseDefinition(), null, platformHeader);
				} else {
					bodyLength = getEncodedSectionLength(body, bodyDefinition.getResponseDefinition());
				}
			} else {
				bodyLength = getEncodedSectionLength(body, bodyDefinition.getRequestDefinition());
			}
		}
		
		session.setAttribute(TOTAL_MESSAGE_SIZE, platformHeader.getLength() + headerLength + bodyLength);
		
		platformHeader.setValue(PlatformHeader.STATUS_CODE, statusCode);
		platformHeader.setValue(PlatformHeader.HEADER_LENGTH, headerLength);
		platformHeader.setValue(PlatformHeader.BODY_LENGTH, bodyLength);
		
		String appId = platformHeader.getValue(PlatformHeader.APPLICATION_ID, String.class);
		String msgId = platformHeader.getValue(PlatformHeader.MESSAGE_ID, String.class);
		long sessId = platformHeader.getValue(PlatformHeader.SESSION_ID, Long.class);
		
		long transactionId = 0L;
		int serviceId = 0;
		String imei = null;
		String msisdn = null;
		
		String wifiMac = null;
		String modelNo = null;
		String ispName = null;
		String osType = null;
		String osVersion = null;
		String uuid = null;
		
		if( version != null && version.equals("PHV101")) {
			transactionId = platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class);
			serviceId = platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class);
			imei = platformHeader.getValue(PlatformHeader.IMEI, String.class);
			msisdn = platformHeader.getValue(PlatformHeader.MSISDN, String.class);
		}else if( version != null && version.equals("PHV102")) {
			transactionId = platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class);
			serviceId = platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class);
			imei = platformHeader.getValue(PlatformHeader.IMEI, String.class);
			msisdn = platformHeader.getValue(PlatformHeader.MSISDN, String.class);
			
			wifiMac = platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class);
			modelNo = platformHeader.getValue(PlatformHeader.MODEL_NO, String.class);
			ispName = platformHeader.getValue(PlatformHeader.ISP_NAME, String.class);
			osType = platformHeader.getValue(PlatformHeader.OS_TYPE, String.class);
			osVersion = platformHeader.getValue(PlatformHeader.OS_VERSION, String.class);
			uuid = platformHeader.getValue(PlatformHeader.UUID, String.class);
		}else if( version != null && version.equals("PHV103")) {
			transactionId = platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class);
			serviceId = platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class);
			imei = platformHeader.getValue(PlatformHeader.IMEI, String.class);
			msisdn = platformHeader.getValue(PlatformHeader.MSISDN, String.class);
			
			wifiMac = platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class);
			modelNo = platformHeader.getValue(PlatformHeader.MODEL_NO, String.class);
			ispName = platformHeader.getValue(PlatformHeader.ISP_NAME, String.class);
			osType = platformHeader.getValue(PlatformHeader.OS_TYPE, String.class);
			osVersion = platformHeader.getValue(PlatformHeader.OS_VERSION, String.class);
			uuid = platformHeader.getValue(PlatformHeader.UUID, String.class);
		}else if( version != null && version.equals("PHV105")) {
			transactionId = platformHeader.getValue(PlatformHeader.TRANSACTION_ID, Long.class);
			serviceId = platformHeader.getValue(PlatformHeader.SERVICE_ID, Integer.class);
			imei = platformHeader.getValue(PlatformHeader.IMEI, String.class);
			msisdn = platformHeader.getValue(PlatformHeader.MSISDN, String.class);
			
			wifiMac = platformHeader.getValue(PlatformHeader.WIFI_MAC, String.class);
			modelNo = platformHeader.getValue(PlatformHeader.MODEL_NO, String.class);
			ispName = platformHeader.getValue(PlatformHeader.ISP_NAME, String.class);
			osType = platformHeader.getValue(PlatformHeader.OS_TYPE, String.class);
			osVersion = platformHeader.getValue(PlatformHeader.OS_VERSION, String.class);
			uuid = platformHeader.getValue(PlatformHeader.UUID, String.class);
		}
		
		IoBuffer tmpBuffer = IoBuffer.allocate(1024, false);
		tmpBuffer.setAutoExpand(true);
		
		if( version != null && !version.equals("PHV100")) {
			tmpBuffer.put(paddNullBytesOrTrim(version.getBytes(), PlatformHeader.VERSION_LENGTH));
		}
	
		tmpBuffer.put(paddNullBytesOrTrim(appId.getBytes(), PlatformHeader.APPLICATION_ID_LENGTH));
		tmpBuffer.put(paddNullBytesOrTrim(msgId.getBytes(), PlatformHeader.MESSAGE_ID_LENGTH));
		tmpBuffer.putLong(sessId);
		
		if( version != null && version.equals("PHV101")) {
			tmpBuffer.putLong(transactionId);
			tmpBuffer.putInt(serviceId);
			tmpBuffer.put(paddNullBytesOrTrim(imei.getBytes(), PlatformHeader.IMEI_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(msisdn.getBytes(), PlatformHeader.MSISDN_LENGTH));
		}else if( version != null && version.equals("PHV102")) {
			tmpBuffer.putLong(transactionId);
			tmpBuffer.putInt(serviceId);
			tmpBuffer.put(paddNullBytesOrTrim(imei.getBytes(), PlatformHeader.IMEI_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(wifiMac.getBytes(), PlatformHeader.WIFI_MAC_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(msisdn.getBytes(), PlatformHeader.MSISDN2_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(modelNo.getBytes(), PlatformHeader.MODEL_NO_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(ispName.getBytes(), PlatformHeader.ISP_NAME_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(osType.getBytes(), PlatformHeader.OS_TYPE_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(osVersion.getBytes(), PlatformHeader.OS_VERSION_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(uuid.getBytes(), PlatformHeader.UUID_LENGTH));
			
		}else if( version != null && version.equals("PHV103")) {
			tmpBuffer.putLong(transactionId);
			tmpBuffer.putInt(serviceId);
			tmpBuffer.put(paddNullBytesOrTrim(imei.getBytes(), PlatformHeader.IMEI_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(wifiMac.getBytes(), PlatformHeader.WIFI_MAC_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(msisdn.getBytes(), PlatformHeader.MSISDN2_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(modelNo.getBytes(), PlatformHeader.MODEL_NO_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(ispName.getBytes(), PlatformHeader.ISP_NAME_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(osType.getBytes(), PlatformHeader.OS_TYPE_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(osVersion.getBytes(), PlatformHeader.OS_VERSION_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(uuid.getBytes(), PlatformHeader.UUID_LENGTH));
			
		}else if( version != null && version.equals("PHV105")) {
			tmpBuffer.putLong(transactionId);
			tmpBuffer.putInt(serviceId);
			tmpBuffer.put(paddNullBytesOrTrim(imei.getBytes(), PlatformHeader.IMEI_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(wifiMac.getBytes(), PlatformHeader.WIFI_MAC2_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(msisdn.getBytes(), PlatformHeader.MSISDN2_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(modelNo.getBytes(), PlatformHeader.MODEL_NO_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(ispName.getBytes(), PlatformHeader.ISP_NAME_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(osType.getBytes(), PlatformHeader.OS_TYPE_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(osVersion.getBytes(), PlatformHeader.OS_VERSION_LENGTH));
			tmpBuffer.put(paddNullBytesOrTrim(uuid.getBytes(), PlatformHeader.UUID2_LENGTH));
			
		}
		
		tmpBuffer.putShort(bodyType);
		tmpBuffer.putShort(statusCode);
		tmpBuffer.putInt(headerLength);
		tmpBuffer.putInt(bodyLength);
		
		tmpBuffer.flip();
		
		session.write(tmpBuffer);
		return platformHeader.getLength(); 
	}
	
	private final int encodeHeader() {
		if( version != null && version.equals("PHV103")) {
			if(statusCode != 0){ // 에러 상태이면 에러 메세지을 첨부하여 전송
				
				SectionDefinition ErrorMsgHeaderDefinition = new SectionDefinition();
				FieldDefinition errorMsgFieldDef = new FieldDefinition();
				errorMsgFieldDef.setName("TAS_ERROR_MSG");
				errorMsgFieldDef.setType(errorMsgFieldDef.type2Int("STRING"));
				
				ErrorMsgHeaderDefinition.addFieldDef(errorMsgFieldDef);
				if(header != null && header.getParamNames().size() > 0) {
					return encodeSection(header, ErrorMsgHeaderDefinition, session);
				}
				
			}else{
				SectionDefinition headerDefinition = definitionFinder.getActiveDefinition().getHeaderDefinition();
				if(header != null && header.getParamNames().size() > 0) {
					return encodeSection(header, headerDefinition, session);
				}
				
			}
		}else{
			SectionDefinition headerDefinition = definitionFinder.getActiveDefinition().getHeaderDefinition();
			if(header != null && header.getParamNames().size() > 0) {
				return encodeSection(header, headerDefinition, session);
			}
		}
		
		return 0;
	}
	
	private final int encodeBody() {
		
		BodyDefinition bodyDefinition = definitionFinder.getActiveDefinition().getBodyDefinition();
		SectionDefinition sectionDefinition;
		if(isServer) {
			sectionDefinition = bodyDefinition.getResponseDefinition();
		} else {
			sectionDefinition = bodyDefinition.getRequestDefinition();
		}
		
		if(body != null  && body.getParamNames().size() > 0) {
			if(isServer) {
				if(bodyEncoder != null) {
					return bodyEncoder.encode(body, sectionDefinition, session, platformHeader);
				} else {
					return encodeBodySection(body, sectionDefinition, session);
				}
			} else {
				return encodeBodySection(body, sectionDefinition, session);
			}
		}
		return 0;
	}

	protected int encodeBodySection(TasBean bean, SectionDefinition sectionDefinition, IoSession session) {
		return encodeSection(bean, sectionDefinition, session);
	}
	
	private final int getEncodedSectionLength(TasBean bean, SectionDefinition sectionDefinition) {
		if( version != null && version.equals("PHV103")) {
			if(statusCode != 0){ // 에러 상태이면 에러 메세지을 첨부하여 전송
				
				SectionDefinition errorMsgHeaderDefinition = new SectionDefinition();
				FieldDefinition errorMsgFieldDef = new FieldDefinition();
				errorMsgFieldDef.setName("TAS_ERROR_MSG");
				errorMsgFieldDef.setType(errorMsgFieldDef.type2Int("STRING"));
				
				errorMsgHeaderDefinition.addFieldDef(errorMsgFieldDef);
				
				FieldDefinition fieldDef;
				int length = 0;
				for(String fieldName : errorMsgHeaderDefinition.getFieldNames()) {
					fieldDef = errorMsgHeaderDefinition.getFieldDef(fieldName);
					length += encodeField(bean, fieldDef, null); // null 인 경우 계산모드
				}
				return length;
			}else{
				FieldDefinition fieldDef;
				int length = 0;
				for(String fieldName : sectionDefinition.getFieldNames()) {
					fieldDef = sectionDefinition.getFieldDef(fieldName);
					length += encodeField(bean, fieldDef, null); // null 인 경우 계산모드
				}
				return length;
			}
		}else{
			FieldDefinition fieldDef;
			int length = 0;
			for(String fieldName : sectionDefinition.getFieldNames()) {
				fieldDef = sectionDefinition.getFieldDef(fieldName);
				length += encodeField(bean, fieldDef, null); // null 인 경우 계산모드
			}
			return length;
		}
		

		

	}
	
	private final int encodeSection(TasBean bean, SectionDefinition sectionDefinition, IoSession session) {
		FieldDefinition fieldDef;
		int length = 0;
		for(String fieldName : sectionDefinition.getFieldNames()) {
			
			fieldDef = sectionDefinition.getFieldDef(fieldName);
			length += encodeField(bean, fieldDef, session);
		}
		return length;
	}
	
	private final int encodeField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		switch(fieldDefinition.getType()) {
		case FieldDefinition.FIELD_TYPE_BOOLEAN :
			return encodeBooleanField(bean, fieldDefinition, session);
		case FieldDefinition.FIELD_TYPE_BYTE :
			return encodeByteField(bean, fieldDefinition, session);
		case FieldDefinition.FIELD_TYPE_STRING :
			return encodeStringField(bean, fieldDefinition, session);
		case FieldDefinition.FIELD_TYPE_SHORT :
			return encodeShortField(bean, fieldDefinition, session);
		case FieldDefinition.FIELD_TYPE_INTEGER :
			return encodeIntegerField(bean, fieldDefinition, session);
		case FieldDefinition.FIELD_TYPE_LONG :
			return encodeLongField(bean, fieldDefinition, session);
		case FieldDefinition.FIELD_TYPE_FLOAT :
			return encodeFloatField(bean, fieldDefinition, session);
		case FieldDefinition.FIELD_TYPE_DOUBLE :
			return encodeDoubleField(bean, fieldDefinition, session);
		case FieldDefinition.FIELD_TYPE_FILE :
			return encodeFileField(bean, fieldDefinition, session);
		case FieldDefinition.FIELD_TYPE_STRUCTURE :
			return encodeStructureField(bean, fieldDefinition, session);
		default :
			return 0;
		}
	}
	
	private final int encodeBooleanField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		Boolean data = bean.getValue(fieldDefinition.getName(), Boolean.class);
		byte fieldData = data.booleanValue() ? (byte)0x01 : (byte) 0x00;
		if(session != null) {
			IoBuffer tmpBuffer = IoBuffer.allocate(1, false);
			tmpBuffer.put(fieldData);
			tmpBuffer.flip();
			session.write(tmpBuffer);
		}
		return 1;
	}
	
	private final int encodeByteField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		Object fieldObject = bean.getValue(fieldDefinition.getName());
		byte[] fieldData = (byte[])fieldObject;
		if(session != null) {
			IoBuffer tmpBuffer = IoBuffer.allocate(4 + fieldData.length, false);
			tmpBuffer.putInt(fieldData.length);
			tmpBuffer.put(fieldData);
			tmpBuffer.flip();
			session.write(tmpBuffer);
		}
		return 4 + fieldData.length;
		
	}
	
	private final int encodeStringField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		String fieldData = bean.getValue(fieldDefinition.getName(), String.class);
		if(session != null) {
			IoBuffer tmpBuffer = IoBuffer.allocate(4 + fieldData.getBytes().length, false);
			tmpBuffer.putInt(fieldData.getBytes().length);
			tmpBuffer.put(fieldData.getBytes());
			tmpBuffer.flip();
			session.write(tmpBuffer);
		}
		return 4 + fieldData.getBytes().length;
	}
	
	private final int encodeShortField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		short fieldData = bean.getValue(fieldDefinition.getName(), Short.class);
		if(session != null) {
			IoBuffer tmpBuffer = IoBuffer.allocate(2, false);
			tmpBuffer.putShort(fieldData);
			tmpBuffer.flip();
			session.write(tmpBuffer);
		}
		return 2;
	}
	
	private final int encodeIntegerField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		int fieldData = bean.getValue(fieldDefinition.getName(), Integer.class);
		if(session != null) {
			IoBuffer tmpBuffer = IoBuffer.allocate(4, false);
			tmpBuffer.putInt(fieldData);
			tmpBuffer.flip();
			session.write(tmpBuffer);
		}
		return 4;
	}
	
	private final int encodeLongField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		long fieldData = bean.getValue(fieldDefinition.getName(), Long.class);
		if(session != null) {
			IoBuffer tmpBuffer = IoBuffer.allocate(8, false);
			tmpBuffer.putLong(fieldData);
			tmpBuffer.flip();
			session.write(tmpBuffer);
		}
		return 8;
	}
	
	private final int encodeFloatField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		float fieldData = bean.getValue(fieldDefinition.getName(), Float.class);
		if(session != null) {
			IoBuffer tmpBuffer = IoBuffer.allocate(4, false);
			tmpBuffer.putFloat(fieldData);
			tmpBuffer.flip();
			session.write(tmpBuffer);
		}
		return 4;
	}
	
	private final int encodeDoubleField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		double fieldData = bean.getValue(fieldDefinition.getName(), Double.class);
		if(session != null) {
			IoBuffer tmpBuffer = IoBuffer.allocate(8, false);
			tmpBuffer.putDouble(fieldData);
			tmpBuffer.flip();
			session.write(tmpBuffer);
		}
		return 8;
	}
	
	private final int encodeFileField(TasBean bean, FieldDefinition fieldDefinition, final IoSession session) {
		FileChannel fc = null;
		try {
			Object fieldObject = bean.getValue(fieldDefinition.getName());
			File file = (File)fieldObject;
			int fileSize = (int)file.length();
			
			
			if(session != null) {
				IoBuffer fileSizeBuffer = IoBuffer.allocate(4, false);
				fileSizeBuffer.putInt((int)fileSize);
				fileSizeBuffer.flip();
				session.write(fileSizeBuffer);
				
				if(isServer) { // Server 에서 Response 시
					session.write(file);
					
				} else { // Client 에서 Request 시
					// File 을 Write 하면 Progress 진행을 알 수 없으니 
					// IoBuffer 를 write 할 수 밖에 없다.
					// File 을 Write 하면 모든 File 이 전송된 후에 messageSent 가 callback 되기 때문이다.
					
					fc = new FileInputStream(file).getChannel();
					ByteBuffer fileBuffer = ByteBuffer.allocate(65536);
					int writeCount = 0;
					while(fc.read(fileBuffer) != -1) {
						writeCount += fileBuffer.limit();
						
						fileBuffer.flip();
						IoBuffer buffer = IoBuffer.wrap(fileBuffer);
						session.write(buffer);
						
						
						if(writeCount + 65536 > fileSize) {
							if((fileSize - writeCount) > 0){
								fileBuffer = null;
								fileBuffer = ByteBuffer.allocate(fileSize - writeCount);	
							}else{  // filesize 65536 작으면
								writeCount = fileSize;
							}
						} else {
							fileBuffer = ByteBuffer.allocate(65536);
						}
						if(fileSize == writeCount) {
							break;
						}
						fileBuffer.clear();
					}
				}
			}
			
			return (int)(4 + fileSize);
			
		} catch (Exception e) {
			e.printStackTrace();
			if(fc != null) {
				try {
					fc.close();
				} catch (IOException ee) {
				}
			}
			throw new TasException(e);
			
		} finally {
			if(fc != null) {
				try {
					fc.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private final int encodeStructureField(TasBean bean, FieldDefinition fieldDefinition, IoSession session) {
		Object object = bean.getValue(fieldDefinition.getName());
		Collection<TasBean> list;
		if (object instanceof Collection) {
			list = (Collection<TasBean>)object;
		} else {
			return 0;
		}
		
		if(session != null) {
			IoBuffer tmpBuffer = IoBuffer.allocate(4, false);
			tmpBuffer.putInt(list.size());
			tmpBuffer.flip();
			session.write(tmpBuffer);
		}
		
		SectionDefinition sectionDef = definitionFinder.getRefDefinition(fieldDefinition.getReference());
		if(sectionDef.getFieldNameCount() == 0) {
			ErrorType errorType = new ErrorType(platformHeader,ErrorType.ERROR_CODE_NOT_FOUND_RESPONSE_STRUCTURE_DEFINITION, "RESPONSE Structure Not Found : " + fieldDefinition.getReference());
			throw new TasException(errorType);

		}
		
		int length = 0;
		for(TasBean subBean : list) {
			length += encodeSection(subBean, sectionDef, session);
		}
		
		return 4 + length;
	}
	
	private byte[] paddNullBytesOrTrim(byte[] src, int fieldLength) {
		if(src.length < fieldLength) {
			int insufficientByteLength = fieldLength - src.length;
			byte[] nullBytes = new byte[insufficientByteLength];
			for(int i=0;i<nullBytes.length;i++) {
				nullBytes[i] = (byte)0x00;
			}
			return ArrayUtils.addAll(src, nullBytes);
		} else if(src.length > fieldLength) {
			return ArrayUtils.subarray(src, 0, fieldLength );
		} else {
			return src;
		}
	}
	
	protected IoBuffer getDataBuffer(IoBuffer ioBuffer) {
		byte[] data = new byte[ioBuffer.limit()];
		ioBuffer.get(data);
		ioBuffer = null;
		return IoBuffer.wrap(data);
	}
	
	public void setBodyEncoder(BodyEncoder bodyEncoder) {
		this.bodyEncoder = bodyEncoder;
	}
	
	public interface BodyEncoder {
		public int encode(TasBean bean, SectionDefinition sectionDefinition, IoSession session,PlatformHeader platformHeader);
	}
	
}
