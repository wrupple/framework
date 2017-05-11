package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;
import com.wrupple.muba.bpm.server.service.impl.TaskRunnerPluginImpl;

/**
 * Created by rarl on 10/05/17.
 */
public class TaskRunnerModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(TaskRunnerPlugin.class).to(TaskRunnerPluginImpl.class).in(Singleton.class);
    }
}
