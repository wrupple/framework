package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertTrue;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.domain.impl.ProcessTaskDescriptorImpl;
import com.wrupple.muba.bpm.domain.impl.WorkRequestImpl;
import com.wrupple.muba.bpm.domain.impl.WorkflowImpl;
import com.wrupple.muba.bpm.server.chain.command.ExplicitOutputPlace;
import com.wrupple.muba.bpm.server.service.ProcessManager;
import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;

import com.wrupple.muba.catalogs.server.domain.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;


/**
 * FIXME Test ExplicitOutputPlaceImpl
 */
public class ExplicitWorkflowRequestTest extends BPMTest {
    private WorkflowImpl createStatisticsApplication;


    //FIXME this tests catching ApplicationUpdateEvent And firing a workflow (History change event?)

    private void createApplication() throws Exception {

        CatalogActionRequestImpl catalogActionRequest = new CatalogActionRequestImpl();

        CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);

        log.info("[-Register Statistics catalog type-]");

        //FIXME stack overflow when no parent is specified, ok when consolidated?
        CatalogDescriptorImpl solutionContract = (CatalogDescriptorImpl) builder.fromClass(Statistics.class, Statistics.CATALOG,
                "Table Statistics",  injector.getInstance(Key.get(CatalogDescriptor.class, Names.named(ContentNode.CATALOG_TIMELINE))));
        solutionContract.setConsolidated(true);
        catalogActionRequest.setEntryValue(solutionContract);


        catalogActionRequest.setEntryValue(solutionContract);
        catalogActionRequest.setName(DataEvent.CREATE_ACTION);
        catalogActionRequest.setFollowReferences(true);
        catalogActionRequest.setCatalog(CatalogDescriptor.CATALOG_ID);
        wrupple.fireEvent(catalogActionRequest,session,null);




        ProcessTaskDescriptorImpl task = new ProcessTaskDescriptorImpl();
        task.setCatalog(Statistics.CATALOG);
        task.setName(DataEvent.WRITE_ACTION);
        //TODO SUBQUERY solver service appends, resolving variables if necesary, excecutes sql and attempts to resolve fields from result set
        task.setSentence(Arrays.asList(
                "SUBQUERY",
                "SELECT",
                "'${task.catalog}' AS "+ HasCatalogId.CATALOG_FIELD,
                "COUNT(*) AS "+Statistics.COUNT_FIELD,
                "FROM","${task.catalog}"));
        /*
         problem.setSentence(
                Arrays.asList(
                        // x * y = 4
                        ProcessTaskDescriptor.CONSTRAINT,"times","ctx:x","ctx:y","int:4",
                        // x + y < 5
                        ProcessTaskDescriptor.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")"
                )
        );
         */

        WorkflowImpl count = new WorkflowImpl();
        count.setName("Count");
        count.setProcessValues( Arrays.asList(task));

        task = new ProcessTaskDescriptorImpl();
        task.setCatalog(Statistics.CATALOG);
        task.setName(DataEvent.CREATE_ACTION);

        WorkflowImpl application = new WorkflowImpl();
        application.setName("Create");
        application.setProcessValues( Arrays.asList(task));
        //////////////////////////////////////////////////////////////////
        //                  TEST (configuration state) SUBJECT          //
        //////////////////////////////////////////////////////////////////
        application.setExplicitSuccessorValue(count);
        catalogActionRequest = new CatalogCreateRequestImpl(application,Workflow.CATALOG);

        application=wrupple.fireEvent(catalogActionRequest,session,null);

        this.createStatisticsApplication =application;



        super.createMockDrivers();
    }

    /**
	 * Done -Triggers are serializable listeners, move trigger logit to an event (fire a workflow with one catalog task)
	 * Done - fire a catalog change event (PublishEventsImpl) ( install remote listeners on Event CHain (vegetate web hooks))
	 * ???? - listen event  and have the desktop fire the first task of a workflow and install a listener for the task submission(desktop)
	 **/
	//CREATE ApplicationState
	//INSTALL LISTENER HERE
	//Update ApplicationState
	//Expect (but don't install an instance of) ApplicationStateUpdatePlace to be fired
	/*
	 * DesktopEngineImpl DesktopBuilderContext DesktopRequestReader
	 * SearchEngineOptimizedDesktopWriterCommandImpl FormWriterImpl
	 * BrowserWriterImpl BusnessEvent ProcessControlNode
	 */




	@Test
	public void consumeJobsFromInbox() throws Exception {
        createApplication();
        ProcessManager bpm = injector.getInstance(ProcessManager.class);
        ApplicationState applicationState = injector.getInstance(ApplicationState.class);
        applicationState.setHandleValue(createStatisticsApplication);
        applicationState= bpm.acquireContext(applicationState,session);

        Statistics statistics=new Statistics();
        statistics.setCatalog(Driver.CATALOG);
        BusinessIntent intent =injector.getInstance(BusinessIntent.class);
        applicationState.setEntryValue(statistics);
        intent.setStateValue(applicationState);
        intent.setDomain( CatalogEntry.PUBLIC_ID);
        //fire business intent to commit that last action, which should result in application state pointing to the next activity (count)
        //update is fired and handler starts count workflow, as event bus is synchrounous
        wrupple.fireEvent(intent,session,null);


        // when thread returns statistics should be updated (assertion)

        statistics = (Statistics) applicationState.getEntryValue();

        assertTrue("statistics dont exist",statistics!=null);
        assertTrue("statistics not created",statistics.getId()!=null);
        ;
        assertTrue("statistics not updated",statistics.getCount()!=null);
        assertTrue("statistics not updated",statistics.getCount().longValue()>0);


	}


}
