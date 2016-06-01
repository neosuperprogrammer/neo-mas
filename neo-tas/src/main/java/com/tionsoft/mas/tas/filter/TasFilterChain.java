package com.tionsoft.mas.tas.filter;

import java.util.Map;
import java.util.Set;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;

import com.tionsoft.mas.tas.TasContext;
import com.tionsoft.mas.tas.protocol.codec.TasCodecFactory;

public class TasFilterChain {
	
	private Map<String, IoFilter> preFilters;
	private TasCodecFactory codecFactory;
	private Map<String, IoFilter> postFilters;
	
	public void setPreFilters(Map<String, IoFilter> filters) {
		this.preFilters = filters;
	}
	
	public void setCodecFactory(TasCodecFactory codecFactory) {
		this.codecFactory = codecFactory;
	}
	
	public void setPostFilters(Map<String, IoFilter> filters) {
		this.postFilters = filters;
	}
	
	public IoFilterChainBuilder build(TasContext tasContext) {
		DefaultIoFilterChainBuilder filterChainBuilder = new DefaultIoFilterChainBuilder();
		
		if(preFilters != null && preFilters.size() > 0) {
			Set<String> keys = preFilters.keySet();
			for(String key : keys) {
				filterChainBuilder.addLast(key, preFilters.get(key));
			}
		}
		
		codecFactory.setTasContext(tasContext);
		ProtocolCodecFilter codecFilter = new ProtocolCodecFilter(codecFactory);
		filterChainBuilder.addLast(codecFilter.getClass().getSimpleName(), codecFilter);
		
		if(postFilters != null && postFilters.size() > 0) {
			Set<String> keys = postFilters.keySet();
			for(String key : keys) {
				if(postFilters.get(key) instanceof TasLoggingFilter) {
					TasLoggingFilter tasLoggingFilter = (TasLoggingFilter)postFilters.get(key);
					tasLoggingFilter.setLogger(tasContext.getTcpAppConfig().getName());
				}
				
				if(postFilters.get(key) instanceof TasSolrLoggingFilter) {
					TasSolrLoggingFilter tasSolrLoggingFilter = (TasSolrLoggingFilter)postFilters.get(key);
					tasSolrLoggingFilter.setTcpappName(tasContext.getTcpAppConfig().getName());
				}
				
				filterChainBuilder.addLast(key, postFilters.get(key));
			}
		}
		return filterChainBuilder;
	}
}
