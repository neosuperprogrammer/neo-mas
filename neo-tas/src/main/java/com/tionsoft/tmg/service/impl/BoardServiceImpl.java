package com.tionsoft.tmg.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.XMLConfiguration;

import com.tionsoft.lgd.Edms;
import com.tionsoft.tmc.util.StringEncoder;
import com.tionsoft.tmg.dao.BoardDao;
import com.tionsoft.tmg.exception.TmgException;
import com.tionsoft.tmg.exception.TmgExceptionType;
import com.tionsoft.tmg.service.BoardService;
import com.tionsoft.tmg.service.domain.GeneralList;
import com.tionsoft.tmg.util.CalendarHelper;
import com.tionsoft.tmg.util.LgdCommon;
import com.tionsoft.tmg.util.PermissionUtil;

/**
 * 게시판 서비스 구현
 * @author 서버개발실 이주용
 */
public class BoardServiceImpl implements BoardService {
	private XMLConfiguration configuration;
	private BoardDao boardDao;
	private Edms edmsClient;
	
	public XMLConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(XMLConfiguration configuration) {
		this.configuration = configuration;
	}

	public BoardDao getBoardDao() {
		return boardDao;
	}

	public void setBoardDao(BoardDao boardDao) {
		this.boardDao = boardDao;
	}

	public Edms getEdmsClient() {
		return edmsClient;
	}

	public void setEdmsClient(Edms edmsClient) {
		this.edmsClient = edmsClient;
	}

	/**
	 * 게시판 목록 조회
	 */
	@Override
	public GeneralList getBoardList(String accountId) {		
		// 2013-11-25 민동원 : 법인별 게시판
		//String[] boardIDs = getDefaultNoticeBoardId(accountId, configuration, "");
		
		// 원본 코드
		String [] boardIDs = configuration.getStringArray("configuration.board.id"); 

		List<Map<String, Object>> daoResult = boardDao.getBoardList(accountId, boardIDs);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> m;
		for (Map<String, Object> m0 : daoResult) {
			result.add(m = new HashMap<String, Object>());
			m.put("Bbs_Name",		m0.get("BOARD_NAME"));
			m.put("Bbs_ID",			m0.get("BOARD_ID"));
			m.put("Parent_ID",		m0.get("PARENT_ID"));
			m.put("Depth",			m0.get("DEPTH"));
			m.put("Today_Count",	m0.get("TODAY_COUNT"));
			m.put("UnRead_Count",	m0.get("UNREAD_COUNT"));
			m.put("Permission",		m0.get("PERMISSION"));
		}
		
		int totalCount = daoResult.size();
		int totalPage = 1;
		
		GeneralList list = new GeneralList();
		list.setList(result);
		list.setTotalCount(totalCount);
		list.setTotalPage(totalPage);
		
		return list;
	}
	
	public String[] getDefaultNoticeBoardId(String accountId, XMLConfiguration configuration, String language) {
		String[] ret = new String[1];
		// 사용자가 속한 법인 정보를 가져온다.
		Map<String, Object> groupInfo = boardDao.getUserGroupInfo(accountId);
		boolean isLgdEmp = groupInfo.get("IS_LGD_EMP").equals("1");
		String bizzGroupId = groupInfo.get("BUSINESS_GROUP_ID").toString();
		String subsidiaryName = groupInfo.get("SUBSIDIARY_NAME").toString();
		
		System.out.println("IS_LGD_EMP : " + isLgdEmp);
		System.out.println("BIZZ_GROUP_ID : " + bizzGroupId);
		System.out.println("SUBSIDIARY_NAME : " + subsidiaryName);
		
		PermissionUtil permissionUtil = new PermissionUtil();
		String permissionKey = permissionUtil.getPermissionKey(isLgdEmp, bizzGroupId, subsidiaryName);
		
		System.out.println("PERMISSION_KEY : " + permissionKey);
	
		// 이 법인 정보에 따라 (편의상 permissionKey로 정의함) 대표 게시판 아이디를 conf 파일에서 가져온다.
		if (permissionKey.equals("OPEN_KR_PERMISSION")) {
			ret[0] = configuration.getString("configuration.board.KO_KR");
		} else if (permissionKey.equals("OPEN_WR_PERMISSION")) {
			ret[0] = configuration.getString("configuration.board.PL_PL");
		} else if (permissionKey.equals("OPEN_GZ_PERMISSION")) {
			ret[0] = configuration.getString("configuration.board.ZH_CN.GZ");
		} else if (permissionKey.equals("OPEN_NJ_PERMISSION")) {
			ret[0] = configuration.getString("configuration.board.ZH_CN.NJ");
		} else if (permissionKey.equals("OPEN_YT_PERMISSION")) {
			ret[0] = configuration.getString("configuration.board.ZH_CN.YT");			
		} else if (permissionKey.equals("OPEN_CA_PERMISSION")) {
			ret[0] = configuration.getString("configuration.board.ZH_CN.CA");
		} else if (permissionKey.equals("OPEN_US_PERMISSION")) {
			ret[0]= configuration.getString("configuration.board.EN_US");
		} else {
			ret[0] = null;
		}

		System.out.println("RET : " + ret[0]);
		
		return ret;
	}

