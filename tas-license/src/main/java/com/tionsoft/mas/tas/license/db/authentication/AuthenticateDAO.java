package com.tionsoft.mas.tas.license.db.authentication;

import javax.sql.DataSource;

import com.tionsoft.mas.tas.license.db.domain.AuthenticationDomain;


public interface AuthenticateDAO {
	
	void setDataSource(DataSource ds);
	public AuthenticationDomain select(String serviceId,String wifi);
	public void setSelectSql(String sql);

}
