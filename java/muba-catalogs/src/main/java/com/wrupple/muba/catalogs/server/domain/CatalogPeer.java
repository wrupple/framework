package com.wrupple.muba.catalogs.server.domain;

import com.wrupple.vegetate.domain.VegetatePeer;

public interface CatalogPeer extends VegetatePeer {

	/**
	 * @return the platform the peer is running on
	 */
	String getAgent();
	void setAgent(String s);
	
	/**
	 * this is usually just the entry's domain as a String
	 * 
	 * @return the domain keys have access to in the foreign server
	 */
	String getCatalogDomain();
	
	/**
	 * used to support multiple users logged in a peer, this value represents the state in the catalog provider side that spawned this peer
	 * 
	 * @return
	 */
	int getStakeHolderIndex();

	String getCatalogUrlBase();
	
}
