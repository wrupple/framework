package com.wrupple.muba.worker.server.chain.command.impl;

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
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.SelectionModelDictionary;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.TransactionPanelMap;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.TaskValueChangeListener;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.impl.SingleSelectionModelImpl;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.chain.command.AssembleBrowser;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;
import com.wrupple.vegetate.domain.CatalogEntry;

import java.util.List;
import java.util.Set;

public class AssembleBrowserImpl implements AssembleBrowser {

    private final CatalogPlaceInterpret interpret;
    private final PlaceHistoryMapper placeTokenizer;
    private final SelectionModelDictionary modelDictionary;
    private final ContentManagementSystem cms;
    private boolean disableBrowserInit;

    @Inject
    public AssembleBrowserImpl(ContentManagementSystem cms, SelectionModelDictionary modelDictionary, TransactionPanelMap transactionPanelMap,
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
    protected HumanTaskProcessor<?, ?> buildUserInteractionInterface(String catalog, final JavaScriptObject properties, JsTransactionApplicationContext parameter,
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


}
