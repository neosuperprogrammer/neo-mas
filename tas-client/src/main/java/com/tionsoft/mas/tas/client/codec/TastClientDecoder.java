package com.tionsoft.mas.tas.client.codec;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import org.apache.commons.configuration.*;
import org.apache.commons.io.*;
import org.apache.mina.core.buffer.*;
import org.apache.mina.core.session.*;
import org.apache.mina.filter.codec.*;

import com.tionsoft.mas.tas.bean.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.exception.*;
import com.tionsoft.mas.tas.protocol.codec.decoder.*;
import com.tionsoft.mas.tas.protocol.definition.*;
import com.tionsoft.mas.tas.client.handler.*;
import com.tionsoft.mas.tas.client.message.*;

class TastClientDecoder extends CumulativeProtocolDecoder {
	
	private final XMLConfiguration protocolConfig;
	private TasProgressListener progressListener = null;
	 
	public TastClientDecoder(XMLConfiguration protocolConfig) {
		this.protocolConfig = protocolConfig;
	}
	
	public void setProgressListener(TasProgressListener progressListener) {
		this.progressListener = progressListener;
	}
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
		int ipos = in.position();
		String hexdump = System.getProperty("mas.tas.logging.hexdump");
		if(hexdump != null && hexdump.equalsIgnoreCase("true")) {
			dump(in);	
		}
		
