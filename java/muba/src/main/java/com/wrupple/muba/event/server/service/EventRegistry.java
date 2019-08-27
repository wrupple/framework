package com.wrupple.muba.event.server.service;

import com.wrupple.muba.event.domain.ServiceManifest;

import java.util.List;

/**
 * Created by japi on 2/08/17.
 */
public interface EventRegistry extends ImplicitEventResolver {

    List<String> generatePathTokens(ServiceManifest handler);

}
