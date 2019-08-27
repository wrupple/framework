package com.wrupple.muba.worker;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.impl.CatalogCreateRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.event.domain.Intent;
import com.wrupple.muba.worker.domain.Driver;
import com.wrupple.muba.worker.domain.Statistics;
import com.wrupple.muba.worker.domain.impl.ApplicationImpl;
import com.wrupple.muba.worker.domain.impl.TaskImpl;
import com.wrupple.muba.worker.domain.impl.WorkerStateImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;


public class SubmitToApplicationTest extends BPMTest {
    private ApplicationImpl createStatisticsWorkflow;


    @Test(expected = IllegalStateException.class)
    public void explicitOutputPlace() throws Exception {
        createApplication();
        //createStatisticsWorkflow

        ApplicationState applicationState = acquireContext(createStatisticsWorkflow, session);

        postSolutionToApplication(applicationState);


        // when thread returns statistics should be updated (assertion)

        Statistics statistics = (Statistics) applicationState.getEntryValue();


        assertTrue("statistics dont exist", statistics != null);
        assertTrue("statistics not created", statistics.getId() != null);

        //if state changed, next problem should be unsolvable and throw an exception
        applicationState.setWorkerStateValue(new WorkerStateImpl());
        List results = wrupple.fireEvent(applicationState, session, null);
/*    if problem where solvable
         statistics= (Statistics) results.get(0);


        assertTrue("statistics not updated",statistics.getCount()!=null);
        assertTrue("statistics not updated",statistics.getCount().longValue()>0);*/


    }

     ApplicationState acquireContext(Application initialState, SessionContext thread) throws Exception {
        ApplicationState newState = injector.getInstance(ApplicationState.class);
        newState.setApplicationValue(initialState);

        assertTrue("An application is not bound",newState.getApplicationValue()!=null);
        CatalogCreateRequestImpl createRequest = new CatalogCreateRequestImpl(newState, ApplicationState.CATALOG);

         newState=  wrupple.fireEvent(createRequest, thread, null);

         assertTrue("An application is not bound",newState.getApplicationValue()!=null);
         return newState;

     }

    private void postSolutionToApplication(ApplicationState applicationState) throws Exception {
        Statistics statistics = new Statistics();
        statistics.setCatalog(Driver.CATALOG);

        Intent intent = injector.getInstance(Intent.class);
        applicationState.setEntryValue(statistics);
        intent.setStateValue(applicationState);
        intent.setDomain(CatalogEntry.PUBLIC_ID);
        //fire business intent to commit that last action, which should result in application state pointing to the next activity (count)
        //update is fired and handler starts count workflow, as event bus is synchrounous
        wrupple.fireEvent(intent, session, null);
    }


    private void createApplication() throws Exception {


        CatalogDescriptorBuilder builder = injector.getInstance(CatalogDescriptorBuilder.class);

        log.info("[-Register Statistics catalog type-]");

        defineSolutionTemplate(builder);


        createWorkflow();


        super.createMockDrivers();
    }

    private void createWorkflow() throws Exception {
        TaskImpl task = new TaskImpl();
        task.setCatalog(Statistics.CATALOG);
        task.setName(DataContract.WRITE_ACTION);
        //TODO SUBQUERY solver service appends, resolving variables if necesary, excecutes sql and attempts to resolve fields from result set
        task.setSentence(Arrays.asList(
                "SUBQUERY",
                "SELECT",
                "'${task.catalog}' AS " + HasCatalogId.CATALOG_FIELD,
                "COUNT(*) AS " + Statistics.COUNT_FIELD,
                "FROM", "${task.catalog}"));
        /*
         problem.setSentence(
                Arrays.asList(
                        // x * y = 4
                        Task.CONSTRAINT,"times","ctx:x","ctx:y","int:4",
                        // x + y < 5
                        Task.CONSTRAINT,"arithm","(","ctx:x", "+", "ctx:y", ">", "int:5",")"
                )
        );
         */

        ApplicationImpl count = new ApplicationImpl();
        count.setName("Count");
        count.setProcessValues(Arrays.asList(task));

        task = new TaskImpl();
        task.setCatalog(Statistics.CATALOG);
        task.setName(DataContract.CREATE_ACTION);

        ApplicationImpl workflow = new ApplicationImpl();
        workflow.setName("Create");
        workflow.setProcessValues(Arrays.asList(task));
        //////////////////////////////////////////////////////////////////
        //                  TEST (configuration state) SUBJECT          //
        //////////////////////////////////////////////////////////////////
        workflow.setExplicitSuccessorValue(count);
        CatalogCreateRequestImpl catalogActionRequest = new CatalogCreateRequestImpl(workflow, Application.CATALOG);

        workflow =wrupple.fireEvent(catalogActionRequest, session, null);

        assertTrue("workflow must have an explicit successor",workflow.getExplicitSuccessorValue()!=null);

        this.createStatisticsWorkflow = workflow;
    }

    private void defineSolutionTemplate(CatalogDescriptorBuilder builder) throws Exception {
        CatalogDescriptorImpl solutionContract = (CatalogDescriptorImpl) builder.fromClass(Statistics.class, Statistics.CATALOG,
                "Table Statistics", injector.getInstance(Key.get(CatalogDescriptor.class, Names.named(ContentNode.CATALOG_TIMELINE))));
        solutionContract.setConsolidated(true);

        CatalogActionRequestImpl catalogActionRequest = new CatalogActionRequestImpl();

        catalogActionRequest.setEntryValue(solutionContract);


        catalogActionRequest.setEntryValue(solutionContract);
        catalogActionRequest.setName(DataContract.CREATE_ACTION);
        catalogActionRequest.setFollowReferences(true);
        catalogActionRequest.setCatalog(CatalogDescriptor.CATALOG_ID);
        wrupple.fireEvent(catalogActionRequest, session, null);
    }

}
