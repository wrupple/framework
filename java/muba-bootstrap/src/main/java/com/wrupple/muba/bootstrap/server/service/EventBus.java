package com.wrupple.muba.bootstrap.server.service;

import com.wrupple.muba.bootstrap.domain.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by japi on 2/08/17.
 */
public interface EventBus {

    public interface HandlerRegistration {
        void removeHandler();
    }

    public ExplicitIntent resolveIntent(ImplicitIntent intent, RuntimeContext context) throws Exception;

    HandlerRegistration addHandler(ServiceManifest serviceManifes);

    //FIXME fireEvent(event)

    List<String> resolveContractSentence(CatalogEntry serviceContract) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException;
}
