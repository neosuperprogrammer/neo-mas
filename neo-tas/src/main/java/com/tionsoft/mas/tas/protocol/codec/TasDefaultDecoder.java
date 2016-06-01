package com.tionsoft.mas.tas.protocol.codec;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

import org.apache.commons.io.*;
import org.apache.mina.core.buffer.*;
import org.apache.mina.core.session.*;
import org.apache.mina.filter.codec.*;
import org.slf4j.*;

import com.tionsoft.mas.tas.*;
import com.tionsoft.mas.tas.bean.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.exception.*;
import com.tionsoft.mas.tas.protocol.codec.decoder.*;
import com.tionsoft.mas.tas.protocol.definition.*;
import com.tionsoft.mas.tas.taslet.*;

class TasDefaultDecoder extends CumulativeProtocolDecoder {
	
	protected final TasContext tasContext;
	private Logger LOGGER = null;
	
	public TasDefaultDecoder(TasContext tasContext) {
		this.tasContext = tasContext;
		LOGGER = LoggerFactory.getLogger(tasContext.getTcpAppConfig().getName());
	}
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
		int ipos = in.position();
		String hexdump = System.getProperty("mas.tas.logging.hexdump");
		if(hexdump != null && hexdump.equalsIgnoreCase("true")) {
			printHexDump(in);	
		}
		else {
			printHexDump(in);
		}
		
