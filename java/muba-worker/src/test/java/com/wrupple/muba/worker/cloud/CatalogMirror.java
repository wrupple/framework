package com.wrupple.muba.worker.cloud;

public class CatalogMirror {
	// The same mechanism used to keep client-cache synchronized
	// Recover from BPM Peer
	/*
	 * Delete local catalog, read all foreign entries and write them locally
	 * (preserving Ids, which means it cannot be done in multidomain JDO
	 * Catalogs)
	 * 
	 * peers suscribed to events
	 */
}
