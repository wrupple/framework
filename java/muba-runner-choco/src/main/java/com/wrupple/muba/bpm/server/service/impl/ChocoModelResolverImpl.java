package com.wrupple.muba.bpm.server.service.impl;

import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.server.service.ChocoModelResolver;
import org.chocosolver.solver.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ChocoModelResolverImpl implements ChocoModelResolver{
    protected Logger log = LoggerFactory.getLogger(ChocoModelResolverImpl.class);

    private final String modelKey;

    @Inject
    public ChocoModelResolverImpl(@Named("choco.model.key")String modelKey) {
        this.modelKey = modelKey;
    }

    private Model createSolverModel(ApplicationContext context) {
        log.info("Resolving new problem model");
        Model model = new Model(String.valueOf(context.getStateValue().getId()));
        return model;
    }

    public Model resolveSolverModel(ApplicationContext context) {

        Model model = (Model) context.get(modelKey);
        if(model==null){
            model = createSolverModel( context);
            context.put(modelKey,model);
        }
        return model;
    }
}
