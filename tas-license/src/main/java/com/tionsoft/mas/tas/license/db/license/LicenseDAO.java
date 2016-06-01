package com.tionsoft.mas.tas.license.db.license;

import java.util.List;

import javax.sql.DataSource;

import com.tionsoft.mas.tas.license.db.domain.LicenseDomain;

public interface LicenseDAO {

	public void setDataSource(DataSource ds);
	public List<LicenseDomain> select();
	public void setSelectSql(String sql);



}
