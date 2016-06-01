package com.tionsoft.mas.tas.protocol.codec.decoder;

import java.io.*;
import java.nio.*;
import java.util.*;

import com.tionsoft.mas.tas.bean.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.exception.*;
import com.tionsoft.mas.tas.protocol.definition.*;

public class ByteBufferDecoder {
	
	private final ByteBuffer byteBuffer;
	private final DefinitionFinder definitionFinder;
	private final boolean isServer;
	private BodyDecoder bodyDecoder = null;
	
	private String version = null;
	private short statusCode = 0;  //PHV103 에서 status_code 사용함
	
	
	public ByteBufferDecoder(ByteBuffer byteBuffer, DefinitionFinder definitionFinder, boolean isServer) {
		
		this.byteBuffer = byteBuffer;
		this.definitionFinder = definitionFinder;
		this.isServer = isServer;
	}
	
	public final TasBean[] decode() {
		TasBean[] bean = new TasBean[2];
		
		int headerLength = definitionFinder.getPlatformHeader().getValue(PlatformHeader.HEADER_LENGTH, Integer.class);
		int bodyLength = definitionFinder.getPlatformHeader().getValue(PlatformHeader.BODY_LENGTH, Integer.class);
		version = definitionFinder.getPlatformHeader().getValue(PlatformHeader.VERSION, String.class);
		
		if( version != null && version.equals("PHV103")) {
			statusCode = definitionFinder.getPlatformHeader().getValue(PlatformHeader.STATUS_CODE, Short.class);
		}
		
		
		if(headerLength > 0) {
			bean[0] = decodeHeader();
		} else {
			bean[0] = new TasBean();
		}
		
		if(bodyLength > 0) {
			bean[1] = decodeBody();
		} else {
			bean[1] =  new TasBean();
		}

		return bean;
	}
	
	private final TasBean decodeHeader() {
		if(isServer) {
			SectionDefinition headerDefinition = definitionFinder.getActiveDefinition().getHeaderDefinition();
			return decodeSection(headerDefinition);
		} else {
			if( version != null && version.equals("PHV103") && (statusCode != 0)){ // tas-client에서 PHV103 이고 상태코드가 0이 아니면 에러 메지시가 서버에서 전송됨 따라서 에러 메세지 파싱함 
				SectionDefinition ErrorMsgHeaderDefinition = new SectionDefinition();
				FieldDefinition errorMsgFieldDef = new FieldDefinition();
				errorMsgFieldDef.setName("TAS_ERROR_MSG");
				errorMsgFieldDef.setType(errorMsgFieldDef.type2Int("STRING"));
				ErrorMsgHeaderDefinition.addFieldDef(errorMsgFieldDef);

				return decodeSection(ErrorMsgHeaderDefinition);
			}else{
				SectionDefinition headerDefinition = definitionFinder.getActiveDefinition().getHeaderDefinition();
				return decodeSection(headerDefinition);
			}
		
		}

	}
	
	private final TasBean decodeBody() {
		BodyDefinition bodyDefinition = definitionFinder.getActiveDefinition().getBodyDefinition();
		SectionDefinition sectionDefinition;
		if(isServer) {
			sectionDefinition = bodyDefinition.getRequestDefinition();
		} else {
			sectionDefinition = bodyDefinition.getResponseDefinition();
		}
		
		if(sectionDefinition.getFieldNameCount() == 0) {
			String appId = definitionFinder.getPlatformHeader().getValue(PlatformHeader.APPLICATION_ID, String.class);
			String msgId = definitionFinder.getPlatformHeader().getValue(PlatformHeader.MESSAGE_ID, String.class); 
			
			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_NOT_FOUND_REQUEST_BODY_DEFINITION, "Not Found Request Body Definition : " + appId + "." + msgId);
			throw new TasException(errorType);
		}
		
