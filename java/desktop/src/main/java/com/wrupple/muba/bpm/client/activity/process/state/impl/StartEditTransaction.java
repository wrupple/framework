package com.wrupple.muba.bpm.client.activity.process.state.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractStartUserTransaction;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.services.command.ExplicitOutputPlace;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.desktop.client.services.presentation.CatalogPlaceInterpret;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFieldDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;

public class StartEditTransaction extends AbstractStartUserTransaction {
	static class EntryReadingCallback extends DataCallback<JsCatalogEntry> {
		StateTransition<JsTransactionActivityContext> onDone;
		JsTransactionActivityContext parameter;

		public EntryReadingCallback(JsTransactionActivityContext parameter, StateTransition<JsTransactionActivityContext> onDone) {
			super();
			this.parameter = parameter;
			this.onDone = onDone;
		}

		@Override
		public void execute() {
			parameter.setTargetEntryId(result.getId());
			parameter.setUserOutput(result);
			onDone.setResultAndFinish(parameter);
		}

	}

	/**
	 * Set entry field values from context attributes and place parameters
	 * 
	 * @author japi
	 *
	 */
	class URLParsingCallback extends DataCallback<JsTransactionActivityContext> {
		StateTransition<JsTransactionActivityContext> onDone;

		public URLParsingCallback(StateTransition<JsTransactionActivityContext> onDone) {
			super();
			this.onDone = onDone;
		}

		@Override
		public void execute() {
			onDone.setResult(result);
			final JsTransactionActivityContext context = result;
			final JsCatalogEntry entry = result.getUserOutput();
			
			contextServices.getStorageManager().loadCatalogDescriptor(contextServices.getDesktopManager().getCurrentActivityHost(), contextServices.getDesktopManager().getCurrentActivityDomain(), taskDescriptor.getCatalogId(), new DataCallback<CatalogDescriptor>() {
				@Override
				public void execute() {
					if (taskDescriptor != null) {
						JavaScriptObject properties = taskDescriptor.getPropertiesObject();
						if (properties != null) {
							JsArray<JsFieldDescriptor> fields = ((JsCatalogDescriptor) result).getFieldArray();
							if (fields != null) {
								String fieldId;
								String urlParameter,contextParameter;
								String parameterValue;
								JsFieldDescriptor field;
								DesktopPlace currentPlace = (DesktopPlace) contextServices.getPlaceController().getWhere();
								for (int i = 0; i < fields.length(); i++) {
									field = fields.get(i);
									fieldId = field.getFieldId();
									//SET ENTRY FIELD VALUES FROM URL PARAMETERS
									urlParameter = GWTUtils.getAttribute(properties, fieldId + ExplicitOutputPlace.URL_PARAMETER_POSTFIX);
									//SET ENTRY FIELD VALUES FROM CONTEXT ATTRIBUTES
									contextParameter =  GWTUtils.getAttribute(properties, fieldId + ExplicitOutputPlace.CONTEXT_PROPERTY_POSTFIX);
									if (urlParameter != null) {
										parameterValue= currentPlace.getProperty(urlParameter);
										if(parameterValue!=null){
											setValue(field, fieldId, entry, parameterValue);
										}
									}
									
									if(contextParameter!=null){
										setValue(fieldId,contextParameter,context,entry);
									}
								}
							}
						}
					}

					onDone.execute();
				}

				
			});

		}
		private native void setValue(String fieldId,
				String contextParameter, JsTransactionActivityContext result, JsCatalogEntry entry) /*-{
			
			var tokens = contextParameter.split(".");
			
			var token;
			var value = result;
			for(var i = 0 ; i< tokens.length; i++){
				token = tokens[i];
				value =value[token];
				if(i==tokens.length-1){
					entry[fieldId]=value;
				}
			}
			
		}-*/;
		private void setValue(JsFieldDescriptor field, String fieldId, JsCatalogEntry entry, String rawValue) {
			conversion.convertToPersistentDatabaseValue(rawValue, field, entry);
		}

	}

	private final FieldConversionStrategy conversion;

	@Inject
	public StartEditTransaction(CatalogPlaceInterpret placeInterpret, FieldConversionStrategy conversion) {
		super(placeInterpret);
		this.conversion = conversion;
	}

	@Override
	public void start(JsTransactionActivityContext context, StateTransition<JsTransactionActivityContext> wrapped, EventBus bus) {

		StateTransition<JsTransactionActivityContext> onDone = new URLParsingCallback(wrapped);
		String targetEntryId = placeInterpret.getCurrentPlaceEntry((DesktopPlace)contextServices.getPlaceController().getWhere());
		
		context.setTargetEntryId(targetEntryId);
		super.start(context, onDone, bus);
		String catalog = taskDescriptor.getCatalogId();
		String transactionType = taskDescriptor.getTransactionType();
		context.setCanceled(false);

		if (CatalogActionRequest.CREATE_ACTION.equals(transactionType)) {
			// create a draft entry
			createDraft(catalog, new EntryReadingCallback(context, onDone));

		} else {
			// editing commands MUST have an entry
			DesktopPlace currentPlace = (DesktopPlace) contextServices.getPlaceController().getWhere();

			String targetEntry = context.getTargetEntryId();
			if (targetEntry == null) {

				targetEntry = placeInterpret.getCurrentPlaceEntry(currentPlace);
			}
			if (targetEntry == null) {
				JsCatalogEntry userOutput = context.getUserOutput();
				if (userOutput != null && GWTUtils.isArray(userOutput)) {
					JsArray<JsCatalogEntry> arr = userOutput.cast();
					userOutput = arr.get(0);
				}
				if (userOutput != null && catalog.equals(userOutput.getCatalog())) {
					targetEntry = userOutput.getId();
				}
				currentPlace.setProperty(CatalogActionRequest.CATALOG_ENTRY_PARAMETER, targetEntry);
			}
			if (targetEntry == null) {
				throw new IllegalArgumentException("No target Entry could be found");
			} else {
				contextServices.getStorageManager().read(contextServices.getDesktopManager().getCurrentActivityHost(), contextServices.getDesktopManager().getCurrentActivityDomain(), catalog, targetEntry, new EntryReadingCallback(context, onDone));
			}

		}

	}

	private void createDraft(String catalog, StateTransition draftCreationCallback) {
		JavaScriptObject entry = JsCatalogEntry.createCatalogEntry(catalog);
		// TODO draft creation (currently server donest return draft field to
		// the client as it is not declared in any catalog)
		GWTUtils.setAttribute(entry, CatalogEntry.DRAFT_FIELD, false);

		draftCreationCallback.setResultAndFinish(entry);
	}
}
