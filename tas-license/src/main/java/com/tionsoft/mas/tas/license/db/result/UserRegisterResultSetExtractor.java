package com.tionsoft.mas.tas.license.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;


public class UserRegisterResultSetExtractor implements ResultSetExtractor<Boolean>  {

	private static Logger logger = LoggerFactory.getLogger(UserRegisterResultSetExtractor.class);
	
	@Override
	public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		String isValid = rs.getString("IS_VALID");
		
		if(logger.isDebugEnabled()) logger.debug("Original User Register Validation is : "+isValid);
		
		if(isValid.equals("1")) isValid = "true";
		else if(isValid.equals("0")) isValid = "false";
		
		if(logger.isDebugEnabled()) logger.debug("Converted User Register Validation is : "+isValid);
		
		
		return Boolean.parseBoolean(isValid);
	}

}
