package com.wrupple.muba.worker.server.service.impl;

import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.worker.server.service.ProcessManager;
import com.wrupple.muba.worker.server.service.Solver;

import javax.inject.Inject;
import javax.inject.Singleton;

/*SequentialProcessManger imports
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.ServiceBus;
import com.wrupple.muba.bpm.client.activity.ActivityProcess;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.bpm.client.services.impl.SimpleActivityTransition;
import com.wrupple.muba.bpm.domain.BPMPeer;
import ProcessManager;
import com.wrupple.muba.bpm.server.service.ProcessServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogPeer;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.desktop.client.activity.widgets.ProcessPresenter;
import com.wrupple.muba.desktop.client.activity.widgets.TaskPresenter;
import com.wrupple.muba.desktop.client.event.ContextSwitchEvent;
import com.wrupple.muba.desktop.client.event.ProcessSwitchEvent;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.ActivityPresenterMap;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.SerializationService;
import com.wrupple.muba.desktop.client.services.logic.ServiceBus;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.HumanActivityContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsTaskProcessRequest;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.desktop.domain.overlay.JsonVegetateResponse;
import com.wrupple.vegetate.client.services.ProcessVegetateChannel;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.client.services.impl.ProcessVegetateChannelImpl;
import com.wrupple.vegetate.domain.VegetatePeer;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.shared.services.PeerManager;
*/
@Singleton
public class ProcessManagerImpl implements ProcessManager {
    private final ServiceBus serviceBus;
    private final Solver solver;

/*
    private static final String PROCESS_USER_AREA_CLASS = "application-content-area";
    //Services
    private final ServiceBus serviceBus;
    private final DesktopManager desktopManager;
    private final ContentManagementSystem contentManager;
    private final StorageManager storageManager;
    Manager
    private final PeerManager peerManager;
    private final PlaceController placeController;
    private final ActivityPresenterMap presenterMap;
    Sequential Process
    private ServiceMap commandRegistry;
    private OutputHandlerMap outputHandlerRegistry;
    private DictionaryRegistry serviceLocator;
    private ProcessVegetateChannel bpm;
*/

    @Inject
    public ProcessManagerImpl(ServiceBus serviceBus, Solver solver) {
        this.serviceBus = serviceBus;
        this.solver = solver;
    }

    @Override
    public Solver getSolver() {
        return solver;
    }



