package com.wrupple.muba;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.*;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.ApplicationModule;
import com.wrupple.muba.event.DispatcherModule;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.server.chain.PublishEvents;
import com.wrupple.muba.event.server.chain.command.BroadcastInterpret;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.ValidationGroupProvider;
import com.wrupple.muba.event.server.service.impl.LambdaModule;
import org.junit.Before;

import javax.validation.Validator;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static com.wrupple.muba.event.domain.SessionContext.SYSTEM;
import static org.junit.Assert.assertTrue;

public class IntegralTest extends AbstractTest{

    /*
	 * mocks
	 */


    public IntegralTest() {
        init(new IntegralTestModule(), new JDBCHSQLTestModule(), new HSQLDBModule(null), new JDBCModule(), new SQLModule(),
                new ValidationModule(), new SingleUserModule(), new CatalogModule(), new LambdaModule(), new DispatcherModule(), new ApplicationModule());
    }

    class IntegralTestModule extends AbstractModule {

        @Override
        protected void configure() {
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


    }

    @Override
    protected void registerServices(Validator v, ValidationGroupProvider g, ServiceBus switchs) {
        CatalogServiceManifest catalogServiceManifest = injector.getInstance(CatalogServiceManifest.class);
        switchs.getIntentInterpret().registerService(catalogServiceManifest, injector.getInstance(CatalogEngine.class),injector.getInstance(CatalogRequestInterpret.class));

        CatalogActionFilterManifest preService = injector.getInstance(CatalogActionFilterManifest.class);
        switchs.getIntentInterpret().registerService(preService, injector.getInstance(CatalogActionFilterEngine.class),injector.getInstance(CatalogActionFilterInterpret.class));

        CatalogIntentListenerManifest listenerManifest = injector.getInstance(CatalogIntentListenerManifest.class);
        switchs.getIntentInterpret().registerService(listenerManifest, injector.getInstance(CatalogEventHandler.class),injector.getInstance(CatalogEventInterpret.class));


        BroadcastServiceManifest broadcastManifest = injector.getInstance(BroadcastServiceManifest.class);
        switchs.getIntentInterpret().registerService(broadcastManifest, injector.getInstance(PublishEvents.class),injector.getInstance(BroadcastInterpret.class));


    }
    protected CatalogDescriptor problemContract;

    @Before
    public void setUp() throws Exception {
        runtimeContext = new RuntimeContextImpl(injector.getInstance(ServiceBus.class), injector.getInstance(Key.get(SessionContext.class, Names.named(SYSTEM))));

        CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);
        log.info("[-create catalog-]");

        // expectations

        replayAll();

        problemContract = builder.fromClass(MathProblem.class, MathProblem.class.getSimpleName(),
                "Math Problem",  injector.getInstance(Key.get(CatalogDescriptor.class, Names.named(ContentNode.CATALOG_TIMELINE))));
        problemContract.setConsolidated(false);
        FieldDescriptor solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
        assertTrue( solutionFieldDescriptor!= null);
        assertTrue("does metadata describe problem as inherited?",problemContract.getParent()!=null);

        CatalogDescriptor argumentContract = builder.fromClass(
                Argument.class,
                Argument.class.getSimpleName(),
                "Argument",
                null);

        CatalogActionRequestImpl action = new CatalogActionRequestImpl();
        action.setEntryValue(argumentContract);
        action.setFollowReferences(true);
        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
        // locale is set in catalog
        runtimeContext.process();
        runtimeContext.reset();

        action = new CatalogActionRequestImpl();
        action.setEntryValue(problemContract);
        action.setFollowReferences(true);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
        runtimeContext.setServiceContract(action);
        runtimeContext.process();

        CatalogActionContext catalogContext = runtimeContext.getServiceContext();

