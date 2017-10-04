package com.wrupple.muba.desktop.client.services.command.impl;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.Process;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.TransactionalActivityAssembly;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.desktop.client.services.logic.ProcessSwitchCommand;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsProcessDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
public class ProcessSwitchCommandImpl implements ProcessSwitchCommand {

	class ProcessAssembledCallback
			extends
			DataCallback<com.wrupple.muba.bpm.client.services.Process<JavaScriptObject, JavaScriptObject>> {

		private final JavaScriptObject input;

		public ProcessAssembledCallback( JavaScriptObject input) {
			super();
			this.input = input;
		}

		@Override
		public void execute() {
			StateTransition<JavaScriptObject> switchCallback=new SwitchCallback();
			// TODO get process name (which is already in memory somewhere)
			contextServices.getProcessManager()
					.processSwitch(result, "", input,switchCallback, contextServices);
		}

	}
	
	class SwitchCallback extends DataCallback<JavaScriptObject>
	{

		@Override
		public void execute() {
			if(task.getProducedField()!=null){
				GWTUtils.setAttribute(processParameters, task.getProducedField(), result);
			}
			callback.setResultAndFinish(processParameters);
		}
		
	}
	protected TransactionalActivityAssembly assembly;
	private StateTransition<JsTransactionApplicationContext> callback;
	private String processId;
	private String task.getProducedField();
	private ProcessContextServices contextServices;
	private JsTransactionApplicationContext nueva;
	private EventBus eventBus;

	private JavaScriptObject processInstance;
	private DesktopTheme theme;
	private JsTransactionApplicationContext processParameters;
	private ContentManagementSystem cms;
	private Process<JsTransactionApplicationContext, JsTransactionApplicationContext> assembledProcess;

	@Inject
	public ProcessSwitchCommandImpl(TransactionalActivityAssembly assembly,DesktopTheme theme, ContentManagementSystem cms) {
		super();
		this.assembly = assembly;
		this.cms=cms;
		this.theme=theme;
	}


	@Override
	public void prepare(String command, JavaScriptObject commandProperties,
			EventBus eventBus, ProcessContextServices processContext,
			JsTransactionApplicationContext processParameters,
			StateTransition<JsTransactionApplicationContext> callback) {
		task.getProducedField()=null;
		//FIXME this property should be set by action not task
		task.getProducedField() = GWTUtils.getAttribute(commandProperties, "task.getProducedField()Nested");
		this.eventBus=eventBus;
		this.processParameters=processParameters;
		processId = GWTUtils.getAttribute(commandProperties,ID_PARAMETER );
		processInstance = GWTUtils.getAttributeAsJavaScriptObject(commandProperties, JsProcessDescriptor.PROCESS_INSTANCE_FIELD);

		if(processId!=null && processId.contains(":")){
			int endTokenIndex = processId.indexOf(':');
			String transaction = processId.substring(0,endTokenIndex);
			String targetCatalog = processId.substring(endTokenIndex+1);
			if(targetCatalog.isEmpty()||"catalogFromCurrentContext".equals(targetCatalog)){
				targetCatalog=processParameters.getUserOutput().getCatalog();
			}
			processId=null;
			processInstance=null;
			
			ContentManager<JsCatalogEntry> cm = cms.getContentManager(targetCatalog);
			if(ProcessTaskDescriptor.SELECT_COMMAND.equals(transaction)){
				assembledProcess=cm.getSelectionProcess(processContext, true, true);
			}else if(CatalogActionRequest.CREATE_ACTION.equals(transaction)){
				assembledProcess=cm.getEditingProcess(CatalogAction.CREATE, eventBus, processContext);
			}else if(CatalogActionRequest.READ_ACTION.equals(transaction)){
				assembledProcess=cm.getEditingProcess(CatalogAction.READ, eventBus, processContext);
			}else if(CatalogActionRequest.WRITE_ACTION.equals(transaction)){
				assembledProcess=cm.getEditingProcess(CatalogAction.UPDATE, eventBus, processContext);
			}
		}
		
		
		nueva = JsTransactionApplicationContext.createObject().cast();
		//FIXME read target entry id for update, and read actions
		String rawPropertiesToCopy = GWTUtils.getAttribute(commandProperties, "propertiesFromCurrentContext");
		if(rawPropertiesToCopy!=null){
			if("copyAll".equals(rawPropertiesToCopy)){
				GWTUtils.copyAllProperties(nueva, processParameters);
			}else{
				JsArrayString propertiesFromCurrentContext = split(rawPropertiesToCopy);
				GWTUtils.copyProperties(nueva, processParameters,propertiesFromCurrentContext,null);
			}
		}
		//TODO use a native function to initialize something? better use a ExcecuteJavaScriptFuntion state
		this.callback=callback;
		this.contextServices=processContext;
	}


	private native JsArrayString split(String rawPropertiesToCopy) /*-{
		return rawPropertiesToCopy.split(",");
	}-*/;


	@Override
	public void execute() {
		 ProcessAssembledCallback switchCallback = new ProcessAssembledCallback( nueva);
		 if(assembledProcess==null){
			 if(processId==null){
					if(processInstance==null){
						throw new IllegalArgumentException("No process provided");
					}else{
						JsProcessDescriptor descriptor=processInstance.cast();
						JsArray<JsProcessTaskDescriptor> processSteps=descriptor.getProcessStepsInstancesArray();
						Process<JavaScriptObject, JavaScriptObject> regreso=new SequentialProcess<JavaScriptObject,JavaScriptObject>();
						assembly.assembleNativeProcess(regreso, processSteps);
						
						switchCallback.setResultAndFinish(regreso);
					}
				}else{
					assembly.loadAndAssembleProcess(processId,switchCallback);
				}
		 }else{
			 switchCallback.setResultAndFinish((Process)assembledProcess);
		 }
		
		
	}

}
