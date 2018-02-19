package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.event.domain.Task;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.DefineSolutionCriteria;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ListIterator;

/**
 * uses event bus sentence interpret to invoke solvers via unaware apis
 * <p>
 * Created by rarl on 11/05/17.
 */
public class DefineSolutionCriteriaImpl implements DefineSolutionCriteria {


    protected Logger log = LoggerFactory.getLogger(DefineSolutionCriteriaImpl.class);

    @Inject
    public DefineSolutionCriteriaImpl() {
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        final ApplicationContext context = (ApplicationContext) ctx;
        Task request = context.getStateValue().getTaskDescriptorValue();

        ListIterator<String> activitySentence = request.getSentence().listIterator();

        log.debug("posting solution constraints from task definition");
        processNextConstraint(activitySentence, context);
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
                log.trace(" {} token signals usage of interpret {}", next, interpret);
                interpret.resolve(sentence, context, next);
                processNextConstraint(sentence, context);
            } else {
                log.trace(" {} does not seem to be an interpret DN", next);
                sentence.previous();
            }
        } else {
            log.debug("all tokens have been used");
        }
    }


}
