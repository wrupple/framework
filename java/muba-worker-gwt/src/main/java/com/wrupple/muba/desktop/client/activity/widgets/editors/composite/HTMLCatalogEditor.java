package com.wrupple.muba.desktop.client.activity.widgets.editors.composite;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.cms.domain.WruppleDomainHTMLPage;
import com.wrupple.muba.desktop.client.activity.widgets.impl.AsynchonousHtmlWidget;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEditorMap;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.GenericFieldFactory;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.desktop.shared.services.FieldDescriptionService;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;

/**
 * 
 * renders an html and adds corresponding Label and Value widgets.
 * 
 * fieldValue_[fieldid] fieldLabel_[fieldid]
 * 
 * @author japi
 */
public class HTMLCatalogEditor extends CompositeCatalogEditor<JsCatalogEntry> {

	class PostRenderFieldCallback extends DataCallback<String> {
		HasValue<Object> field;
		FieldDescriptor fdescriptor;

		public PostRenderFieldCallback(HasValue<Object> field, FieldDescriptor fdescriptor) {
			super();
			this.field = field;
			this.fdescriptor = fdescriptor;
		}

		@Override
		public void execute() {
			renderField(field, fdescriptor);
		}

	}

	private final AsynchonousHtmlWidget main;

	@Inject
	public HTMLCatalogEditor(ContentManagementSystem cms, FieldDescriptionService fieldService, StorageManager catalogSErvice,
			FieldConversionStrategy conversion, GenericFieldFactory fieldDactory, StorageManager constants, DesktopManager dm,
			CatalogEditorMap configurationServices) {
		super(cms, fieldService, conversion, fieldDactory, configurationServices);
		main = new AsynchonousHtmlWidget(true, constants, dm);
		main.getElement().setAttribute(CatalogEntry.NAME_FIELD, "htmlCatalogEditor");
		initWidget(main);
	}

	@Override
	protected void maybeAddField(HasValue<Object> field, FieldDescriptor fdescriptor, JavaScriptObject fieldProperties) {
		main.hookCallback(new PostRenderFieldCallback(field, fdescriptor));
	}

	private void renderField(HasValue<Object> field, FieldDescriptor fdescriptor) {
		Widget widget = ((IsWidget) field).asWidget();
		String fieldId = fdescriptor.getFieldId();
		String labelId = "fieldLabel_" + fieldId;
		String valueDesc = "fieldDescription_" + fieldId;
		String valueHelp = "fieldHelp_" + fieldId;
		String valueId = "fieldValue_" + fieldId;

		Element valueHolder = main.getElementById(valueId);

		if (fdescriptor.getName() != null) {
			Element labelHolder = main.getElementById(labelId);
			if (labelHolder != null) {
				labelHolder.setInnerText(fdescriptor.getName());
			}
		}
		if (fdescriptor.getDescription() != null) {
			Element descHolder = main.getElementById(valueDesc);
			if (descHolder != null) {
				descHolder.setInnerText(fdescriptor.getDescription());
			}
		}

		if (fdescriptor.getHelp() != null) {
			Element helpHolder = main.getElementById(valueHelp);
			if (helpHolder != null) {
				helpHolder.setInnerText(fdescriptor.getHelp());
			}
		}

		if (main.getHtmlPanel().getWidgetIndex(widget) < 0) {
			if (valueHolder == null) {
				// GWT.log("No holder for field "+fieldId+" DOM id: "+valueId);
			} else {
				main.getHtmlPanel().add(widget, valueHolder);
			}
		}

	}

	@Override
	public void initialize(String catalog, CatalogAction mode, EventBus bus, ProcessContextServices processServices, JavaScriptObject properties,
			JsTransactionApplicationContext contextProcessParameters) {
		String htmlPageId = GWTUtils.getAttribute(properties, WruppleDomainHTMLPage.CATALOG);

		main.initialize(htmlPageId, null);

		super.initialize(catalog, mode, bus, processServices, properties, contextProcessParameters);
	}

}
