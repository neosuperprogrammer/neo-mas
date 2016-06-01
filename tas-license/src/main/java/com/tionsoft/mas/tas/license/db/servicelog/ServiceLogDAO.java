package com.tionsoft.mas.tas.license.db.servicelog;

import java.util.Map;

import javax.sql.DataSource;

public interface ServiceLogDAO {
	
	public void setDataSource(DataSource ds);
	public void setInsertSql(String sql);
	public void setUpdateSql(String sql);
	public void insert(Map<String,Object> args);
	public void update(Map<String,Object> args);

}
