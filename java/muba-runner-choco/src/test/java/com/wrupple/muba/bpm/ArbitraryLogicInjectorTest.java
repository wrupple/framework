package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.IntegralTest;
import com.wrupple.muba.bpm.domain.ApplicationContext;
import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.bpm.domain.impl.ApplicationStateImpl;
import com.wrupple.muba.bpm.domain.impl.TaskImpl;
import com.wrupple.muba.bpm.domain.impl.WorkflowImpl;
import com.wrupple.muba.bpm.server.service.ChocoPlugin;
import com.wrupple.muba.bpm.server.service.Solver;
import com.wrupple.muba.bpm.server.service.impl.ChocoInterpret;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.bpm.domain.EquationSystemSolution;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import org.apache.commons.chain.Context;
import org.junit.Test;

import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;

/*
 * GreatestAnomalyRangePicker
 * AdjustErrorByDriverDistance
 */
public class ArbitraryLogicInjectorTest extends IntegralTest {
    private static final String CONSTRAINT = "constraint";
    static class GreeterInterpret implements NaturalLanguageInterpret{

        @Override
        public void run(ListIterator<String> sentence, Context c) throws Exception {
            ApplicationContext context = (ApplicationContext) c;
            EquationSystemSolution solution = (EquationSystemSolution) context.getStateValue().getEntryValue();
            solution.setSolverWitness("hey there!");
        }
    }


/*
 * GreatestAnomalyRangePicker
 * AdjustErrorByDriverDistance
 */

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

        ChocoInterpret constraintInterpret = injector.getInstance(ChocoInterpret.class);
        wrupple.registerInterpret(CONSTRAINT,constraintInterpret);
        GreeterInterpret greeter = injector.getInstance(GreeterInterpret.class);
        wrupple.registerInterpret("say hello",greeter);

        ChocoPlugin plugin = injector.getInstance(ChocoPlugin.class);
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


        List results = wrupple.fireEvent(state, session, null);
        EquationSystemSolution solution = (EquationSystemSolution) results.get(0);
        assertTrue(solution.getX()==2);

        assertTrue(solution.getY()==2);
        assertTrue("custom phrase did not run :(",solution.getSolverWitness()!=null);

    }

    private TaskImpl createProblem( SessionContext session ) throws Exception {
        CatalogActionRequestImpl catalogRequest;
        TaskImpl problem = new TaskImpl();
        problem.setDistinguishedName("equation system");
        problem.setName("equation system");
        problem.setCatalog(EquationSystemSolution.CATALOG);
        problem.setName(CatalogActionRequest.CREATE_ACTION);
        problem.setSentence(
                Arrays.asList(
                        // x * y = 4
                        CONSTRAINT,"times","ctx:x","ctx:y","int:4",
                        // x + y < 5
                        CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")",
                        "say hello"
                )
        );

        catalogRequest = new CatalogActionRequestImpl();
        catalogRequest.setEntryValue(problem);
        catalogRequest.setCatalog(Task.CATALOG);
        catalogRequest.setName(CatalogActionRequest.CREATE_ACTION);

        problem = (TaskImpl) ((List)wrupple.fireEvent(catalogRequest,session,null)).get(0);
        return problem;
    }

    private void defineConstrainedSolution(CatalogDescriptorBuilder builder, SessionContext session) throws Exception {
        CatalogDescriptorImpl solutionContract = (CatalogDescriptorImpl) builder.fromClass(EquationSystemSolution.class, EquationSystemSolution.CATALOG,
                "Equation System Solution", 0,  injector.getInstance(Key.get(CatalogDescriptor.class, Names.named(ContentNode.CATALOG_TIMELINE))));

        CatalogActionRequestImpl catalogRequest = new CatalogActionRequestImpl();
        catalogRequest.setEntryValue(solutionContract);
        catalogRequest.setName( CatalogActionRequest.CREATE_ACTION);
        catalogRequest.setCatalog(CatalogDescriptor.CATALOG_ID);
        wrupple.fireEvent(catalogRequest,session,null);
    }

}