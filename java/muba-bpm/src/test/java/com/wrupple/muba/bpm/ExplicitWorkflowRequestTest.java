package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertTrue;

import com.wrupple.muba.bpm.domain.*;
import com.wrupple.muba.bpm.domain.impl.ProcessTaskDescriptorImpl;
import com.wrupple.muba.bpm.domain.impl.WorkRequestImpl;
import com.wrupple.muba.bpm.domain.impl.WorkflowImpl;
import com.wrupple.muba.bpm.server.chain.command.ExplicitOutputPlace;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.EventBus;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.DataEvent;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


/**
 * FIXME Test ExplicitOutputPlaceImpl
 */
public class ExplicitWorkflowRequestTest extends BPMTest {


    //FIXME this tests catching ApplicationUpdateEvent And firing a workflow (History change event?)

    @Before
    public void createApplication() throws Exception {
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

        WorkflowImpl application = new WorkflowImpl();
        application.setName("Count");
        application.setProcessValues( Arrays.asList(task));

        CatalogActionRequestImpl catalogActionRequest = new CatalogActionRequestImpl();
        catalogActionRequest.setCatalog(Statistics.CATALOG);
        catalogActionRequest.setEntryValue(application);
        catalogActionRequest.setName(DataEvent.CREATE_ACTION);
        catalogActionRequest.setFollowReferences(true);
        wrupple.fireEvent(catalogActionRequest,session,null);

        task = new ProcessTaskDescriptorImpl();
        task.setCatalog(Statistics.CATALOG);
        task.setName(DataEvent.CREATE_ACTION);

        application = new WorkflowImpl();
        application.setName("Create");
        application.setProcessValues( Arrays.asList(task));
        application.setExit(ExplicitOutputPlace.COMMAND);

        catalogActionRequest.setEntryValue(application);

        application=wrupple.fireEvent(catalogActionRequest,session,null);



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
        Statistics statistics=new Statistics();
        statistics.setCatalog(Driver.CATALOG);
        CatalogActionRequestImpl catalogActionRequest = new CatalogActionRequestImpl();
        catalogActionRequest.setCatalog(Statistics.CATALOG);
        catalogActionRequest.setEntryValue(statistics);
        catalogActionRequest.setName(DataEvent.CREATE_ACTION);
        wrupple.fireEvent(catalogActionRequest,session,null);
        //TODO commit last (and only) create action from a first activity TEST output handler invocation of an explicit workflow


		WorkRequestImpl inboxNotification = new WorkRequestImpl();
		inboxNotification.setOutputCatalog(Statistics.CATALOG);
		inboxNotification.setCatalog(Statistics.CATALOG);
        inboxNotification.setEntry(statistics.getId());
        wrupple.fireEvent(inboxNotification,session,null);


        ApplicationState applicationState = inboxNotification.getStateValue();
        statistics = (Statistics) applicationState.getEntryValue();

        assertTrue("statistics not updated",statistics.getCount().longValue()>0);


	}


}
