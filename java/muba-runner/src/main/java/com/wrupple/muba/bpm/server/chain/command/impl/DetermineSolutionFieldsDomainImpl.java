package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.bpm.domain.VariableDescriptor;
import com.wrupple.muba.bpm.server.chain.command.DetermineSolutionFieldsDomain;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.DataEvent;
import com.wrupple.muba.event.domain.FieldDescriptor;
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

    private final ProcessManager bpm;

    @Inject
    public DetermineSolutionFieldsDomainImpl(ProcessManager bpm){
        this.bpm = bpm;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        final ApplicationContext context = (ApplicationContext) ctx;
        Task request = context.getStateValue().getTaskDescriptorValue();

        final Solver solver = bpm.getSolver();
        log.debug("Resolving Solution Type");
        String solutionType =(String) request.getCatalog();

        CatalogActionRequestImpl solutionTypeInquiry = new CatalogActionRequestImpl();
        solutionTypeInquiry.setEntry(solutionType);
        solutionTypeInquiry.setCatalog(CatalogDescriptor.CATALOG_ID);
        solutionTypeInquiry.setName(DataEvent.READ_ACTION);

        List results = context.getRuntimeContext().getEventBus().fireEvent(solutionTypeInquiry,context.getRuntimeContext(),null);
        CatalogDescriptor solutionDescriptor = (CatalogDescriptor) results.get(0);
        context.getStateValue().setCatalogValue(solutionDescriptor);

        //by default, all fields are eligible for solving
        Collection<FieldDescriptor> fieldValues = solutionDescriptor.getFieldsValues();
        log.debug("Resolving problem variable names");
        List<VariableDescriptor> variables  = fieldValues.stream().
                filter(field -> solver.isEligible(field,context)).
                map(field -> solver.createVariable(field,context)).
                collect(Collectors.toList());
        context.getStateValue().setSolutionVariablesValues(variables);

        return CONTINUE_PROCESSING;
    }



}
