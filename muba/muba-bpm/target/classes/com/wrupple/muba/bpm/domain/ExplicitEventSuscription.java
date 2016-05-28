package com.wrupple.muba.bpm.domain;

public interface ExplicitEventSuscription extends ManagedObject {

	String CATALOG = "ExplicitEventSuscription";
	
	//explicit notifications may create both an inbox notification and a cache invalidation message
}