	/**
	 * 게시물 목록 조회
	 */
	@Override
	public GeneralList getArticleList(String accountId, String boardId, int requestPage, int countPerPage) throws TmgException {
		return getArticleList(accountId, boardId, "", null, requestPage, countPerPage);
	}

	/**
	 * 게시물 목록 조회
	 */
	@Override
	public GeneralList getArticleList(String accountId, String boardId, String searchType, String keyword, int requestPage, int countPerPage) throws TmgException {
		int beginNum	= (requestPage - 1) * countPerPage + 1;
		int endNum		= requestPage * countPerPage;
		
		List<Map<String, Object>> daoResult = boardDao.getArticleList(accountId, boardId, searchType, keyword, beginNum, endNum);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		Map<String, Object> m;
		for (Map<String, Object> m0 : daoResult) {
			result.add(m = new HashMap<String, Object>());
			m.put("Doc_ID",				m0.get("ARTICLE_ID"));
			m.put("Parent_ID",			0);
			m.put("Category",			"");
			m.put("IsNotice",			m0.get("ARTICLE_TYPE"));
			m.put("IsReply",			0);
			m.put("Depth",				0);
			m.put("Reply_Total_Count",	0);
			m.put("Reply_Index",		"");
			m.put("Subject",			m0.get("SUBJECT"));
			m.put("Creator",			String.format("%s|%s|%s", m0.get("REG_ACCOUNT_NAME"), m0.get("REG_DEPT_NAME"), m0.get("REG_ACCOUNT_ID")));
			m.put("Attached_File",		m0.get("IS_ATTACH"));
			m.put("Created_Date",		CalendarHelper.getFormatedDateString((Date) m0.get("REG_DATE"), "yyyy-MM-dd HH:mm"));
		}
		
		int totalCount = boardDao.getTotalArticleCount(accountId, boardId, searchType, keyword);
		int totalPage = (totalCount - 1) / countPerPage + 1;
		
		GeneralList list = new GeneralList();
		list.setList(result);
		list.setTotalCount(totalCount);
		list.setTotalPage(totalPage);
		
		return list;
	}

	/**
	 * 게시물 내용 조회
	 */
	@Override
	public Map<String, Object> getArticleDetail(String accountId, String articleId) throws TmgException {
		Map<String, Object> daoResult = null;
		Map<String, Object> result = new HashMap<String, Object>();
		int commentCount = 0;

		daoResult = boardDao.getArticleDetail(accountId, articleId);
		
		// 권한 체크하여 권한이 없을 경우 권한이 없다는 메시지를 표시한다.
		String boardId = daoResult.get("BOARD_ID").toString();
		String regAccountId = daoResult.get("REG_ACCOUNT_ID").toString();
		daoResult.remove("ARTICLE_ID");
		
		boolean hasPermission = hasPermission(accountId, boardId, articleId);
		System.out.println("accountId = " + accountId);
		System.out.println("regAccountId = " + regAccountId);
		System.out.println("hasPermission = " + hasPermission);
		if (accountId.equals(regAccountId) || hasPermission) {
			commentCount = boardDao.getTotalCommentCount(accountId, articleId);

			// 본문 내부 이미지 처리
			String body, baseUrl, downloadPath;
			String [] internalDomains;
			baseUrl			= configuration.getString("configuration.image.board.baseUrl");
			downloadPath	= configuration.getString("configuration.image.board.downloadPath");
			internalDomains = configuration.getStringArray("configuration.image.internalDomain");
			body = LgdCommon.getBoardContent(daoResult.get("BODY").toString(), articleId, internalDomains, downloadPath, baseUrl);
			result.put("Body", body);
		} else {
			daoResult.put("Comm_Count", "0");
			daoResult.put("COMMENT_IS_ENABLE_REG", "0");
			throw new TmgException(TmgExceptionType.DOCUMENT_PERMISSION_DENIED);
		}
		
		result.put("Category",		"");
		result.put("IsNotice",		daoResult.get("ARTICLE_TYPE"));
		result.put("IsReply",		"0");
		result.put("Subject",		daoResult.get("SUBJECT"));
		result.put("Creator",		String.format("%s|%s|%s|%s", daoResult.get("REG_ACCOUNT_NAME"), daoResult.get("REG_DEPT_NAME"), daoResult.get("REG_ACCOUNT_ID"), daoResult.get("EMAIL")));
		result.put("Created_Date",	CalendarHelper.getFormatedDateString((Date) daoResult.get("REG_DATE"), "yyyy-MM-dd HH:mm"));
		result.put("Modify_Date",	CalendarHelper.getFormatedDateString((Date) daoResult.get("LAST_EDIT_DATE"), "yyyy-MM-dd HH:mm"));
		result.put("Attached_File",	serializeAttachedList(accountId, articleId));
		result.put("Doc_Status",	String.format("0::%s::0::0", daoResult.get("COMMENT_IS_ENABLE_REG")));
		result.put("Comm_Count",	commentCount);
		result.put("Permission",	daoResult.get("PERMISSION"));
		
		return result;
	}

