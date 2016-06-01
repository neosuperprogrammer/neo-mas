package com.tionsoft.mas.tas.taslet;

import org.aspectj.lang.ProceedingJoinPoint;

import com.tionsoft.mas.tas.taslet.TasRequest;
import com.tionsoft.mas.tas.taslet.TasResponse;
import com.tionsoft.platform.exception.PlatformException;


/**
 * Interface for intercepter is used in tas application logic
 * This implement class must use spring aop
 * @author Administrator
 *
 */
public interface Intercepter {
	
	
	/**
	 * before taslet
	 * @param req
	 * @param res
	 * @throws PlatformException
	 */
	public void tasletBeforeHandler(TasRequest req, TasResponse res) throws PlatformException;
	
	/**
	 * 
	 * @param req
	 * @param res
	 * @throws PlatformException
	 */
	public void tasletAfterHandler(TasRequest req, TasResponse res) throws PlatformException;
	public void tasletAroundHandler(ProceedingJoinPoint joinPoint) throws PlatformException;
}