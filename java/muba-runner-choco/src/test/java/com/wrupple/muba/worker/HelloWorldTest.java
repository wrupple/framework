package com.wrupple.muba.worker;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.IntegralTest;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.domain.EquationSystemSolution;
import com.wrupple.muba.event.domain.VariableDescriptor;
import com.wrupple.muba.worker.domain.impl.ApplicationStateImpl;
import com.wrupple.muba.worker.domain.impl.WorkerStateImpl;
import com.wrupple.muba.worker.domain.impl.TaskImpl;
import com.wrupple.muba.worker.server.service.*;
import com.wrupple.muba.worker.server.service.impl.ChocoInterpret;
import org.apache.commons.chain.Context;
import org.junit.Test;

import java.util.Arrays;
import java.util.ListIterator;

import static com.wrupple.muba.worker.domain.EquationSystemSolution.WITNESS_FIELD;
import static org.junit.Assert.assertTrue;

/*
 * GreatestAnomalyRangePicker
 * AdjustErrorByDriverDistance
 */
public class HelloWorldTest extends IntegralTest {
    private static final String GREETING_SUBJECT = "world!";
    /*
     * GreatestAnomalyRangePicker
     * AdjustErrorByDriverDistance
     */
    private static final String CONSTRAINT = "constraint";

    private static final Long GREETER_RUNNER = 1L;

    /**
     * <ol>
     * <li>create math problem catalog (with getInheritance)</li>
     * <li></li>
     * <li></li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void equationSolverTest() throws Exception {
        SessionContext session = injector.getInstance(Key.get(SessionContext.class, Names.named(SessionContext.SYSTEM)));

        CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);

        Greeter greetingplugin = injector.getInstance(Greeter.class);
        injector.getInstance(Solver.class).register(greetingplugin);
        GreeterInterpret greeter = injector.getInstance(GreeterInterpret.class);
        wrupple.registerInterpret("greet", greeter);

        ChocoInterpret constraintInterpret = injector.getInstance(ChocoInterpret.class);
        wrupple.registerInterpret(CONSTRAINT, constraintInterpret);
        ChocoRunner plugin = injector.getInstance(ChocoRunner.class);
        injector.getInstance(Solver.class).register(plugin);


        // expectations

        replayAll();

        log.info("[-Register EquationSystemSolution catalog type-]");

        defineConstrainedSolution(builder, session);


        log.info("[-create a task with problem constraints-]");
        TaskImpl problem = createProblem(session);
        ApplicationStateImpl state = new ApplicationStateImpl();

        state.setTaskDescriptorValue(problem);
        state.setDomain(session.getSessionValue().getDomain());
        log.info("[-post a solver request to the runner engine-]");
        /*runtimeContext.setServiceContract(problem);
        runtimeContext.setSentence(SolverServiceManifest.SERVICE_NAME, FIXME allow constrains to be posted in service request sentence
                        // x + y < 5
                        Task.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")");

        runtimeContext.process();*/
        state.setWorkerStateValue(new WorkerStateImpl());
        EquationSystemSolution solution =  wrupple.fireEvent(state, session, null);
        assertTrue(solution.getX() == 2);

        assertTrue(solution.getY() == 2);
        assertTrue("custom solver not used :(", solution.getSolverWitness() != null);
        assertTrue("custom phrase did not resolve :(", solution.getSolverWitness().contains(GREETING_SUBJECT));

        log.info(solution.getSolverWitness());

    }

    private TaskImpl createProblem(SessionContext session) throws Exception {
        CatalogActionRequestImpl catalogRequest;
        TaskImpl problem = new TaskImpl();
        problem.setDistinguishedName("equation system");
        problem.setName("equation system");
        problem.setCatalog(EquationSystemSolution.CATALOG);
        problem.setName(CatalogActionRequest.CREATE_ACTION);
        problem.setSentence(
                Arrays.asList(
                        // x * y = 4
                        CONSTRAINT, "times", "ctx:x", "ctx:y", "int:4",
                        // x + y < 5
                        CONSTRAINT, "arithm", "(", "ctx:x", "+", "ctx:y", ">", "int:5", ")",
                        "greet", GREETING_SUBJECT
                )
        );

        catalogRequest = new CatalogActionRequestImpl();
        catalogRequest.setEntryValue(problem);
        catalogRequest.setCatalog(Task.CATALOG);
        catalogRequest.setName(CatalogActionRequest.CREATE_ACTION);

        problem = wrupple.fireEvent(catalogRequest, session, null);
        return problem;
    }

    private void defineConstrainedSolution(CatalogDescriptorBuilder builder, SessionContext session) throws Exception {
        CatalogDescriptorImpl solutionContract = (CatalogDescriptorImpl) builder.fromClass(EquationSystemSolution.class, EquationSystemSolution.CATALOG,
                "Equation System Solution", 0, injector.getInstance(Key.get(CatalogDescriptor.class, Names.named(ContentNode.CATALOG_TIMELINE))));

        CatalogActionRequestImpl catalogRequest = new CatalogActionRequestImpl();
        catalogRequest.setEntryValue(solutionContract);
        catalogRequest.setName(CatalogActionRequest.CREATE_ACTION);
        catalogRequest.setCatalog(CatalogDescriptor.CATALOG_ID);
        wrupple.fireEvent(catalogRequest, session, null);
    }

    static class Greeter implements Runner {

        @Override
        public boolean canHandle(FieldDescriptor field, ApplicationContext context) {
            boolean isWitnessField = field.getFieldId().equals(WITNESS_FIELD);
            return isWitnessField;
        }

        @Override
        public VariableEligibility handleAsVariable(final FieldDescriptor field, final ApplicationContext context) {
            return new VariableEligibility() {
                @Override
                public VariableDescriptor createVariable() {
                    return new NameVariable(field);
                }
            };
        }

        @Override
        public boolean solve(ApplicationContext context, StateTransition<ApplicationContext> callback) {
            try {
                callback.execute(context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }


    }

    static class GreeterInterpret implements NaturalLanguageInterpret {

        @Override
        public void resolve(ListIterator<String> sentence, Context c, String interpretGivenName) throws Exception {
            ApplicationContext context = (ApplicationContext) c;
            ApplicationState state = context.getStateValue();
            (
                    //this interpret can only see Name Variables :(
                    (HelloWorldTest.NameVariable)
                            state.getSolutionVariablesValues().stream().
                                    filter(
                                            v -> v.getField().getFieldId().equals(WITNESS_FIELD)
                                    ).findAny().get()).
                    //this is what makes it purr
                            setName(sentence.next());
        }
    }

    private static class NameVariable implements VariableDescriptor {
        private final FieldDescriptor field;
        String name;

        public NameVariable(FieldDescriptor field) {
            this.field = field;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            if (name != null) {
                this.name = name;
            }

        }

        @Override
        public FieldDescriptor getField() {
            return field;
        }

        @Override
        public <T> T getConvertedResult() {
            return (T) getResult();
        }

        @Override
        public Object getResult() {
            return "hello " + name + "!";
        }

        @Override
        public void setResult(Object o) {

        }

        @Override
        public Long getRunner() {
            return GREETER_RUNNER;
        }
    }
}