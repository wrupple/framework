package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.BinaryOperation;
import com.wrupple.muba.event.domain.impl.CatalogOperand;
import com.wrupple.muba.event.domain.impl.PathToken;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.AssignedForeignKey;
import com.wrupple.muba.worker.domain.ChocoVariableDescriptorImpl;
import com.wrupple.muba.worker.server.service.ChocoModelResolver;
import com.wrupple.muba.worker.server.service.ChocoRunner;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.server.service.VariableEligibility;
import org.chocosolver.solver.Model;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.chocosolver.solver.variables.IntVar;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class ChocoRunnerImpl implements ChocoRunner {
    private final Long runnerId;
    protected Logger log = LogManager.getLogger(ChocoRunnerImpl.class);

    private final Provider<FutureChocoVariable> future;
    private final ChocoModelResolver delegate;
    private final FieldAccessStrategy access;

    @Inject
    public ChocoRunnerImpl(@Named("com.wrupple.runner.choco") Long runnerId,Provider<FutureChocoVariable> future, ChocoModelResolver delegate, FieldAccessStrategy access) {
        this.future = future;
        this.delegate = delegate;
        this.runnerId=runnerId;
        this.access = access;
    }


    @Override
    public void prepare(ApplicationContext context) {

    }

    @Override
    public void onProblemSolved(ApplicationContext context) {
        delegate.clearModel(context);
    }

    @Override
    public boolean canSolve(ApplicationContext context) {
        return  delegate.resolveSolverModel(context)!=null;
    }

    @Override
    public boolean canHandle(FieldDescriptor field, ApplicationContext context) {
        //only integer fields with constraints or defined domains are eligible
        boolean eligibility = field.getDataType()== CatalogEntry.INTEGER_DATA_TYPE && ((field.getDefaultValueOptions()!=null && !field.getDefaultValueOptions().isEmpty())
                || (field.getConstraintsValues()!=null && !field.getConstraintsValues().isEmpty())||(field.getSentence()!=null && !field.getSentence().isEmpty()));


        return eligibility;
    }

    @Override
    public VariableEligibility handleAsVariable(FieldDescriptor field, ApplicationContext context) {
        return future.get().of(field,context);
    }

    @Override
    public boolean solve(ApplicationContext context, StateTransition<ApplicationContext> callback) {
        Model model = delegate.resolveSolverModel(context);
         /*else if(model.getSolver().hasReachedLimit()){
            //System.out.println("The could not find a solution nor prove that none exists in the given limits");
        }*/
        log.info("Solving {}",model.toString());

        boolean retorno = model.getSolver().solve();
        try {
            callback.execute(context);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return retorno;
    }

    @Override
    public void model(Operation result, ApplicationContext context, Instrospection intros) {
        if("-".equals(result.getName())){
            Model model = delegate.resolveSolverModel(context);

            BinaryOperation operation = (BinaryOperation) result;
            int vector_1_length = applyOperand(operation.getOperand_1(),operation,context,intros,model);
            int vector_2_length = applyOperand(operation.getOperand_2(),operation,context,intros,model);
            log.debug("modeling substraction");

            if(vector_1_length>1&&vector_2_length>1){
                throw new IllegalArgumentException("Substraction cannot be applied with both operand's vectors' length > 1 ");
            }else if(vector_1_length>1&&vector_2_length>1){
                throw new IllegalArgumentException("Binary operation requires two parameters");
            } else if (vector_1_length==vector_2_length){
                throw new IllegalArgumentException("Unsupported scenario");

            } else{
                int difference_vector_length = vector_1_length > vector_2_length ? vector_1_length : vector_2_length;
                IntVar[] difference = new IntVar[difference_vector_length];
                IntVar singleVariable = (IntVar) (vector_1_length > vector_2_length ? operation.getOperandVariable_2() : operation.getOperandVariable_1());
                IntVar[] multipleVariable = (IntVar[]) (vector_1_length > vector_2_length ? operation.getOperandVariable_1() : operation.getOperandVariable_2());
                for (int j = 0; j < difference_vector_length; j++) {
                    difference[j]=singleVariable.sub(multipleVariable[j]).abs().intVar();
                    if(log.isDebugEnabled()){
                        log.debug("modeled substraction {}-{}",operation.getOperand_1(),operation.getOperand_2());
                    }
                }


                List<VariableDescriptor> variableDescriptors = context.getStateValue().getSolutionVariablesValues();



                Optional<VariableDescriptor> bookingDistance = variableDescriptors.stream().filter(v->v.getField()==operation.getTargetField()).findAny();

                if(bookingDistance.isPresent()){
                    PathToken foreignOperand = operation.getPath();
                    IntVar foreignKeyAssignation = model.intVar(operation.getTargetField().getDistinguishedName()+"_diff_vector", 1, difference_vector_length, false);
                    model.element((IntVar) ((ChocoVariableDescriptorImpl)bookingDistance.get()).getVariable(), difference, foreignKeyAssignation, 1).post();
                    variableDescriptors.add(new AssignedForeignKey(runnerId,foreignOperand.getTargetField(),foreignKeyAssignation,foreignOperand.getResults()));
                    log.info("Constrained element [ {} ] = [ {} ]",bookingDistance.get(),foreignKeyAssignation);
                    operation.setModeled(true);

                }else{
                    throw new IllegalStateException("No variable modeled for field "+operation.getTargetField().getDistinguishedName());
                }


            }

        }else if(CatalogOperand.CATALOG.equals(result.getName())){
            CatalogOperand operation = (CatalogOperand)result;

            FieldDescriptor field = operation.getTargetField();
            List<CatalogEntry> results=operation.getRequest().getResults();
            if(results==null){
                log.info("... waiting for results");
            }else{
                int NUM_DRIVERS =results.size();
                log.info("resolving catalog bound variable domain with {} posible outcomes",NUM_DRIVERS);
                int[] LOCATIONS = new int[NUM_DRIVERS];
                CatalogEntry current;
                Number fieldValue;
                for(int i =0; i < NUM_DRIVERS; i++) {
                    current = results.get(i);
                    try {
                        fieldValue= (Number) access.getPropertyValue(field,current,null,intros);
                        LOCATIONS[i]=fieldValue.intValue();
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException("While resolving "+field.getDistinguishedName(),e);
                    }
                }

            }

        }else{
            log.info("no operations matched {}",result.getName());
        }


    }


    private int applyOperand(Object operand, BinaryOperation operation, ApplicationContext context, Instrospection intros, Model model) {

        if(operand instanceof CatalogOperand){
            return modelCatalogOperand(operation,(CatalogOperand)operand,context,intros,model);
        }else {
            if(operand instanceof  Number){
                if(operation.getOperandVariable_1()==null){
                    IntVar bookingLocation = model.intVar(((Number) operand).intValue());
                    log.info("new constant as operation's first operand {}",bookingLocation);
                    operation.setOperandVariable_1(bookingLocation);
                }else if(operation.getOperandVariable_2()==null){
                    IntVar bookingLocation = model.intVar(((Number) operand).intValue());
                    log.info("new constant as  binary operation's second operand {}",bookingLocation);
                    operation.setOperandVariable_2(bookingLocation);
                }
            }

            return 1;
        }
    }


    private int modelCatalogOperand(BinaryOperation operation, CatalogOperand catalogRequest, ApplicationContext context, Instrospection intros, Model model) {
        log.info("model catalog operand {}",catalogRequest.getTargetField());

        RuntimeContext runtime = context.getRuntimeContext();

        List<CatalogEntry> results = null;
        try {
            results = runtime.getServiceBus().fireEvent(catalogRequest.getRequest(), runtime, null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrive foreign entries",e);
        }

        if(results==null){
            log.info("No results, so no values.");
        }else {
            List<Long> values = results.stream().map(entry -> {
                try {
                    return access.getPropertyValue(catalogRequest.getTargetField(), entry, null, intros);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Unable to read " + catalogRequest.getTargetField().getDistinguishedName() + " from foreign entry", e);
                }
            }).map(value -> (Long) value).collect(Collectors.toList());


            int[] vector = new int[values.size()];

            for (int i = 0; i < values.size(); i++) {
                vector[i] = values.get(i).intValue();
            }

            IntVar[] driverLocations = model.intVarArray(catalogRequest.getRequest().getCatalog() + "_" + catalogRequest.getTargetField().getDistinguishedName(), vector.length, vector);
            if(operation.getOperandVariable_1()==null){
                log.info("set binary operation's first operand {}",driverLocations);
                operation.setOperandVariable_1(driverLocations);
            }else{
                log.info("set binary operation's first operand {}",driverLocations);
                operation.setOperandVariable_2(driverLocations);

            }
            catalogRequest.getPath().setResults(results);
            operation.setPath(catalogRequest.getPath());

            return vector.length;
        }
        return 0;
    }


}
