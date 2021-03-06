package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.Constraint;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.ChocoVariableDescriptorImpl;
import com.wrupple.muba.event.domain.VariableDescriptor;
import com.wrupple.muba.worker.server.service.ChocoModelResolver;
import com.wrupple.muba.worker.server.service.VariableEligibility;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

public class FutureChocoVariable implements VariableEligibility {
    protected Logger log = LogManager.getLogger(FutureChocoVariable.class);
    private final ChocoModelResolver delegate;
    private final Long chocoRunnerId;

    private FieldDescriptor field;
    private ApplicationContext context;

    @Inject
    public FutureChocoVariable(ChocoModelResolver delegate, @Named("com.wrupple.runner.choco") Long runnerId) {
        this.delegate = delegate;
        this.chocoRunnerId = runnerId;
    }


    public VariableEligibility of(FieldDescriptor field, ApplicationContext context) {
        this.field=field;
        this.context=context;
        return this;
    }

    @Override
    public VariableDescriptor createVariable() {
        return new ChocoVariableDescriptorImpl(makeIntegerVariable(field, delegate.resolveSolverModel(context)), field, chocoRunnerId);
    }




    private Variable makeIntegerVariable(FieldDescriptor field, Model model ) {
        String fieldId = field.getDistinguishedName();
        log.debug("Assigning solution domain for {}",fieldId);


        IntVar variable;
        //TODO each of this cases should be named and mapped to easily add more variable types
        if(field.getDefaultValueOptions()!=null && !field.getDefaultValueOptions().isEmpty()){
            log.info("New variable with enumerated domain {}",fieldId);
            variable = model.intVar(fieldId, resolveEnumeratedDomain(field.getDefaultValueOptions())); // y in {2, 3, 8}
        }else if(field.getConstraintsValues()!=null && !field.getConstraintsValues().isEmpty()){
            log.info("New variable with bounded domain {}",fieldId);
            List<Constraint> constraints = field.getConstraintsValues();
            variable = model.intVar(fieldId, resolveDomainBound(constraints,fieldId,false),resolveDomainBound(constraints,fieldId,true)); // x in [0,5]
        }else{
            throw new IllegalArgumentException("unable to make a variable out of field "+fieldId);
        }
        return variable;

    }

    private IntVar parseVariableFromSentence(FieldDescriptor field, Model model) {
        return null;
    }

    private int resolveDomainBound(List<Constraint> constraints,String fieldId,boolean upperBound) {
        String constriantName ;
        if(upperBound){
            log.trace("resolving variable upper bound");
            constriantName = Max.class.getSimpleName();
        }else{
            log.trace("resolving variable lower bound");
            constriantName = Min.class.getSimpleName();
        }
        Optional<Constraint> match = constraints.stream().filter(constraint -> constriantName.equals(constraint.getDistinguishedName())).findAny();
        if(match.isPresent()){
            return constraintValue(match.get().getProperties(),fieldId);
        }else{
            throw new IllegalArgumentException("No Max or Min bounds defined by field "+fieldId);
        }
    }

    private int constraintValue(List<String> properties,String fieldId) {
        Optional<String> match = properties.stream().filter(s -> s.startsWith("value=")).findAny();
        if(match.isPresent()){
            String property = match.get();
            int split = property.indexOf('=');
            String value = property.substring(split+1 );
            if(log.isDebugEnabled()){
                log.debug("bounded value :"+value);
            }
            return Integer.parseInt(value);
        }else{
            throw new IllegalArgumentException("No Max or Min bounds defined by field "+fieldId);
        }
    }


    private int[] resolveEnumeratedDomain(List<String> domain) {
        int[] regreso = new int[domain.size()];
        for(int i = 0 ; i < regreso.length; i++){
            regreso[i] = Integer.parseInt(domain.get(i));
        }
        return regreso;
    }


}
