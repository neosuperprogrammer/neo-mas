package com.tionsoft.mas.tas.taslet;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.tionsoft.platform.error.ErrorCode;
import com.tionsoft.platform.exception.PlatformException;
import com.tionsoft.platform.utils.StringUtils;


@Component
@Aspect
@DependsOn("tasBackgroundQueueItemListener")
public class TasletIntercepter implements Intercepter  {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	private TasletIntercepterBeforeHandler beforeHandler;
	
	
	public TasletIntercepter()
	{
		System.out.println(">>>>>>>>>>>>>>>>TasletIntercepter  init>>>>>>>>>>>>>>>>>>>>>");
	}
	
	
	@Around("execution(* com.tionsoft.**.**.taslet.**.execute(..))")
	public void tasletAroundHandler(ProceedingJoinPoint joinPoint) throws PlatformException
	{
		String intercepter = "true";
		intercepter = StringUtils.defaultIfEmpty(System.getProperty("tmas.intercepter.handling"), "true") ;
		
		if(!Boolean.parseBoolean(intercepter)){
			if(logger.isDebugEnabled()) logger.debug("tasletAroundHandler", "tas around handler is skipped because of the value of tmas.intercepter.handling,false ");
		}
		
		Object [] args = joinPoint.getArgs();
		
		TasRequest req=null;
		TasResponse res=null;
		
		for(int i=0; i <args.length;i++){
			if(args[i] instanceof TasRequest) req = (TasRequest)args[i];
			if(args[i] instanceof TasResponse) res = (TasResponse)args[i];
		}
		
		TasRequest tReq=null;
		TasResponse tRes=null;
		
		try{
			
			tReq = req.deepCopy();
			tRes = res.deepCopy();
			
			tasletBeforeHandler(tReq,tRes);
			joinPoint.proceed(args);
			tRes = res.deepCopy();
			
			
		}
		catch(Throwable e)
		{
			if(e instanceof PlatformException ){
				throw PlatformException.createException(((PlatformException) e).getErrorCode(), ((PlatformException) e).getErrorNumber(), new Exception(e));
			}else if(e instanceof TasletException){
				
				Throwable t ;
				t = e;
				while(e!=null)
				{
					if(e.getCause() instanceof PlatformException){
						e=e.getCause();
						throw PlatformException.createException(((PlatformException) e).getErrorCode(), Short.valueOf(((PlatformException)e).getErrorNumber()), new Exception(t));
					}else if(e.getCause()!=null){
						e=e.getCause();
					}else {
						throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.APPLICATION, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.APPLICATION_ERROR, new Exception(t));
					}
				}
				
				
			}else {
				throw PlatformException.createException(ErrorCode.TION_MEAP_APPLICATION_SYSTEM_ERROR.APPLICATION, ErrorCode.TION_MEAP_APPLICATION_SYSTEM_CODE.APPLICATION_ERROR, new Exception(e));
			}
			
		}finally{
			//tasletAfterHandler(tReq, tRes);
		}
	}


	@Override
	public void tasletBeforeHandler(TasRequest req, TasResponse res)throws PlatformException {
		
		beforeHandler.handle(req, res);
	}


	@Override
	public void tasletAfterHandler(TasRequest req, TasResponse res)	throws PlatformException {
		//at this moment
		//Logic is not required
	}
	
	
	
	

}
