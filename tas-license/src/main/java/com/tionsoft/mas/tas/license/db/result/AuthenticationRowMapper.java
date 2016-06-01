package com.tionsoft.mas.tas.license.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.tionsoft.mas.tas.license.db.domain.AuthenticationDomain;

public class AuthenticationRowMapper implements RowMapper<AuthenticationDomain>  {

	@Override
	public AuthenticationDomain mapRow(ResultSet rs, int arg1) throws SQLException {
		
		AuthenticationResultSetExtractor extractor = new AuthenticationResultSetExtractor();
		
		return extractor.extractData(rs);
	}

}
