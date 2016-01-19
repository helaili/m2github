package com.github.citools;

import java.util.Collection;
import java.util.Hashtable;

public class SpyConfig {
	private Collection<Mapping> mappings;
	private Collection<String> ignore;
	
	
	private Hashtable<String, String> mappingHash;
	
	@Override
    public String toString() {
		StringBuilder sb = new StringBuilder();
        if(mappings != null) {
        	for(Mapping m : mappings) {
        		sb.append(m.toString());
        	}
        }
        return sb.toString();
    }
	
	public class Mapping {
		private String source;
		private String destination;
		
		@Override
	    public String toString() {
	        return source + " : " + destination;
	    }
	}
	
	public boolean isIgnored(String key) {
		return ignore.contains(key);
	}
	
	public String getMapping(String key) {
		if(mappingHash == null) {
			//First call, so building a hashmap to find key/values faster (I hope)
			//TODO : configure GSON to avoid this copy and create a hashmap right away
			mappingHash = new Hashtable<String, String>();
			for(Mapping m : mappings) {
				mappingHash.put(m.source, m.destination);
			}
		}
		
		return mappingHash.get(key);
	}

}
