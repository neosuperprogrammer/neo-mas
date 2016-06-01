package com.tionsoft.mas.tas.license.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UserRegisterRowMapper implements RowMapper<Boolean>  {

	@Override
	public Boolean mapRow(ResultSet rs, int arg1) throws SQLException {
		
		UserRegisterResultSetExtractor extractor = new UserRegisterResultSetExtractor();
		
		return extractor.extractData(rs);
	}

}
