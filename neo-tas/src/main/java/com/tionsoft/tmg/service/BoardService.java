package com.tionsoft.tmg.service;

import java.util.Map;

import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.service.domain.GeneralList;

/**
 * 게시판 서비스
 * @author 서버개발실 이주용
 */
public interface BoardService {
	/**
	 * 게시판 목록 조회
	 * @param accountId 사용자 계정
	 * @return
	 */
	public GeneralList getBoardList(String accountId);
	
	/**
	 * 게시물 목록 조회
	 * @param accountId 사용자 계정
	 * @param boardId 게시판 아이디
	 * @param requestPage 조회 페이지
	 * @param countPerPage 페이당 게시물 수
	 * @return
	 */
	public GeneralList getArticleList(String accountId, String boardId, int requestPage, int countPerPage) throws TmgException;
	
	/**
	 * 게시물 목록 조회
	 * @param accountId 사용자 계정
	 * @param boardId 게시판 아이디
	 * @param keyword 키워드
	 * @param requestPage 조회 페이지
	 * @param countPerPage 페이지당 게시물 수
	 * @return
	 */
	public GeneralList getArticleList(String accountId, String boardId, String searchType, String keyword, int requestPage, int countPerPage) throws TmgException;
	
	/**
	 * 게시물 내용 조회
	 * @param accountId 사용자 계정
	 * @param articleId 게시물 아이디
	 * @return
	 */
	public Map<String, Object> getArticleDetail(String accountId, String articleId) throws TmgException;
	
	/**
	 * 댓글 목록 조회
	 * @param accountId 사용자 계정
	 * @param articleId 게시물 아이디
	 * @param requestPage 조회 페이지
	 * @param countPerPage 페이지당 게시물 수
	 * @return
	 */
	public GeneralList getCommentList(String accountId, String articleId, int requestPage, int countPerPage) throws TmgException;

	/**
	 * 댓글 등록
	 * @param accountId 사용자 계정
	 * @param articleId 게시물 아이디
	 * @param body 댓글
	 */
	public void insertComment(String accountId, String articleId, String body) throws TmgException;
	
	/**
	 * 댓글 삭제
	 * @param articleId 사용자 계정
	 * @param commentId 댓글 아이디
	 */
	public void deleteComment(String articleId, String commentId) throws TmgException;
}
