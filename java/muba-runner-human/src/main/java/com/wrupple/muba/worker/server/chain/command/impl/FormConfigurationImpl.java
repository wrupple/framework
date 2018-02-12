package com.wrupple.muba.worker.server.chain.command.impl;

import com.google.inject.Inject;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.worker.server.chain.command.FormConfiguration;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.shared.domain.HumanApplicationContext;
import com.wrupple.muba.worker.shared.services.FieldConversionStrategy;

public class FormConfigurationImpl implements FormConfiguration {
    @Override
    public boolean execute(HumanApplicationContext context) throws Exception {


        StateTransition<JsTransactionApplicationContext> onDone = new URLParsingCallback(wrapped);
        String targetEntryId = placeInterpret.getCurrentPlaceEntry((DesktopPlace) contextServices.getPlaceController().getWhere());

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


        
        return CONTINUE_PROCESSING;
    }

    private final FieldConversionStrategy conversion;

    @Inject
    public StartEditTransaction(CatalogPlaceInterpret placeInterpret, FieldConversionStrategy conversion) {
        super(placeInterpret);
        this.conversion = conversion;
    }


    private void createDraft(String catalog, StateTransition draftCreationCallback) {
        JavaScriptObject entry = JsCatalogEntry.createCatalogEntry(catalog);
        // TODO draft creation (currently server donest return draft field to
        // the client as it is not declared in any catalog)
        GWTUtils.setAttribute(entry, CatalogEntry.DRAFT_FIELD, false);

        draftCreationCallback.setResultAndFinish(entry);
    }

    static class EntryReadingCallback extends DataCallback<JsCatalogEntry> {
        StateTransition<JsTransactionApplicationContext> onDone;
        JsTransactionApplicationContext parameter;

        public EntryReadingCallback(JsTransactionApplicationContext parameter, StateTransition<JsTransactionApplicationContext> onDone) {
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
     */
    class URLParsingCallback extends DataCallback<JsTransactionApplicationContext> {
        StateTransition<JsTransactionApplicationContext> onDone;

        public URLParsingCallback(StateTransition<JsTransactionApplicationContext> onDone) {
            super();
            this.onDone = onDone;
        }

        @Override
        public void execute() {
            onDone.setResult(result);
            final JsTransactionApplicationContext context = result;
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
                                String urlParameter, contextParameter;
                                String parameterValue;
                                JsFieldDescriptor field;
                                DesktopPlace currentPlace = (DesktopPlace) contextServices.getPlaceController().getWhere();
                                for (int i = 0; i < fields.length(); i++) {
                                    field = fields.get(i);
                                    fieldId = field.getFieldId();
                                    //SET ENTRY FIELD VALUES FROM URL PARAMETERS
                                    urlParameter = GWTUtils.getAttribute(properties, fieldId + ExplicitOutputPlace.URL_PARAMETER_POSTFIX);
                                    //SET ENTRY FIELD VALUES FROM CONTEXT ATTRIBUTES
                                    contextParameter = GWTUtils.getAttribute(properties, fieldId + ExplicitOutputPlace.CONTEXT_PROPERTY_POSTFIX);
                                    if (urlParameter != null) {
                                        parameterValue = currentPlace.getProperty(urlParameter);
                                        if (parameterValue != null) {
                                            setValue(field, fieldId, entry, parameterValue);
                                        }
                                    }

                                    if (contextParameter != null) {
                                        setValue(fieldId, contextParameter, context, entry);
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
                                     String contextParameter, JsTransactionApplicationContext result, JsCatalogEntry entry) /*-{

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
            conversion.setAsPersistentValue(rawValue, field, entry);
        }

    }
}
