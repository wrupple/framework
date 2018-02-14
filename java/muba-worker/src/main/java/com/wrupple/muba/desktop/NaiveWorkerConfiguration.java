package com.wrupple.muba.desktop;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.client.widgets.TaskContainer;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.Person;
import com.wrupple.muba.event.domain.impl.ContentNodeImpl;
import com.wrupple.muba.worker.server.service.BusinessPlugin;
import com.wrupple.muba.worker.server.service.SolverCatalogPlugin;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

public class NaiveWorkerConfiguration extends AbstractModule{
    @Override
    protected void configure() {

    }



    @Provides
    @com.google.inject.Singleton
    @com.google.inject.Inject
    @com.google.inject.name.Named(Person.CATALOG)
    public CatalogDescriptor activity(
            CatalogDescriptorBuilder builder) {
        CatalogDescriptor r = builder.fromClass(ContentNodeImpl.class, Person.CATALOG,  Person.CATALOG,
                -13344556, null);

        return r;
    }



    @Provides
    @Inject
    @Singleton
    @Named("catalog.plugins")
    public Object plugins(SolverCatalogPlugin /* this is what makes it purr */ runner, BusinessPlugin bpm, SystemCatalogPlugin system) {
        CatalogPlugin[] plugins = new CatalogPlugin[]{system,bpm, runner};
        return plugins;
    }


    @Provides
    public ProcessWindow queryRunner() {
        return new ProcessWindow() {
            @Override
            public TaskContainer getRootTaskPresenter() {
                return null;
            }
        };
    }
}
