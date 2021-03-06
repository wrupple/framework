package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.VariableDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.ChocoVariableDescriptorImpl;
import com.wrupple.muba.worker.server.service.ChocoModelResolver;
import org.chocosolver.solver.Model;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.chocosolver.solver.variables.IntVar;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class ChocoModelResolverImpl implements ChocoModelResolver {
    protected Logger log = LogManager.getLogger(ChocoModelResolverImpl.class);

    private final String modelKey;

    @Inject
    public ChocoModelResolverImpl(@Named("choco.model.key")String modelKey) {
        this.modelKey = modelKey;
    }

    private Model createSolverModel(ApplicationContext context) {
        log.info("Generating new problem model");
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

    @Override
    public void clearModel(ApplicationContext context) {
        context.remove(modelKey);
    }

}