    /*


        @Inject
        public SequentialProcessManagerImpl(OutputHandlerMap outputHandlerRegistry,
                                            ServiceMap services, DictionaryRegistry serviceLocator, ActivityPresenterMap presenterMap, PlaceController placeController, StorageManager storageManager,
                                            ContentManagementSystem contentManager, DesktopManager desktopManager, PeerManager peerManager, ServiceBus serviceBus) {
            super();
            this.presenterMap = presenterMap;
            this.contentManager = contentManager;
            this.placeController = placeController;
            this.storageManager = storageManager;
            this.desktopManager = desktopManager;
            this.serviceBus = serviceBus;
            this.peerManager = peerManager;

            this.outputHandlerRegistry = outputHandlerRegistry;
            this.commandRegistry = services;
            this.serviceLocator = serviceLocator;

        }
    */


/*

@Override
    public void contextSwitch(ActivityProcess activityProcess, JsApplicationItem applicationItem, AcceptsOneWidget containerr, ServiceBus bus) {
        // Read output feature for this application
        JavaScriptObject configuration = applicationItem.getPropertiesObject();
        ProcessPresenter outputFeature = presenterMap.getConfigured(configuration, null, bus, null);

        // attach output feature to activity container
        containerr.setWidget(outputFeature);

        // set activitie's root process context
        TaskPresenter taskPresenter = outputFeature.getRootTaskPresenter();
        taskPresenter.setUserContentClass(PROCESS_USER_AREA_CLASS);
        HumanActivityContextServices context = new HumanActivityContextServices(activityProcess, applicationItem, placeController, taskPresenter, outputFeature,
                this, serviceBus, desktopManager, contentManager, storageManager, peerManager, bus);
        activityProcess.setContext(context);

        // start process
        taskPresenter.setProcessName(context.getProcessLocalizedName(), null);
        bus.fireEvent(new ContextSwitchEvent(context, activityProcess));
        activityProcess.start((DesktopPlace) placeController.getWhere(), new SimpleActivityTransition(placeController), bus);
    }
    */
/*
    @Override
	public <I, O> void processSwitch(Process<I, O> newProcess, String localizedName, I input, final StateTransition<O> callback,
			ProcessContextServices oldContext) {
		// logical detach, and set callback result to null
		final ProcessKiller killer = new ProcessKiller(callback);

		// Create a new context for the new process to resolve on
		TaskPresenter childTaskPresenter = oldContext.getNestedTaskPresenter().spawnChild(killer);
		childTaskPresenter.setUserContentClass(PROCESS_USER_AREA_CLASS);
		childTaskPresenter.setProcessName(localizedName, oldContext.getProcessLocalizedName());
		ProcessContextServices context = new HumanActivityContextServices(newProcess, oldContext.getItem(), placeController, childTaskPresenter,
				oldContext.getActivityOutputFeature(), this, serviceBus, new NestedProcessDesktopManager(desktopManager), contentManager, storageManager,
				peerManager, oldContext.getServiceBus());
		context.setProcessLocalizedName(localizedName);
		newProcess.setContext(context);

		context.getServiceBus().fireEvent(new ProcessSwitchEvent(oldContext.getProcess(), newProcess));
		callback.hook(new DataCallback<O>() {

			@Override
			public void execute() {
				killer.disableCallback();
				killer.setResultAndFinish(null);
			}
		});
		newProcess.start(input, callback, context.getServiceBus());
	}

	private static class ProcessKiller extends DataCallback<Void> {
		final StateTransition<?> callback;
		private boolean disableCallbac;
		private boolean called;

		public ProcessKiller(StateTransition<?> callback) {

			this.callback = callback;
			disableCallbac = false;
			called = false;
		}

		public void disableCallback() {
			this.disableCallbac = true;
		}

		@Override
		public void execute() {
			if (!called) {
				called = true;
				if (!disableCallbac) {
					callback.setResultAndFinish(null);
				}
			}

		}

	}

	@Override
	public void getCurrentTaskOutput(ProcessContextServices context, JsTransactionApplicationContext state, StateTransition<JavaScriptObject> callback) {
		if (context.getNestedTaskPresenter() == null) {
			if (bpm == null) {

				// FIXME Read service tree to figure out the path of a given
				// service (bpm, in this case) and thus enable developers to
				// define their own paths instead of having hardcoded ones
				//hint:: en el consctuctor del ProcessVegetateChannel 
				//quitar el parámetro ProcessServiceManifest.CHANNEL_ID
				DesktopManager dm = context.getDesktopManager();
				VegetateServiceManifest bpmServiceManifest = null;
				BPMPeer peer = context.getPeerManager().getPeer(hostId);
				SerializationService<JsTaskProcessRequest, JsonVegetateResponse> serializer;
				bpm = new ProcessVegetateChannelImpl(dm.getCurrentActivityHost(), dm.isSSL(), bpmServiceManifest, context.getServiceBus(), serializer,
						peer.getPublicKey(), peer.getPrivateKey());
			}
			JsTaskProcessRequest object = JsTaskProcessRequest.createObject().cast();
			// TODO strip context of all non-native data (get rid of all POJOs)
			object.setActivityContext(state);
			object.setProcess(process);
			object.setTask(task);
			// THE COMPUTER CAN ATTEMPT TO FIND A RESULT
			bpm.send(object, (StateTransition) callback);

		} else {
			// THE HUMAN HAS A PARTIAL RESULT;
			JavaScriptObject value = context.getNestedTaskPresenter().getTaskContent().getMainTaskProcessor().getValue();
			callback.setResultAndFinish(value);
		}
	}

	@Override
	public void setCurrentProcess(String id) {
		this.process = id;
	}

	@Override
	public void setCurrentTask(String id) {
		this.task = id;
	}



	@Override
	public void excecuteCommand(String command, JavaScriptObject properties,
								ServiceBus serviceBus, ProcessContextServices processContext,
								JsTransactionApplicationContext processParameters,
								StateTransition<JsTransactionApplicationContext> callback) {



		setCommand(properties,command);

		CommandService service = commandRegistry.getConfigured(properties, processContext, serviceBus, processParameters);

		excecuteCommand(command,service, properties, serviceBus, processContext, processParameters, callback);
	}
	private native void setCommand(JavaScriptObject properties,  String command) /*-{
		properties.command=command;
	}-*//*;

	public void excecuteCommand( CommandService service, JavaScriptObject properties,
								 ServiceBus serviceBus, ProcessContextServices processContext,
								 JsTransactionApplicationContext processParameters,
								 StateTransition<JsTransactionApplicationContext> callback) {
		excecuteCommand(null,service, properties, serviceBus, processContext, processParameters, callback);
	}

	private void excecuteCommand(String command, CommandService service, JavaScriptObject properties,
								 ServiceBus serviceBus, ProcessContextServices processContext,
								 JsTransactionApplicationContext processParameters,
								 StateTransition<JsTransactionApplicationContext> callback) {


		if (callback == null) {
			callback = DataCallback.nullCallback();
		}

		service.prepare(command, properties, serviceBus, processContext,
				processParameters, callback);
		service.execute();
	}


	public ServiceDictionary<?> getServiceDictionary(String dictionary){
		ServiceDictionary<?> regreso = this.serviceLocator.get(dictionary);
		if(regreso==null){
			throw new IllegalArgumentException("not a registered service dictionary: "+dictionary);
		}
		return regreso;
	}*/
}
