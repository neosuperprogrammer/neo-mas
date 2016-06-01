package com.tionsoft.mas.tas.protocol.codec.decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.exception.*;
import com.tionsoft.mas.tas.protocol.definition.BodyDefinition;
import com.tionsoft.mas.tas.protocol.definition.DefinitionFinder;
import com.tionsoft.mas.tas.protocol.definition.FieldDefinition;
import com.tionsoft.mas.tas.protocol.definition.SectionDefinition;

public class FileChannelDecoder {
	
	private final FileChannel fileChannel;
	private final DefinitionFinder definitionFinder;
	private final boolean isServer;
	private BodyDecoder bodyDecoder = null;
	
	public FileChannelDecoder(FileChannel fileChannel, DefinitionFinder definitionFinder, boolean isServer) {
		this.fileChannel = fileChannel;
		this.definitionFinder = definitionFinder;
		this.isServer = isServer;
	}
	
	public final TasBean[] decode() {
		TasBean[] bean = new TasBean[2];
		int headerLength = definitionFinder.getPlatformHeader().getValue(PlatformHeader.HEADER_LENGTH, Integer.class);
		int bodyLength = definitionFinder.getPlatformHeader().getValue(PlatformHeader.BODY_LENGTH, Integer.class);
		
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
		SectionDefinition headerDefinition = definitionFinder.getActiveDefinition().getHeaderDefinition();
		return decodeSection(headerDefinition);
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
		}catch (Exception e) {
			
			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_PROTOCOL_PARSING, "Protocol.xml parsing Exception , [Name : " + fieldDefinition.getName() + "][Length : " + fieldDefinition.getLength() + "]");
			throw new TasException(errorType, e);
		}
	}
	
	private final void decodeBooleanField(FieldDefinition fieldDefinition, TasBean bean) {
		ByteBuffer buffer = null;
		try {
			buffer = ByteBuffer.allocate(1);
			fileChannel.read(buffer);
			buffer.flip();
			Boolean value = buffer.get() == (byte)0x01 ? new Boolean(true) : new Boolean(false);
			bean.setValue(fieldDefinition.getName(), value);
		} catch (IOException e) {
			throw new TasException(e);
		}
	}
	
	private final void decodeByteField(FieldDefinition fieldDefinition, TasBean bean) {
		ByteBuffer buffer = null;
		try {
			int byteLength = decodeHiddenField();
			
			buffer = ByteBuffer.allocate(byteLength);
			fileChannel.read(buffer);
			buffer.flip();
			bean.setValue(fieldDefinition.getName(), buffer.array());
		} catch (IOException e) {
			throw new TasException(e);
		} finally {
			if(buffer != null) {
				buffer = null;
			}
		}
	}
	
	private final void decodeStringField(FieldDefinition fieldDefinition, TasBean bean) {
		ByteBuffer buffer = null;
		try {
			int strLength = decodeHiddenField();
			
			buffer = ByteBuffer.allocate(strLength);
			fileChannel.read(buffer);
			buffer.flip();
			String value = new String(buffer.array());
			bean.setValue(fieldDefinition.getName(), value);
		} catch (IOException e) {
			throw new TasException(e);
		} finally {
			if(buffer != null) {
				buffer = null;
			}
		}
	}
	
	private final void decodeShortField(FieldDefinition fieldDefinition, TasBean bean) {
		ByteBuffer buffer = null;
		try {
			buffer = ByteBuffer.allocate(2);
			fileChannel.read(buffer);
			buffer.flip();
			Short value = buffer.getShort();
			bean.setValue(fieldDefinition.getName(), value);
		} catch (IOException e) {
			throw new TasException(e);
		} finally {
			if(buffer != null) {
				buffer = null;
			}
		}
	}
	
	private final void decodeIntegerField(FieldDefinition fieldDefinition, TasBean bean) {
		ByteBuffer buffer = null;
		try {
			buffer = ByteBuffer.allocate(4);
			fileChannel.read(buffer);
			buffer.flip();
			Integer value = buffer.getInt();
			bean.setValue(fieldDefinition.getName(), value);
		} catch (IOException e) {
			throw new TasException(e);
		} finally {
			if(buffer != null) {
				buffer = null;
			}
		}
	}
	
	private final void decodeLongField(FieldDefinition fieldDefinition, TasBean bean) {
		ByteBuffer buffer = null;
		try {
			buffer = ByteBuffer.allocate(8);
			fileChannel.read(buffer);
			buffer.flip();
			Long value = buffer.getLong();
			bean.setValue(fieldDefinition.getName(), value);
		} catch (IOException e) {
			throw new TasException(e);
		} finally {
			if(buffer != null) {
				buffer = null;
			}
		}
	}
	
	private final void decodeFloatField(FieldDefinition fieldDefinition, TasBean bean) {
		ByteBuffer buffer = null;
		try {
			buffer = ByteBuffer.allocate(4);
			fileChannel.read(buffer);
			buffer.flip();
			Float value = buffer.getFloat();
			bean.setValue(fieldDefinition.getName(), value);
		} catch (IOException e) {
			throw new TasException(e);
		} finally {
			if(buffer != null) {
				buffer = null;
			}
		}
	}
	
	private final void decodeDoubleField(FieldDefinition fieldDefinition, TasBean bean) {
		ByteBuffer buffer = null;
		try {
			buffer = ByteBuffer.allocate(8);
			fileChannel.read(buffer);
			buffer.flip();
			Double value = buffer.getDouble();
			bean.setValue(fieldDefinition.getName(), value);
		} catch (IOException e) {
			throw new TasException(e);
		} finally {
			if(buffer != null) {
				buffer = null;
			}
		}
	}
	
	private final void decodeFileField(FieldDefinition fieldDefinition, TasBean bean) {
		FileChannel fc = null;
		try {
			int fileLength = decodeHiddenField();
			
			String tmpFile = System.getProperty("mas.tmpdir") + "/" + System.nanoTime();
			fc = new FileOutputStream(tmpFile).getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(65536);
			int writeCount = 0;
			while(fileChannel.read(buffer) != -1) {
				writeCount += buffer.limit();
				buffer.flip();
				fc.write(buffer);
				if(writeCount + 65536 > fileLength) {
					buffer = null;
					buffer = ByteBuffer.allocate(fileLength - writeCount);
				}
				if(fileLength == writeCount) {
					break;
				}
				buffer.clear();
			}
			bean.setValue(fieldDefinition.getName(), new File(tmpFile));
		} catch (IOException e) {

			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_PROTOCOL_PARSING, "Protocol.xml parsing Exception , [Name : " + fieldDefinition.getName() + "][Length : " + fieldDefinition.getLength() + "]");
			throw new TasException(errorType, e);
		} finally {
			if(fc != null) {
				try {
					fc.close();
				} catch (IOException e) {
				}
			}
		}
		
	}
	
	private final int decodeHiddenField() {
		ByteBuffer buffer = null;
		try {
			buffer = ByteBuffer.allocate(4);
			fileChannel.read(buffer);
			buffer.flip();
			int lengthOrCount = buffer.getInt(); // String Length or Structure Iteration Count
			return lengthOrCount;
		} catch (IOException e) {
			throw new TasException(e);
		} finally {
			if(buffer != null) {
				buffer = null;
			}
		}
	}
	
	private final void decodeStructureField(FieldDefinition fieldDefinition, TasBean parentBean) {
		SectionDefinition sectionDefinition = definitionFinder.getRefDefinition(fieldDefinition.getReference());
		if(sectionDefinition.getFieldNameCount() == 0) {
			ErrorType errorType = new ErrorType(definitionFinder.getPlatformHeader(),ErrorType.ERROR_CODE_NOT_FOUND_REQUEST_STRUCTURE_DEFINITION, "REQUEST Structure Not Found : " + fieldDefinition.getReference());
			throw new TasException(errorType);
			
		}
		int iterCount = decodeHiddenField();
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
