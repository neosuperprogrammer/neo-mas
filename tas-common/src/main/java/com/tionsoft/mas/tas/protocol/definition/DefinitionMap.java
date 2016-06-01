package com.tionsoft.mas.tas.protocol.definition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefinitionMap {
	
	private Map<String,SectionDefinition> sectionDefinitionMap= new ConcurrentHashMap<String, SectionDefinition>();
	private Map<String,ActiveDefinition> activeDefinitionMap= new ConcurrentHashMap<String, ActiveDefinition>();
	private static DefinitionMap dMap;
	
	private DefinitionMap()
	{
		
	}
	
	public void putSectionDefinition(String key,SectionDefinition sectionDefinition)
	{
		sectionDefinitionMap.put(key,sectionDefinition);
	}
	
	public boolean existInSectionDefinitionMap(String key)
	{
		return sectionDefinitionMap.containsKey(key);
	}
	
	
	public SectionDefinition getSectionDefinition(String key)
	{
		if(sectionDefinitionMap.containsKey(key)){
			return sectionDefinitionMap.get(key);
		}
		
		return null;
	}
	
	
	public void putActiveDefinition(String key,ActiveDefinition activeDefinition)
	{
		activeDefinitionMap.put(key,activeDefinition);
	}
	
	
	public ActiveDefinition getActiveDefinition(String key)
	{
		if(activeDefinitionMap.containsKey(key)){
			return activeDefinitionMap.get(key);
		}
		
		return null;
	}
	
	public boolean existInActiveDefinitionMap(String key)
	{
		return activeDefinitionMap.containsKey(key);
	}
	
	
	public static DefinitionMap newInstance()
	{
		if(dMap==null)
		{
			dMap = new DefinitionMap();
		}
		
		return dMap;
	}
	
}
