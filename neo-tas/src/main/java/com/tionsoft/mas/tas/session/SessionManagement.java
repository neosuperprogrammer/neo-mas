package com.tionsoft.mas.tas.session;

import org.apache.mina.core.service.IoAcceptor;

public interface SessionManagement {

	public void setIoAcceptor(IoAcceptor acceptor);
	public int getCurrentSessionCnt();
	public boolean isViolated();
	
}