		try {
			String mode = null;
			if(session.getAttribute(TasClientCodecFactory.MODE) != null) {
				mode = (String)session.getAttribute(TasClientCodecFactory.MODE);
			}
			
			int totalMessageLengthToReceive = 0;
			int receivedMessageLength = 0;
			
			if(session.getAttribute(TasClientCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE) != null) {
				totalMessageLengthToReceive = (Integer)session.getAttribute(TasClientCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE);
			}
			
			if(session.getAttribute(TasClientCodecFactory.RECEIVED_MESSAGE_LENGTH) != null) {
				receivedMessageLength = (Integer)session.getAttribute(TasClientCodecFactory.RECEIVED_MESSAGE_LENGTH);
			}
			
			if(mode != null && mode.equalsIgnoreCase("FILE")) {
				FileChannel fc = (FileChannel)session.getAttribute(TasClientCodecFactory.TEMP_FILE_CHANNEL);
				if((receivedMessageLength + in.remaining()) > totalMessageLengthToReceive) {
					int requiredToSaveSize = totalMessageLengthToReceive - receivedMessageLength;
					fileProgressEvent(totalMessageLengthToReceive, totalMessageLengthToReceive);
					int limit = in.limit();
					in.limit(requiredToSaveSize);
					fc.write(in.buf());
					in.limit(limit);
					session.removeAttribute(TasClientCodecFactory.MODE);
					session.removeAttribute(TasClientCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE);
					session.removeAttribute(TasClientCodecFactory.RECEIVED_MESSAGE_LENGTH);
					session.removeAttribute(TasClientCodecFactory.TEMP_FILE_CHANNEL);
					TasMessage tasMessage = decode(fc);
					String tmpFilePath = (String)session.getAttribute(TasClientCodecFactory.TEMP_FILE_PATH);
					File tmpFile = new File(tmpFilePath);
					tmpFile.delete();
					out.write(tasMessage);
				} else if((receivedMessageLength + in.remaining()) == totalMessageLengthToReceive) {
					fileProgressEvent(totalMessageLengthToReceive, totalMessageLengthToReceive);
					fc.write(in.buf());
					dispose(session);
					session.removeAttribute(TasClientCodecFactory.MODE);
					session.removeAttribute(TasClientCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE);
					session.removeAttribute(TasClientCodecFactory.RECEIVED_MESSAGE_LENGTH);
					session.removeAttribute(TasClientCodecFactory.TEMP_FILE_CHANNEL);
					TasMessage tasMessage = decode(fc);
					String tmpFilePath = (String)session.getAttribute(TasClientCodecFactory.TEMP_FILE_PATH);
					File tmpFile = new File(tmpFilePath);
					tmpFile.delete();
					out.write(tasMessage);
				} else {
					fileProgressEvent(totalMessageLengthToReceive, receivedMessageLength + in.remaining());
					session.setAttribute(TasClientCodecFactory.RECEIVED_MESSAGE_LENGTH, receivedMessageLength + in.remaining());
					fc.write(in.buf());
					dispose(session);
				}
				return true;
			}
			
			if(in.remaining() >= PlatformHeader.VERSION_LENGTH) { // PHV100, PHV101
				in.mark();
				String phv = getPlatformHeaderVersion(in.buf());
				PlatformHeader platformHeader = PlatformHeaderFactory.getPlatformHeader(phv);
				if (in.remaining() >= platformHeader.getLength()) {
					platformHeader.decode(in.buf());
					int headerLength = platformHeader.getValue(PlatformHeader.HEADER_LENGTH, Integer.class);
					int bodyLength = platformHeader.getValue(PlatformHeader.BODY_LENGTH, Integer.class);
					int msgLength = headerLength + bodyLength;
					session.setAttribute(TasClientCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE, platformHeader.getLength() + msgLength);
					session.setAttribute(TasClientCodecFactory.RECEIVED_MESSAGE_LENGTH, platformHeader.getLength() + in.remaining());
					if(msgLength > (3 * 1024 * 1024) ) { // 3M 이상이면 FILE 모드로 자동 변환
						session.setAttribute(TasClientCodecFactory.MODE, "FILE");
						String tmpFile = System.getProperty("mas.tmpdir") + "/" + System.nanoTime();
						RandomAccessFile file = new RandomAccessFile(tmpFile, "rw");
						FileChannel fc = file.getChannel();
						in.reset();
						fileProgressEvent(msgLength + platformHeader.getLength(), in.remaining());
						fc.write(in.buf()); // 처음 들어온 데이터를  저장한다.
						System.out.println("Temporary File is created : " + tmpFile);
						session.setAttribute(TasClientCodecFactory.TEMP_FILE_PATH, tmpFile);
						session.setAttribute(TasClientCodecFactory.TEMP_FILE_CHANNEL, fc);
						dispose(session);
						return true;
					}
					
					if (in.remaining() == msgLength) {
						fileProgressEvent(platformHeader.getLength() + msgLength, platformHeader.getLength() + msgLength);
						session.removeAttribute(TasClientCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE);
						session.removeAttribute(TasClientCodecFactory.RECEIVED_MESSAGE_LENGTH);
						TasMessage tasMessage = decode(in.buf(), platformHeader);
						out.write(tasMessage);
						// Handler 에서 exception 발생 했을 경우 TAS_MESSAGE Attribute 가 존재하면 
						// Decoding 까지는 성공했다는 가정하에 처리하도록 하기 위함 
						session.setAttribute("TAS_MESSAGE", tasMessage);
						return true;
					} else if(in.remaining() > msgLength) { // 붙어 들어오는 데이터 처리
						fileProgressEvent(platformHeader.getLength() + msgLength, platformHeader.getLength() + msgLength);
						session.removeAttribute(TasClientCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE);
						session.removeAttribute(TasClientCodecFactory.RECEIVED_MESSAGE_LENGTH);
						TasMessage tasMessage = decode(in.buf(), platformHeader);
						out.write(tasMessage);
						// Handler 에서 exception 발생 했을 경우 TAS_MESSAGE Attribute 가 존재하면 
						// Decoding 까지는 성공했다는 가정하에 처리하도록 하기 위함 
						session.setAttribute("TAS_MESSAGE", tasMessage);
						
						in.mark();
						return true;
					} else {
						fileProgressEvent(platformHeader.getLength() + msgLength, in.remaining());
						session.setAttribute(TasClientCodecFactory.RECEIVED_MESSAGE_LENGTH, receivedMessageLength + in.remaining());
						if(ipos !=0) {
							try {
								in.reset();
							} catch (InvalidMarkException e) {
								
							}
						} else {
							in.rewind();
						}
						
						return false;
					}
				} else {
					if(ipos !=0) {
						try {
							in.reset();
						} catch (InvalidMarkException e) {
							
						}
					} else {
						in.rewind();
					}
					return false;
				}
			} else {
				return false;
			}
		} catch(Exception e) {
			throw new TasClientException(e);
		}
	}
	
	private TasMessage decode(ByteBuffer byteBuffer, PlatformHeader platformHeader) {
		short bodyType = platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class);
		ByteBufferDecoder decoder = new ByteBufferDecoder(byteBuffer, new DefinitionFinder(protocolConfig, platformHeader), false);
		switch(bodyType) {
		case PlatformHeader.BODY_TYPE_JSON :
			decoder.setBodyDecoder(new JsonBodyDecoder());
			break;
		}
		
		TasBean[] resHeaderBody = decoder.decode();
		return createTasMessage(platformHeader, resHeaderBody);
	}
	
	private final TasMessage decode(FileChannel fc) {
		String phv = null;
		try {
			fc.position(0);
			phv = getPlatformHeaderVersion(fc);
			PlatformHeader platformHeader = PlatformHeaderFactory.getPlatformHeader(phv);
			platformHeader.decode(fc);
			short bodyType = platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class);
			FileChannelDecoder decoder = new FileChannelDecoder(fc, new DefinitionFinder(protocolConfig, platformHeader), false);
			switch(bodyType) {
			case PlatformHeader.BODY_TYPE_JSON :
				decoder.setBodyDecoder(new JsonBodyDecoder());
				break;
			}
			
			TasBean[] reqHeaderBody = decoder.decode();
			fc.close();
			return createTasMessage(platformHeader, reqHeaderBody);
		} catch (IOException e) {
			throw new TasClientException(e);
		}
	}
	
	private final TasMessage createTasMessage(PlatformHeader platformHeader, TasBean[] resHeaderBody) {
		TasResponse response = createTasResponse(platformHeader, resHeaderBody);
		TasRequest request = createTasRequest(response);
		return new TasMessage(request, response);
	}
	
	private final TasResponse createTasResponse(TasBean platformHeader, TasBean[] resHeaderBody) {
		TasBean header = resHeaderBody[0];
		TasBean body = resHeaderBody[1];
		return new TasResponse(platformHeader, header, body);
	}
	
	private final String getPlatformHeaderVersion(ByteBuffer buffer) {
		byte[] buf = new byte[PlatformHeader.VERSION_LENGTH];
		buffer.get(buf);
		String phvField = new String(buf); // Platform Header Version Field

		buffer.reset();
		if(phvField.startsWith("PHV")) {
			return phvField;
		} else {
			return "PHV100";
		}
	}

	private final TasRequest createTasRequest(TasResponse response) {
		TasBean platformHeader = response.getPlatformHeader();
		TasBean header = response.getHeader();
		TasBean body = new TasBean();
		return new TasRequest(platformHeader, header, body);
	}
	
	private final String getPlatformHeaderVersion(FileChannel fc) {
		String phv = "";
		try {
			ByteBuffer buffer = ByteBuffer.allocate(PlatformHeader.VERSION_LENGTH);
			fc.read(buffer);
			String phvField = new String(buffer.array()); // Platform Header Version Field

			fc.position(0);
			if(phvField.startsWith("PHV")) {
				phv = phvField;
			} else {
				phv = "PHV100";
			}
			
			return phv;
		} catch (IOException e) {
			throw new TasClientException(e);

		}
	}
	
	private final void dump(IoBuffer in) {
		try {
			int tmpPositon = in.position();
			int bufferSize = in.limit() - in.position();
			
			byte[] data = new byte[bufferSize];
			in.get(data);
			System.out.println("===================================================================================================");
			System.out.println("##### Client --> Server #####");
			System.out.println("===================================================================================================");
			HexDump.dump(data, 0, System.out, 0);
			in.position(tmpPositon);
		} catch (Exception e) {
			throw new TasDumpPrintException(e);  //exceptionCaught 에서 버전 구분 필요
		}
	}
	
	private final void fileProgressEvent(int totalMessageLengthToReceive, int receivedMessageLength) {
		if(progressListener != null) {
			progressListener.onMessageReceived(totalMessageLengthToReceive, receivedMessageLength);
		}
	}
}