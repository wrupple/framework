package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.server.chain.RemoteServiceChain;

public interface Channel extends CatalogEntry, RemoteServiceChain.Link{

    String CATALOG = "Channel";


}
