package com.tionsoft.tmg.service.domain;

import java.util.List;
import java.util.Map;

/**
 * 그룹웨어 범용 리스트 도메인
 * @author 서버개발실 이주용
 */
public class GeneralList {
	/**
	 * 전체 페이지 수
	 */
	private int totalPage;
	
	/**
	 * 검색 결과 개수
	 */
	private int totalCount;
	
	/**
	 * 주소록 목록
	 */
	private List<Map<String, Object>> list;
	
	/**
	 * 기타 데이터
	 */
	private Map<String, Object> otherData;
	
	/**
	 * 전체 페이지 수 조회
	 * @return 전체 페이지 수
	 */
	public int getTotalPage() {
		return totalPage;
	}
	
	/**
	 * 전체 페이지 수 설정
	 * @param totalPage 전체 페이지 수
	 */
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
	/**
	 * 검색 결과 개수 조회
	 * @return 검색 결과 개수
	 */
	public int getTotalCount() {
		return totalCount;
	}
	
	/**
	 * 검색 결과 개수 설정
	 * @param totalCount 검색 결과 개수
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	/**
	 * 검색 결과 목록 조회
	 * @return 검색 결과 목록
	 */
	public List<Map<String, Object>> getList() {
		return list;
	}
	
	/**
	 * 검색 결과 목록 설정
	 * @param list 검색 결과 목록
	 */
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	
	/**
	 * 기타 데이타 설정
	 * @param otherData
	 */
	public void setOtherData(Map<String, Object> otherData) {
		this.otherData = otherData;
	}
	
	/**
	 * 기타 데이터 조회
	 * @return
	 */
	public Map<String, Object> getOtherData() {
		return otherData;
	}
}
