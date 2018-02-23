package com.wrupple.vegetate.service.impl;

import com.wrupple.muba.event.server.chain.ChannelCatalog;
import com.wrupple.vegetate.chain.command.LocalServiceDelegation;
import org.apache.commons.chain.impl.CatalogBase;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChannelCatalogImpl extends CatalogBase implements ChannelCatalog {

    @Inject
    public ChannelCatalogImpl(LocalServiceDelegation local){
        super.addCommand(LocalServiceDelegation.class.getSimpleName(),local);
    }

}
