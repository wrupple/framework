package com.wrupple.muba.event.server.chain;

import org.apache.commons.chain.Chain;

/**
 * each command of the chain adds a host that is some how concerned about the event
 * @author japi
 *
 */
public interface EventSuscriptionChain extends Chain {
	
	final String CURRENT_EVENT="catalog.event.processing";
	final String CONCERNED_CLIENTS = "catalog.event.concerned";

}
