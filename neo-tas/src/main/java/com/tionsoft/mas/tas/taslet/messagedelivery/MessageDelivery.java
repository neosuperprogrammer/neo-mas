package com.tionsoft.mas.tas.taslet.messagedelivery;

import java.io.*;
import java.net.*;

import org.apache.commons.configuration.*;
import org.apache.commons.configuration.tree.xpath.*;
import org.apache.commons.io.*;
import org.slf4j.*;

import com.tionsoft.mas.tas.bean.*;
import com.tionsoft.mas.tas.bean.platform.*;
import com.tionsoft.mas.tas.client.*;
import com.tionsoft.mas.tas.client.ssl.*;
import com.tionsoft.mas.tas.taslet.*;

public abstract class MessageDelivery {
	
	
	//PMAS 연동을 위한 Configuration
	private XMLConfiguration MDconfiguration;
	private TasClient tasClient = null;
	private boolean exceptionFlag = false;
	private TasClientSslConfig tasClientSslConfig = null;
	private Logger LOGGER;
	
	
	public void setMDConfiguration(XMLConfiguration MDconfiguration) {
		this.MDconfiguration = MDconfiguration;
		MDconfiguration.setExpressionEngine(new XPathExpressionEngine());
	}
	
	
	public MessageDelivery() {
		
	}
	
	/**
	 * PBI 서버로 메세지 전송처리
	 * @param tasRequest
	 */
	public com.tionsoft.mas.tas.client.message.TasResponse  requestToTCPServer(TasRequest tasRequest,TasResponse tasResponse) {
		LOGGER = LoggerFactory.getLogger("tas");
		
		LOGGER.debug("=================== [MessageDelivery requestToTCPServer start] ===================");
		com.tionsoft.mas.tas.client.message.TasResponse tasClientResponse = null;
		//Configuration 에서 pbi 접속정보를 가져옴
		String	ip			= MDconfiguration.getString("configuration/pbi/ip");
		int		port		= MDconfiguration.getInt("configuration/pbi/port");
		int		idleTime	= MDconfiguration.getInt("configuration/pbi/idleTime");
		int		responseTimeout	= MDconfiguration.getInt("configuration/pbi/responseTimeout");
		String	psProtocol	= MDconfiguration.getString("configuration/pbi/protocol");
		String	prefix		= FilenameUtils.getFullPath(MDconfiguration.getBasePath());
		LOGGER.debug("MessageDelivery  ip:"+ip);
		LOGGER.debug("MessageDelivery port:"+port);
		LOGGER.debug("MessageDelivery idleTime:"+idleTime);
		LOGGER.debug("MessageDelivery responseTimeout:"+responseTimeout);
		LOGGER.debug("MessageDelivery psProtocol:"+psProtocol);
		LOGGER.debug("MessageDelivery prefix:"+prefix);
		
		
		//Configuration 에서 pbi 접속시 SSLFilter 정보를 가져옴
		String	sslFilterEnable ="false";
		try{
			sslFilterEnable	= MDconfiguration.getString("configuration/pbi/sslFilter/enable");
			if(sslFilterEnable != null && sslFilterEnable.equalsIgnoreCase("true")) {
				String	certfile = MDconfiguration.getString("configuration/pbi/sslFilter/certfile");
				String	protocol = MDconfiguration.getString("configuration/pbi/sslFilter/protocol");
				String	keyManagerAlgorithm = MDconfiguration.getString("configuration/pbi/sslFilter/keyManagerAlgorithm");
				String	passwd = MDconfiguration.getString("configuration/pbi/sslFilter/passwd");
				String	certType = MDconfiguration.getString("configuration/pbi/sslFilter/certType");
				
				tasClientSslConfig = getTasClientSslConfig(prefix,certfile,protocol,keyManagerAlgorithm,passwd,certType);
				LOGGER.debug("MessageDelivery sslFilter certfile:"+certfile);
				LOGGER.debug("MessageDelivery sslFilter protocol:"+protocol);
				LOGGER.debug("MessageDelivery sslFilter keyManagerAlgorithm:"+keyManagerAlgorithm);
				LOGGER.debug("MessageDelivery sslFilter passwd:"+passwd);
				LOGGER.debug("MessageDelivery sslFilter certType:"+certType);

			}
		}catch (Exception e) {
			sslFilterEnable ="false";
			exceptionFlag = true;
			e.printStackTrace();
		}
		
		//2. TasClient 생성
		tasClient = createTasClient(ip,port,idleTime,psProtocol,prefix,sslFilterEnable);

		//4. TCP 전송용 Request Message 생성
		com.tionsoft.mas.tas.client.message.TasRequest psRequest = createPBIRequestMessage(tasRequest);
		
		try {

			tasClientResponse = tasClient.sendSyncRequest(psRequest, responseTimeout);
			LOGGER.debug("=================== [MessageDelivery requestToTCPServer end] ===================");
		} catch (Exception e) {
			exceptionFlag = true;
			e.printStackTrace();
			throw new TasletException(e);
		} finally {
			if(tasClient != null){
				tasClient.close();
			}
			
		}
		return tasClientResponse;
	}
	
