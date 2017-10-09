package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.bpm.server.chain.command.DetermineSolutionFieldsDomain;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.DataEvent;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rarl on 11/05/17.
 */
public class DetermineSolutionFieldsDomainImpl implements DetermineSolutionFieldsDomain {

    protected Logger log = LoggerFactory.getLogger(DetermineSolutionFieldsDomainImpl.class);

    private final SolverCatalogPlugin plugin;

    @Inject
    public DetermineSolutionFieldsDomainImpl(SolverCatalogPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        final ApplicationContext context = (ApplicationContext) ctx;
        ProcessTaskDescriptor request = context.getStateValue().getTaskDescriptorValue();

        final Solver solver = plugin.getSolver();
        log.debug("Resolving Solution Type");
        String solutionType =(String) request.getCatalog();

        CatalogActionRequestImpl solutionTypeInquiry = new CatalogActionRequestImpl();
        solutionTypeInquiry.setEntry(solutionType);
        solutionTypeInquiry.setCatalog(CatalogDescriptor.CATALOG_ID);
        solutionTypeInquiry.setName(DataEvent.READ_ACTION);

        CatalogDescriptor solutionDescriptor = context.getRuntimeContext().getEventBus().fireEvent(solutionTypeInquiry,context.getRuntimeContext(),null);
        context.getStateValue().setSolutionDescriptor(solutionDescriptor);

        //by default, all fields are eligible for solving
        Collection<FieldDescriptor> fieldValues = solutionDescriptor.getFieldsValues();
        log.debug("Resolving problem variable names");
        List<VariableDescriptor> variables  = fieldValues.stream().
                filter(field -> solver.isEligible(field,context)).
                map(field -> solver.createVariable(field,context)).
                collect(Collectors.toList());
        context.getStateValue().setSolutionVariables(variables);

        return CONTINUE_PROCESSING;
    }



}
