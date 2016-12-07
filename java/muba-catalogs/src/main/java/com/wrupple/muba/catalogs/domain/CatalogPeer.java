package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.bootstrap.domain.Host;

public interface CatalogPeer extends Host {
	

	/**
	 * used to support multiple users logged in a peer, this value represents the state in the catalog provider side that spawned this peer
	 * 
	 * @return
	 */
	Integer getStakeHolderIndex();

	String getCatalogUrlBase();
	
//	List<String> getSuscriptions();
	
	
	
}
