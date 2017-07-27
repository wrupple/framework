package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.JavaNativeInterfaceContext;
import com.wrupple.muba.bootstrap.server.chain.command.SentenceNativeInterface;
import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.server.chain.command.DefineSolutionCriteria;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import org.apache.commons.chain.Context;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.ListIterator;

/**
 * Created by rarl on 11/05/17.
 */
public class DefineSolutionCriteriaImpl implements DefineSolutionCriteria {

    private final SolverCatalogPlugin plugin;
    private final SentenceNativeInterface nativeInterface;

    @Inject
    public DefineSolutionCriteriaImpl(SolverCatalogPlugin plugin, SentenceNativeInterface nativeInterface) {
        this.plugin = plugin;
        this.nativeInterface=nativeInterface;
    }

    protected Logger log = LoggerFactory.getLogger(DefineSolutionCriteriaImpl.class);
    @Override
    public boolean execute(Context ctx) throws Exception {
        final ApplicationContext context = (ApplicationContext) ctx;
        ProcessTaskDescriptor request = context.getTaskDescriptorValue();

        log.info("Resolving problem model");
        Model model = plugin.getSolver().resolveSolverModel(context);

        ListIterator<String> activitySentence = request.getSentence().listIterator();
        JavaNativeInterfaceContext invoker = new JavaNativeInterfaceContext(model,activitySentence);
        log.debug("exposing problem variables to native apit ivoker");

        Arrays.stream(model.getVars()).forEach(var -> invoker.put(var.getName(),var));

        log.debug("posting solution constraints from task definition");
        processNextConstraint(invoker,activitySentence,model,request,context);
        log.debug("posting solution constraints from excecution context");
        activitySentence = context.getRuntimeContext().getSentence().listIterator();
        processNextConstraint(invoker,activitySentence,model,request,context);

        return CONTINUE_PROCESSING;
    }

    private void processNextConstraint(JavaNativeInterfaceContext invokerContext,ListIterator<String> sentence, Model model, ProcessTaskDescriptor request, ApplicationContext context) throws Exception {
        if(sentence.hasNext()){
            String next = sentence.next();
            if(ProcessTaskDescriptor.CONSTRAINT.equals(next)){
                invokerContext.sentenceIterator=sentence;
                nativeInterface.execute(invokerContext);
                postConstraint(invokerContext);
                processNextConstraint(invokerContext,sentence,model,request,context);
            }else{
                log.warn("no constraint definition was found");
                sentence.previous();
            }
        }else{
            log.debug("all tokens have been used");
        }

    }

    private void postConstraint(JavaNativeInterfaceContext invokerContext) {
        log.info("posting new solution constraint");
        Constraint constraint = (Constraint) invokerContext.result;
        constraint.post();
        if(log.isDebugEnabled()){
            log.debug("    "+constraint.toString());
        }
    }
}
