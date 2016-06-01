package com.tionsoft.mas.tas.license.db.deviceregister;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.tionsoft.mas.tas.license.db.result.DeviceRegisterRowMapper;

public class DeviceRegisterDAOImpl implements DeviceRegisterDAO {
	
	private DataSource dataSource;
	private String selectSql ="SELECT CASE WHEN COUNT(*) <= ? THEN 1 ELSE 0 END IS_VALID FROM mPMS_USER_DEVICE10 WHERE regiStusDstcd in(10,20) ";

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
	
	
	public void setSelectSql(String sql){
		this.selectSql = sql;
	}
	

	@Override
	public Boolean isValid(String deviceCnt) {
		JdbcTemplate select = new JdbcTemplate(dataSource);
		
		Boolean isValid= select.query(selectSql,new String[]{deviceCnt},new DeviceRegisterRowMapper()).get(0);
		
		return isValid;
	}

}
