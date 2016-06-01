package com.tionsoft.mas.tas.license.db.authentication;

import java.util.List;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tionsoft.mas.tas.license.db.domain.AuthenticationDomain;
import com.tionsoft.mas.tas.license.db.result.AuthenticationRowMapper;


public class AuthenticateDAOImpl implements AuthenticateDAO {
	
	private static Logger logger = LoggerFactory.getLogger(AuthenticateDAOImpl.class);
	private DataSource dataSource;
	private String selectSql ="SELECT A.SVCIDNFR,B.USERIDNFR, CASE WHEN COUNT(*)>0 THEN 1 ELSE 0 END AS IS_USE " +
							  "FROM MPMS_SVC_USER00 A,MPMS_USER_DEVICE10 B WHERE A.USERDEVCEIDNFR = B.USERDEVCEIDNFR AND B.WIFI=? GROUP BY A.SVCIDNFR, B.USERIDNFR";

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
	
	
	public void setSelectSql(String sql){
		this.selectSql = sql;
	}
	

	@Override
	public AuthenticationDomain select(String serviceId,String wifi) {
		JdbcTemplate select = new JdbcTemplate(dataSource);
		
		wifi = wifi.trim();
		serviceId = serviceId.trim();
		if(logger.isDebugEnabled()) logger.debug("service id : "+serviceId+" wifi : " +wifi +"  in authentication");
		
	    List<AuthenticationDomain> auth= select.query(selectSql, new Object[] {serviceId,wifi},new AuthenticationRowMapper());
	    
	    if(auth.size() >0){
	    	if(logger.isDebugEnabled()) logger.debug("service id : "+serviceId+" wifi : " +wifi +" is authenticated");
	    	return auth.get(0);
	    }
	    
	    if(logger.isDebugEnabled()) logger.debug("service id : "+serviceId+" wifi : " +wifi +" is not authenticated");
	    
	    return new AuthenticationDomain();

	}

}
