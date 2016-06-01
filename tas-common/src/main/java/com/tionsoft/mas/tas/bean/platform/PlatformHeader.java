package com.tionsoft.mas.tas.bean.platform;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.tionsoft.mas.tas.bean.TasBean;

public abstract class PlatformHeader extends TasBean {
	
	private static final long serialVersionUID = 787340191985747877L;
	
	public static final int BODY_TYPE_BINARY = 1;
	public static final int BODY_TYPE_JSON = 2;
	public static final int BODY_TYPE_XML = 3;
	
	public static final String PLATFORM_APPLICATION_ID = "PFAP"; // keep alive 을 위한값
	public static final String KEEPALIVE_MESSAGE_ID = "M00000000"; // keep alive 을 위한값
	public static final String PUSH_MESSAGE_ID = "M00000001"; // Push 을 위한값
	public static final String PUSH_ACK_MESSAGE_ID = "M00000002"; // Ack For Push 을 위한값	
	public static final String PUSH_MESSAGE_ID_FOR_PI = "M00000003"; // Push 을 위한값
			
	
	public static final String VERSION = "VERSION";
	public static final String APPLICATION_ID = "APPLICATION_ID";
	public static final String MESSAGE_ID = "MESSAGE_ID";
	public static final String SESSION_ID = "SESSION_ID";
	public static final String TRANSACTION_ID = "TRANSACTION_ID";
	public static final String SERVICE_ID = "SERVICE_ID";
	public static final String IMEI = "IMEI";
	
	public static final String WIFI_MAC = "WIFI_MAC";
	
	public static final String MSISDN = "MSISDN";
	
	public static final String MODEL_NO = "MODEL_NO";
	public static final String ISP_NAME = "ISP_NAME";
	public static final String OS_TYPE = "OS_TYPE";
	public static final String OS_VERSION = "OS_VERSION";
	public static final String UUID = "UUID";
	
	public static final String BODY_TYPE = "BODY_TYPE";
	public static final String STATUS_CODE = "STATUS_CODE";
	public static final String HEADER_LENGTH = "HEADER_LENGTH";
	public static final String BODY_LENGTH = "BODY_LENGTH";
	
	public static final String JSONSTRINGDATA = "_JSONSTRINGDATA";//body type이 json일때도 json string을 taslet에 전달함 
	
	public static final int VERSION_LENGTH = 6; // String 6자리
	public static final int APPLICATION_ID_LENGTH = 4; // String 4자리
	public static final int MESSAGE_ID_LENGTH = 9; // String 9자리
	public static final int SESSION_ID_LENGTH = 8; // Long 
	public static final int TRANSACTION_ID_LENGTH = 8; // Long
	public static final int SERVICE_ID_LENGTH = 4; // int
	public static final int IMEI_LENGTH = 20; // String
	
	public static final int WIFI_MAC_LENGTH = 20; // String
	public static final int WIFI_MAC2_LENGTH = 32; // String
	
	public static final int MSISDN_LENGTH = 11; // String  PHV101
	public static final int MSISDN2_LENGTH = 20; // String  PHV102
	
	
	public static final int MODEL_NO_LENGTH = 30; // String
	public static final int ISP_NAME_LENGTH = 10; // String
	public static final int OS_TYPE_LENGTH = 1; // String
	public static final int OS_VERSION_LENGTH = 20; // String
	public static final int UUID_LENGTH = 24; // String
	public static final int UUID2_LENGTH = 32; // String
	
	public static final int BODY_TYPE_LENGTH = 2; // short
	public static final int STATUS_CODE_LENGTH = 2; // short
	
	public static final int HEADER_LENGTH_LENGTH = 4; // integer
	public static final int BODY_LENGTH_LENGTH = 4; //integer
	
	protected transient ByteBuffer buffer;
	protected transient FileChannel fc;
	
	abstract public int getLength();
	abstract public void decode(ByteBuffer buffer);
	abstract public void decode(FileChannel fc);
	abstract public PlatformHeader deepCopy();
	abstract public PlatformHeader structureCopy();
	
}