		try {
			
			String mode = null;
			if(session.getAttribute(TasCodecFactory.MODE) != null) {
				mode = (String)session.getAttribute(TasCodecFactory.MODE);
			}
			
			if(mode != null && mode.equalsIgnoreCase("FILE")) {
				int totalMessageLengthToReceive = (Integer)session.getAttribute(TasCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE);
				int receivedMessageLength = (Integer)session.getAttribute(TasCodecFactory.RECEIVED_MESSAGE_LENGTH);
				FileChannel fc = (FileChannel)session.getAttribute(TasCodecFactory.TEMP_FILE_CHANNEL);
				if((receivedMessageLength + in.remaining()) > totalMessageLengthToReceive) {
					int requiredToSaveSize = totalMessageLengthToReceive - receivedMessageLength;
					int originaLimit = in.limit();
					in.limit(requiredToSaveSize);
					fc.write(in.buf());
					in.limit(originaLimit);
					session.removeAttribute(TasCodecFactory.MODE);
					session.removeAttribute(TasCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE);
					session.removeAttribute(TasCodecFactory.RECEIVED_MESSAGE_LENGTH);
					session.removeAttribute(TasCodecFactory.TEMP_FILE_CHANNEL);
					TasMessage tasMessage = decode(session, fc);
					String tmpFilePath = (String)session.getAttribute(TasCodecFactory.TEMP_FILE_PATH);
					File tmpFile = new File(tmpFilePath);
					tmpFile.delete();
					out.write(tasMessage);
					session.setAttribute(TasCodecFactory.TAS_MESSAGE, tasMessage);

				} else if((receivedMessageLength + in.remaining()) == totalMessageLengthToReceive) {
					fc.write(in.buf());
					dispose(session);
					session.removeAttribute(TasCodecFactory.MODE);
					session.removeAttribute(TasCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE);
					session.removeAttribute(TasCodecFactory.RECEIVED_MESSAGE_LENGTH);
					session.removeAttribute(TasCodecFactory.TEMP_FILE_CHANNEL);
					TasMessage tasMessage = decode(session, fc);
					String tmpFilePath = (String)session.getAttribute(TasCodecFactory.TEMP_FILE_PATH);
					File tmpFile = new File(tmpFilePath);
					tmpFile.delete();
					out.write(tasMessage);
					session.setAttribute(TasCodecFactory.TAS_MESSAGE, tasMessage);

				} else {
					session.setAttribute(TasCodecFactory.RECEIVED_MESSAGE_LENGTH, receivedMessageLength + in.remaining());
					fc.write(in.buf());
					dispose(session);
				}
				return true;
			}
			if(in.remaining() >= PlatformHeader.VERSION_LENGTH) { // PHV100, PHV101, PHV102
				in.mark();
				String phv = getPlatformHeaderVersion(in.buf());
				PlatformHeader platformHeader = PlatformHeaderFactory.getPlatformHeader(phv);
				if (in.remaining() >= platformHeader.getLength()) {
					platformHeader.decode(in.buf());
					int headerLength = platformHeader.getValue(PlatformHeader.HEADER_LENGTH, Integer.class);
					int bodyLength = platformHeader.getValue(PlatformHeader.BODY_LENGTH, Integer.class);
					
					int msgLength = headerLength + bodyLength;
					
					if(msgLength > (3 * 1024 * 1024) ) { // 3M 이상이면 FILE 모드로 자동 변환
						//에러시 사용할 platformheader 정보
						session.setAttribute(TasCodecFactory.PLATFORMHEADER_ERROR_MESSAGE, platformHeader);
						
						session.setAttribute(TasCodecFactory.MODE, "FILE");
						String tmpFile = System.getProperty("mas.tmpdir") + "/" + System.nanoTime();
						RandomAccessFile file = new RandomAccessFile(tmpFile, "rw");
						FileChannel fc = file.getChannel();
						in.reset();
						session.setAttribute(TasCodecFactory.TOTAL_MESSAGE_LENGTH_TO_RECEIVE, msgLength + platformHeader.getLength());
						session.setAttribute(TasCodecFactory.RECEIVED_MESSAGE_LENGTH, in.remaining());
						fc.write(in.buf()); // 처음 들어온 데이터를  저장한다.
						session.setAttribute(TasCodecFactory.TEMP_FILE_PATH, tmpFile);
						session.setAttribute(TasCodecFactory.TEMP_FILE_CHANNEL, fc);
						dispose(session);
						return true;
					}
					if (in.remaining() == msgLength) {
						//에러시 사용할 platformheader 정보
						session.setAttribute(TasCodecFactory.PLATFORMHEADER_ERROR_MESSAGE, platformHeader);
						
						TasMessage tasMessage = decode(session, in.buf(), platformHeader);
						out.write(tasMessage);
						// Handler 에서 exception 발생 했을 경우 TAS_MESSAGE Attribute 가 존재하면 
						// Decoding 까지는 성공했다는 가정하에 처리하도록 하기 위함 
						session.setAttribute(TasCodecFactory.TAS_MESSAGE, tasMessage);

						return true;
					} else if(in.remaining() > msgLength) {// 연속으로 들어 오는 데이터 처리
						//에러시 사용할 platformheader 정보
						session.setAttribute(TasCodecFactory.PLATFORMHEADER_ERROR_MESSAGE, platformHeader);
						
						TasMessage tasMessage = decode(session, in.buf(), platformHeader);
						out.write(tasMessage);
						// Handler 에서 exception 발생 했을 경우 TAS_MESSAGE Attribute 가 존재하면 
						// Decoding 까지는 성공했다는 가정하에 처리하도록 하기 위함 
						session.setAttribute(TasCodecFactory.TAS_MESSAGE, tasMessage);

						
						// 첫번 째 doDecode() callBack 에서 두 개의 메시지가 연속으로 들어 왔을 때(첫번째 메시지는 모두 포함하고 두번째 메시지는 일부만 포함한 경우)
						// 이 조건 절에서 첫번째 메시지를 Decoding 하고 return true 를 한다. 
						// return true 를 하였지만 두번 째 doDecode callBack 시 이전 callBack에서 Decoding 했던 데이터가를 포함하는 IoBuffer 가 넘어온다.
						// 두번 째 doDecode() callBack 으로 넘어온 IoBuffer 가 포함하는 두번째 메시지의 Data 길이가 msgLength 보다 작다면 
						// 두번 째 doDecode() callBack 처리는 아래 else 절로 넘어가게 된다.
						// 이때 rewind() 호출하게 되면 세번 째 doDecode() callBack 에서 이전에 decoding 한 데이터를 다시 decoding 하게 되어
						// 메시지 중복 수신이 발생할 수 있다.
						// 따라서 연속으로 들어온 메시지의 두번 째 메시지의 시작위치를 mark 해 두고
						// 세번 째 doDecode() callBack 에 넘어온 두번 째 Data의 길이가 여전히 모자랄 경우 rewind() 가 아닌 reset() 을 호출하여
						// 다시 두번 째 메시지의 시작을 가리키도록 할 필요가 있다.
						in.mark();  
						return true;
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
				// rewind() 혹은 reset 하지 않아도 된다.
				return false;
			}
		} catch(Exception e) {
			throw new TasException(e);

		}
	}
	
	private final TasMessage decode(IoSession ioSession, ByteBuffer byteBuffer, PlatformHeader platformHeader) {
		short bodyType = platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class);
		ByteBufferDecoder decoder = new ByteBufferDecoder(byteBuffer, new DefinitionFinder(tasContext.getProtocolConfig(), platformHeader), true);
		switch(bodyType) {
		case PlatformHeader.BODY_TYPE_JSON :
			decoder.setBodyDecoder(new JsonBodyDecoder());
			break;
		}
		
		TasBean[] reqHeaderBody = decoder.decode();
		return createTasMessage(ioSession, platformHeader, reqHeaderBody);
	}
	
