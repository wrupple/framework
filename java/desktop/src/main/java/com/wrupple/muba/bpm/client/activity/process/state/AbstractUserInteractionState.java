package com.wrupple.muba.bpm.client.activity.process.state;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.widget.HumanTaskProcessor;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.widgets.BigFatMessage;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel;
import com.wrupple.muba.desktop.client.factory.dictionary.TransactionPanelMap;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.TaskValueChangeListener;
import com.wrupple.muba.desktop.client.services.presentation.ToolbarAssemblyDelegate;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsArrayList;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogEntry;

/**
 *
 * 
 * @author japi
 *
 */
public abstract class AbstractUserInteractionState implements UserInteractionState {

	class AutoSelectionCallback extends DataCallback {
		StateTransition<JsTransactionActivityContext> onDone;
		private JsTransactionActivityContext parameter;

		public AutoSelectionCallback(JsTransactionActivityContext parameter, StateTransition<JsTransactionActivityContext> onDone) {
			super();
			this.parameter = parameter;
			this.onDone = onDone;
		}

		@Override
		public void execute() {

			if (userOutputIsArray) {
				JsArray<JsCatalogEntry> arr = JsArrayList.unwrap((List<JsCatalogEntry>) result);
				parameter.setUserOutput(arr);
			} else {
				parameter.setUserOutput((JavaScriptObject) result);
			}
			parameter.setRecoveredOutput(true);
			onDone.setResultAndFinish(parameter);
		}

	}

	/*
	 * Services
	 */
	protected final ToolbarAssemblyDelegate assembly;
	private final TransactionPanelMap transactionPanelMap;
	private final SimpleLayoutPanel wrapper;
	/*
	 * State
	 */
	protected JsProcessTaskDescriptor taskDescriptor;
	protected ProcessContextServices context;
	private String layoutUnit;
	private String transactionViewClass;
	private TaskValueChangeListener valueChangeListener;
	private String saveTo;
	/**
	 * used by selection transactions that may output an array of entries
	 */
	public boolean userOutputIsArray = false;

	@Inject
	public AbstractUserInteractionState(TransactionPanelMap transactionPanelMap, ToolbarAssemblyDelegate userInterfaceAssembler,
			TaskValueChangeListener valueChangeListener) {
		super();
		this.transactionPanelMap = transactionPanelMap;
		this.assembly = userInterfaceAssembler;
		wrapper = new SimpleLayoutPanel();
		wrapper.setStyleName("userInteractionState");
		this.valueChangeListener = valueChangeListener;
	}

