package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.TaskToolbarDescriptor;
import com.wrupple.muba.bpm.domain.WruppleActivityAction;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.domain.CatalogIdentificationImpl;
import com.wrupple.muba.catalogs.server.domain.ValidationExpression;
import org.apache.commons.chain.Command;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.List;

/**
 * Created by rarl on 10/05/17.
 */
public class SolverCatalogPluginImpl implements SolverCatalogPlugin {

    private final Provider<CatalogDescriptor> taskDescP;
    private final Provider<CatalogDescriptor> actionDescP;
    private final Provider<CatalogDescriptor> toolbarDescP;
    private final Solver solver;

    @Inject
    public SolverCatalogPluginImpl(Solver solver, @Named(ProcessTaskDescriptor.CATALOG) Provider<CatalogDescriptor> taskDescP,
                                   @Named(TaskToolbarDescriptor.CATALOG) Provider<CatalogDescriptor> toolbarDescP,
                                   @Named(WruppleActivityAction.CATALOG) Provider<CatalogDescriptor> actionDescP) {
        this.taskDescP = taskDescP;
        this.actionDescP = actionDescP;
        this.toolbarDescP = toolbarDescP;
        this.solver=solver;
    }

    @Override
    public CatalogDescriptor getDescriptor(String catalogId, CatalogActionContext context) throws RuntimeException {
        if (ProcessTaskDescriptor.CATALOG.equals(catalogId)) {
            return taskDescP.get();
        } else if (TaskToolbarDescriptor.CATALOG.equals(catalogId)) {
            return toolbarDescP.get();
        } else if (WruppleActivityAction.CATALOG.equals(catalogId)) {
            return actionDescP.get();
        }
        return null;
    }

    @Override
    public CatalogDescriptor getDescriptor(Long key, CatalogActionContext context) throws RuntimeException {
        return null;
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
    public void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogActionContext context) throws Exception {
        names.add(new CatalogIdentificationImpl(ProcessTaskDescriptor.CATALOG, "Task Descriptor",
                "/static/img/task.png"));
        names.add(
                new CatalogIdentificationImpl(WruppleActivityAction.CATALOG, "Task Action", "/static/img/action.png"));
        names.add(new CatalogIdentificationImpl(TaskToolbarDescriptor.CATALOG, "Task Toolbar",
                "/static/img/task-piece.png"));
    }

    @Override
    public void postProcessCatalogDescriptor(CatalogDescriptor c, CatalogActionContext context) {

    }

    @Override
    public Solver getSolver() {
        return solver;
    }
}
