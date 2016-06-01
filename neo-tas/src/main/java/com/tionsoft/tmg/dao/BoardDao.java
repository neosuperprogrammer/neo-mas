package com.tionsoft.tmg.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 게시판 DAO 
 * @author 서버개발실 이주용
 */
public interface BoardDao {
	/**
	 * 게시판 목록 조회
	 * @param boardIDs 조회할 게시판 아이디
	 * @return
	 */
	public List<Map<String, Object>> getBoardList(String accountId, String [] boardIDs);
	
	/**
	 * 게시물 목록 조회
	 * @param accountId 사용자 계정
	 * @param boardId 게시판 아이디
	 * @param keyword 키워드
	 * @param beginNum 조회 시작 순번
	 * @param endNum 조회 종료 순번 
	 * @return
	 */
	public List<Map<String, Object>> getArticleList(String accountId, String boardId, String searchType, String keyword, int beginNum, int endNum);
	
	/**
	 * 전체 게시물 개수
	 * @param accountId 사용자 계정
	 * @param boardId 게시판 아이디
	 * @param keyword 키워드
	 * @return
	 */
	public int getTotalArticleCount(String accountId, String boardId, String searchType, String keyword);
	
	/**
	 * 게시물 조회
	 * @param accountId 사용자 계정
	 * @param articleId 게시물 아이디
	 * @return
	 */
	public Map<String, Object> getArticleDetail(String accountId, String articleId);
	
	/**
	 * 댓글 목록 조회
	 * @param accountId 사용자 계정
	 * @param articleId 게시물 아이디
	 * @param beginNum 조회 시작 순번
	 * @param endNum 조회 종료 순번
	 * @return
	 */
	public List<Map<String, Object>> getCommentList(String accountId, String articleId, int beginNum, int endNum);

	/**
	 * 전체 댓글 개수 조회
	 * @param accountId 사용자 계정
	 * @param articleId 게시물 아이디
	 * @return
	 */
	public int getTotalCommentCount(String accountId, String articleId);
	
	/**
	 * 댓글 상세 조회
	 * @param commentId 댓글 아이디
	 * @return
	 */
	public Map<String, Object> getCommentDetail(String commentId);
	
	/**
	 * 댓글 등록
	 * @param accountId 사용자 계정
	 * @param articleId 게시물 아이디
	 * @param body 댓글
	 * @return
	 * @throws SQLException
	 */
	public void insertComment(String accountId, String articleId, String body) throws SQLException;
	
	/**
	 * 댓글 삭제
	 * @param articleId 게시물 아이디
	 * @param commentId 댓글 아이디
	 * @throws SQLException
	 */
	public void deleteComment(String articleId, String commentId) throws SQLException;
	
	public Map<String, Object> getUserGroupInfo(String accountId);

	public Map<String, Object> getArticlePermission(String atticleId);

	public Map<String, Object> getBoardPermission(String boardId);

	public Map<String, Object> checkBoardPermission(String boardId, String accountId, String deptId, String copCode);

	public Map<String, Object> checkArticlePermission(String articleId, String accountId, String deptId, String groupCode);	

	public String hasAdditionalBoardPermission(String accountId, String deptId, String boardId, String copCode);

	public String hasAdditionalItemPermission(String accountId, String deptId, String boardId, String articleId, String copCode);
}