	@Override
	public void start(JsTransactionActivityContext parameter, StateTransition<JsTransactionActivityContext> onDone, EventBus eventBus) {

		context.getProcessManager().setCurrentTask(taskDescriptor.getId());
		String catalog = taskDescriptor.getCatalogId();

		JsArrayString filterSelection = null;

		if (saveTo != null) {
			JavaScriptObject savedData = GWTUtils.getAttributeAsJavaScriptObject(parameter, saveTo);

			if (savedData == null) {
				DesktopPlace place = (DesktopPlace) context.getPlaceController().getWhere();
				String savedRawKeys = place.getProperty(saveTo);
				if (savedRawKeys != null) {
					filterSelection = split(savedRawKeys);
					if (filterSelection.length() <= 0) {
						filterSelection = null;
					}
				}

			} else {
				parameter.setUserOutput(savedData);
				onDone.setResultAndFinish(parameter);
				return;
			}
		}

		if (filterSelection == null) {

			/*
			 * USER INTERACTION REQUIRED
			 */

			JsApplicationItem applicationItem = parameter.getApplicationItem().cast();
			JavaScriptObject properties = taskDescriptor.getTaskPropertiesObject();

			// build main user intereaction widget
			HumanTaskProcessor<?, ?> transactionView = buildUserInteractionInterface(catalog, properties, parameter, eventBus, context);
			if (transactionViewClass != null) {
				transactionView.asWidget().addStyleName(transactionViewClass);
			}
			// build task content
			ContentPanel panel = getContentPanel(properties, eventBus, parameter);
			panel.setUnit(layoutUnit);

			// logical attach
			panel.setMainTaskProcessor(transactionView);
			context.getNestedTaskPresenter().setTaskContent(panel);
			context.getNestedTaskPresenter().setUserInteractionTaskCallback(onDone);

			// physical attach
			assembly.assebleToolbars(panel, transactionView, taskDescriptor, properties, parameter, eventBus, context, applicationItem);
			panel.setWidget(transactionView);
			// wrapper was attached to the task presenter during task start by
			// sequential process
			wrapper.setWidget(panel);
			transactionView.addValueChangeHandler(valueChangeListener);
			valueChangeListener.setContext(catalog, parameter, context, properties, eventBus);

			// transmit to user
			afterUIAssembled(catalog, applicationItem, transactionView, context, eventBus, parameter, parameter.getFilterData());
		} else {
			/*
			 * PRECIOUS RESULT WILL BE RECOVERED FROM SAVED STATE
			 */

			StorageManager sm = context.getStorageManager();
			DesktopManager dm = context.getDesktopManager();
			AutoSelectionCallback autoCallback = new AutoSelectionCallback(parameter, onDone);
			if (userOutputIsArray) {
				JsFilterData autoSelectionFilter = JsFilterData.createSingleFieldFilter(CatalogEntry.ID_FIELD, filterSelection);

				sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, autoSelectionFilter, autoCallback);

			} else {
				sm.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, filterSelection.get(0), autoCallback);
			}
			// support auto create, update, read, etc...

			wrapper.setWidget(new BigFatMessage("..."));
		}
	}

	@Override
	public Widget asWidget() {
		return wrapper;
	}

	@Override
	public void setTaskDescriptor(JsProcessTaskDescriptor traskDescriptor) {
		if (traskDescriptor == null) {
			throw new IllegalArgumentException();
		}
		this.taskDescriptor = traskDescriptor;
		wrapper.addStyleName("userInteractionState-" + traskDescriptor.getTransactionType());
		wrapper.addStyleName("userInteractionState-" + traskDescriptor.getId());
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context = context;
	}

	@Override
	public void setLayoutUnit(String s) {
		this.layoutUnit = s;
	}

	@Override
	public void setTransactionViewClass(String s) {
		this.transactionViewClass = s;
	}

	@Override
	public void setSaveTo(String task.getProducedField()) {
		this.saveTo = task.getProducedField();
	}

	protected abstract HumanTaskProcessor<?, ?> buildUserInteractionInterface(String catalog, JavaScriptObject properties,
			JsTransactionActivityContext parameter, EventBus eventBus, ProcessContextServices ctx);

	protected <T extends JavaScriptObject> void afterUIAssembled(String catalog, JsApplicationItem applicationItem, HumanTaskProcessor<T, ?> transactionView,
			ProcessContextServices context, EventBus eventBus, JsTransactionActivityContext parameter, JsFilterData filterData) {
		T taskValue = (T) parameter.getUserOutput();
		if (taskValue != null) {
			transactionView.setValue(taskValue);
		} else {
			GWT.log("assuming user-interface initialized someplace else");
		}
	}

	private native JsArrayString split(String savedRawKeys) /*-{
															return savedRawKeys.split(",");
															}-*/;

	private ContentPanel getContentPanel(JavaScriptObject properties, EventBus eventBus, JsTransactionActivityContext parameter) {
		ContentPanel panel;
		if (this.taskDescriptor.isKeepOutputFeature()) {
			panel = context.getActivityOutputFeature().getRootTaskPresenter().getTaskContent();
			if (panel == null) {
				panel = transactionPanelMap.getConfigured(properties, context, eventBus, parameter);
			}
		} else {
			panel = transactionPanelMap.getConfigured(properties, context, eventBus, parameter);
		}

		return panel;
	}

}
