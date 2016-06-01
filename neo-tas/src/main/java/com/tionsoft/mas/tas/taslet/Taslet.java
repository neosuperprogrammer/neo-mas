package com.tionsoft.mas.tas.taslet;

/**
 * TAS는 Staged Event Driven Architecture 로 
 * Connect.xml 에 Taslet Interface을 구현한 Class을 호출한다.
 * @version : 1.0.0
 * 
 */
public interface Taslet {
	/**
	  * 통신시 tcp.xml 에 Taslet Interface을 구현한 Class의 execute 메소드을 호출한다.
	  * @param request - Client 요청 전문을 Protocol.xml 정의된 내용으로 파싱된 Object.  
	  * @param response - Client 응답 전문을 Protocol.xml에정의된 내용으로 정의함.  
	  * @return 
	  * @exception 
	  */
	
	public void execute(TasRequest request, TasResponse response) throws TasletException;
}
