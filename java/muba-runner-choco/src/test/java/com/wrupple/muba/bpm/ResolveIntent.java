package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.IntegralTest;
import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.bpm.domain.impl.TaskImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.bpm.domain.EquationSystemSolution;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import org.junit.Test;

import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;

/*
 * GreatestAnomalyRangePicker
 * AdjustErrorByDriverDistance
 */
public class ResolveIntent extends IntegralTest {



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

        // expectations

        replayAll();

        log.info("[-Register EquationSystemSolution catalog type-]");

        defineConstrainedSolution(builder, session);


        log.info("[-create a task with problem constraints-]");
        TaskImpl problem = createProblem(session);

        log.info("[-post a solver request to the runner engine-]");
        /*runtimeContext.setServiceContract(problem);
        runtimeContext.setSentence(SolverServiceManifest.SERVICE_NAME, FIXME allow constrains to be posted in service request sentence
                        // x + y < 5
                        Task.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")");

        runtimeContext.process();*/


        List results = wrupple.fireEvent(problem, session, null);
        EquationSystemSolution solution = (EquationSystemSolution) results.get(0);
        assertTrue(solution.getX()==2);

        assertTrue(solution.getY()==2);

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
                        Task.CONSTRAINT,"times","ctx:x","ctx:y","int:4",
                        // x + y < 5
                        Task.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")"
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