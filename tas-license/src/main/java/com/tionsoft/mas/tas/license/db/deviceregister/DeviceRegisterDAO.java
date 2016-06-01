package com.tionsoft.mas.tas.license.db.deviceregister;

import javax.sql.DataSource;

public interface DeviceRegisterDAO {

	public void setDataSource(DataSource ds);
	public Boolean isValid(String deviceCnt);
	public void setSelectSql(String sql);



}