	/**
	 * TCP 전송용 TasClient 생성
	 * 
	 * @return void
	 */
	private TasClient createTasClient(String ip,int port,int idleTime,String psProtocol,String prefix,String sslFilterEnable) {
		LOGGER.debug("createTasClient sslFilterEnable:"+sslFilterEnable);
		
		XMLConfiguration psProtocolConfig = null;
		try {
			psProtocolConfig = new XMLConfiguration(prefix + psProtocol);
			psProtocolConfig.setExpressionEngine(new XPathExpressionEngine());
		} catch (Exception e) {
			exceptionFlag = true;
			e.printStackTrace();
			throw new TasletException(e);
		}

		try {
			if(sslFilterEnable != null && sslFilterEnable.equalsIgnoreCase("true")) {
				//sslFilter 적용
				tasClient = new TasClient(ip, port, idleTime, psProtocolConfig, tasClientSslConfig);
			}else{
				tasClient = new TasClient(ip, port, idleTime, psProtocolConfig);
			}
			
//			tasClient.connect(); 코멘트
			
			
//		} catch (IOException ioe) {
//			throw new RuntimeException("Cannot Load Cert.");
		} catch (Exception e) {
			exceptionFlag = true;
			e.printStackTrace();
			throw new TasletException(e);
		}
		
		return tasClient;
	}
	
	
	/**
	 * TCP 전송용 메세지(TasRequest) 생성
	 * 
	 * @param	TasRequest - Client로 부터 받은 Request
	 * @return	TasRequest - PBI 서버쪽에 전달할 Request
	 */
	abstract protected com.tionsoft.mas.tas.client.message.TasRequest createPBIRequestMessage(TasRequest tasRequest);
	
	
	/**
	 * TCP 전송용시 SSLFileter 적용을 위해서  TasClientSslConfig 생성
	 * @param	cerfile - cerfile 위치
	 * @param	protocol - protocol (TLS)
	 * @param	keyManagerAlgorithm - keyManagerAlgorithm(SunX509)
	 * @param	passwd - cerfile의 passwd
	 * @param	certType - certType
	 * @return	TasClientSslConfig
	 */
	private TasClientSslConfig getTasClientSslConfig(String	prefix,String cerfile,String protocol, String keyManagerAlgorithm,
			String passwd, String certType) {
		URI uri = null;
		try {
			uri = new URI(prefix + cerfile);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LOGGER.debug("getTasClientSslConfig uri:"+uri);
		
		File certfile = new File(uri);
		
		InputStream inCertfile = null;
		try {
			inCertfile = new FileInputStream(certfile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TasClientSslConfig tasClientSslConfig = new TasClientSslConfig(protocol, keyManagerAlgorithm, passwd, inCertfile, certType);
		
		return tasClientSslConfig;
		
	}

	/**
	 * TCP 전송용 메세지(tasServerResponse) 생성
	 * 
	 * @param	TasRequest - Client로 부터 받은 Request
	 * @return	tasServerResponse - Response 메세지 생성
	 */
	public void createResponseMessage(com.tionsoft.mas.tas.client.message.TasResponse tasResponse, com.tionsoft.mas.tas.taslet.TasResponse tasServerResponse){
		
		PlatformHeader platformHeader = tasResponse.getPlatformHeader();
		TasBean header = tasResponse.getHeader();
		TasBean body = tasResponse.getBody();
	
		tasServerResponse.setPlatformHeader(platformHeader);
		tasServerResponse.setHeader(header);
		tasServerResponse.setBody(body);

	}
	


	/**
	 * JsonStringData 검색
	 */
	public String getJsonStringData(TasBean bodyBean, String key) {
		String searchKey = key+PlatformHeader.JSONSTRINGDATA;
		LOGGER.debug("getJsonStringData  key:["+key+"] searchKey:["+searchKey+"]");
		
		String jsonStringData = "";
		Object obj = bodyBean.getValue(searchKey);
		if(obj instanceof String) {
			jsonStringData = bodyBean.getValue(searchKey, String.class);
			LOGGER.debug("MessageDelivery jsonStringData:["+searchKey+"]"+jsonStringData);
		}else{

		}
		return jsonStringData;
	}
	
}
