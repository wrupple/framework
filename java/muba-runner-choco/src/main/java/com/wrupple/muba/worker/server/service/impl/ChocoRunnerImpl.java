package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.BinaryOperation;
import com.wrupple.muba.event.domain.impl.CatalogOperand;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.domain.ApplicationContext;
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
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class ChocoRunnerImpl implements ChocoRunner {
    protected Logger log = LogManager.getLogger(ChocoRunnerImpl.class);

    private final Provider<FutureChocoVariable> future;
    private final ChocoModelResolver delegate;
    private final FieldAccessStrategy access;

    @Inject
    public ChocoRunnerImpl(Provider<FutureChocoVariable> future, ChocoModelResolver delegate, FieldAccessStrategy access) {
        this.future = future;
        this.delegate = delegate;
        this.access = access;
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
        log.info("Solving...");
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
        if(result.getName().equals("-")){
            Model model = delegate.resolveSolverModel(context);

            BinaryOperation operation = (BinaryOperation) result;
            int vector_1_length = applyOperand(operation.getOperand_1(),operation,context,intros,model);
            int vector_2_length = applyOperand(operation.getOperand_2(),operation,context,intros,model);


            if(vector_1_length>1&&vector_2_length>1){
                throw new IllegalArgumentException("Substraction cannot be applied with both operand's vectors' length > 1 ");
            }else if(vector_1_length>1&&vector_2_length>1){
                throw new IllegalArgumentException("Binary operation requires two parameters");
            } else if (vector_1_length==vector_2_length){
                throw new IllegalArgumentException("Unsupported scenario");

            } else{
                int difference_vector_length = vector_1_length > vector_2_length ? vector_1_length : vector_2_length;
                IntVar[] difference = new IntVar[difference_vector_length];
                IntVar singleVariable= (IntVar) (vector_1_length > vector_2_length ? operation.getOperandVariable_2() : operation.getOperandVariable_1());
                IntVar[] multipleVariable= (IntVar[]) (vector_1_length > vector_2_length ? operation.getOperandVariable_1() : operation.getOperandVariable_2());
                for (int j = 0; j < difference_vector_length; j++) {

                    difference[j]=singleVariable.sub(multipleVariable[j]).abs().intVar();

                }

                IntVar foreignKeyAssignation = model.intVar(operation.getTargetField().getFieldId()+"_fka", 1, difference_vector_length, false);

                List<VariableDescriptor> variableDescriptors = context.getStateValue().getSolutionVariablesValues();



                Optional<VariableDescriptor> bookingDistance = variableDescriptors.stream().filter(v->v.getField()==operation.getTargetField()).findAny();

                if(bookingDistance.isPresent()){
                    model.element((IntVar) ((ChocoVariableDescriptorImpl)bookingDistance.get()).getVariable(), difference, foreignKeyAssignation, 1).post();
                }else{
                    throw new IllegalStateException("No variable modeled for field "+operation.getTargetField().getFieldId());
                }


            }






        }


    }

    private int applyOperand(Object operand, BinaryOperation operation, ApplicationContext context, Instrospection intros, Model model) {

        if(operand instanceof CatalogOperand){
            return modelCatalogOperand(operation,(CatalogOperand)operand,context,intros,model);
        }else{
            IntVar bookingLocation = model.intVar((Integer) operand);
            if(operation.getOperand_1()==null){
                operation.setOperand_1(bookingLocation);
            }else{
                operation.setOperand_2(bookingLocation);
            }

            return 1;
        }
    }

    private int modelCatalogOperand(BinaryOperation operation, CatalogOperand catalogRequest, ApplicationContext context, Instrospection intros, Model model) {

        RuntimeContext runtime = context.getRuntimeContext();

        List<CatalogEntry> results = null;
        try {
            results = runtime.getServiceBus().fireEvent(catalogRequest.getRequest(), runtime, null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrive foreign entries",e);
        }

        if(results==null){
            log.info("No results to provide foreign key");
        }else {
            List<Long> values = results.stream().map(entry -> {
                try {
                    return access.getPropertyValue(catalogRequest.getTargetField(), entry, null, intros);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException("Unable to read " + catalogRequest.getTargetField().getFieldId() + " from foreign entry", e);
                }
            }).map(value -> (Long) value).collect(Collectors.toList());


            int[] vector = new int[values.size()];

            for (int i = 0; i < values.size(); i++) {
                vector[i] = values.get(i).intValue();
            }

            IntVar[] driverLocations = model.intVarArray(catalogRequest.getRequest().getCatalog() + "_" + catalogRequest.getTargetField().getFieldId(), vector.length, vector);
            if(operation.getOperand_1()==null){
                operation.setOperand_1(driverLocations);
            }else{
                operation.setOperand_2(driverLocations);
            }


            return vector.length;
        }
        return 0;
    }


}
