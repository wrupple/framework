package com.wrupple.muba.bpm.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.JavaNativeInterfaceContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasCommand;
import com.wrupple.muba.bootstrap.server.chain.command.SentenceNativeInterface;
import com.wrupple.muba.bpm.domain.ActivityContext;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.server.chain.command.DefineSolutionCriteria;
import com.wrupple.muba.bpm.server.service.TaskRunnerPlugin;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import org.apache.commons.chain.Context;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Stream;

/**
 * Created by rarl on 11/05/17.
 */
public class DefineSolutionCriteriaImpl implements DefineSolutionCriteria {

    private final TaskRunnerPlugin plugin;
    private final SentenceNativeInterface nativeInterface;

    @Inject
    public DefineSolutionCriteriaImpl(TaskRunnerPlugin plugin,SentenceNativeInterface nativeInterface) {
        this.plugin = plugin;
        this.nativeInterface=nativeInterface;
    }

    protected Logger log = LoggerFactory.getLogger(DefineSolutionCriteriaImpl.class);
    @Override
    public boolean execute(Context ctx) throws Exception {
        ExcecutionContext requestContext = (ExcecutionContext) ctx;
        ProcessTaskDescriptor request = (ProcessTaskDescriptor) requestContext.getServiceContract();
        ActivityContext context = requestContext.getServiceContext();

        log.info("Resolving problem model");
        Model model = plugin.getSolver().resolveProblemContext(context);

        ListIterator<String> activitySentence = request.getSentence().listIterator();
        JavaNativeInterfaceContext invoker = new JavaNativeInterfaceContext(model,activitySentence);
        log.debug("exposing problem variables to native apit ivoker");

        Arrays.stream(model.getVars()).forEach(var -> invoker.put(var.getName(),var));

        log.debug("posting solution constraints from task definition");
        processNextConstraint(invoker,activitySentence,model,request,context);
        log.debug("posting solution constraints from excecution context");
        activitySentence = requestContext.getSentence().listIterator();
        processNextConstraint(invoker,activitySentence,model,request,context);

        return CONTINUE_PROCESSING;
    }

    private void processNextConstraint(JavaNativeInterfaceContext invokerContext,ListIterator<String> sentence, Model model, ProcessTaskDescriptor request, ActivityContext context) throws Exception {
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
