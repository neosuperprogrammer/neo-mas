package com.tionsoft.mas.tas;

import org.springframework.util.StopWatch;

import com.tionsoft.platform.utils.DateUtil;

import junit.framework.TestCase;

public class TaskCase extends TestCase {

	public void testPrintOutExecutor() {
		
		StopWatch watch = new StopWatch();
		
		System.out.println(DateUtil.getCurrentTimeMilli("yyyy-MM-dd HH:mm:ss SSS"));
		
		for(int i=0;i<10000;i++)
		{
			watch.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			watch.stop();
			System.out.println("aa::" + watch.toString());
			
		}
		
		
	}

}
