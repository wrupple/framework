package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import com.wrupple.muba.catalogs.server.service.impl.StaticCatalogDescriptorProvider;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rarl on 10/05/17.
 */
public class SolverCatalogPluginImpl extends StaticCatalogDescriptorProvider implements SolverCatalogPlugin {

    @Inject
    public SolverCatalogPluginImpl(Solver solver, @Named(Workflow.WORKFLOW_CATALOG) CatalogDescriptor appItem, @Named(ApplicationState.CATALOG) CatalogDescriptor state, @Named(Task.CATALOG) CatalogDescriptor taskDescP,
                                   @Named(TaskToolbarDescriptor.CATALOG) CatalogDescriptor toolbarDescP,
                                   @Named(WruppleActivityAction.CATALOG) CatalogDescriptor actionDescP) {
        super.put(taskDescP);
        super.put(actionDescP);
        super.put(toolbarDescP);
        super.put(state);
        super.put(appItem);
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
    public void postProcessCatalogDescriptor(CatalogDescriptor c, CatalogActionContext context) {

    }

}
