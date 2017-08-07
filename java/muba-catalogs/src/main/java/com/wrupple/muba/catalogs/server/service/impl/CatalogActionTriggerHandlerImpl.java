package com.wrupple.muba.catalogs.server.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionTrigger;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CatalogActionTriggerHandler;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;

@Singleton
public class CatalogActionTriggerHandlerImpl implements CatalogActionTriggerHandler {

	private static final Logger log = LoggerFactory.getLogger(CatalogActionTriggerHandlerImpl.class);

	private CatalogTriggerInterpret interpret;

	@Inject
	public CatalogActionTriggerHandlerImpl(CatalogTriggerInterpret interpret) {
		super();
		this.interpret = interpret;
	}

	@Override
	public boolean execute(Context c) throws Exception {

		return extecute((CatalogActionContext) c, true);
	}

	private boolean extecute(CatalogActionContext context, boolean advise) throws Exception {
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		List<CatalogActionTrigger> triggers = catalog.getTriggersValues();
		if (triggers == null || triggers.isEmpty()) {
			log.trace("no triggers to process");
		} else {
			int action;
			boolean r;
			CatalogActionTrigger trigger;
			for (int i = 0; i < triggers.size(); i++) {
				trigger = triggers.get(i);
				action = trigger.getAction();
				r = CONTINUE_PROCESSING;
				switch (action) {
				case 0:// Create
					if (CatalogActionRequest.CREATE_ACTION.equals(context.getName())) {
						r = excecuteTrigger(trigger, context, advise);
					}
					break;
				case 1:// Update
					if (CatalogActionRequest.WRITE_ACTION.equals(context.getName())) {
						r = excecuteTrigger(trigger, context, advise);
					}
					break;
				case 2:// Delete
					if (CatalogActionRequest.DELETE_ACTION.equals(context.getName())) {
						r = excecuteTrigger(trigger, context, advise);
					}
					break;
				default:
					break;
				}
				if (PROCESSING_COMPLETE == r) {
					return CONTINUE_PROCESSING;
				}
			}
		}

		return CONTINUE_PROCESSING;
	}

	@Override
	public boolean postprocess(Context c, Exception exception) {

		CatalogActionContext context = (CatalogActionContext) c;
		// AFTER
		try {
			return extecute(context, false);
		} catch (Exception e) {
			log.error("[FATAL TRIGGER ERROR]", e);
			throw new RuntimeException(e);
		}
	}

	private boolean excecuteTrigger(CatalogActionTrigger trigger, CatalogActionContext context, boolean before)
			throws Exception {

		log.trace("[PROCESS trigger] ");

		List<String> rawProperties = trigger.getProperties();

		Map<String, String> properties = parseProperties(rawProperties, trigger, context);

		if (before == trigger.isAdvice()) {
			if (!trigger.isFailSilence() || trigger.isStopOnFail()) {
				log.trace("invoking trigger {}", trigger);
				interpret.invokeTrigger( properties, (CatalogActionContext) context, trigger);
			} else {
				try {
					log.trace("invoking trigger");
					interpret.invokeTrigger( properties, (CatalogActionContext) context, trigger);
				} catch (Exception e) {

					log.error("[TRIGGER FAIL]", e);
					context.getRuntimeContext().addWarning("Trigger failed " + trigger.getName());

					if (trigger.isStopOnFail()) {
						return PROCESSING_COMPLETE;
					}
				}
			}
		}

		log.trace("[PROCESS trigger DONE] ");
		return CONTINUE_PROCESSING;
	}

	private static Map<String, String> parseProperties(List<String> rawProperties, CatalogActionTrigger trigger,
			CatalogActionContext context) {
		Map<String, String> regreso = trigger.getParsedProperties(rawProperties, context.getNamespaceContext());
		if (regreso == null) {
			regreso = parseProperties(rawProperties);
		}
		trigger.setParsedProperties(regreso, rawProperties, context.getNamespaceContext());
		return regreso;
	}

	public static Map<String, String> parseProperties(List<String> rawProperties) {
		if (rawProperties == null||rawProperties.isEmpty()) {
			return Collections.EMPTY_MAP;
		} else {
			HashMap<String, String> regreso = new HashMap<String, String>(rawProperties.size());

			String value;
			String element;
			// String[] tokens;
			int split;
			for (String property : rawProperties) {
				split = property.indexOf('=');
				if (split > 0) {
					element = property.substring(0, split);
					value = property.substring(split + 1, property.length());
					// tokens = value.split("\\.");
					regreso.put(element, value);
				}
			}

			return regreso;
		}
	}

}
