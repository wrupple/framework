package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import javax.validation.Validator;

import com.wrupple.muba.ChocoSolverTestModule;
import com.wrupple.muba.IntegralTest;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.bpm.domain.EquationSystemSolution;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.SolverServiceManifest;
import com.wrupple.muba.bpm.domain.impl.ProcessTaskDescriptorImpl;
import com.wrupple.muba.bpm.server.chain.SolverEngine;
import com.wrupple.muba.bpm.server.chain.command.ActivityRequestInterpret;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.CatalogDescriptorImpl;
import org.apache.commons.chain.Command;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;
import com.wrupple.muba.catalogs.CatalogModule;
import com.wrupple.muba.catalogs.HSQLDBModule;
import com.wrupple.muba.catalogs.JDBCModule;
import com.wrupple.muba.catalogs.SingleUserModule;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogFileUploadUrlHandlerTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.chain.command.DataCreationCommand;
import com.wrupple.muba.catalogs.server.chain.command.DataDeleteCommand;
import com.wrupple.muba.catalogs.server.chain.command.DataQueryCommand;
import com.wrupple.muba.catalogs.server.chain.command.DataReadCommand;
import com.wrupple.muba.catalogs.server.chain.command.DataWritingCommand;
import com.wrupple.muba.catalogs.server.chain.command.WriteAuditTrails;
import com.wrupple.muba.catalogs.server.chain.command.WriteOutput;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataCreationCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataDeleteCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataQueryCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataReadCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataWritingCommandImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;

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
     * <li>create math problem catalog (with inheritance)</li>
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
                "Equation System Solution", 0,  builder.fromClass(ContentNode.class, ContentNode.CATALOG,
                        ContentNode.class.getSimpleName(), -1l, null));
        CatalogActionRequestImpl catalogRequest = new CatalogActionRequestImpl();
        catalogRequest.setEntryValue(solutionContract);

        runtimeContext.setServiceContract(catalogRequest);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);

        runtimeContext.process();

        CatalogActionContext catalogContext = runtimeContext.getServiceContext();

        solutionContract = catalogContext.getEntryResult();

        runtimeContext.reset();
        log.info("[-create a task with problem constraints-]");
        ProcessTaskDescriptorImpl problem = new ProcessTaskDescriptorImpl();
        problem.setDistinguishedName("equation system");
        problem.setName("equation system");
        problem.setCatalog(EquationSystemSolution.CATALOG);
        problem.setTransactionType(CatalogActionRequest.CREATE_ACTION);
        problem.setSentence(
                Arrays.asList(
                        // x * y = 4
                        ProcessTaskDescriptor.CONSTRAINT,"times","ctx:x","ctx:y","int:4",
                        // x + y < 5
                        ProcessTaskDescriptor.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")"
                )
        );

        catalogRequest = new CatalogActionRequestImpl();
        catalogRequest.setEntryValue(problem);

        runtimeContext.setServiceContract(catalogRequest);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                CatalogActionRequest.LOCALE_FIELD, ProcessTaskDescriptor.CATALOG, CatalogActionRequest.CREATE_ACTION);

        runtimeContext.process();
        catalogContext = runtimeContext.getServiceContext();

        problem = catalogContext.getEntryResult();

        runtimeContext.reset();
        log.info("[-post a solver request to the runner engine-]");
        runtimeContext.setServiceContract(problem);
        //TODO maybe CONSTRAINT is a child of solver
        runtimeContext.setSentence(SolverServiceManifest.SERVICE_NAME/*, FIXME allow constrains to be posted in service request sentence
                        // x + y < 5
                        ProcessTaskDescriptor.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")"*/);

        runtimeContext.process();

        EquationSystemSolution solution = runtimeContext.getConvertedResult();

        assertTrue(solution.getX()==2);

        assertTrue(solution.getY()==2);

    }

}