package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.FieldSynthesizer;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.DefineSolutionCriteria;
import com.wrupple.muba.worker.server.service.ProcessManager;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import java.util.ListIterator;

/**
 * uses event bus sentence interpret to invoke solvers via unaware apis
 * <p>
 * Created by rarl on 11/05/17.
 */
public class DefineSolutionCriteriaImpl implements DefineSolutionCriteria {


    protected Logger log = LogManager.getLogger(DefineSolutionCriteriaImpl.class);
    private final FieldSynthesizer synthetizationDelegate;
    private final FieldAccessStrategy access;

    private final ProcessManager bpm;

    @Inject
    public DefineSolutionCriteriaImpl(FieldSynthesizer synthetizationDelegate, FieldAccessStrategy access, ProcessManager bpm) {
        this.synthetizationDelegate = synthetizationDelegate;
        this.access = access;
        this.bpm = bpm;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        final ApplicationContext context = (ApplicationContext) ctx;
        Task request = context.getStateValue().getTaskDescriptorValue();

        CatalogEntry subject = context.getStateValue().getEntryValue();
        CatalogDescriptor descriptor = context.getStateValue().getCatalogValue();
        if(!DataContract.READ_ACTION.equals(request.getName())&&descriptor!=null){
            Instrospection intros=null;
            for (FieldDescriptor field : descriptor.getFieldsValues()) {
                if (field.getSentence() != null && !field.getSentence().isEmpty()) {
                    log.info("posting solution constraints from field {} definition",field.getDistinguishedName());
                    if(intros==null){
                        intros = access.newSession(subject);
                    }
                    RuntimeContext runtime = context.getRuntimeContext();
                    Object result = synthetizationDelegate.synthethizeFieldValue(field.getSentence().listIterator(), context, subject,descriptor, field, intros, runtime.getServiceBus());
                    if(result instanceof Operation){
                        if(((Operation) result).getName()==null){
                            throw new NullPointerException("operation with no name was synthesized with "+field.getDistinguishedName());
                        }
                        bpm.getSolver().model((Operation) result,context,intros);
                    }
                }
            }
        }
        ListIterator<String> activitySentence;
        if(request.getSentence()!=null){
            activitySentence= request.getSentence().listIterator();
            //Arrays.asList(EVALUATING_VARIABLE,"setObjective","(","false","ctx:driverDistance",")")
            //model.setObjective(false/*ResolutionPolicy.MINIMIZE*/, bookingDistance);

            log.debug("posting solution constraints from task definition");
            processNextConstraint(activitySentence, context);
        }

        log.debug("posting solution constraints from excecution context");
        activitySentence = context.getRuntimeContext();
        processNextConstraint(activitySentence, context);

        return CONTINUE_PROCESSING;
    }

    private void processNextConstraint(ListIterator<String> sentence, ApplicationContext context) throws Exception {
        if (sentence.hasNext()) {
            String next = sentence.next();
            if (context.getRuntimeContext().getServiceBus().hasInterpret(next)) {
                NaturalLanguageInterpret interpret = context.getRuntimeContext().getServiceBus().getInterpret(next);
                log.info(" {} signals usage of {}", next, interpret);
                interpret.resolve(sentence, context, next);
                processNextConstraint(sentence, context);
            } else {
                log.debug(" {} does not seem to be an interpret DN", next);
                sentence.previous();
            }
        } else {
            log.debug("all tokens have been used");
        }
    }


}
