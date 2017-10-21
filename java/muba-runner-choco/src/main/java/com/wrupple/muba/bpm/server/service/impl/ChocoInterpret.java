package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.ChocoModelResolver;
import com.wrupple.muba.event.domain.impl.JavaNativeInterfaceContext;
import com.wrupple.muba.event.server.chain.command.SentenceNativeInterface;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import org.apache.commons.chain.Context;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.ListIterator;

@Singleton
public class ChocoInterpret implements NaturalLanguageInterpret {

    private final SentenceNativeInterface nativeInterface;
    protected Logger log = LoggerFactory.getLogger(ChocoInterpret.class);
    private final ChocoModelResolver delegate;

    @Inject
    public ChocoInterpret(SentenceNativeInterface nativeInterface, ChocoModelResolver delegate) {
        this.nativeInterface=nativeInterface;
        this.delegate = delegate;
    }

    @Override
    public void run(ListIterator<String> sentence, Context context) throws Exception {
        Model model = delegate.resolveSolverModel((ApplicationContext) context);
        JavaNativeInterfaceContext invoker = new JavaNativeInterfaceContext(model,sentence);
        Arrays.stream(model.getVars()).forEach(var -> invoker.put(var.getName(),var));
        invoker.sentenceIterator=sentence;
        nativeInterface.execute(invoker);

        postConstraint(invoker);
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
