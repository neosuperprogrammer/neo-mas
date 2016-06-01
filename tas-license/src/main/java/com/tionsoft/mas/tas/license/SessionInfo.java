package com.tionsoft.mas.tas.license;

import org.springframework.stereotype.Component;


@Component
public class SessionInfo {
	private int sessionCount = 0;
	private boolean isViolated = false;
	
	
	
	public int getSessionCount() {
		return sessionCount;
	}
	public void setSessionCount(int sessionCount) {
		this.sessionCount = sessionCount;
	}
	public boolean isViolated() {
		return isViolated;
	}
	public void setViolated(boolean isViolated) {
		this.isViolated = isViolated;
	}
	
	
	
	
}