	private final TasMessage decode(IoSession ioSession, FileChannel fc) {
		try {
			fc.position(0);
			String phv = getPlatformHeaderVersion(fc);
			PlatformHeader platformHeader = PlatformHeaderFactory.getPlatformHeader(phv);
			platformHeader.decode(fc);
			short bodyType = platformHeader.getValue(PlatformHeader.BODY_TYPE, Short.class);
			FileChannelDecoder decoder = new FileChannelDecoder(fc, new DefinitionFinder(tasContext.getProtocolConfig(), platformHeader), true);
			switch(bodyType) {
			case PlatformHeader.BODY_TYPE_JSON :
				decoder.setBodyDecoder(new JsonBodyDecoder());
				break;
			}
			
			TasBean[] reqHeaderBody = decoder.decode();
			fc.close();
			return createTasMessage(ioSession, platformHeader, reqHeaderBody);
		} catch (IOException e) {
			throw new TasException(e);
		}
	}
	
	/**
	 * request.setUselessSession(tasContext.getUselessSessionStore()); is added
	 * @param ioSession
	 * @param platformHeader
	 * @param reqHeaderBody
	 * @return
	 */
	private final TasMessage createTasMessage(IoSession ioSession, PlatformHeader platformHeader, TasBean[] reqHeaderBody) {
		
		TasRequest request = createTasRequest(ioSession, platformHeader, reqHeaderBody);
		TasResponse response = createTasResponse(request);
		request.setUselessSession(tasContext.getUselessSessionStore());
		
		return new TasMessage(request, response);
	}
	
	private final TasRequest createTasRequest(IoSession ioSession, TasBean platformHeader, TasBean[] reqHeaderBody) {
		TasBean header = reqHeaderBody[0];
		TasBean body = reqHeaderBody[1];
//		return new TasRequest(tasContext.getSessionCache(), ioSession, platformHeader, header, body, ioSession.getId(),tasContext.getSharedMemory());
		return new TasRequest(tasContext.getSessionCache(), ioSession, platformHeader, header, body, ioSession.getId());
	}

	private final TasResponse createTasResponse(TasRequest request) {
		TasBean platformHeader = request.getPlatformHeader();
		TasBean header = request.getHeader();
		TasBean body = new TasBean();
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
	
	private final String getPlatformHeaderVersion(FileChannel fc) {
		try {
			ByteBuffer buffer = ByteBuffer.allocate(PlatformHeader.VERSION_LENGTH);
			fc.read(buffer);
			String phvField = new String(buffer.array()); // Platform Header Version Field

			fc.position(0);
			if(phvField.startsWith("PHV")) {
				return phvField;
			} else {
				return "PHV100";
			}
		} catch (IOException e) {
			throw new TasException(e);
		}
	}

	private final void printHexDump(IoBuffer in) {
		try {
			int tmpPositon = in.position();
			int bufferSize = in.limit() - in.position();
			
			byte[] data = new byte[bufferSize];
			in.get(data);
			System.out.println("===================================================================================================");
			System.out.println("##### Client --> Server [" + tasContext.getTcpAppConfig().getName() + "] #####");
			System.out.println("===================================================================================================");
			HexDump.dump(data, 0, System.out, 0);
			in.position(tmpPositon);
		} catch (Exception e) {
//			throw new TasException(ErrorTypeOld.ERROR_TYPE_UNKNOWN, e);
			
			throw new TasDumpPrintException(e);  //exceptionCaught 에서 버전 구분 필요
		}
	}
}