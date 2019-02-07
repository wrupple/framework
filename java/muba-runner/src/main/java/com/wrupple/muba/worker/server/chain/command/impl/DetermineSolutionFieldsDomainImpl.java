package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.DetermineSolutionFieldsDomain;
import com.wrupple.muba.worker.server.chain.command.SolveTask;
import com.wrupple.muba.worker.server.service.ProcessManager;
import com.wrupple.muba.worker.server.service.Solver;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * Created by rarl on 11/05/17.
 */
public class DetermineSolutionFieldsDomainImpl implements DetermineSolutionFieldsDomain {

    protected Logger log = LogManager.getLogger(DetermineSolutionFieldsDomainImpl.class);

    private final ProcessManager bpm;
    private final SolveTask.Callback callback;

    @Inject
    public DetermineSolutionFieldsDomainImpl(ProcessManager bpm, SolveTask.Callback callback){
        this.bpm = bpm;
        this.callback = callback;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        final ApplicationContext context = (ApplicationContext) ctx;
        ApplicationState state = context.getStateValue();
        Task task = state.getTaskDescriptorValue();
        context.getStateValue().setUserSelection(null);

        final Solver solver = bpm.getSolver();
        log.info("Resolving variables of task: "+task.getDistinguishedName());



        CatalogActionRequestImpl solutionTypeInquiry = new CatalogActionRequestImpl();
        solutionTypeInquiry.setEntry( task.getCatalog());
        solutionTypeInquiry.setCatalog(CatalogDescriptor.CATALOG_ID);
        solutionTypeInquiry.setName(DataContract.READ_ACTION);
        solutionTypeInquiry.setFollowReferences(true);

        CatalogDescriptor catalog = context.getRuntimeContext().getServiceBus().fireEvent(solutionTypeInquiry,context.getRuntimeContext(),null);

        context.getStateValue().setCatalogValue(catalog);



        if(task.getKeepOutput()==null||!task.getKeepOutput()){
            state.setEntryValue(null);
        }
        //FIXME missing methods might be in ProblemPresenterImpl
        solver.prepare(context);
        log.debug("Resolving problem variable names");
        List<VariableDescriptor> variables  = catalog.getFieldsValues().stream().
                map(field -> solver.isEligible(field,context)).
                filter(e->e!=null).
                map(eligibility -> eligibility.createVariable()).
                collect(Collectors.toList());
        context.getStateValue().setSolutionVariablesValues(variables);

        return CONTINUE_PROCESSING;
    }


}
