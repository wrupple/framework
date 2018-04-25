package com.wrupple.muba.catalogs.server.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.wrupple.muba.catalogs.domain.Trigger;
import org.apache.commons.chain.Command;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.google.inject.Singleton;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;

@Singleton
public class CatalogActionTriggerHandlerImpl  {

	protected static final Logger log = LogManager.getLogger(CatalogActionTriggerHandlerImpl.class);

	private CatalogTriggerInterpret interpret;

	@Inject
	public CatalogActionTriggerHandlerImpl(CatalogTriggerInterpret interpret) {
		super();
		this.interpret = interpret;
	}


	protected boolean extecute(CatalogActionContext context, boolean advise) throws Exception {
		List<Trigger> triggers = interpret.getTriggersValues(context);
		if (triggers == null || triggers.isEmpty()) {
			log.trace("no triggers to process");
		} else {
			boolean r;
			Trigger trigger;
			for (int i = 0; i < triggers.size(); i++) {
				trigger = triggers.get(i);
				if(trigger.isAdvice()==advise){

					r = excecuteTrigger(trigger, context);
					if (Command.PROCESSING_COMPLETE == r) {
						log.warn("[Trigger stops trigger chain]");
						return Command.CONTINUE_PROCESSING;
					}
				}
			}
		}

		return Command.CONTINUE_PROCESSING;
	}


	private boolean excecuteTrigger(Trigger trigger, CatalogActionContext context)
			throws Exception {

		log.trace("[PROCESS trigger] ");

		List<String> rawProperties = trigger.getProperties();

		Map<String, String> properties = parseProperties(rawProperties, trigger, context);


			if (!trigger.getFailSilence() || trigger.getStopOnFail()) {
				log.trace("invoking trigger {}", trigger);
				interpret.invokeTrigger( properties, (CatalogActionContext) context, trigger);
			} else {
				try {
					log.trace("invoking trigger");
					interpret.invokeTrigger( properties, (CatalogActionContext) context, trigger);
				} catch (Exception e) {

					log.error("[TRIGGER FAIL]", e);
					context.getRuntimeContext().addWarning("Trigger failed " + trigger.getName());

					if (trigger.getStopOnFail()) {
						return Command.PROCESSING_COMPLETE;
					}
				}
			}


		log.trace("[PROCESS trigger DONE] ");
		return Command.CONTINUE_PROCESSING;
	}

	private static Map<String, String> parseProperties(List<String> rawProperties, Trigger trigger,
			CatalogActionContext context) {
		Map<String, String> regreso = getParsedProperties(rawProperties, context.getNamespaceContext(),trigger);
		if (regreso == null) {
			regreso = parseProperties(rawProperties);
		}
		setParsedProperties(regreso, rawProperties, context.getNamespaceContext(),trigger);
		return regreso;
	}

	 static Map<String, String> getParsedProperties(List<String> rawProperties, Map context, Trigger trigger) {
		return (Map<String, String>) context.get(trigger.getCatalogType()+trigger.getId());
	}

	static void setParsedProperties(Map<String, String> parsed, List<String> rawProperties, Map context, Trigger trigger) {
		context.put(trigger.getCatalogType()+trigger.getId(), parsed);
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
