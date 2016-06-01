package com.tionsoft.mas.tas.license.db.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.tionsoft.mas.tas.license.db.domain.LicenseDomain;

public class LicenseResultSetExtractor implements ResultSetExtractor<LicenseDomain>  {

	private static Logger logger = LoggerFactory.getLogger(LicenseResultSetExtractor.class);
	
	@Override
	public LicenseDomain extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		LicenseDomain license = new LicenseDomain();
		
		if(logger.isDebugEnabled()) {
			logger.debug(" USER CNT : " + rs.getString("USER_CNT"));
			logger.debug(" DEVICE CNT : " + rs.getString("DEVICE_CNT"));
			logger.debug(" CONCURRENT DEVICE CNT : " + rs.getString("CONCURRENT_DEVICE_CNT"));
			logger.debug(" APPLICATION USER CNT : " + rs.getString("APPLICATION_USER_CNT"));
			logger.debug(" EXPIRED DATE : " + rs.getString("EXPIRED_DT"));
		}
		
		license.setUserCnt(rs.getString("USER_CNT"));
		license.setDeviceCnt(rs.getString("DEVICE_CNT"));
		license.setConcurrentUseCnt(rs.getString("CONCURRENT_DEVICE_CNT"));
		license.setApplicationUserCnt(rs.getString("APPLICATION_USER_CNT"));
		license.setExpiredDate(rs.getString("EXPIRED_DT"));
		
		return license;
	}

}
