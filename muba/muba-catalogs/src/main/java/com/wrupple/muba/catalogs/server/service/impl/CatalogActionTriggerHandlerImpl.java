package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.CatalogKey;
import com.wrupple.vegetate.domain.CatalogTrigger;

public class CatalogActionTriggerHandlerImpl implements CatalogActionTriggerHandler {
	
	private static final Logger log = LoggerFactory.getLogger(CatalogActionTriggerHandlerImpl.class);


	public static class Aggregate implements CatalogActionTriggerHandler.Trigger {

		List<CatalogActionTriggerHandler.Trigger> handlers = new ArrayList<CatalogActionTriggerHandler.Trigger>(1);

		public void add(CatalogActionTriggerHandler.Trigger trigg) {
			handlers.add(trigg);
		}

		public boolean hasHandlers() {
			return !handlers.isEmpty();
		}

		@Override
		public void before(CatalogDescriptor catalog, CatalogKey old, CatalogKey entry, CatalogExcecutionContext context) throws Exception {
			for (CatalogActionTriggerHandler.Trigger t : handlers) {
				if(t.getTrigger().isStopOnFail()){
					t.before(catalog, old, entry, context);
				}else{
					try{
						t.before(catalog, old, entry, context);
					}catch(Exception e){
						log.error("[BEFORE TRIGGER FAIL]",e);
						context.getRequest().addWarning("Trigger failed "+t.getTrigger().getName());
					}
				}
				
			}
		}

		@Override
		public void after(CatalogDescriptor catalog, CatalogKey e, CatalogExcecutionContext context) throws Exception {
			for (CatalogActionTriggerHandler.Trigger t : handlers) {
				if(t.getTrigger().isStopOnFail()){
					t.after(catalog, e, context);
				}else{
					try{
						t.after(catalog, e, context);
					}catch(Exception e1){
						log.error("[AFTER TRIGGER FAIL]",e1);
						context.getRequest().addWarning("Trigger failed "+t.getTrigger().getName());
					}
				}
			}
		}

		@Override
		public CatalogTrigger getTrigger() {
			throw new IllegalStateException();
		}

	}

	public static class Trigg implements CatalogActionTriggerHandler.Trigger {

		private final boolean before;
		private final Map<String, String> properties;
		private final CatalogTriggerInterpret interpret;
		private final CatalogTrigger trigger;

		public Trigg(CatalogTrigger trigger, boolean before, Map<String, String> properties, CatalogTriggerInterpret interpret) {
			super();
			this.trigger = trigger;
			this.before = before;
			this.properties = properties;
			this.interpret = interpret;
		}

		@Override
		public void before(CatalogDescriptor catalog, CatalogKey old, CatalogKey entry, CatalogExcecutionContext context) throws Exception {
			if (before) {
				// CatalogDescriptor catalog, CatalogEntry entry, CatalogEntry
				// old,Map<String, String> properties, CatalogExcecutionContext
				// original, CatalogTrigger matchingRegistry, List<String>
				// commandChain
				interpret.invokeTrigger(catalog, entry, old, properties, (CatalogExcecutionContext) context, trigger);
			}
		}

		@Override
		public void after(CatalogDescriptor catalog, CatalogKey entry, CatalogExcecutionContext context) throws Exception {
			if (!before) {
				interpret.invokeTrigger(catalog, entry, entry, properties, (CatalogExcecutionContext) context, trigger);

			}
		}

		@Override
		public CatalogTrigger getTrigger() {
			return trigger;
		}

	}

	private Aggregate updateHandlers;
	private Aggregate createHandlers;
	private Aggregate deleteHandlers;
	private CatalogTriggerInterpret interpret;

	@Inject
	public CatalogActionTriggerHandlerImpl(CatalogTriggerInterpret interpret) {
		super();
		this.interpret = interpret;
	}

	@Override
	public Trigger getUpdateHandler() {
		return updateHandlers;
	}

	@Override
	public Trigger getCreateHandler() {
		return createHandlers;
	}

	@Override
	public Trigger getDeleteHandler() {
		return deleteHandlers;
	}

	@Override
	public void addUpdate(Trigger trigger) {
		if (updateHandlers == null) {
			updateHandlers = new Aggregate();
		}
		updateHandlers.add(trigger);
		log.trace("[PROCESSED] {}",trigger);
	}

	@Override
	public void addCreate(Trigger trigger) {
		if (createHandlers == null) {
			createHandlers = new Aggregate();
		}
		createHandlers.add(trigger);
		log.trace("[PROCESSED] {}",trigger);
	}

	@Override
	public void addDelete(Trigger trigger) {
		if (deleteHandlers == null) {
			deleteHandlers = new Aggregate();
		}
		deleteHandlers.add(trigger);
		log.trace("[PROCESSED] {}",trigger);
	}

	public boolean isEmpty() {
		return (updateHandlers != null && updateHandlers.hasHandlers()) || (createHandlers != null && createHandlers.hasHandlers())
				|| (deleteHandlers != null && deleteHandlers.hasHandlers());
	}

	public void process(CatalogActionTrigger trigger) {
		log.trace("[PROCESS] {}",trigger);
		int action = trigger.getAction();
		List<String> rawProperties = trigger.getProperties();

		Map<String, String> properties = parseProperties(rawProperties);

		Trigg t = new Trigg(trigger, trigger.isBefore(), properties, interpret);

		switch (action) {
		case 0:// Create
			addCreate(t);
			break;
		case 1:// Update
			addUpdate(t);
			break;
		case 2:// Delete
			addDelete(t);
			break;
		case 3 :
			//system event
			break;
		}
		System.err.println("[trigger ] building done");
	}

	public static Map<String, String> parseProperties(List<String> rawProperties) {
		if (rawProperties == null) {
			return Collections.EMPTY_MAP;
		} else {
			Map<String, String> regreso = new HashMap<String, String>(rawProperties.size());
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
