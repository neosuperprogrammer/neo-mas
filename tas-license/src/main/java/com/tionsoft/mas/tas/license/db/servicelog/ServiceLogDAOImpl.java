package com.tionsoft.mas.tas.license.db.servicelog;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class ServiceLogDAOImpl implements ServiceLogDAO {
	
	private DataSource dataSource;
	private String insertSql ="INSERT INTO PMS.MPMS_SVC_LOGS00(SVCIDNFR,SERNO,REGIYHS,PHV,SESSION_ID,TRANDSTCD,TRANSACTIONID,APPIDNFR,MSGIDNFR" +
										",IMEI,WIFI,MSISDN,MODEL_NO,ISP_NAME,OS_TYPE,OS_VERSION,UUID,BODY_TYPE,STATUS_CODE,HEADER_LENGTH,BODY_LENGTH)" +
										"VALUES(?,SEQ_SVCLOGS00.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"; 
	private String updateSql ="";

	@Override
	public void setDataSource(DataSource ds) {
		this.dataSource = ds;
	}
	

	@Override
	public void setInsertSql(String sql) {
		this.insertSql = sql;
	}


	@Override
	public void setUpdateSql(String sql) {
		this.updateSql = sql;
	}


	@Override
	public void insert(Map<String,Object> args) {
		
		
		Integer SVCIDNFR = (Integer)args.get("SVCIDNFR");
		String REGIYHS = (String)args.get("REGIYHS");
		String PHV = (String)args.get("PHV");
		Long SESSION_ID = (Long)args.get("SESSION_ID");
		String TRANDSTCD = (String)args.get("TRANDSTCD");
		Long TRANSACTIONID = (Long)args.get("TRANSACTIONID");
		String APPIDNFR = (String)args.get("APPIDNFR");
		String MSGIDNFR = (String)args.get("MSGIDNFR");
		String IMEI = (String)args.get("IMEI");
		String WIFI = (String)args.get("WIFI");
		String MSISDN = (String)args.get("MSISDN");
		String MODEL_NO = (String)args.get("MODEL_NO");
		String ISP_NAME = (String)args.get("ISP_NAME");
		String OS_TYPE = (String)args.get("OS_TYPE");
		String OS_VERSION = (String)args.get("OS_VERSION");
		String UUID = (String)args.get("UUID");
		Short BODY_TYPE = (Short)args.get("BODY_TYPE");
		Short STATUS_CODE = (Short)args.get("STATUS_CODE");
		Integer HEADER_LENGTH = (Integer)args.get("HEADER_LENGTH");
		Integer BODY_LENGTH = (Integer)args.get("BODY_LENGTH");
		
		
		JdbcTemplate insert = new JdbcTemplate(dataSource);
		
		insert.update(insertSql, new Object[] { SVCIDNFR, REGIYHS, PHV, SESSION_ID, TRANDSTCD, TRANSACTIONID, APPIDNFR, MSGIDNFR, IMEI, WIFI, MSISDN, MODEL_NO,
												ISP_NAME, OS_TYPE, OS_VERSION, UUID, BODY_TYPE, STATUS_CODE, HEADER_LENGTH, BODY_LENGTH });
	}


	@Override
	public void update(Map<String,Object> args) {
		
	}
	

	

}
