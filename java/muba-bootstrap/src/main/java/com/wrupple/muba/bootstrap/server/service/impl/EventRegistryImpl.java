package com.wrupple.muba.bootstrap.server.service.impl;

import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bootstrap.server.service.EventRegistry;
import org.apache.commons.beanutils.BeanUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 *
 * Service Bus: where to keep the intent dictionary
 * Created by japi on 2/08/17.
 */
@Singleton
public class EventRegistryImpl implements EventRegistry {


    @Inject
    public EventRegistryImpl() {
    }

    //sentence(Explicit intent) with contract
    //returnConfigured (contract with sentence) this doesnt appear to need to exist in server

    @Override
    public ExplicitIntent resolveIntent(ImplicitIntent intent, RuntimeContext context) throws Exception {
        //ApplicationResolver
        //ServiceResolver

    }

}
