package com.tionsoft.mas.tas.license.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.tionsoft.mas.tas.license.db.domain.AuthenticationDomain;

public class AuthenticationResultSetExtractor implements ResultSetExtractor<AuthenticationDomain>  {

	private static Logger logger = LoggerFactory.getLogger(AuthenticationResultSetExtractor.class);
	
	
	@Override
	public AuthenticationDomain extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		AuthenticationDomain auth = new AuthenticationDomain();
		
		String isAuth = rs.getString("IS_USE");
		
		if(isAuth.equals("1")) isAuth = "true";
		else if(isAuth.equals("0")) isAuth = "false";
		
		if(logger.isDebugEnabled()){
			
			logger.debug("SERVICE ID : " + rs.getString("SERVICE_ID"));
			logger.debug("USE : " + isAuth);
			logger.debug("USER ID : " + rs.getString("USER_ID"));
		}
		
		auth.setServiceId(rs.getString("SERVICE_ID"));
		auth.setUse(Boolean.parseBoolean(isAuth));
		auth.setUserId(rs.getString("USER_ID"));
		
		return auth;
	}

}
