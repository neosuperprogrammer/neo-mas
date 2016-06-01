package com.tionsoft.tmg.util;

import java.util.Map;

public class PermissionUtil {

	public String getCopCode(String accountId, Map<String, Object> groupInfo) {
		// 사용자 정보를 이용해 사용자가 속한 법인 코드를 얻어온다.
		boolean isLgdEmp = groupInfo.get("IS_LGD_EMP").equals("1");
		String bizzGroupId = groupInfo.get("BUSINESS_GROUP_ID").toString();
		String subsidiaryName = groupInfo.get("SUBSIDIARY_NAME").toString();
		
		return getCopCode(isLgdEmp, bizzGroupId, subsidiaryName);
	}
	
	public boolean hasBoardPermission(String accountId, Map<String, Object> groupInfo, Map<String, Object> boardPermission) {	
		// 사용자 정보를 이용해 사용자가 속한 법인 코드를 얻어온다.
		boolean isLgdEmp = groupInfo.get("IS_LGD_EMP").equals("1");
		String bizzGroupId = groupInfo.get("BUSINESS_GROUP_ID").toString();
		String subsidiaryName = groupInfo.get("SUBSIDIARY_NAME").toString();
		
		// 사용자가 속한 법인이 게시판 / 게시물이 오픈한 법인에 속해 있는지를 검사한다.
		String permissionKey = getPermissionKey(isLgdEmp, bizzGroupId, subsidiaryName);
		return checkBoardPermission(isLgdEmp, permissionKey, boardPermission);
	}
	
	public boolean hasItemPermission(String accountId, Map<String, Object> groupInfo, Map<String, Object> articlePermission) {			
		// 사용자 정보를 이용해 사용자가 속한 법인 코드를 얻어온다.
		boolean isLgdEmp = groupInfo.get("IS_LGD_EMP").equals("1");
		String bizzGroupId = groupInfo.get("BUSINESS_GROUP_ID").toString();
		String subsidiaryName = groupInfo.get("SUBSIDIARY_NAME").toString();
		
		// 사용자가 속한 법인이 게시판 / 게시물이 오픈한 법인에 속해 있는지를 검사한다.
		String permissionKey = getPermissionKey(isLgdEmp, bizzGroupId, subsidiaryName);
		return checkPermission(isLgdEmp, permissionKey, articlePermission);
	}
	
	public String getPermissionKey(boolean isLgdEmp, String bizzGroupId, String subsidiaryName) {
		String ret = "";
	
		if (isLgdEmp) {
			if (bizzGroupId.equals("81")) {
				ret = "OPEN_KR_PERMISSION";
				
			} else if (bizzGroupId.equals("83")) {
				ret = "OPEN_WR_PERMISSION";
				
			} else if (bizzGroupId.equals("12599")) {
				ret = "OPEN_RS_PERMISSION";
				
			} else if (bizzGroupId.equals("82")) {
	            if (subsidiaryName.equals("LGDGZA.LGDGZ")) 
	            	ret = "OPEN_GZ_PERMISSION";
	            else if (subsidiaryName.equals("LGDNJA.LGDNJ")) 
	            	ret = "OPEN_NJ_PERMISSION";
	            else if (subsidiaryName.equals("LGDYTA.LGDYT")) 
	            	ret = "OPEN_YT_PERMISSION";
	            else if (subsidiaryName.equals("LGDCAA.LGDCA")) 
	            	ret = "OPEN_CA_PERMISSION";
	            else 
	            	ret = "OPEN_US_PERMISSION";
	            
			} else {
				ret = "OPEN_US_PERMISSION";
			}
		} else {
			ret = "OPEN_KR_PERMISSION";
			
			if (bizzGroupId.equals("F")) {
				if (subsidiaryName.equals("08")) {
					ret = "OPEN_NJ_PERMISSION";
					
				} else if (subsidiaryName.equals("11")) {
					ret = "OPEN_WR_PERMISSION";
					
				} else if (subsidiaryName.equals("15")) {
					ret = "OPEN_GZ_PERMISSION";
					
				} else if (subsidiaryName.equals("13")) {
					ret = "OPEN_US_PERMISSION";
					
				} else if (subsidiaryName.equals("18")) {
					ret = "OPEN_YT_PERMISSION";
					
				} else {
					ret = "OPEN_US_PERMISSION";
				}
			}
		}
		
		return ret;	
	}
	
	private String getCopCode(boolean isLgdEmp, String bizzGroupId, String subsidiaryName) {
		String ret = "";
	
		if (isLgdEmp) {
			if (bizzGroupId.equals("81")) {
				ret = "12274979001";
				
			} else if (bizzGroupId.equals("83")) {
				ret = "12274979003";
				
			} else if (bizzGroupId.equals("12599")) {
				ret = "12274979013";
				
			} else if (bizzGroupId.equals("82")) {
	            if (subsidiaryName.equals("LGDGZA.LGDGZ")) 
	            	ret = "12274979007";
	            else if (subsidiaryName.equals("LGDNJA.LGDNJ")) 
	            	ret = "12274979009";
	            else if (subsidiaryName.equals("LGDYTA.LGDYT")) 
	            	ret = "12274979011";
	            else if (subsidiaryName.equals("LGDCAA.LGDCA")) 
	            	ret = "12274979015";
	            else 
	            	ret = "12274979005";
	            
			} else {
				ret = "12274979005";
			}
		} else {
			ret = "12274979002";
			
			if (bizzGroupId.equals("F")) {
				if (subsidiaryName.equals("08")) {
					ret = "12274979010";
					
				} else if (subsidiaryName.equals("11")) {
					ret = "12274979004";
					
				} else if (subsidiaryName.equals("15")) {
					ret = "12274979008";
					
				} else if (subsidiaryName.equals("13")) {
					ret = "12274979006";
					
				} else if (subsidiaryName.equals("18")) {
					ret = "12274979012";
					
				} else {
					ret = "12274979006";
				}
			}
		}
		
		return ret;	
	}

	private boolean checkBoardPermission(boolean isLgdEmp, String permissionKey, Map<String, Object> permissionMap) {
		boolean ret = false;
		
		try {
			int openAll = Integer.parseInt(permissionMap.get("OPEN_ALL_PERMISSION").toString());
			int permissionType = Integer.parseInt(permissionMap.get(permissionKey).toString());
			
			if (openAll == 1 || (openAll == 2 && isLgdEmp == true)) {
				ret = true;
			} else {
				ret = permissionType != 0;
			}
		} catch (Exception e) {
		}
		
		return ret;
	}
	
	private boolean checkPermission(boolean isLgdEmp, String permissionKey, Map<String, Object> permissionMap) {
		boolean ret = false;
		
		try {
			int openAll = Integer.parseInt(permissionMap.get("OPEN_ALL_PERMISSION").toString());
			int permissionType = Integer.parseInt(permissionMap.get(permissionKey).toString());
			
			if (openAll == 1 || (openAll == 2 && isLgdEmp == true)) {
				ret = true;
			} else if (permissionType == 1 || (permissionType == 2 && isLgdEmp == true)) {
				ret = true;
			}
		} catch (Exception e) {
		}
		
		return ret;
	}
		
}
