package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.server.chain.RemoteServiceChain;

/**
 * Method to send events to a host
 */
public interface Channel extends CatalogEntry, RemoteServiceChain.Link{

    String CATALOG = "Channel";


}
