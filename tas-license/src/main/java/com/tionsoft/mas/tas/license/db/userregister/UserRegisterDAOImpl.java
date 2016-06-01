package com.tionsoft.mas.tas.license.db.userregister;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tionsoft.mas.tas.license.db.result.UserRegisterRowMapper;


public class UserRegisterDAOImpl implements UserRegisterDAO {
	
	private static Logger logger = LoggerFactory.getLogger(UserRegisterDAOImpl.class);
	private DataSource dataSource;
	private String selectSql ="SELECT CASE WHEN COUNT(*) <= ? THEN 1 ELSE 0 END IS_VALID FROM mPMS_USER_INFO10 WHERE regiStusDstcd in(10,20) ";

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
	
	
	public void setSelectSql(String sql){
		this.selectSql = sql;
	}
	
	@Override
	public Boolean isValid(String userCnt) {
		JdbcTemplate select = new JdbcTemplate(dataSource);
		
		if(logger.isDebugEnabled()) {
			logger.debug("user cnt : "+userCnt);
		}
		
	    Boolean isValid= select.query(selectSql,new String[]{userCnt},new UserRegisterRowMapper()).get(0);
	    
	    if(logger.isDebugEnabled()) {
			logger.debug("user's validation : "+isValid);
		}
	    
	    return isValid;
	}

}
