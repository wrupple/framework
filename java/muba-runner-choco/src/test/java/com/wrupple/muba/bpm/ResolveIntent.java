package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.IntegralTest;
import com.wrupple.muba.bpm.domain.Task;
import com.wrupple.muba.bpm.domain.impl.TaskImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.bpm.domain.EquationSystemSolution;
import com.wrupple.muba.bpm.domain.SolverServiceManifest;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogDescriptorImpl;
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
        CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);

        // expectations

        replayAll();

        log.info("[-Register EquationSystemSolution catalog type-]");

        //FIXME stack overflow when no parent is specified, ok when consolidated?
        CatalogDescriptorImpl solutionContract = (CatalogDescriptorImpl) builder.fromClass(EquationSystemSolution.class, EquationSystemSolution.CATALOG,
                "Equation System Solution", 0,  injector.getInstance(Key.get(CatalogDescriptor.class, Names.named(ContentNode.CATALOG_TIMELINE))));
        CatalogActionRequestImpl catalogRequest = new CatalogActionRequestImpl();
        catalogRequest.setEntryValue(solutionContract);

        runtimeContext.setServiceContract(catalogRequest);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);

        runtimeContext.process();

        CatalogActionContext catalogContext = runtimeContext.getServiceContext();

        solutionContract = catalogContext.getEntryResult();

        runtimeContext.reset();
        log.info("[-create a task with problem constraints-]");
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

        runtimeContext.setServiceContract(catalogRequest);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD, Task.CATALOG, CatalogActionRequest.CREATE_ACTION);

        runtimeContext.process();
        catalogContext = runtimeContext.getServiceContext();

        problem = catalogContext.getEntryResult();

        runtimeContext.reset();
        log.info("[-post a solver request to the runner engine-]");
        runtimeContext.setServiceContract(problem);
        //TODO maybe CONSTRAINT is a child of solver
        runtimeContext.setSentence(SolverServiceManifest.SERVICE_NAME/*, FIXME allow constrains to be posted in service request sentence
                        // x + y < 5
                        Task.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")"*/);

        runtimeContext.process();

        EquationSystemSolution solution = runtimeContext.getConvertedResult();

        assertTrue(solution.getX()==2);

        assertTrue(solution.getY()==2);

    }

}