package com.tionsoft.tmg.service.domain;

import java.util.ArrayList;
import java.util.Map;

/**
 * Map으로 구성된 리스트
 * @author 이주용
 * @param <T> 리스트를 구성하는 Map의 Key 데이터 타입 
 * @param <U> 리스트를 구성하는 Map의 Value 데이터 타입 
 * @param <V> 리스트에 포함되는 특별데이터의 데이터 타입  
 */
public class ParticularArrayList<T, U, V> extends ArrayList<Map<T, U>> {
	private static final long serialVersionUID = 1L;
	
	private V particularData;
	
	/**
	 * 특별데이터 조회
	 * @return 특별데이터
	 */
	public V getParticularData() {
		return particularData;
	}
	
	/**
	 * 특별데이터 설정 
	 * @param particularData 특별데이터
	 */
	public void setParticularData(V particularData) {
		this.particularData = particularData;
	}
}
