package com.wrupple.muba.bpm;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertTrue;

import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.Booking;
import com.wrupple.muba.bpm.domain.ProcessTaskDescriptor;
import com.wrupple.muba.bpm.domain.impl.WorkflowImpl;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.event.EventBus;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import org.junit.Test;

import java.util.Arrays;


/**
 * FIXME Test ExplicitOutputPlaceImpl
 */
public class ExplicitWorkflowRequestTest extends BPMTest {



	//FIXME this tests catching ApplicationUpdateEvent And firing a workflow (History change event?)
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


	}


}
