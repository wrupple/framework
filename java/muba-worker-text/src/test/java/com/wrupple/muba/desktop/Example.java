package com.wrupple.muba.desktop;


import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.catalogs.*;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.domain.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.desktop.client.chain.LaunchWorkerEngine;
import com.wrupple.muba.desktop.client.chain.command.LaunchWorkerInterpret;
import com.wrupple.muba.desktop.client.chain.command.SwitchWorkerContext;
import com.wrupple.muba.desktop.client.chain.command.ReadWorkerMetadata;
import com.wrupple.muba.desktop.client.chain.command.StartWorkerHeartBeat;
import com.wrupple.muba.desktop.client.chain.command.impl.*;
import com.wrupple.muba.desktop.client.service.WorkerRequestHandler;
import com.wrupple.muba.desktop.client.service.impl.LaunchWorkerHandlerImpl;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.client.widgets.TaskContainer;
import com.wrupple.muba.desktop.domain.ContextSwitchHandler;
import com.wrupple.muba.desktop.domain.WorkerRequestManifest;
import com.wrupple.muba.desktop.domain.LaunchWorkerManifest;
import com.wrupple.muba.desktop.domain.impl.LaunchWorkerManifestImpl;
import com.wrupple.muba.desktop.domain.impl.WorkerRequestImpl;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.domain.impl.ContentNodeImpl;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.server.ExplicitIntentInterpret;
import com.wrupple.muba.event.server.chain.command.BindService;
import com.wrupple.muba.event.server.chain.command.Dispatch;
import com.wrupple.muba.event.server.chain.command.impl.BindServiceImpl;
import com.wrupple.muba.event.server.chain.command.impl.DispatchImpl;
import com.wrupple.muba.event.server.service.impl.LambdaModule;
import com.wrupple.muba.worker.BusinessModule;
import com.wrupple.muba.worker.HumanSolverModule;
import com.wrupple.muba.worker.SolverModule;
import com.wrupple.muba.worker.domain.impl.ApplicationImpl;
import com.wrupple.muba.worker.domain.impl.TaskImpl;
import com.wrupple.muba.worker.domain.impl.WorkerStateImpl;
import com.wrupple.muba.worker.server.service.BusinessPlugin;
import com.wrupple.muba.worker.server.service.SolverCatalogPlugin;
import com.wrupple.muba.worker.server.service.VariableConsensus;
import com.wrupple.muba.worker.server.service.impl.ArbitraryDesicion;
import com.wrupple.muba.worker.shared.services.WorkerContainer;
import com.wrupple.muba.worker.shared.services.HumanRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.easymock.EasyMockRule;
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


public class Example {

    WorkerContainer container;

