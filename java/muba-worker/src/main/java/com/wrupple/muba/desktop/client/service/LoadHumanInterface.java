package com.wrupple.muba.desktop.client.service;

import com.wrupple.muba.bpm.domain.Application;
import com.wrupple.muba.desktop.domain.LaunchWorker;
import com.wrupple.muba.event.domain.RuntimeContext;

public interface LoadHumanInterface {
    Application getInitialActivity(LaunchWorker request, RuntimeContext parent) throws Exception;
}
