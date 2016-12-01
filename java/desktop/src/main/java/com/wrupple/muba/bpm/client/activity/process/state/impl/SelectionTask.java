package com.wrupple.muba.bpm.client.activity.process.state.impl;

import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractUserInteractionState;
import com.wrupple.muba.bpm.client.activity.widget.HumanTaskProcessor;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.client.factory.dictionary.SelectionModelDictionary;
import com.wrupple.muba.desktop.client.factory.dictionary.TransactionPanelMap;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.TaskValueChangeListener;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.ToolbarAssemblyDelegate;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.SingleSelectionModelImpl;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.CatalogEntry;

public class SelectionTask extends AbstractUserInteractionState {

	private boolean disableBrowserInit;
	private final CatalogPlaceInterpret interpret;
	private final PlaceHistoryMapper placeTokenizer;
	private final SelectionModelDictionary modelDictionary;
	private final ContentManagementSystem cms;

	@Inject
	public SelectionTask(ContentManagementSystem cms, SelectionModelDictionary modelDictionary, TransactionPanelMap transactionPanelMap,
			ToolbarAssemblyDelegate userInterfaceAssembler, CatalogPlaceInterpret interpret, PlaceHistoryMapper placeTokenizer,
			TaskValueChangeListener valueChangeListener) {
		super(transactionPanelMap, userInterfaceAssembler, valueChangeListener);
		super.userOutputIsArray = true;
		this.interpret = interpret;
		this.placeTokenizer = placeTokenizer;
		this.modelDictionary = modelDictionary;
		this.cms = cms;
	}

	public void setDisableBrowserInit(boolean disableBrowserInit) {
		this.disableBrowserInit = disableBrowserInit;
	}

	@Override
	protected HumanTaskProcessor<?, ?> buildUserInteractionInterface(String catalog, final JavaScriptObject properties, JsTransactionActivityContext parameter,
			EventBus eventBus, ProcessContextServices ctx) {
		ContentManager<JsCatalogEntry> contentManager = cms.getContentManager(catalog);
		ContentBrowser transactionView = (ContentBrowser) contentManager.getSelectTransaction(parameter, properties, eventBus, ctx);
		final String preselectionUrlParameter = GWTUtils.getAttribute(properties, "preselectionUrlParameter");
		if (preselectionUrlParameter != null) {
			DesktopPlace place = (DesktopPlace) context.getPlaceController().getWhere();
			String rawPreselectedKeys = place.getProperty(preselectionUrlParameter);
			final SelectionModel<? super JsCatalogEntry> selectionModel = transactionView.getSelectionModel();
			final boolean isSingle = SingleSelectionModelImpl.NAME.equals(GWTUtils.getAttribute(properties, modelDictionary.getPropertyName()));

			selectionModel.addSelectionChangeHandler(new Handler() {
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					JsArrayMixed keys = JavaScriptObject.createArray().cast();
					if (isSingle) {
						JsCatalogEntry sec = ((SingleSelectionModel<JsCatalogEntry>) selectionModel).getSelectedObject();
						if (sec != null) {
							keys.push(sec.getId());
						}
					} else {
						Set<JsCatalogEntry> set = ((MultiSelectionModel<JsCatalogEntry>) selectionModel).getSelectedSet();
						for (JsCatalogEntry sec : set) {
							keys.push(sec.getId());
						}
					}
					String value = interpret.getRawPreselectedKeys(keys);

					context.getDesktopManager().putPlaceParameter(preselectionUrlParameter, value);
				}
			});
			if (rawPreselectedKeys != null) {
				JsArrayMixed keys = interpret.getPreselectedKeys(rawPreselectedKeys);
				if (keys != null) {
					JsFilterData filter = JsFilterData.createSingleFieldFilter(CatalogEntry.ID_FIELD, keys);
					DesktopManager dm = context.getDesktopManager();
					context.getStorageManager().read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), parameter.getTaskDescriptor().getCatalogId(), filter,
							new DataCallback<List<JsCatalogEntry>>() {
								@Override
								public void execute() {
									if (result != null && !result.isEmpty()) {
										for (JsCatalogEntry asdf : result) {
											selectionModel.setSelected(asdf, true);
										}
									}
								}
							});
				}
			}
		}

		return transactionView;
	}

	protected <T extends JavaScriptObject> void afterUIAssembled(String catalog, JsApplicationItem applicationItem, HumanTaskProcessor<T, ?> transactionView,
			ProcessContextServices context, EventBus eventBus, JsTransactionActivityContext parameter, JsFilterData filterData) {
		if (!disableBrowserInit) {
			T initialValue = null;
			boolean navigationflag = taskDescriptor.getCurrentPlaceNavigationFlag();
			if (navigationflag && applicationItem != null && catalog.equals(ApplicationItem.CATALOG)) {
				JsArray<JsApplicationItem> array = applicationItem.getChildItemsValuesArray();
				if (array != null) {
					initialValue = array.cast();
				}
			} else {
				if (filterData == null) {
					// TODO read filtervalue from configuration
					initialValue = JsFilterData.newFilterData().cast();
				} else {
					initialValue = filterData.cast();
				}
			}
			transactionView.setValue(initialValue);
		}
	}

}
