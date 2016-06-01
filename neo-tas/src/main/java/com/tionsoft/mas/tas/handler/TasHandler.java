package com.tionsoft.mas.tas.handler;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.handler.chain.IoHandlerChain;

import com.tionsoft.mas.tas.TasContext;
import com.tionsoft.platform.listener.TasSessionClosedListener;
import com.tionsoft.platform.listener.TasSessionIdleListener;

public abstract class TasHandler implements IoHandler {
	
	abstract public void setTasContext(TasContext tasContext);
	abstract public void setHandlerChain(IoHandlerChain handlerChain);
	abstract public void setTasSessionIdleListener(TasSessionIdleListener idleListener);
	abstract public void setTasSessionClosedListener(TasSessionClosedListener closedListener);
}