        problemContract = catalogContext.getEntryResult();
        assertTrue(problemContract.getId() != null);
        assertTrue(problemContract.getDomain() != null);
        assertTrue(problemContract.getDomain().longValue() == CatalogEntry.PUBLIC_ID);
        assertTrue("does metadata describe problem as inherited?",problemContract.getParent()!=null);
        assertTrue("does metadata provide problem's parent type?",problemContract.getParentValue()!=null);

        assertTrue(problemContract.getDistinguishedName().equals(MathProblem.class.getSimpleName()));
        solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
        assertTrue( solutionFieldDescriptor!= null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues()!=null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues().size()==2);
        runtimeContext.reset();


        log.info("[-see changes in catalog list-]");


        runtimeContext.setServiceContract(null);
        runtimeContext.setSentence(
                CatalogServiceManifest.SERVICE_NAME,
                CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD,
                CatalogDescriptor.CATALOG_ID,
                CatalogActionRequest.READ_ACTION);
        runtimeContext.process();
        catalogContext = runtimeContext.getServiceContext();
        List<CatalogEntry> catalogList = catalogContext.getResults();
        assertTrue(catalogList != null);
        assertTrue(!catalogList.isEmpty());
        boolean contained = false;
        log.info("Looking for just created catalog {}={}",problemContract.getId(),problemContract.getName());
        for (CatalogEntry existingCatalog : catalogList) {
            log.info("Existing catalog {}={}",existingCatalog.getId(),existingCatalog.getName());
            if(existingCatalog.getId().equals(problemContract.getId())){
                contained = true ;
                break;
            }
        }

        assertTrue(contained);
        log.info("[-see registered catalog Descriptor-]");
        runtimeContext.reset();
        action = new CatalogActionRequestImpl();
        action.setFollowReferences(true);
        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(
                CatalogServiceManifest.SERVICE_NAME,
                CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD,
                CatalogDescriptor.CATALOG_ID,
                CatalogActionRequest.READ_ACTION,
                MathProblem.class.getSimpleName());
        runtimeContext.process();
        catalogContext = runtimeContext.getServiceContext();

        problemContract = catalogContext.getConvertedResult();
        log.info("[-verifying catalog graph integrity-]");
        assertTrue(problemContract.getId() != null);
        assertTrue(problemContract.getDistinguishedName().equals(MathProblem.class.getSimpleName()));
        solutionFieldDescriptor = problemContract.getFieldDescriptor("solution");
        assertTrue(solutionFieldDescriptor!= null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues()!=null);
        assertTrue(solutionFieldDescriptor.getConstraintsValues().size()==2);
        assertTrue("does metadata describe problem as inherited?",problemContract.getParentValue()!=null);
        assertTrue("does metadata include it's ancestry?",problemContract.getRootAncestor()!=null);
        assertTrue("does metadata describe problem as a timeline?",ContentNode.CATALOG_TIMELINE.equals(problemContract.getRootAncestor().getDistinguishedName()));
        runtimeContext.reset();
        log.debug("-create test catalogs-");

        argumentContract = builder.fromClass(
                Credit.class,
                Credit.CATALOG,
                "Credit",
                null);

        action = new CatalogActionRequestImpl();
        action.setEntryValue(argumentContract);
        action.setFollowReferences(true);
        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
        runtimeContext.process();
        runtimeContext.reset();
        argumentContract = builder.fromClass(
                Endorser.class,
                Endorser.CATALOG,
                "Endorser",
                null);

        action = new CatalogActionRequestImpl();
        action.setEntryValue(argumentContract);
        action.setFollowReferences(true);
        runtimeContext.setServiceContract(action);
        runtimeContext.setSentence(CatalogServiceManifest.SERVICE_NAME, CatalogDescriptor.DOMAIN_FIELD,
                CatalogActionRequest.LOCALE_FIELD, CatalogDescriptor.CATALOG_ID, CatalogActionRequest.CREATE_ACTION);
        runtimeContext.process();
        runtimeContext.reset();


    }


}
