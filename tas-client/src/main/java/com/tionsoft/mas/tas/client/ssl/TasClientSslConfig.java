package com.tionsoft.mas.tas.client.ssl;

import java.io.InputStream;

public class TasClientSslConfig {
	
	private String protocol = null;
	private String keyManagerAlgorithm = null;
	private String passwd = null;
	private InputStream inCertFile = null;
	private String certType = "JKS";
	
	public TasClientSslConfig(String protocol, String keyManagerAlgorithm,
			String passwd, InputStream inCertFile, String certType) {
		super();
		this.protocol = protocol;
		this.keyManagerAlgorithm = keyManagerAlgorithm;
		this.passwd = passwd;
		this.inCertFile = inCertFile;
		this.certType = certType;
	}

	public TasClientSslConfig(String protocol, String keyManagerAlgorithm,
			String passwd, InputStream inCertFile) {
		super();
		this.protocol = protocol;
		this.keyManagerAlgorithm = keyManagerAlgorithm;
		this.passwd = passwd;
		this.inCertFile = inCertFile;
	}
	
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getKeyManagerAlgorithm() {
		return keyManagerAlgorithm;
	}
	public void setKeyManagerAlgorithm(String keyManagerAlgorithm) {
		this.keyManagerAlgorithm = keyManagerAlgorithm;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public InputStream getInCertFile() {
		return inCertFile;
	}
	public void setInCertFile(InputStream inCertFile) {
		this.inCertFile = inCertFile;
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
	}

	@Override
	public String toString() {
		return "TasClientSslConfig [protocol=" + protocol
				+ ", keyManagerAlgorithm=" + keyManagerAlgorithm + ", passwd="
				+ passwd + ", inCertFile=" + inCertFile + ", certType="
				+ certType + "]";
	}
	
	
}
