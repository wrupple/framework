package com.wrupple.muba.desktop.client.services.logic.impl;

import java.util.Collection;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.client.services.evaluation.CatalogEvaluationDelegate;
import com.wrupple.muba.desktop.client.services.command.CommitCommand;
import com.wrupple.muba.bpm.shared.services.FieldConversionStrategy;
import com.wrupple.muba.desktop.client.services.logic.TaskValueChangeListener;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import com.wrupple.muba.desktop.domain.overlay.JsFilterCriteria;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;

/**
 * 
 * Applies a set of filters to the user's output and if conditions are met the
 * service bus is fired by a specific command, commit, by default
 * 
 * @author japi
 *
 */
public class BPMValueChangeListenerImpl implements TaskValueChangeListener {

	private final CatalogEvaluationDelegate delegate;
	protected final FieldConversionStrategy conversionService;
	JsArray<JsFilterCriteria> filters;
	private String catalog;
	private ProcessContextServices context;
	private EventBus eventBus;
	private JavaScriptObject properties;
	private JsTransactionActivityContext parameter;

	@Inject
	public BPMValueChangeListenerImpl(CatalogEvaluationDelegate delegate,
			FieldConversionStrategy conversionService) {
		super();
		this.delegate = delegate;
		this.conversionService = conversionService;
	}

	@Override
	public void onValueChange(final ValueChangeEvent event) {
		final JavaScriptObject value = (JavaScriptObject) event.getValue();
		if (value != null) {
			context.getStorageManager().loadCatalogDescriptor(context.getDesktopManager().getCurrentActivityHost(),
					context.getDesktopManager().getCurrentActivityDomain(), catalog, new DataCallback<CatalogDescriptor>() {

						@Override
						public void execute() {
							// MAY ONLY FIRE commit command ONCE!
							if (filters == null) {
								filters = getBPMCriteria(result);
							}
							if(filters!=null){
								JsCatalogKey entry;
								if (GWTUtils.isArray(value)) {
									int minRequiredMathches = getMinMatchesRequired();
									if (minRequiredMathches > 0) {
										JsArray<JsCatalogKey> values = value.cast();
										int matchCount = 0;
										for (int i = 0; i < values.length(); i++) {
											entry = values.get(i);
											if (delegate.matchAgainstFilters(entry,
													filters, result)) {
												matchCount++;
												if (matchCount == minRequiredMathches) {
													callCommit();
													break;
												}
											}
										}

									}
								} else {
									entry = value.cast();

									if (delegate.matchAgainstFilters(entry,
											filters, result)) {
										callCommit();
									}
								}
							}
							

						}
					});
		}
	}

	@Override
	public void setContext(String catalog,
			JsTransactionActivityContext parameter, ProcessContextServices context,
			JavaScriptObject properties, EventBus eventBus) {
		this.catalog = catalog;
		this.context = context;
		this.eventBus = eventBus;
		this.properties = properties;
		this.parameter = parameter;
	}

	protected void callCommit() {
		String command = getCommand();
		StateTransition<JsTransactionActivityContext> c = DataCallback
				.nullCallback();
		context.getServiceBus().excecuteCommand(command, properties, eventBus,
				context, parameter, c);
	}

	private String getCommand() {
		return CommitCommand.COMMAND;
	}

	protected int getMinMatchesRequired() {
		return GWTUtils.getAttributeAsInt(properties, "selectionSize");
	}

	protected JsArray<JsFilterCriteria> getBPMCriteria(CatalogDescriptor result) {
		Collection<FieldDescriptor> fields = result.getOwnedFieldsValues();
		String fieldId, rule;
		JsArray<JsFilterCriteria> regreso = JsArray.createArray().cast();
		JsFilterCriteria criteria;
		for (FieldDescriptor field : fields) {
			fieldId = field.getFieldId();
			rule = GWTUtils.getAttribute(properties, fieldId + "ValueRule");
			if (rule != null) {
				criteria = parseRule(rule, field);
				if (criteria != null) {
					regreso.push(criteria);
				}
			}
		}
		if(regreso.length()==0){
			return null;
		}
		return regreso;
	}

	private JsFilterCriteria parseRule(String rule, FieldDescriptor field) {
		JsFilterCriteria regreso = null;
		if (rule == null) {
		} else if (rule.startsWith(FilterData.DIFFERENT)) {
			regreso = fromStringValue(FilterData.DIFFERENT.length(), rule,
					FilterData.DIFFERENT, field);
		} else if (rule.startsWith("=")) {
			regreso = fromStringValue(1, rule, FilterData.EQUALS, field);
		} else if (rule.startsWith(FilterData.LESS)) {
			regreso = fromStringValue(FilterData.LESS.length(), rule,
					FilterData.LESS, field);
		} else if (rule.startsWith(FilterData.GREATEREQUALS)) {
			regreso = fromStringValue(FilterData.GREATEREQUALS.length(), rule,
					FilterData.GREATEREQUALS, field);
		} else if (rule.startsWith(FilterData.GREATER)) {
			regreso = fromStringValue(FilterData.GREATER.length(), rule,
					FilterData.GREATER, field);
		} else if (rule.startsWith(FilterData.LESSEQUALS)) {
			regreso = fromStringValue(FilterData.LESSEQUALS.length(), rule,
					FilterData.LESSEQUALS, field);
		} else if (rule.startsWith(FilterData.LIKE)) {
			regreso = fromStringValue(FilterData.LIKE.length(), rule,
					FilterData.LIKE, field);
		} else if (rule.startsWith(FilterData.REGEX)) {
			regreso = fromStringValue(FilterData.REGEX.length(), rule,
					FilterData.REGEX, field);
		} else if (rule.startsWith(FilterData.ENDS)) {
			regreso = fromStringValue(FilterData.ENDS.length(), rule,
					FilterData.ENDS, field);
		} else if (rule.startsWith(FilterData.STARTS)) {
			regreso = fromStringValue(FilterData.STARTS.length(), rule,
					FilterData.STARTS, field);
		} else if (rule.startsWith(FilterData.IN)) {
			regreso = fromStringValue(FilterData.IN.length(), rule,
					FilterData.IN, field);
		} else if (rule.startsWith(FilterData.CONTAINS_EITHER)) {
			regreso = fromStringValue(FilterData.CONTAINS_EITHER.length(),
					rule, FilterData.CONTAINS_EITHER, field);
		}
		if (regreso != null) {
			regreso.pushToPath(field.getFieldId());
		}
		return regreso;
	}

	private JsFilterCriteria fromStringValue(int length, String rule,
			String operator, FieldDescriptor field) {
		JsFilterCriteria criteria = JsFilterCriteria.createObject().cast();

		criteria.setOperator(operator);
		String value = rule.substring(length, rule.length());

		this.conversionService.setAsPersistentValue(value, field,
				criteria);
		rewriteValue(field.getFieldId(), criteria);
		return criteria;
	}

	private native void rewriteValue(String fieldId, JsFilterCriteria criteria) /*-{
		if(criteria.values==null){
			criteria.values=[];
		}
		criteria.values[0]=criteria[fieldId];
	}-*/;

}