		if(bodyDecoder != null) {
			return bodyDecoder.decode(decodeSection(sectionDefinition),definitionFinder.getPlatformHeader());
		} else {
			return decodeSection(sectionDefinition);
		}
		
	}
	
	private final TasBean decodeSection(SectionDefinition sectionDefinition) {
		
		TasBean bean = new TasBean();
		FieldDefinition fieldDefinition;
		for(String fieldName : sectionDefinition.getFieldNames()) {
			fieldDefinition = sectionDefinition.getFieldDef(fieldName);
			decodeField(fieldDefinition, bean);
			
		}
		return bean;
	}
	
	private final void decodeField(FieldDefinition fieldDefinition, TasBean bean) {
		try {
			switch(fieldDefinition.getType()) {
			case FieldDefinition.FIELD_TYPE_BOOLEAN :
				decodeBooleanField(fieldDefinition, bean);
				break;
			case FieldDefinition.FIELD_TYPE_BYTE :
				decodeByteField(fieldDefinition, bean);
				break;
			case FieldDefinition.FIELD_TYPE_STRING :
				decodeStringField(fieldDefinition, bean);
				break;
			case FieldDefinition.FIELD_TYPE_SHORT :
				decodeShortField(fieldDefinition, bean);
				break;
			case FieldDefinition.FIELD_TYPE_INTEGER :
				decodeIntegerField(fieldDefinition, bean);
				break;
			case FieldDefinition.FIELD_TYPE_LONG :
				decodeLongField(fieldDefinition, bean);
				break;
			case FieldDefinition.FIELD_TYPE_FLOAT :
				decodeFloatField(fieldDefinition, bean);
				break;
			case FieldDefinition.FIELD_TYPE_DOUBLE :
				decodeDoubleField(fieldDefinition, bean);
				break;
			case FieldDefinition.FIELD_TYPE_FILE :
				decodeFileField(fieldDefinition, bean);
				break;
			case FieldDefinition.FIELD_TYPE_STRUCTURE :
				decodeStructureField(fieldDefinition, bean);
				break;
			}
		} catch (BufferUnderflowException e) {
			
			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_BUFFER_UNDERFLOW, "Buffer underflow in a field , [Name : " + fieldDefinition.getName() + "][Length : " + fieldDefinition.getLength() + "]");
			throw new TasException(errorType, e);
		} catch (OutOfMemoryError e) {
			
			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_BUFFER_OUTOFMEMORY, "Buffer OutOfMemoryError in a field , [Name : " + fieldDefinition.getName() + "][Length : " + fieldDefinition.getLength() + "]");
			throw new TasException(errorType, e);

		}catch (Exception e) {
			
			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_PROTOCOL_PARSING, "Protocol.xml parsing Exception , [Name : " + fieldDefinition.getName() + "][Length : " + fieldDefinition.getLength() + "]");
			throw new TasException(errorType, e);
		}
	}
	
	private final void decodeBooleanField(FieldDefinition fieldDefinition, TasBean bean) {
		Boolean value = byteBuffer.get() == (byte)0x01 ? new Boolean(true) : new Boolean(false);
		bean.setValue(fieldDefinition.getName(), value);
	}
	
	private final void decodeByteField(FieldDefinition fieldDefinition, TasBean bean) {
		int byteLength = decodeHiddenField();
		
		byte[] buf = new byte[byteLength];
		byteBuffer.get(buf);
		
		bean.setValue(fieldDefinition.getName(), buf);
	}
	
	private final void decodeStringField(FieldDefinition fieldDefinition, TasBean bean) {
		int strLength = decodeHiddenField();
		
		byte[] buf = new byte[strLength];
		byteBuffer.get(buf);
		String value = new String(buf);
		
		bean.setValue(fieldDefinition.getName(), value);
	}
	
	private final void decodeShortField(FieldDefinition fieldDefinition, TasBean bean) {
		Short value = byteBuffer.getShort();
		bean.setValue(fieldDefinition.getName(), value);
	}
	
	private final void decodeIntegerField(FieldDefinition fieldDefinition, TasBean bean) {
		Integer value = byteBuffer.getInt();
		bean.setValue(fieldDefinition.getName(), value);
	}
	
	private final void decodeLongField(FieldDefinition fieldDefinition, TasBean bean) {
		Long value = byteBuffer.getLong();
		bean.setValue(fieldDefinition.getName(), value);
	}
	
	private final void decodeFloatField(FieldDefinition fieldDefinition, TasBean bean) {
		Float value = byteBuffer.getFloat();
		bean.setValue(fieldDefinition.getName(), value);
	}
	
	private final void decodeDoubleField(FieldDefinition fieldDefinition, TasBean bean) {
		Double value = byteBuffer.getDouble();
		bean.setValue(fieldDefinition.getName(), value);
	}
	
	private final void decodeFileField(FieldDefinition fieldDefinition, TasBean bean) {
		FileOutputStream fos = null;
		try {
			int fileLength = decodeHiddenField();
			byte[] buf = new byte[fileLength];
			byteBuffer.get(buf);
			
			String tmpFile = System.getProperty("mas.tmpdir") + "/" + System.nanoTime();
			File file = new File(tmpFile);
			fos = new FileOutputStream(file);
			fos.write(buf);
			
			bean.setValue(fieldDefinition.getName(), file);

		} catch (FileNotFoundException e) {
			
			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_FILETYPE_PROTOCOL_PARSING, "File Not Found Exception in protocol.xml parsing , [Name : " + fieldDefinition.getName() + "][Length : " + fieldDefinition.getLength() + "]");
			throw new TasException(errorType, e);
		} catch (IOException e) {
			
			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_PROTOCOL_PARSING, "Protocol.xml parsing Exception , [Name : " + fieldDefinition.getName() + "][Length : " + fieldDefinition.getLength() + "]");
			throw new TasException(errorType, e);
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private final int decodeHiddenField() {
		int lengthOrCount = byteBuffer.getInt(); // String Length or Structure Iteration Count
		return lengthOrCount;
	}
	
	private final void decodeStructureField(FieldDefinition fieldDefinition, TasBean parentBean) {
		SectionDefinition sectionDefinition = definitionFinder.getRefDefinition(fieldDefinition.getReference());
		if(sectionDefinition.getFieldNameCount() == 0) {
			
			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_NOT_FOUND_REQUEST_STRUCTURE_DEFINITION, "REQUEST Structure Not Found : " + fieldDefinition.getReference());
			throw new TasException(errorType);
		}
		int iterCount = decodeHiddenField();
//		System.out.println("decodeStructureField ["+fieldDefinition.getName()+"] iterCount["+iterCount+"]");
		
		Collection<TasBean> subBean = Collections.synchronizedList(new ArrayList<TasBean>());
		
		if(iterCount == 0) {
			parentBean.setValue(fieldDefinition.getName(), subBean);
			return;
		}
		
		for(int i=0;i<iterCount;i++) {
			subBean.add(decodeSection(sectionDefinition));
			parentBean.setValue(fieldDefinition.getName(), subBean);
		}
		
	}
	
	public void setBodyDecoder(BodyDecoder bodyDecoder) {
		this.bodyDecoder = bodyDecoder;
	}
	
}
