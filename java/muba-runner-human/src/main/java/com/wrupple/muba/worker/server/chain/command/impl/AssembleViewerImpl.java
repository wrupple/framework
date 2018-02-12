package com.wrupple.muba.worker.server.chain.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.TransactionPanelMap;
import com.wrupple.muba.desktop.client.services.logic.TaskValueChangeListener;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.server.chain.command.AssembleViewer;
import com.wrupple.muba.worker.shared.widgets.HumanTaskProcessor;

public class AssembleViewerImpl implements AssembleViewer {
    private final ContentManagementSystem cms;

    @Inject
    public AssembleViewerImpl(ContentManagementSystem cms, TransactionPanelMap transactionPanelMap,
                              ToolbarAssemblyDelegate userInterfaceAssembler, TaskValueChangeListener valueChangeListener) {
        super(transactionPanelMap, userInterfaceAssembler, valueChangeListener);
        this.cms = cms;
    }

    @Override
    protected HumanTaskProcessor<?, ?> buildUserInteractionInterface(String catalog, JavaScriptObject properties, JsTransactionApplicationContext parameter,
                                                                     EventBus eventBus, ProcessContextServices ctx) {
        ContentManager<JsCatalogEntry> contentManager = cms.getContentManager(catalog);
        HumanTaskProcessor<JsCatalogEntry, ?> transactionView = contentManager.getReadTransaction(parameter, properties, eventBus, ctx);
        return transactionView;
    }

}
