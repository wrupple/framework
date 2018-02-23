package com.wrupple.muba.worker.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.impl.FilterCriteriaImpl;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.impl.CatalogActionTriggerHandlerImpl;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.FilterData;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.domain.reserved.HasFieldId;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.worker.server.chain.command.AbstractComparationCommand;
import com.wrupple.muba.worker.server.chain.command.ValueChangeListener;
import com.wrupple.muba.worker.server.domain.ValueChangeTrigger;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Singleton
public class ValueChangeListenerImpl extends AbstractComparationCommand implements ValueChangeListener {

	private final Provider<CatalogTriggerInterpret> interpret;

	@Inject
	public ValueChangeListenerImpl(Provider<CatalogTriggerInterpret> interpret, FieldAccessStrategy accessStrategy) {
		super(accessStrategy);
		this.interpret = interpret;
	}

	@Override
	protected void compare(String codedFinalValue, String codedInitialValue, Object initialValue, Object finalValue, FieldDescriptor field,CatalogDescriptor catalog,
			CatalogActionContext context) throws Exception {

		if ((initialValue == null && finalValue != null)
				|| (initialValue != null && !initialValue.equals(finalValue))) {

			// TODO this means mny unecesary  searches when no triggers are provided
			List<ValueChangeTrigger> changeTriggers = (List<ValueChangeTrigger>) context
					.get(ValueChangeListener.CONTEXT_TRIGGERS_KEY);
			if (changeTriggers == null) {
				CatalogActionRequestImpl spawned = new CatalogActionRequestImpl();
				FilterData filterData = FilterDataUtils.createSingleFieldFilter(HasCatalogId.CATALOG_FIELD,
						(String)context.getRequest().getCatalog());
				filterData.setConstrained(false);
				filterData.addFilter(new FilterCriteriaImpl(HasFieldId.FIELD, field.getFieldId()));
				spawned.setFilter(filterData);

				changeTriggers = context.getRuntimeContext().getServiceBus().fireEvent(spawned,context.getRuntimeContext(),null);
				context.put(CONTEXT_TRIGGERS_KEY, changeTriggers);
			}
			if (changeTriggers != null) {
				changeTriggers = filterByValue(codedInitialValue, codedFinalValue, changeTriggers);
				if (changeTriggers != null) {

					for (ValueChangeTrigger trigger : changeTriggers) {
						// TODO this mean unecesary instances
						Map<String, String> properties = CatalogActionTriggerHandlerImpl
								.parseProperties(trigger.getProperties());
						interpret.get().invokeTrigger( properties, context, trigger);
					}

				}
			}
		}

	}

	private List<ValueChangeTrigger> filterByValue(String codedInitialValue, String codedFinalValue,
			List<ValueChangeTrigger> changeTriggers) {
		int encoding;
		List<ValueChangeTrigger> results = null;
		for (ValueChangeTrigger trigger : changeTriggers) {
			encoding = trigger.getEncoding();
			if (trigger.getFinalValue() == null && trigger.getInitialValue() == null) {
				// not value dependent
			} else {
				if (results == null) {
					results = new ArrayList<ValueChangeTrigger>(changeTriggers.size());
				}
				// depends on value
				if (encoding == 1) {
					// regex

					if (checkValueRegex(codedInitialValue, trigger.getInitialValue())
							&& checkValueRegex(codedInitialValue, trigger.getInitialValue())) {
						results.add(trigger);
					}

				} else {
					// plain
					if (checkValue(codedInitialValue, trigger.getInitialValue())
							&& checkValue(codedInitialValue, trigger.getInitialValue())) {
						results.add(trigger);
					}
				}
			}
		}
		if (results == null) {
			results = changeTriggers;
		}
		return results;
	}

	private boolean checkValue(String value, String filter) {
		if (value == null) {
			return filter != null;
		} else if (filter != null && filter.equals(WILDCARD)) {
			return true;
		} else {
			return value.equals(filter);
		}
	}

	private boolean checkValueRegex(String value, String filter) {
		if (value == null) {
			return filter != null;
		} else {
			if (filter == null) {
				return false;
			} else {
				return Pattern.matches(filter, value);
			}
		}
	}

}
