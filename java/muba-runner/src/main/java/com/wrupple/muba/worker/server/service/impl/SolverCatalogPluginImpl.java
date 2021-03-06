package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.service.PrimaryKeyReaders;
import com.wrupple.muba.catalogs.server.service.QueryReaders;
import com.wrupple.muba.catalogs.server.service.TriggerCreationScope;
import com.wrupple.muba.catalogs.server.service.impl.StaticCatalogDescriptorProvider;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.domain.WruppleActivityAction;
import com.wrupple.muba.catalogs.server.chain.command.KnownHostsProvider;
import com.wrupple.muba.worker.server.service.SolverCatalogPlugin;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rarl on 10/05/17.
 */
public class SolverCatalogPluginImpl extends StaticCatalogDescriptorProvider implements SolverCatalogPlugin {

    @Inject
    public SolverCatalogPluginImpl(KnownHostsProvider mirrors, @Named("catalog.storage.peers") String friendsAndFamily, QueryReaders queryers,
                                   PrimaryKeyReaders primaryKeyers,
                                   @Named(ApplicationDependency.CATALOG) CatalogDescriptor dependency,
                                   @Named(Application.CATALOG) CatalogDescriptor appItem,
                                   @Named(ApplicationState.CATALOG) CatalogDescriptor state,
                                   @Named(WorkerState.CATALOG) CatalogDescriptor container,
                                   @Named(Task.CATALOG) CatalogDescriptor taskDescP,
                                   @Named(TaskToolbarDescriptor.CATALOG) CatalogDescriptor toolbarDescP,
                                   @Named(WruppleActivityAction.CATALOG) CatalogDescriptor actionDescP) {
        super.put(taskDescP);
        super.put(dependency);
        super.put(actionDescP);
        super.put(toolbarDescP);
        super.put(appItem);
        super.put(state);
        super.put(container);
        queryers.addCommand(friendsAndFamily,mirrors);
        primaryKeyers.addCommand(friendsAndFamily,mirrors);
    }


    @Override
    public ValidationExpression[] getValidations() {
        return null;
    }

    @Override
    public Command[] getCatalogActions() {
        return null;
    }

    @Override
    public void postProcessCatalogDescriptor(CatalogDescriptor c, CatalogActionContext context, TriggerCreationScope scope) {

    }

}
