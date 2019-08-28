package com.wrupple.muba.worker;

import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.catalogs.*;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.impl.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.desktop.WorkerModule;
import com.wrupple.muba.desktop.client.service.LaunchWorkerHandler;
import com.wrupple.muba.desktop.client.service.WorkerRequestHandler;
import com.wrupple.muba.desktop.client.widgets.ProcessWindow;
import com.wrupple.muba.desktop.client.widgets.TaskContainer;
import com.wrupple.muba.desktop.domain.ContextSwitchHandler;
import com.wrupple.muba.desktop.domain.WorkerRequestManifest;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.domain.impl.ManagedObjectImpl;
import com.wrupple.muba.event.domain.reserved.HasStakeHolder;
import com.wrupple.muba.event.server.ExplicitIntentInterpret;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import com.wrupple.muba.event.server.service.impl.LambdaModule;
import com.wrupple.muba.event.server.service.impl.NaturalLanguageInterpretImpl;
import com.wrupple.muba.worker.domain.Driver;
import com.wrupple.muba.worker.domain.RiderBooking;
import com.wrupple.muba.worker.domain.impl.ApplicationImpl;
import com.wrupple.muba.worker.domain.impl.TaskImpl;
import com.wrupple.muba.worker.server.service.*;
import com.wrupple.muba.worker.server.service.impl.ArbitraryDesicion;
import com.wrupple.muba.worker.server.service.impl.CatalogRunnerImpl;
import com.wrupple.muba.worker.server.service.impl.ChocoInterpret;
import com.wrupple.muba.worker.shared.services.WorkerContainer;
import com.wrupple.vegetate.VegetateCatalogsModule;
import com.wrupple.vegetate.VegetateModule;
import com.wrupple.vegetate.server.service.VegetateCatalogPlugin;
import com.wrupple.vegetate.service.RemoteBroadcastHandler;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.wrupple.muba.event.domain.Constraint.EVALUATING_VARIABLE;
import static junit.framework.TestCase.assertTrue;

public abstract class WorkerTest extends EasyMockSupport {
    static final String HOME = "workerTest";

    WorkerContainer container;



    @Rule
    public EasyMockRule rule = new EasyMockRule(this);
    protected Injector injector;


    public WorkerTest() {

        List<Module> modules = Arrays.asList(
                (Module)new IntegralTestModule(),
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
                new VegetateCatalogsModule(),
                new CatalogModule(),
                new LambdaModule(),
                new VegetateModule(),
                new DispatcherModule(),
                new ApplicationModule()
        );

        /*TODO imitate explicitOutputPlace test method in SubmitToApplicationTest to create event handlers to launch a worker*/
        List handlers = Arrays.asList(
                ContextSwitchHandler.class,
                WorkerRequestHandler.class,
                LaunchWorkerHandler.class,
                RemoteBroadcastHandler.class

        );

        container= new WorkerContainer(modules, handlers);

        container.registerInterpret(Constraint.EVALUATING_VARIABLE, ChocoInterpret.class);
        container.registerInterpret(WorkerRequestManifest.NAME, ExplicitIntentInterpret.class);
        container.registerInterpret(NaturalLanguageInterpretImpl.ASSIGNATION, NaturalLanguageInterpret.class);

        container.registerRunner(ChocoRunner.class);
        container.registerRunner(CatalogRunner.class);

        injector = container.getInjector();



    }

    protected Logger log = LogManager.getLogger(WorkerTest.class);



    protected void createMockDrivers() throws Exception {
        CatalogDescriptorImpl solutionContract = (CatalogDescriptorImpl) container.getInstance(CatalogDescriptorBuilder.class).fromClass(Driver.class, Driver.CATALOG,
                "Driver", 0, null);
        solutionContract.setId(null);
        solutionContract.setConsolidated(true);
        CatalogCreateRequestImpl catalogActionRequest = new CatalogCreateRequestImpl(solutionContract, CatalogDescriptor.CATALOG_ID);
        catalogActionRequest.setFollowReferences(true);
        container.fireEvent(catalogActionRequest);
        Driver driver;


        long[] LOCATIONS = new long[]{20l, 7l, 2l, 20l, 30l};
        int NUM_DRIVERS = LOCATIONS.length;
        for (int i = 0; i < NUM_DRIVERS; i++) {
            driver = new Driver();
            //thus, best driver will have a location of 6, or 8 because 7 will not be available
            driver.setLocation(LOCATIONS[i]);

            driver.setAvailable(i % 2 == 0);

            catalogActionRequest = new CatalogCreateRequestImpl(driver, Driver.CATALOG);
            container.fireEvent(catalogActionRequest);

        }
    }


    static class IntegralTestModule extends AbstractModule {


        @Override
        protected void configure() {

            bind(String.class).annotatedWith(Names.named("worker.intialTitle")).toInstance("..::Desktop::..");
            bind(Long.class).annotatedWith(Names.named("com.wrupple.runner.choco")).toInstance(1l);
            bind(Long.class).annotatedWith(Names.named("com.wrupple.runner.catalog")).toInstance(2l);

            bind(VariableConsensus.class).to(ArbitraryDesicion.class);

            bind(CatalogRunner.class).to(CatalogRunnerImpl.class);
        }

