package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.BinaryOperation;
import com.wrupple.muba.event.domain.impl.CatalogOperand;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.FieldSynthesizer;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.server.chain.command.DefineSolutionCriteria;
import com.wrupple.muba.worker.server.service.ChocoModelResolver;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import javax.inject.Inject;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * uses event bus sentence interpret to invoke solvers via unaware apis
 * <p>
 * Created by rarl on 11/05/17.
 */
public class DefineSolutionCriteriaImpl implements DefineSolutionCriteria {


    protected Logger log = LogManager.getLogger(DefineSolutionCriteriaImpl.class);
    private final FieldSynthesizer synthetizationDelegate;
    private final FieldAccessStrategy access;

    @Inject
    public DefineSolutionCriteriaImpl(FieldSynthesizer synthetizationDelegate, FieldAccessStrategy access) {
        this.synthetizationDelegate = synthetizationDelegate;
        this.access = access;
    }

    @Override
    public boolean execute(Context ctx) throws Exception {
        final ApplicationContext context = (ApplicationContext) ctx;
        Task request = context.getStateValue().getTaskDescriptorValue();
        CatalogEntry subject = context.getStateValue().getEntryValue();
        CatalogDescriptor descriptor = context.getStateValue().getCatalogValue();
        if(descriptor!=null){
            Instrospection intros=null;
            for (FieldDescriptor field : descriptor.getFieldsValues()) {
                if (field.getSentence() != null && !field.getSentence().isEmpty()) {
                    log.debug("posting solution constraints from field {} definition",field.getFieldId());
                    if(intros==null){
                        intros = access.newSession(subject);
                    }
                    RuntimeContext runtime = context.getRuntimeContext();
                    Object result = synthetizationDelegate.synthethizeFieldValue(field.getSentence().listIterator(), context, subject,descriptor, field, intros, runtime.getServiceBus());
                    if(result instanceof Operation){
                        intros = resolveOperation(runtime, (BinaryOperation) result);
                    }
                }
            }
        }
        ListIterator<String> activitySentence;
        if(request.getSentence()!=null){
            activitySentence= request.getSentence().listIterator();
//Arrays.asList(EVALUATING_VARIABLE,"setObjective","(","false","ctx:driverDistance",")")
            //model.setObjective(false/*ResolutionPolicy.MINIMIZE*/, bookingDistance);

            log.debug("posting solution constraints from task definition");
            processNextConstraint(activitySentence, context);
        }

        log.debug("posting solution constraints from excecution context");
        activitySentence = context.getRuntimeContext();
        processNextConstraint(activitySentence, context);

        return CONTINUE_PROCESSING;
    }


    private final ChocoModelResolver delegate;

    private void resolveOperation(ApplicationContext context ,RuntimeContext runtime, BinaryOperation result,Instrospection intros) throws Exception {

        BinaryOperation operation = result;

        CatalogOperand catalogRequest = (CatalogOperand) operation.getOperand_1();

        List<CatalogEntry> results = runtime.getServiceBus().fireEvent(catalogRequest.getRequest(),runtime,null);

        if(results==null){
            throw new NullPointerException("no results");
        }else{
            List<Integer> values = results.stream().map(entry-> {
                try {
                    return access.getPropertyValue(catalogRequest.getTargetField(),entry,null,intros);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }).map(value->(Integer)value).collect(Collectors.toList());
            Model model = delegate.resolveSolverModel(context);

            int[] LOCATIONS = new int[values.size()];
            int NUM_DRIVERS=LOCATIONS.length;

            for(int i = 0 ; i < values.size(); i++){
                LOCATIONS[i] =  values.get(i);
            }
            IntVar[] driverLocations = model.intVarArray("driverLocations",NUM_DRIVERS,LOCATIONS);

            Integer bookingLocationValue = (Integer) operation.getOperand_2();

            IntVar bookingLocation = model.intVar(bookingLocationValue);


            IntVar[] distances = new IntVar[NUM_DRIVERS];

            for (int j = 0; j < NUM_DRIVERS; j++) {
                if(operation.getName().equals("-")){
                    distances[j]=bookingLocation.sub(driverLocations[j]).abs().intVar();
                }else{
                    throw new IllegalArgumentException(operation.getName());
                }
            }

            IntVar foreignKeyAssignation = model.intVar("foreignKeyAssignation", 1, NUM_DRIVERS, false);

            IntVar bookingDistance = model.intVar("bookingDistance", 0, 100, true);

            model.element(bookingDistance, distances, foreignKeyAssignation, 1).post();

        }
    }

    private void processNextConstraint(ListIterator<String> sentence, ApplicationContext context) throws Exception {
        if (sentence.hasNext()) {
            String next = sentence.next();
            if (context.getRuntimeContext().getServiceBus().hasInterpret(next)) {
                NaturalLanguageInterpret interpret = context.getRuntimeContext().getServiceBus().getInterpret(next);
                log.info(" {} signals usage of {}", next, interpret);
                interpret.resolve(sentence, context, next);
                processNextConstraint(sentence, context);
            } else {
                log.debug(" {} does not seem to be an interpret DN", next);
                sentence.previous();
            }
        } else {
            log.debug("all tokens have been used");
        }
    }


}
