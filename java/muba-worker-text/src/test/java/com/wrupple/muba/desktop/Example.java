package com.wrupple.muba.desktop;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.bpm.BusinessModule;
import com.wrupple.muba.bpm.ConstraintSolverModule;
import com.wrupple.muba.bpm.SolverModule;
import com.wrupple.muba.bpm.server.service.ChocoRunner;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.bpm.server.service.VariableConsensus;
import com.wrupple.muba.bpm.server.service.impl.ArbitraryDesicion;
import com.wrupple.muba.bpm.server.service.impl.ChocoInterpret;
import com.wrupple.muba.bpm.shared.services.ApplicationContainer;
import com.wrupple.muba.catalogs.*;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.desktop.client.chain.LaunchWorkerEngine;
import com.wrupple.muba.desktop.client.chain.command.LaunchApplicationState;
import com.wrupple.muba.desktop.client.chain.command.WorkerContainerLauncher;
import com.wrupple.muba.desktop.client.chain.command.ReadWorkerMetadata;
import com.wrupple.muba.desktop.client.chain.command.StartWorkerHeartBeat;
import com.wrupple.muba.desktop.client.chain.command.impl.*;
import com.wrupple.muba.desktop.client.service.impl.LaunchWorkerHandlerImpl;
import com.wrupple.muba.desktop.domain.LaunchWorker;
import com.wrupple.muba.desktop.domain.LaunchWorkerManifest;
import com.wrupple.muba.desktop.domain.impl.LaunchWorkerImpl;
import com.wrupple.muba.desktop.domain.impl.LaunchWorkerManifestImpl;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.Constraint;
import com.wrupple.muba.event.server.ExplicitIntentInterpret;
import com.wrupple.muba.event.server.chain.command.BindService;
import com.wrupple.muba.event.server.chain.command.Dispatch;
import com.wrupple.muba.event.server.chain.command.impl.BindServiceImpl;
import com.wrupple.muba.event.server.chain.command.impl.DispatchImpl;
import com.wrupple.muba.event.server.service.impl.LambdaModule;
import org.apache.commons.dbutils.QueryRunner;
import org.hsqldb.jdbc.JDBCDataSource;

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


    public static void main(String[] args) throws Exception {


        List<AbstractModule> modules = Arrays.asList(
                new ExampleModule(),
                new BusinessModule(),
                new ConstraintSolverModule(),
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
                LaunchWorkerHandlerImpl.class
        );

        ApplicationContainer container = new ApplicationContainer(modules, handlers);

        container.registerInterpret(":", ExplicitIntentInterpret.class);
        container.registerInterpret(Constraint.EVALUATING_VARIABLE, ChocoInterpret.class);
        container.registerRunner(ChocoRunner.class);

        container.fireEvent(new LaunchWorkerImpl());
    }

    static class ExampleModule extends AbstractModule {
        public ExampleModule() {
        }

        protected void configure() {

            bind(LaunchWorkerEngine.class).to(LaunchWorkerEngineImpl.class);
            bind(WorkerContainerLauncher.class).to(WorkerContainerLauncherImpl.class);
            bind(LaunchWorkerManifest.class).to(LaunchWorkerManifestImpl.class);

            bind(LaunchWorker.class).to(LaunchWorkerImpl.class);
            bind(LaunchApplicationState.class).to(LaunchApplicationStateImpl.class);
            bind(ReadWorkerMetadata.class).to(ReadWorkerMetadataImpl.class);
            bind(StartWorkerHeartBeat.class).to(StartWorkerHeartBeatImpl.class);


            this.bind(BindService.class).to(BindServiceImpl.class);
            this.bind(Dispatch.class).to(DispatchImpl.class);

            bind(VariableConsensus.class).to(ArbitraryDesicion.class);
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
        @com.google.inject.name.Named(LaunchWorker.CATALOG)
        public CatalogDescriptor session(CatalogDescriptorBuilder builder) {
            CatalogDescriptor r = builder.fromClass(LaunchWorkerImpl.class, LaunchWorker.CATALOG, "LaunchWorker", -2917198,
                    null);
            return r;
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
        public Object plugins(SolverCatalogPlugin /* this is what makes it purr */ runner, SystemCatalogPlugin system) {
            CatalogPlugin[] plugins = new CatalogPlugin[]{system, runner};
            return plugins;
        }

    }
}
