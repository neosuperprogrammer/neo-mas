package com.tionsoft.mas.tas;

public class TcpAppConfig {
	
	private String name;
	private String base;
	private String ip;
	private int port;
	private boolean reuseAddress=false;
	private int SEND_BUFFER_SIZE = 10240;
	private int RECV_BUFFER_SIZE = 10240;
	private int backlog=1000;
	private boolean tcpNoDelay;
	

	private final ClusterConfig clusterConfig;
	private final SessionConfig sessionConfig;
	private final ConnectionConfig connectionConfig;
	private final ListenerConfig listenerConfig;
	
	public TcpAppConfig() {
		this.clusterConfig = new ClusterConfig();
		this.sessionConfig = new SessionConfig();
		this.connectionConfig = new ConnectionConfig();
		this.listenerConfig = new ListenerConfig();
	}

	public String getName() {
		return name;
	}

	public boolean isReuseAddress() {
		return reuseAddress;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}

	public int getSEND_BUFFER_SIZE() {
		return SEND_BUFFER_SIZE;
	}

	public void setSEND_BUFFER_SIZE(int sEND_BUFFER_SIZE) {
		SEND_BUFFER_SIZE = sEND_BUFFER_SIZE;
	}

	public int getRECV_BUFFER_SIZE() {
		return RECV_BUFFER_SIZE;
	}

	public void setRECV_BUFFER_SIZE(int rECV_BUFFER_SIZE) {
		RECV_BUFFER_SIZE = rECV_BUFFER_SIZE;
	}
	

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}
	

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public ClusterConfig getClusterConfig() {
		return clusterConfig;
	}

	public SessionConfig getSessionConfig() {
		return sessionConfig;
	}
	
	public ConnectionConfig getConnectionConfig() {
		return connectionConfig;
	}
	
	public ListenerConfig getListenerConfig() {
		return listenerConfig;
	}

	public class ClusterConfig {
		
		private boolean enable;
		private String name;
		private String machineId;
		private String rackId;
		private String siteId;
		private int numOwners;
		
		public boolean isEnable() {
			return enable;
		}
		
		public void setEnable(boolean enable) {
			this.enable = enable;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMachineId() {
			return machineId;
		}
		public void setMachineId(String machineId) {
			this.machineId = machineId;
		}
		public String getRackId() {
			return rackId;
		}
		public void setRackId(String rackId) {
			this.rackId = rackId;
		}
		public String getSiteId() {
			return siteId;
		}
		public void setSiteId(String siteId) {
			this.siteId = siteId;
		}
		
		public void setNumOwners(int numOwners) {
			this.numOwners = numOwners;
		}
		
		public int getNumOwners()
		{
			return this.numOwners;
		}
		
	}
	
	public class SessionConfig {
		
		private long timeout;

		public long getTimeout() {
			return timeout;
		}

		public void setTimeout(long timeout) {
			this.timeout = timeout;
		}
		
	}
	
	public class ConnectionConfig {
		
		private int timeout;

		public int getTimeout() {
			return timeout;
		}

		public void setTimeout(int timeout) {
			this.timeout = timeout;
		}
		
	}
	
	public class ListenerConfig {
		
		private String startup;

		public String getStartup() {
			return startup;
		}

		public void setStartup(String startup) {
			this.startup = startup;
		}

	}
	

}
