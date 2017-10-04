package com.wrupple.muba.bpm.client.activity.impl;

import java.util.List;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.AbstractSequentialActivity;
import com.wrupple.muba.bpm.client.activity.ActivityProcess;
import com.wrupple.muba.bpm.client.activity.SequentialActivity;
import com.wrupple.muba.bpm.client.activity.process.impl.ParallelProcess;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.cms.domain.WruppleDomainJavascript;
import com.wrupple.muba.cms.domain.WruppleDomainStyleSheet;
import com.wrupple.muba.desktop.client.event.VegetateEvent;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.ProcessManager;
import com.wrupple.muba.desktop.client.services.logic.impl.ActivityVegetateEventHandler;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogActionRequest;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsProcessDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.client.services.RemoteStorageUnit;
import com.wrupple.vegetate.client.services.StorageManager;

public class TransactionalActivity extends AbstractSequentialActivity implements SequentialActivity {

	static class ResourceLoadingCallback extends DataCallback<ActivityProcess> {
		final DataCallback<ActivityProcess> wrapped;

		boolean activityProcess = false;
		boolean loadedstyles = false;
		boolean loadedscripts = false;

		public ResourceLoadingCallback(final DesktopManager dm, DataCallback<ActivityProcess> wrapped, final JsArrayString scripts, JsArrayString sheets,
				final StorageManager sm, final EventBus eventBus) {
			super();
			this.wrapped = wrapped;
			final RemoteStorageUnit remoteStorage = sm.getRemoteStorageUnit(dm.getCurrentActivityHost());
			/*
			 * LOAD JAVASCRIPT FILES
			 */
			if (scripts == null || scripts.length() == 0) {
				loadedscripts = true;
			} else {
				final ParallelProcess<String, String> loadScripts = new ParallelProcess<String, String>(new State<String, String>() {

					@Override
					public void start(final String parameter, final StateTransition<String> onDone, EventBus bus) {
						JsCatalogActionRequest request = JsCatalogActionRequest.newRequest(dm.getCurrentActivityDomain(), CatalogActionRequest.LOCALE,
								WruppleDomainJavascript.CATALOG, CatalogActionRequest.READ_ACTION, parameter, "0", null, null);
						String scriptUrl = remoteStorage.buildServiceUrl(request);

						ScriptInjector.fromUrl(scriptUrl).setWindow(ScriptInjector.TOP_WINDOW).setCallback(new Callback<Void, Exception>() {
							public void onFailure(Exception reason) {
								Window.alert("Script load failed.");
								onDone.setResultAndFinish(parameter);
							}

							public void onSuccess(Void result) {
								onDone.setResultAndFinish(parameter);
							}
						}).inject();
					}
				}, false, false);

				remoteStorage.assertManifest(new DataCallback<Void>() {

					@Override
					public void execute() {
						loadScripts.start(GWTUtils.asStringList(scripts), new DataCallback<List<String>>() {

							@Override
							public void execute() {
								loadedscripts = true;
								testAndFinish();
							}
						}, eventBus);
					}
				});

			}
			if (sheets == null || sheets.length() == 0) {
				loadedstyles = true;
			} else {
				sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), WruppleDomainStyleSheet.CATALOG, GWTUtils.asStringList(sheets),
						new DataCallback<List<JsCatalogEntry>>() {

							@Override
							public void execute() {
								loadedstyles = true;
								testAndFinish();
								for (JsCatalogEntry js : result) {
									StyleInjector.inject(js.getStringValue());
								}
							}
						});

			}
		}

		protected void testAndFinish() {
			if (activityProcess && loadedscripts && loadedstyles) {
				wrapped.setResultAndFinish(result);
			}
		}

		@Override
		public void execute() {
			activityProcess = true;
			testAndFinish();
		}

	}

	class ProcessDescriptorCallback extends DataCallback<List<JsProcessDescriptor>> {

		private DataCallback<ActivityProcess> callback;

		public ProcessDescriptorCallback(DataCallback<ActivityProcess> callback) {
			this.callback = callback;
		}

		@Override
		public void execute() {
			if (result == null || result.isEmpty()) {
				// FIXME process 404
				throw new IllegalArgumentException("Activity Descriptor not found for current activity");
			} else {
				JsProcessDescriptor process = result.get(0);
				assembly.start(process, callback, eventBus);

			}
		}

	}

	/*
	 * SERVICES
	 */
	private EventBus eventBus;
	private final TransactionalActivityAssembly assembly;
	protected JsArray<JsProcessTaskDescriptor> overridenProcessSteps;
	private final ActivityVegetateEventHandler vegetateHandler;

	// cachuky tuku
	@Inject
	public TransactionalActivity(ProcessManager pm, DesktopManager dm, PlaceController pc, TransactionalActivityAssembly assembly,
			ActivityVegetateEventHandler vegetateHandler) {
		super(dm, pm, pc);
		this.vegetateHandler = vegetateHandler;
		this.assembly = assembly;

	}

	@Override
	public void start(AcceptsOneWidget panel, com.google.gwt.event.shared.EventBus eventBus) {
		this.eventBus = eventBus;

		super.start(panel, eventBus);
	}

	@Override
	public void getActivityProcess(final DesktopPlace input, JsApplicationItem actd, DataCallback<ActivityProcess> callback) {
		callback.hook(vegetateHandler);
		eventBus.addHandler(VegetateEvent.TYPE, vegetateHandler);
		/*
		 * Load transaction data
		 */
		final JsApplicationItem applicationItem;
		if (actd == null) {
			applicationItem = null;
		} else {
			applicationItem = actd.cast();

			JsArrayString scripts = applicationItem.getRequiredScriptsArray();
			JsArrayString sheets = applicationItem.getRequiredStyleSheetsArray();

			if ((scripts != null && scripts.length() > 0) || (sheets != null && sheets.length() > 0)) {
				callback = new ResourceLoadingCallback(dm, callback, scripts, sheets, assembly.getSm(), eventBus);
			}

			final String welcomeProcessId = applicationItem.getWelcomeProcess();
			if (welcomeProcessId != null) {

				callback.hook(new DataCallback<ActivityProcess>() {
					@Override
					public void execute() {
						final StateTransition<Process<JavaScriptObject, JavaScriptObject>> transactionInfoCallback = new DataCallback<Process<JavaScriptObject, JavaScriptObject>>() {

							@Override
							public void execute() {
								JsTransactionApplicationContext i = JsTransactionApplicationContext.createObject().cast();
								StateTransition<JavaScriptObject> o = DataCallback.nullCallback();
								pm.processSwitch(result, applicationItem.getName(), i, o, result.getContext());

							}
						};
						assembly.loadAndAssembleProcess(welcomeProcessId, transactionInfoCallback);
					}
				});
			}

		}
		assembly.setApplicationItem(applicationItem);

		if (overridenProcessSteps == null) {
			StateTransition transactionInfoCallback = new ProcessDescriptorCallback(callback);
			String processId = applicationItem.getProcessAsId();
			assembly.loadProcess(processId, transactionInfoCallback);
		} else {
			assembly.assembleActivityProcess(overridenProcessSteps, callback);
		}

	}

}
