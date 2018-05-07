package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasHostValue;

/**
 * Binds channel used to send events to a host
 */
public interface ChannelAgreement extends CatalogEntry,HasHostValue{

    String CATALOG = "ChannelAgreement";

    Channel getChannelValue();
}
