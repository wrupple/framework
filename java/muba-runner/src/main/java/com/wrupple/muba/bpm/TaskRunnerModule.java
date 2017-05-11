package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.domain.RunnerServiceManifest;
import com.wrupple.muba.bpm.domain.impl.RunnerServiceManifestImpl;
import com.wrupple.muba.bpm.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.bpm.server.chain.command.impl.ActivityRequestInterpretImpl;
import com.wrupple.muba.bpm.server.service.impl.TaskRunnerPluginImpl;

/**
 * Created by rarl on 10/05/17.
 */
public class TaskRunnerModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(ActivityRequestInterpret.class).to(ActivityRequestInterpretImpl.class).in(Singleton.class);
        bind(RunnerServiceManifest.class).to(RunnerServiceManifestImpl.class).in(Singleton.class);
        bind(TaskRunnerPlugin.class).to(TaskRunnerPluginImpl.class).in(Singleton.class);
    }
}
