package com.tionsoft.mas.tas.pushlet;

import org.apache.mina.core.session.IoSession;

import com.tionsoft.mas.tas.taslet.TasMessage;

public interface InterestProcessor  {
	
	public void addInterest(TasMessage tasMessage, IoSession session);
	public void removeInterest(TasMessage tasMessage, IoSession session);
}