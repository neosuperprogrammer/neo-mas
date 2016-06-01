package com.tionsoft.mas.tas.license.db.license;

import java.util.List;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.tionsoft.mas.tas.license.db.domain.LicenseDomain;
import com.tionsoft.mas.tas.license.db.result.LicenseRowMapper;

public class LicenseDAOImpl implements LicenseDAO {
	
	private DataSource dataSource;
	private String selectSql ="SELECT MAC_ADDRESS,USER_CNT,DEVICE_CNT,CONCURRENT_DEVICE_CNT,APPLICATION_USER_CNT,EXPIRED_DT,ERROR_YN " +
							  "FROM MPMS_LICENSE_MGNT";

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
	
	
	public void setSelectSql(String sql){
		this.selectSql = sql;
	}
	

	@Override
	public List<LicenseDomain> select() {
		JdbcTemplate select = new JdbcTemplate(dataSource);
		
	    List<LicenseDomain> license= select.query(selectSql,new LicenseRowMapper());
	    
	    if(license.size() >0) return license;
	    
	    return null;

	}

}
