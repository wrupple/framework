package com.wrupple.muba.worker;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.catalogs.*;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.domain.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.desktop.NaiveWorkerConfiguration;
import com.wrupple.muba.desktop.WorkerModule;
import com.wrupple.muba.desktop.client.service.WorkerRequestHandler;
import com.wrupple.muba.desktop.client.service.impl.LaunchWorkerHandlerImpl;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.client.widgets.TaskContainer;
import com.wrupple.muba.desktop.domain.ContextSwitchHandler;
import com.wrupple.muba.desktop.domain.WorkerRequestManifest;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.domain.impl.ContentNodeImpl;
import com.wrupple.muba.event.server.ExplicitIntentInterpret;
import com.wrupple.muba.event.server.chain.command.BindService;
import com.wrupple.muba.event.server.chain.command.Dispatch;
import com.wrupple.muba.event.server.chain.command.impl.BindServiceImpl;
import com.wrupple.muba.event.server.chain.command.impl.DispatchImpl;
import com.wrupple.muba.event.server.service.impl.LambdaModule;
import com.wrupple.muba.worker.domain.Driver;
import com.wrupple.muba.worker.server.service.BusinessPlugin;
import com.wrupple.muba.worker.server.service.ChocoRunner;
import com.wrupple.muba.worker.server.service.SolverCatalogPlugin;
import com.wrupple.muba.worker.server.service.VariableConsensus;
import com.wrupple.muba.worker.server.service.impl.ArbitraryDesicion;
import com.wrupple.muba.worker.server.service.impl.ChocoInterpret;
import com.wrupple.muba.worker.shared.services.WorkerContainer;
import org.apache.commons.dbutils.QueryRunner;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public abstract class WorkerTest extends EasyMockSupport {

    WorkerContainer container;


    public WorkerTest() {

        List<Module> modules = Arrays.asList(
                (Module)new IntegralTestModule(),
                new NaiveWorkerConfiguration(),
                new WorkerModule(),
                new BusinessModule(),
                new ConstraintSolverModule(),
                new SolverModule(),
                new SimpleDatabaseModule(null),
                new HSQLDBModule(null),
                new JDBCModule(),
                new SQLModule(),
                new ValidationModule(),
                new SingleUserModule(),
                new CatalogModule(),
                new LambdaModule(),
                new DispatcherModule(),
                new ApplicationModule()
        );

        /*TODO imitate explicitOutputPlace test method in SubmitToApplicationTest to create event handlers to launch a worker*/
        List handlers = Arrays.asList(
                ContextSwitchHandler.class,
                WorkerRequestHandler.class,
                LaunchWorkerHandlerImpl.class
        );

        container= new WorkerContainer(modules, handlers);

        container.registerInterpret(Constraint.EVALUATING_VARIABLE, ChocoInterpret.class);
        container.registerInterpret(WorkerRequestManifest.NAME, ExplicitIntentInterpret.class);

        container.registerRunner(ChocoRunner.class);
        //container.registerRunner(HumanRunner.class);


    }



    static {
        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
    }

    @Rule
    public EasyMockRule rule = new EasyMockRule(this);
    protected Logger log = LoggerFactory.getLogger(WorkerTest.class);



    protected void createMockDrivers() throws Exception {
        CatalogDescriptorImpl solutionContract = (CatalogDescriptorImpl) container.getInstance(CatalogDescriptorBuilder.class).fromClass(Driver.class, Driver.CATALOG,
                "Driver", 0, null);
        solutionContract.setId(null);
        solutionContract.setConsolidated(true);
        CatalogCreateRequestImpl catalogActionRequest = new CatalogCreateRequestImpl(solutionContract, CatalogDescriptor.CATALOG_ID);
        catalogActionRequest.setFollowReferences(true);
        container.fireEvent(catalogActionRequest);
        Driver driver;
        for (long i = 0; i < 10; i++) {
            driver = new Driver();
            //thus, best driver will have a location of 6, or 8 because 7 will not be available
            driver.setLocation(i);
            driver.setAvailable(i % 2 == 0);

            catalogActionRequest = new CatalogCreateRequestImpl(driver, Driver.CATALOG);
            container.fireEvent(catalogActionRequest);

        }
    }


    static class IntegralTestModule extends AbstractModule {


        @Override
        protected void configure() {


            bind(String.class).annotatedWith(Names.named("worker.defaultActivity")).toInstance("home");
            bind(String.class).annotatedWith(Names.named("worker.intialTitle")).toInstance("..::Desktop::..");
            bind(Long.class).annotatedWith(Names.named("com.wrupple.runner.choco")).toInstance(1l);

            bind(VariableConsensus.class).to(ArbitraryDesicion.class);

        }



    }


}
