package com.wrupple.muba.catalogs.server.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.wrupple.muba.catalogs.domain.CatalogEventListener;
import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;

@Singleton
public class CatalogActionTriggerHandlerImpl  {

	protected static final Logger log = LoggerFactory.getLogger(CatalogActionTriggerHandlerImpl.class);

	private CatalogTriggerInterpret interpret;

	@Inject
	public CatalogActionTriggerHandlerImpl(CatalogTriggerInterpret interpret) {
		super();
		this.interpret = interpret;
	}


	protected boolean extecute(CatalogActionContext context, boolean advise) throws Exception {
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		List<CatalogEventListener> triggers = interpret.getTriggersValues(context,advise);
		if (triggers == null || triggers.isEmpty()) {
			log.trace("no triggers to process");
		} else {
			int action;
			boolean r;
			CatalogEventListener trigger;
			for (int i = 0; i < triggers.size(); i++) {
				trigger = triggers.get(i);
				action = trigger.getAction();
				r = Command.CONTINUE_PROCESSING;
				switch (action) {
				case 0:// Create
					if (CatalogActionRequest.CREATE_ACTION.equals(context.getRequest().getName())) {
						r = excecuteTrigger(trigger, context);
					}
					break;
				case 1:// Update
					if (CatalogActionRequest.WRITE_ACTION.equals(context.getRequest().getName())) {
						r = excecuteTrigger(trigger, context);
					}
					break;
				case 2:// Delete
					if (CatalogActionRequest.DELETE_ACTION.equals(context.getRequest().getName())) {
						r = excecuteTrigger(trigger, context);
					}
					break;
				default:
					break;
				}
				if (Command.PROCESSING_COMPLETE == r) {
					return Command.CONTINUE_PROCESSING;
				}
			}
		}

		return Command.CONTINUE_PROCESSING;
	}


	private boolean excecuteTrigger(CatalogEventListener trigger, CatalogActionContext context)
			throws Exception {

		log.trace("[PROCESS trigger] ");

		List<String> rawProperties = trigger.getProperties();

		Map<String, String> properties = parseProperties(rawProperties, trigger, context);


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
						return Command.PROCESSING_COMPLETE;
					}
				}
			}


		log.trace("[PROCESS trigger DONE] ");
		return Command.CONTINUE_PROCESSING;
	}

	private static Map<String, String> parseProperties(List<String> rawProperties, CatalogEventListener trigger,
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
