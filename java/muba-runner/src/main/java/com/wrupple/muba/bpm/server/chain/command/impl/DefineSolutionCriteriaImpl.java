package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.bpm.server.chain.command.DefineSolutionCriteria;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.event.server.chain.command.SentenceNativeInterface;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ListIterator;

/**
 * uses event bus sentence interpret to invoke solvers via unaware apis
 *
 * Created by rarl on 11/05/17.
 */
public class DefineSolutionCriteriaImpl implements DefineSolutionCriteria {

    private final ProcessManager plugin;
    private final SentenceNativeInterface nativeInterface;

    @Inject
    public DefineSolutionCriteriaImpl(ProcessManager plugin, SentenceNativeInterface nativeInterface) {
        this.plugin = plugin;
        this.nativeInterface=nativeInterface;
    }

    protected Logger log = LoggerFactory.getLogger(DefineSolutionCriteriaImpl.class);
    @Override
    public boolean execute(Context ctx) throws Exception {
        final ApplicationContext context = (ApplicationContext) ctx;
        Task request = context.getStateValue().getTaskDescriptorValue();

        ListIterator<String> activitySentence = request.getSentence().listIterator();
        log.debug("exposing problem variables to native apit ivoker");

        log.debug("posting solution constraints from task definition");
        processNextConstraint(activitySentence,context);
        log.debug("posting solution constraints from excecution context");
        activitySentence = context.getRuntimeContext();
        processNextConstraint(activitySentence,context);

        return CONTINUE_PROCESSING;
    }

    private void processNextConstraint(ListIterator<String> sentence,  ApplicationContext context) throws Exception {
        if(sentence.hasNext()){
            String next = sentence.next();
            if(context.getRuntimeContext().getEventBus().hasInterpret(next)){
                context.getRuntimeContext().getEventBus().getInterpret(next).run(sentence, context, next);
                processNextConstraint(sentence,context);
            }else{
                log.trace(" {} does not seem to be an interpret dn",next);
                sentence.previous();
            }
        }else{
            log.debug("all tokens have been used");
        }
    }


}
