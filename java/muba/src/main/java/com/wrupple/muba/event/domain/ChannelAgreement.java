package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasHostValue;

public interface ChannelAgreement extends CatalogEntry,HasHostValue{

    String CATALOG = "ChannelAgreement";

    Channel getChannelValue();
}
