package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.activity.widgets.editors.SequentialListEditor;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.MultiTextCelll;
import com.wrupple.muba.desktop.client.activity.widgets.fields.cells.templates.ClickableElementTemplate;
import com.wrupple.muba.desktop.shared.services.factory.dictionary.SolverConcensorMap;
import com.wrupple.muba.desktop.shared.services.factory.help.SolverConcensor;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.CatalogUserInterfaceMessages;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.activity.process.impl.SequentialProcess;
import com.wrupple.vegetate.domain.FieldDescriptor;

import javax.inject.Provider;

public class MultiTextProvider implements CatalogFormFieldProvider {

	private ClickableElementTemplate template;
	private CatalogUserInterfaceMessages messages;
    private final SolverConcensorMap provider;

	@Inject
    public MultiTextProvider(ClickableElementTemplate template, CatalogUserInterfaceMessages messages, SolverConcensorMap provider) {
        super();
		this.template = template;
		this.messages = messages;
		this.provider = provider;
	}

	@Override
	public Cell<JsArrayString> createCell(final EventBus bus, final ProcessContextServices contextServices, final JsTransactionApplicationContext contextParameters,
			final JavaScriptObject formDescriptor, FieldDescriptor desc, CatalogAction mode) {
		final  JsFieldDescriptor d=(JsFieldDescriptor) desc;
		Provider<Process<JsArrayString, JsArrayString>> processProvider = new Provider<Process<JsArrayString, JsArrayString>>() {
			
			@Override
			public Process<JsArrayString, JsArrayString> get() {
				JavaScriptObject fieldProperties = d.getPropertiesObject();

				JavaScriptObject casado = JavaScriptObject.createObject().cast();

				GWTUtils.copyAllProperties(casado, formDescriptor);

				GWTUtils.copyAllProperties(casado, fieldProperties);
                SolverConcensor assisotor = null;
                try {
					assisotor = provider.getConfigured(casado, contextServices, bus, contextParameters);
				} catch (Exception e) {
                    GWT.log("multitext setRuntimeContext undeclared aid provider", e);
                }
				SequentialListEditor wrapped = new SequentialListEditor(messages, assisotor);
				String name = messages.editingMultipleSingleLineTextProcess();
				Process<JsArrayString, JsArrayString> process = SequentialProcess.wrap(wrapped, wrapped, name);
				return process;
			}
		};
		Cell<JsArrayString> lista = new MultiTextCelll(bus, contextServices, contextParameters, d, mode, processProvider,
				messages.editingMultipleSingleLineTextProcess(), template, messages);
		return lista;
	}

}
