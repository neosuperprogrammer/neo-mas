package com.tionsoft.mas.tas.license.db.userregister;

import javax.sql.DataSource;

public interface UserRegisterDAO {

	public void setDataSource(DataSource ds);
	public Boolean isValid(String userCnt);
	public void setSelectSql(String sql);

}
