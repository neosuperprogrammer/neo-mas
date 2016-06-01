package com.tionsoft.mas.tas.handler;

import org.apache.mina.handler.chain.IoHandlerCommand;

import com.tionsoft.mas.tas.TasContext;

public abstract class TasCommand implements IoHandlerCommand {
	
	abstract public void setTasContext(TasContext tasContext); 
	
}