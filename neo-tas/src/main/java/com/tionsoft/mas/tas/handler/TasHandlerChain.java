package com.tionsoft.mas.tas.handler;

import java.util.Map;
import java.util.Set;

import org.apache.mina.handler.chain.IoHandlerChain;
import org.apache.mina.handler.chain.IoHandlerCommand;

import com.tionsoft.mas.tas.TasContext;

public class TasHandlerChain extends IoHandlerChain {
	
	private Map<String, IoHandlerCommand> preCommands;
	private TasCommand command;
	private Map<String, IoHandlerCommand> postCommands;
	
	public void setPreCommands(Map<String, IoHandlerCommand> commands) {
		this.preCommands = commands;
	}
	
	public void setCommand(TasCommand command) {
		this.command = command;
	}
	
	public void setPostCommands(Map<String, IoHandlerCommand> commands) {
		this.postCommands = commands;
	}
	
	public void build(TasContext tasContext) {
		if(preCommands != null && preCommands.size() > 0) {
			Set<String> keys = preCommands.keySet();
			for(String key : keys) {
				addLast(key, preCommands.get(key));
			}
		}
		command.setTasContext(tasContext);
		addLast("TasCommand", command);
		if(postCommands != null && postCommands.size() > 0) {
			Set<String> keys = postCommands.keySet();
			for(String key : keys) {
				addLast(key, postCommands.get(key));
			}
		}
	}
}