	/**
	 * EDMS 첨부파일 처리 
	 * @param accountId
	 * @param articleId
	 * @return
	 * @throws Exception
	 */
	private String serializeAttachedList(String accountId, String articleId) {
		StringBuffer sb = new StringBuffer("");
		
		try {
			List<Map<String, Object>> files = edmsClient.getFileList(accountId, articleId);
			
			for (Map<String, Object> file : files) {
				sb.append(file.get("FILE_NAME")).append("|")
				  .append(file.get("FILE_LENGTH")).append("|")
				  .append(file.get("FILE_ID")).append("|")
				  .append(StringEncoder.decodeUrl(file.get("URL").toString())).append("::");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.length() == 0 ? "" : sb.toString().substring(0, sb.toString().length() - 2);
	}

	/**
	 * 댓글 목록 조회
	 */
	@Override
	public GeneralList getCommentList(String accountId, String articleId, int requestPage, int countPerPage) throws TmgException {
		int beginNum	= (requestPage - 1) * countPerPage + 1;
		int endNum		= requestPage * countPerPage;
		
		List<Map<String, Object>> daoResult = boardDao.getCommentList(accountId, articleId, beginNum, endNum);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		Map<String, Object> m;
		for (Map<String, Object> m0 : daoResult) {
			result.add(m = new HashMap<String, Object>());
			m.put("Comment_ID",		m0.get("COMMENT_ID"));
			m.put("Comment",		m0.get("BODY"));
			m.put("Creator",		String.format("%s|%s|%s|%s", m0.get("REG_ACCOUNT_NAME"), m0.get("REG_ACCOUNT_DEPT"), m0.get("REG_ACCOUNT_ID"), m0.get("EMAIL")));
			m.put("Created_Date",	CalendarHelper.getFormatedDateString((Date) m0.get("REG_DATE"), "yyyy-MM-dd HH:mm"));
			m.put("Comment_Status",	String.format("%s::%s", m0.get("REG_ACCOUNT_ID").equals(accountId) ? "1" : "0", m0.get("IS_ENABLE_DELETE")));
		}
		
		int totalCount = boardDao.getTotalCommentCount(accountId, articleId);
		int totalPage = (totalCount - 1) / countPerPage + 1;
		
		GeneralList list = new GeneralList();
		list.setList(result);
		list.setTotalCount(totalCount);
		list.setTotalPage(totalPage);
		
		return list;
	}

	/**
	 * 댓글 등록
	 */
	@Override
	public void insertComment(String accountId, String articleId, String body) throws TmgException {
		try {
			boardDao.insertComment(accountId, articleId, body);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION, e.getMessage());
		}
	}

	/**
	 * 댓글 삭제
	 */
	@Override
	public void deleteComment(String articleId, String commentId) throws TmgException {
		try {
			boardDao.deleteComment(articleId, commentId);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new TmgException(TmgExceptionType.UNDEFINED_EXCEPTION, e.getMessage());
		}
	}

	private boolean hasPermission(String accountId, String boardId, String articleId) {
		boolean ret = false;
		
		// 사용자의 권한 관련된 정보를 먼저 가져온다.
		PermissionUtil permissionUtil = new PermissionUtil();
		Map<String, Object> groupInfo = null;
		Map<String, Object> boardPermission = null;
		String deptId = "";
		String copCode = "";
		
		boolean isBoardEnable = false;
		
		groupInfo = boardDao.getUserGroupInfo(accountId);
		boardPermission = boardDao.getBoardPermission(boardId);
		deptId = groupInfo.get("DEPT_ID").toString();
		copCode = permissionUtil.getCopCode(accountId, groupInfo);
		
		isBoardEnable = permissionUtil.hasBoardPermission(accountId, groupInfo, boardPermission) 
						|| boardDao.hasAdditionalBoardPermission(accountId, deptId, boardId, copCode).equals("1");

		if (isBoardEnable == true) {
			Map<String, Object> articlePermission = boardDao.getArticlePermission(articleId);
			ret = permissionUtil.hasItemPermission(accountId, groupInfo, articlePermission)
				  || boardDao.hasAdditionalItemPermission(accountId, deptId, boardId, articleId, copCode).equals("1");
		}
		
		return ret;
	}

}
