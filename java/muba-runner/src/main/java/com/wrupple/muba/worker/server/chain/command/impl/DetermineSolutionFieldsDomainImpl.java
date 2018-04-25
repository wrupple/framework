package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.DataContract;
import com.wrupple.muba.event.domain.Task;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.VariableDescriptor;
import com.wrupple.muba.worker.server.chain.command.DetermineSolutionFieldsDomain;
import com.wrupple.muba.worker.server.service.ProcessManager;
import com.wrupple.muba.worker.server.service.Solver;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by rarl on 11/05/17.
 */
public class DetermineSolutionFieldsDomainImpl implements DetermineSolutionFieldsDomain {

    protected Logger log = LogManager.getLogger(DetermineSolutionFieldsDomainImpl.class);

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
        solutionTypeInquiry.setName(DataContract.READ_ACTION);

        CatalogDescriptor solutionDescriptor = context.getRuntimeContext().getServiceBus().fireEvent(solutionTypeInquiry,context.getRuntimeContext(),null);

        context.getStateValue().setCatalogValue(solutionDescriptor);

        log.debug("Resolving problem variable names");
        List<VariableDescriptor> variables  = solutionDescriptor.getFieldsValues().stream().
                map(field -> solver.isEligible(field,context)).
                filter(e->e!=null).
                map(eligibility -> eligibility.createVariable()).
                collect(Collectors.toList());
        context.getStateValue().setSolutionVariablesValues(variables);

        return CONTINUE_PROCESSING;
    }



}