        @Provides
        @Inject
        @Singleton
        @Named("catalog.plugins")
        public Object plugins(VegetateCatalogPlugin vegetate,SolverCatalogPlugin /* this is what makes it purr */ runner, BusinessPlugin bpm, SystemCatalogPlugin system) {
            CatalogPlugin[] plugins = new CatalogPlugin[]{system,bpm, runner,vegetate};
            return plugins;
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

    }



    void defineModel() throws Exception {

        CatalogDescriptorBuilder builder = container.getInstance(CatalogDescriptorBuilder.class);
        log.info("         [-register catalogs-]");



        CatalogDescriptor managed = builder.fromClass(ManagedObjectImpl.class, ManagedObjectImpl.class.getSimpleName(),
                ManagedObjectImpl.class.getSimpleName(), 2, container.getInjector().getInstance(Key.get(CatalogDescriptor.class, Names.named(ContentNode.CATALOG_TIMELINE))));

        FieldDescriptor stakeHolderField = managed.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
        assertTrue ("stakeHolder field missing",stakeHolderField != null);
        assertTrue ("stakeHolder is multiple",!stakeHolderField.isMultiple());
        assertTrue ("stakeHolder has the wrong data type",stakeHolderField.getDataType() == CatalogEntry.INTEGER_DATA_TYPE);
        assertTrue ("stakeHolder is not a Person key",Person.CATALOG.equals(stakeHolderField.getCatalog()));


        CatalogActionRequestImpl action = new CatalogCreateRequestImpl(managed,CatalogDescriptor.CATALOG_ID);

        managed = container.fireEvent(action);

        CatalogDescriptor bookingDescriptor = builder.fromClass(RiderBooking.class, RiderBooking.class.getSimpleName(),
                RiderBooking.class.getSimpleName(), 0, managed);

        stakeHolderField = bookingDescriptor.getFieldDescriptor(HasStakeHolder.STAKE_HOLDER_FIELD);
        assertTrue ("Booking inherit ManagedObject",stakeHolderField != null && !stakeHolderField.isMultiple()
                && stakeHolderField.getDataType() == CatalogEntry.INTEGER_DATA_TYPE
                && Person.CATALOG.equals(stakeHolderField.getCatalog()));
        FieldDescriptor driverDistanceField = bookingDescriptor.getFieldDescriptor("bookingDistance");
        assertTrue ("driver distance variable definition",driverDistanceField != null && !driverDistanceField.isMultiple()
                && driverDistanceField.getDataType() == CatalogEntry.INTEGER_DATA_TYPE
                && driverDistanceField.getSentence()!=null);

        bookingDescriptor.setConsolidated(true);

        action = new CatalogCreateRequestImpl(bookingDescriptor,CatalogDescriptor.CATALOG_ID);

        container.fireEvent(action);

        action = new CatalogCreateRequestImpl(builder.fromClass(Driver.class, Driver.class.getSimpleName(),
                "Driver", 1, null),CatalogDescriptor.CATALOG_ID);

        container.fireEvent(action);
    }

    ApplicationImpl createApplication(WorkerContainer container, String home) throws Exception {

        TaskImpl resolve  = new TaskImpl();
        resolve.setDistinguishedName("findDriver");
        resolve.setName(DataContract.WRITE_ACTION);
        resolve.setCatalog(RiderBooking.class.getSimpleName());
        resolve.setSentence(Arrays.asList(EVALUATING_VARIABLE,"setObjective","(","boolean:false","ctx:bookingDistance",")"));

        TaskImpl cargar  = new TaskImpl();
        cargar.setDistinguishedName("loadBooking");
        cargar.setName(DataContract.READ_ACTION);
        resolve.setKeepOutput(true);
        cargar.setCatalog(RiderBooking.class.getSimpleName());
        cargar.setGrammar(Arrays.asList(CatalogActionRequest.ENTRY_ID_FIELD));

        ApplicationImpl ilegal= new ApplicationImpl();
        ilegal.setDistinguishedName("peticionInvalida");

        ApplicationImpl trabajo = new ApplicationImpl();
        trabajo.setDistinguishedName("findDriver");
        trabajo.setKeepOutput(true);
        trabajo.setCatalog(RiderBooking.class.getSimpleName());
        trabajo.setProcessValues(Collections.unmodifiableList(Arrays.asList(resolve)));

        ApplicationImpl terminado = new ApplicationImpl();
        terminado.setDistinguishedName("terminado");

        ApplicationImpl error = new ApplicationImpl();
        terminado.setDistinguishedName("error");

        ApplicationImpl root  = new ApplicationImpl();
        root.setDistinguishedName(home);
        root.setProcessValues(Arrays.asList(cargar));

        root.setChildrenValues(Arrays.asList( (ServiceManifest)trabajo,ilegal));

        trabajo.setChildrenValues(Arrays.asList((ServiceManifest)terminado, error));

        CatalogCreateRequestImpl action  = new CatalogCreateRequestImpl(root, Application.CATALOG);
        action.setFollowReferences(true);

        return container.fireEvent(action);
    }


}
