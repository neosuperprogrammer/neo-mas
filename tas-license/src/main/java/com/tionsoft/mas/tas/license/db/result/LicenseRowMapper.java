package com.tionsoft.mas.tas.license.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.tionsoft.mas.tas.license.db.domain.LicenseDomain;

public class LicenseRowMapper implements RowMapper<LicenseDomain>  {

	@Override
	public LicenseDomain mapRow(ResultSet rs, int arg1) throws SQLException {
		
		LicenseResultSetExtractor extractor = new LicenseResultSetExtractor();
		
		return extractor.extractData(rs);
	}

}
