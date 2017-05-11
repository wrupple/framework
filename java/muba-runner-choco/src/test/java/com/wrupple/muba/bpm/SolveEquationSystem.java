package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.io.OutputStream;
import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import javax.validation.Validator;

import com.wrupple.muba.ChocoRunnerTestModule;
import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bpm.domain.EquationSystemSolution;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.impl.ProcessTaskDescriptorImpl;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.EventSuscriptionChain;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import org.apache.commons.chain.Command;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.bootstrap.BootstrapModule;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;
import com.wrupple.muba.bootstrap.server.service.ValidationGroupProvider;
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


public class SolveEquationSystem extends MubaTest {
	/*
	 * mocks
	 */

    protected WriteOutput mockWriter;

    protected WriteAuditTrails mockLogger;

    protected CatalogPeer peerValue;

    protected EventSuscriptionChain chainMock;

    class RunnerTestModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
            // this makes JDBC the default storage unit
            bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
            bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
            bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
            bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
            bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);

            // mocks
            mockWriter = mock(WriteOutput.class);
            mockLogger = mock(WriteAuditTrails.class);
            peerValue = mock(CatalogPeer.class);
            chainMock = mock(EventSuscriptionChain.class);
            bind(WriteAuditTrails.class).toInstance(mockLogger);
            bind(WriteOutput.class).toInstance(mockWriter);
            bind(EventSuscriptionChain.class).toInstance(chainMock);
			/*
			 * COMMANDS
			 */

            bind(CatalogFileUploadTransaction.class).toInstance(mock(CatalogFileUploadTransaction.class));
            bind(CatalogFileUploadUrlHandlerTransaction.class)
                    .toInstance(mock(CatalogFileUploadUrlHandlerTransaction.class));

        }

        @Provides
        @Inject
        @Singleton
        public SessionContext sessionContext(@Named("host") String peer) {
            long stakeHolder = 1;
            Person stakeHolderValue = mock(Person.class);

            return new SessionContextImpl(stakeHolder, stakeHolderValue, peer, peerValue, CatalogEntry.PUBLIC_ID);
        }

        @Provides
        public UserTransaction localTransaction() {
            return mock(UserTransaction.class);
        }

        @Provides
        public Trash trash() {
            return mock(Trash.class);
        }

        @Provides
        public CatalogDeserializationService catalogDeserializationService() {
            return mock(CatalogDeserializationService.class);
        }

    }

    public SolveEquationSystem() {
        init(new RunnerTestModule(), new ChocoRunnerTestModule(), new SingleUserModule(),new TaskRunnerModule(),new HSQLDBModule(), new JDBCModule(),
                new ValidationModule(), new CatalogModule(), new BootstrapModule());
    }

    @Override
    protected void registerServices(Validator v, ValidationGroupProvider g, ApplicationContext switchs) {
        CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
        switchs.registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class));
        switchs.registerContractInterpret(catalogServiceManifest, injector.getInstance(CatalogRequestInterpret.class));
    }


    @Before
    public void setUp() throws Exception {
        expect(mockWriter.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(chainMock.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(mockLogger.execute(anyObject(CatalogActionContext.class))).andStubReturn(Command.CONTINUE_PROCESSING);
        expect(peerValue.getSubscriptionStatus()).andStubReturn(CatalogPeer.STATUS_ONLINE);

        excecutionContext = injector.getInstance(ExcecutionContext.class);
        log.trace("NEW TEST EXCECUTION CONTEXT READY");
    }


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
    public void engineTest() throws Exception {
        CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);


        // expectations

        //replayAll();
        log.trace("[-Register EquationSystemSolution catalog type-]");


        CatalogDescriptor solutionContract = builder.fromClass(EquationSystemSolution.class, EquationSystemSolution.CATALOG,
                "Equation System Solution", 0, null);

        CatalogActionRequestImpl action = new CatalogActionRequestImpl();
        action.setEntryValue(solutionContract);

        excecutionContext.setServiceContract(action);
        excecutionContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
        // locale is set in catalog
        excecutionContext.process();

        CatalogActionContext catalogContext = excecutionContext.getServiceContext();

        solutionContract = catalogContext.getEntryResult();

        log.trace("[-create a task with problem constraints-]");
        ProcessTaskDescriptorImpl problem = new ProcessTaskDescriptorImpl();
        problem.setDistinguishedName("my first problem");
        problem.setName("my first problem");
        problem.setCatalog(EquationSystemSolution.CATALOG);
        problem.setTransactionType(CatalogActionRequest.CREATE_ACTION);
        problem.setSentence(
                Arrays.asList(
                // x + y < 5
                ProcessTaskDescriptor.CONSTRAINT,"arithm","x", "+", "y", "<", "5",
                // x * y = 4
                ProcessTaskDescriptor.CONSTRAINT,"times","x","y","4")
        );

        action = new CatalogActionRequestImpl();
        action.setEntryValue(problem);

        excecutionContext.setServiceContract(action);
        excecutionContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_TOKEN,
                CatalogActionRequest.LOCALE_FIELD, ProcessTaskDescriptor.CATALOG, CatalogActionRequest.CREATE_ACTION);
        // locale is set in catalog
        excecutionContext.process();

        log.trace("[-post a solver request to the runner engine-]");
        //TODO

        // 1. Create a Model
        Model model = new Model("my first problem");
        // 2. Create variables
        IntVar x = model.intVar("x", 0, 5); // x in [0,5]
        IntVar y = model.intVar("y", new int[]{2, 3, 8}); // y in {2, 3, 8}
// 3. Post constraints
        model.arithm(x, "+", y, "<", 5).post(); // x + y < 5
        model.times(x,y,4).post(); // x * y = 4

// 4. Solve the problem
        model.getSolver().solve();
// 5. Print the solution
        System.out.println(x); // Prints X = 2
        System.out.println(y); // Prints Y = 2

    }

}