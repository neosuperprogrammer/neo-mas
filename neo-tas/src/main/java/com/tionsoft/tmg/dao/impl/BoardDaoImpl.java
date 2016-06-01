package com.tionsoft.tmg.dao.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.tionsoft.tmg.dao.BoardDao;

/**
 * 게시판 DAO 구현
 * @author 서버개발실 이주용
 */
public class BoardDaoImpl extends SqlMapClientDaoSupport implements BoardDao {
	/**
	 * 게시판 정보 조회
	 */
	@Override
	public List<Map<String, Object>> getBoardList(String accountId, String[] boardIDs) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("BOARD_IDS",		boardIDs);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) getSqlMapClientTemplate().queryForList("command.getBoardList", params);

		return result;
	}

	/**
	 * 게시물 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getArticleList(String accountId, String boardId, String searchType, String keyword, int beginNum, int endNum) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("BOARD_ID",		boardId);
		params.put("BEGIN_NUM",		beginNum);
		params.put("END_NUM",		endNum);
		
		if (searchType.equals("1")) {
			params.put("ITEM_TITLE",		keyword);
			params.put("REGIST_USER_NAME",	keyword);
		} else if (searchType.equals("2")) {
			params.put("ITEM_TITLE",		keyword);
		} else if (searchType.equals("3")) {
			params.put("REGIST_USER_NAME",	keyword);
		} else {
			params.put("ITEM_TITLE",		"");
			params.put("REGIST_USER_NAME",	"");
		}
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) getSqlMapClientTemplate().queryForList("command.getArticleList", params);

		return result;
	}

	/**
	 * 전체 게시물 개수
	 */
	@Override
	public int getTotalArticleCount(String accountId, String boardId, String searchType, String keyword) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("BOARD_ID",		boardId);
		
		if (searchType.equals("1")) {
			params.put("ITEM_TITLE",		keyword);
			params.put("REGIST_USER_NAME",	keyword);
		} else if (searchType.equals("2")) {
			params.put("ITEM_TITLE",		keyword);
		} else if (searchType.equals("3")) {
			params.put("REGIST_USER_NAME",	keyword);
		} else {
			params.put("ITEM_TITLE",		"");
			params.put("REGIST_USER_NAME",	"");
		}
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.getTotalArticleCount", params);
		int totalCount = (Integer) result.get("COUNT");
		
		return totalCount;
	}

	/**
	 * 게시물 조회
	 */
	@Override
	public Map<String, Object> getArticleDetail(String accountId, String articleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID", accountId);
		params.put("ARTICLE_ID", articleId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.getArticleDetail", params);
		
		return result;
	}

	/**
	 * 댓글 목록 조회
	 */
	@Override
	public List<Map<String, Object>> getCommentList(String accountId, String articleId, int beginNum, int endNum) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("ARTICLE_ID",	articleId);
		params.put("BEGIN_NUM",		beginNum);
		params.put("END_NUM",		endNum);
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = (List<Map<String, Object>>) getSqlMapClientTemplate().queryForList("command.getCommentList", params);

		return result;
	}

	/**
	 * 전체 댓글 개수 조회
	 */
	@Override
	public int getTotalCommentCount(String accountId, String articleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("ARTICLE_ID",	articleId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.getTotalCommentCount", params);
		int totalCount = (Integer) result.get("COUNT");
		
		return totalCount;
	}

	/**
	 * 댓글 상세 조회
	 */
	@Override
	public Map<String, Object> getCommentDetail(String commentId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("COMMENT_ID", commentId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.getCommentDetail", params);
		
		return result;
	}

	/**
	 * 댓글 등록
	 */
	@Override
	public void insertComment(String accountId, String articleId, String body) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("ARTICLE_ID",	articleId);
		params.put("BODY",			body);
		
		getSqlMapClientTemplate().insert("command.insertComment", params);
	}

	/**
	 * 댓글 삭제
	 */
	@Override
	public void deleteComment(String articleId, String commentId) throws SQLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ARTICLE_ID",	articleId);
		params.put("COMMENT_ID",	commentId);
		
		getSqlMapClientTemplate().delete("command.deleteComment", params);
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> getUserGroupInfo(String accountId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ACCOUNT_ID", accountId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.getUserGroupInfo", params);
		
		return result;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> getArticlePermission(String atticleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ARTICLE_ID", atticleId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.getArticlePermission", params);
		
		return result;
	}

	/**
	 * 
	 */
	@Override
	public Map<String, Object> getBoardPermission(String boardId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("BOARD_ID", boardId);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.getBoardPermission", params);
		
		return result;
	}

	@Override
	public Map<String, Object> checkBoardPermission(String boardId, String accountId, String deptId, String copCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("BOARD_ID",		boardId);
		params.put("ACCOUNT_ID",	accountId);
		params.put("DEPT_ID",		deptId);
		params.put("COP_CODE",		copCode);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.checkBoardPermission", params);
		
		return result;
	}

	@Override
	public Map<String, Object> checkArticlePermission(String articleId, String accountId, String deptId, String copCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ARTICLE_ID",	articleId);
		params.put("ACCOUNT_ID",	accountId);
		params.put("DEPT_ID",		deptId);
		params.put("COP_CODE",		copCode);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = (Map<String, Object>) getSqlMapClientTemplate().queryForObject("command.checkArticlePermission", params);
		
		return result;
	}
	
	@Override
	public String hasAdditionalBoardPermission(String accountId, String deptId, String boardId, String copCode) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("DEPT_ID",		deptId);
		params.put("BOARD_ID",		boardId);
		params.put("COP_CODE",		copCode);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> boardPermission = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.checkBoardPermission", params);
		if (boardPermission == null || boardPermission.get("HAS_PERMISSION") == null) throw new EmptyResultDataAccessException(1);
		return boardPermission.get("HAS_PERMISSION").toString();
	}
	
	@Override
	public String hasAdditionalItemPermission(String accountId, String deptId, String boardId, String articleId, String copCode) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("ACCOUNT_ID",	accountId);
		params.put("DEPT_ID",		deptId);
		params.put("BOARD_ID",		boardId);
		params.put("ARTICLE_ID",	articleId);
		params.put("COP_CODE",		copCode);

		@SuppressWarnings("unchecked")
		Map<String, Object> articlePermission = (Map<String, Object>)getSqlMapClientTemplate().queryForObject("command.checkArticlePermission", params);
		if (articlePermission == null || articlePermission.get("HAS_PERMISSION") == null) throw new EmptyResultDataAccessException(1);
		
		System.out.println("HAS_PERMISSION = " + articlePermission.get("HAS_PERMISSION").toString());
		
		return articlePermission.get("HAS_PERMISSION").toString();
	}
	
}
