package com.tionsoft.mas.tas.pushlet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.infinispan.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tionsoft.mas.tas.TasContext;
import com.tionsoft.mas.tas.bean.TasBean;
import com.tionsoft.mas.tas.bean.platform.PlatformHeader;
import com.tionsoft.mas.tas.bean.platform.PlatformHeaderFactory;
import com.tionsoft.mas.tas.taslet.TasMessage;
import com.tionsoft.mas.tas.taslet.TasPushResponse;
import com.tionsoft.mas.tas.taslet.TasResponse;

public class PushService {
	
	public static final String CLIENT_KEY = "CLIENT_KEY";
	
	private final TasContext tasContext;
	private TasPushResponse pushItems;
	private Logger LOGGER;
	private PushDatabaseService pushDatabaseService;
	
	public PushService(TasContext tasContext, TasPushResponse pushItems) {
		this.LOGGER = LoggerFactory.getLogger(tasContext.getTcpAppConfig().getName());
		this.tasContext = tasContext;
		this.pushItems = pushItems;
	}
	
	public PushService(TasContext tasContext) {
		this.LOGGER = LoggerFactory.getLogger(tasContext.getTcpAppConfig().getName());
		this.tasContext = tasContext;
	}
	
	public void pushMultiItems() {
		if(pushItems == null) return;
		Map<String, TasResponse> pushRespones = pushItems.getResponses();
		Iterator<String> clientKeyIter = pushRespones.keySet().iterator();
		String clientKey;
		while(clientKeyIter.hasNext()) {
			clientKey = clientKeyIter.next();
			TasResponse pushItem = pushRespones.get(clientKey);
			pushItem(clientKey, pushItem);
		}
	}
	
	public void pushItem(String clientKey, TasResponse pushItem) {
		if(pushItem == null) return;
		IPushService pushService = null;
		if(isLocalClient(clientKey)) {
			if(pushDatabaseService != null) pushDatabaseService.updatePpcPushItemStatus(1005);
			pushService = new InternalPushService(tasContext.getAcceptor().getManagedSessions(), clientKey);
		} else {
			if(pushDatabaseService != null) pushDatabaseService.updatePpcPushItemStatus(1001);
			if(tasContext.getTcpAppConfig().getClusterConfig().isEnable()) {
				LOGGER.debug("Message is not pushed, delegated to clustered servers [" + clientKey + "]");
				pushService = new ExternalPushService(tasContext.getPushCache(), clientKey);
			} else {
				LOGGER.debug("Message is not pushed, session not found [" + clientKey + "]");
				return;
			}
		}
		pushService.push(pushItem);
	}
	
	public void pushItemToLocalOnly(String clientKey, TasResponse pushItem) {
		if(pushItem == null) return;
		IPushService pushService = null;
		if(isLocalClient(clientKey)) {
			pushService = new InternalPushService(tasContext.getAcceptor().getManagedSessions(), clientKey);
		} else {
			LOGGER.debug("Message is not pushed, but other clustered server could push [" + clientKey + "]");
			return;
		}
		pushService.push(pushItem);
	}
	
	private boolean isLocalClient(String targetClientKey) {
		Collection<IoSession> ioSessions =  tasContext.getAcceptor().getManagedSessions().values();
		
		for(IoSession ioSession : ioSessions) {
			if(ioSession.getAttribute(CLIENT_KEY) != null) {
				String clientKey = (String)ioSession.getAttribute(CLIENT_KEY);
				
				if(clientKey.equalsIgnoreCase(targetClientKey)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void setPushDatabaseService(PushDatabaseService databaseService) {
		this.pushDatabaseService = databaseService;
	}
	
	public interface IPushService {
		public void push(TasResponse tasResponse);
	}
	
	public class InternalPushService implements IPushService {
		
		private final Map<Long, IoSession> managedSessions;
		private final String clientKey;
		
		public InternalPushService(Map<Long, IoSession> managedSessions, String clientKey) {
			this.managedSessions = managedSessions;
			this.clientKey = clientKey;
		}
		
		@Override
		public void push(TasResponse tasResponse) {
			try {
				IoSession session = getTargetSession(clientKey);
				String phv = (String)session.getAttribute(PlatformHeader.VERSION);
				
				// PS,PC 하위 버전 호환을 위한 패치[시작]
				if(phv.equals("PHV100")) {
					PlatformHeader ph = tasResponse.getPlatformHeader(); 
					tasResponse.setPlatformHeader(createPhv100PlatformHeader(ph));
				}
				// PS,PC 하위 버전 호환을 위한 패치[끝]
				TasMessage tasMessage = new TasMessage(null, tasResponse);
				session.write(tasMessage);
				
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		
		private IoSession getTargetSession(String taragetClientKey) {
			Collection<IoSession> ioSessions =  managedSessions.values();
			for(IoSession ioSession : ioSessions) {
				if(ioSession.getAttribute(CLIENT_KEY) != null) {
					String clientKey = (String)ioSession.getAttribute(CLIENT_KEY);
					if(clientKey.equalsIgnoreCase(taragetClientKey)) {
						return ioSession;
					}
				}
				
			}
			
			return null;
		}
	}
	
	public class ExternalPushService implements IPushService {
		
		private final Cache<Object, Object> pushCache;
		private final String clientKey;
		
		public ExternalPushService(Cache<Object, Object> pushCache, String clientKey) {
			this.pushCache = pushCache;
			this.clientKey = clientKey;
		}
		
		@Override
		public void push(TasResponse pushItem) {
			pushCache.put(clientKey, pushItem);
		}
	}
	
	private static TasBean createPhv100PlatformHeader(PlatformHeader ph) {
		
		PlatformHeader platformHeader = PlatformHeaderFactory.getPlatformHeader("PHV100");
		
		platformHeader.setValue(PlatformHeader.APPLICATION_ID, "PC02");
		platformHeader.setValue(PlatformHeader.MESSAGE_ID, "M00000002");
		platformHeader.setValue(PlatformHeader.SESSION_ID, ph.getValue(PlatformHeader.SESSION_ID, Long.class));
		platformHeader.setValue(PlatformHeader.BODY_TYPE, (short)PlatformHeader.BODY_TYPE_BINARY);
		platformHeader.setValue(PlatformHeader.STATUS_CODE, ph.getValue(PlatformHeader.STATUS_CODE, Short.class));

		return platformHeader;
	}
	
}
