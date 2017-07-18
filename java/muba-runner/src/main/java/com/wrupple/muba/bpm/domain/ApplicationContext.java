package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.bootstrap.domain.ServiceContext;

/**
 * Created by japi on 11/05/17.
 */
public interface ApplicationContext extends ApplicationState,ServiceContext {
    final String CATALOG = "ApplicationContext";
}