    public Example() throws Exception {

        List<AbstractModule> modules = Arrays.asList(
                new IntegralTestModule(),
                new WorkerModule(),
                new BusinessModule(),
                new HumanSolverModule(),
                new SolverModule(),
                new HSQLDBModule(),
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


        //container.registerInterpret(Constraint.EVALUATING_VARIABLE, ChocoInterpret.class);
        container.registerInterpret(WorkerRequestManifest.NAME, ExplicitIntentInterpret.class);

        //container.registerRunner(ChocoRunner.class);
        container.registerRunner(HumanRunner.class);

        System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        CatalogDescriptorBuilder builder = container.getInstance(CatalogDescriptorBuilder.class);

        log.trace("[-create tasks (problem definition)-]");

        Task show = new TaskImpl();
        show.setDistinguishedName("show");
        show.setName("Show Applications");
        show.setCatalog(Application.CATALOG);
        show.setName(Task.SELECT_COMMAND);
       /* problem.setSentence(
                Arrays.asList(
                        // x * y = 4
                        Task.CONSTRAINT,"times","ctx:x","ctx:y","int:4",
                        // x + y < 5
                        Task.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")"
                )
        );*/


        Task create = new TaskImpl();
        create.setDistinguishedName("create");
        create.setName("Create Application");
        show.setCatalog(Application.CATALOG);
        create.setName(CatalogActionRequest.CREATE_ACTION);


        log.trace("[-create application tree-]");
        ApplicationImpl root = new ApplicationImpl();
        root.setDistinguishedName(container.getInjector().getInstance(Key.get(String.class, Names.named("worker.defaultActivity"))));

        ApplicationImpl item = new ApplicationImpl();
        String testActivity = "createApp";
        item.setDistinguishedName(testActivity);
        item.setProcessValues(Arrays.asList(create,show));
        //this tells bpm to use this application to resolve bookings
        //item.setCatalog(bookingDescriptor.getDistinguishedName());
        //item.setOutputCatalog(bookingDescriptor.getDistinguishedName());
        item.setOutputField("app");

        root.setChildrenValues(Arrays.<ServiceManifest>asList(item));
        CatalogCreateRequestImpl action = new CatalogCreateRequestImpl(root, Application.CATALOG);
        action.setFollowReferences(true);

        container.fireEvent(action);

        root = (ApplicationImpl) action.getEntryValue();

        log.trace("[-use riderBooking id to launch container with previously created riderBooking -]");
        container.fireEvent(new WorkerRequestImpl(Arrays.asList(testActivity),container.getInjector().getInstance(Key.get(Long.class,Names.named("com.wrupple.runner.choco")))));

    }


public static void main(String... args) throws Exception {
        new Example();
}

    @Rule
    public EasyMockRule rule = new EasyMockRule(this);
    protected Logger log = LoggerFactory.getLogger(Example.class);


    static class IntegralTestModule extends AbstractModule {


        protected void configure() {


            this.bind(BindService.class).to(BindServiceImpl.class);
            this.bind(Dispatch.class).to(DispatchImpl.class);

            bind(VariableConsensus.class).to(ArbitraryDesicion.class);



            bind(String.class).annotatedWith(Names.named("worker.defaultActivity")).toInstance("home");
            bind(String.class).annotatedWith(Names.named("worker.charset")).toInstance("UTF-8");
            bind(String.class).annotatedWith(Names.named("worker.intialTitle")).toInstance("..::Desktop::..");
            bind(String.class).annotatedWith(Names.named("worker.importHandler.catalog")).toInstance("workerImportHandlers");
            //bind(Long.class).annotatedWith(Names.named("com.wrupple.runner.choco")).toInstance(2l);
            bind(String.class).annotatedWith(Names.named("host")).toInstance("localhost");
            bind(Boolean.class).annotatedWith(Names.named("event.parallel")).toInstance(false);
            bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
            bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);

            // this makes JDBC the default storage unit
            bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
            bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
            bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
            bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
            bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);
        }

        /*
         * CONFIGURATION
         */


        @Provides
        @com.google.inject.Singleton
        @com.google.inject.Inject
        @com.google.inject.name.Named(Person.CATALOG)
        public CatalogDescriptor activity(
                CatalogDescriptorBuilder builder) {
            CatalogDescriptor r = builder.fromClass(ContentNodeImpl.class, Person.CATALOG,  Person.CATALOG,
                    -13344556, null);

            return r;
        }

        @Provides
        public ProcessWindow queryRunner() {
            return new ProcessWindow() {
                @Override
                public TaskContainer getRootTaskPresenter() {
                    return null;
                }
            };
        }

        @Provides
        @Inject
        public QueryRunner queryRunner(DataSource ds) {
            return new QueryRunner(ds);
        }

        @Provides
        @Singleton
        @Inject
        public DataSource dataSource() throws SQLException {
            /*
             * Alternative
             * http://www.exampit.com/blog/javahunter/9-8-2016-Connection-
             * Pooling-using-Apache-common-DBCP-And-DBUtils
             */
            JDBCDataSource ds = new JDBCDataSource();
            ds.setLogWriter(new PrintWriter(System.err));
            ds.setPassword("");
            ds.setUser("SA");
            ds.setUrl("jdbc:hsqldb:mem:aname");
            return ds;
        }

        @Provides
        @Inject
        @Singleton
        @Named("catalog.plugins")
        public Object plugins(SolverCatalogPlugin /* this is what makes it purr */ runner, BusinessPlugin bpm, SystemCatalogPlugin system) {
            CatalogPlugin[] plugins = new CatalogPlugin[]{system,bpm, runner};
            return plugins;
        }



    }

}
