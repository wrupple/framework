package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManager;
import com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates.AbstractValueRelationEditor.RelationshipDelegate;
import com.wrupple.muba.worker.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.presentation.CatalogEditor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;

public interface GenericFieldFactory {

    HasValue<Object> getOrCreateField(JavaScriptObject fieldProperties, CatalogAction mode, EventBus bus, ProcessContextServices contextServices, RelationshipDelegate delegate, JsTransactionApplicationContext contextParameters, String host, String domain, JsCatalogEntry currentEntry, FieldDescriptor field);

    HasValue<String> getParentSelectionField(String resolvedHost,
                                             String resolvedDomain, String catalogParentId, StorageManager catalogService, ContentManager<JsCatalogEntry> parentCatalogManager, ProcessContextServices processServices, CatalogEditor<? extends JavaScriptObject> editor);
}
