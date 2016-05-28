package com.wrupple.muba.catalogs.server.service.impl;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;

import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.CatalogKey;
import com.wrupple.vegetate.domain.CatalogTrigger;

@Singleton
public class CatalogTriggerInterpretImpl implements CatalogTriggerInterpret {
	private static final Logger log = LoggerFactory.getLogger(CatalogTriggerInterpretImpl.class);

	private final CatalogServiceManifest manifest;
	private final CatalogPropertyAccesor accessor;
	private final CatalogDeserializationService deserializer;

	@Inject
	public CatalogTriggerInterpretImpl(CatalogServiceManifest manifest, CatalogPropertyAccesor accessor, CatalogDeserializationService deserializer) {
		super();
		this.manifest = manifest;
		this.accessor = accessor;
		this.deserializer = deserializer;
	}

	@Override
	public void invokeTrigger(CatalogDescriptor catalog, CatalogKey entry, CatalogKey old, Map<String, String> properties, CatalogExcecutionContext original,
			CatalogTrigger trigger) throws Exception {
		log.trace("[INVOKE] {}",trigger);
		String targetAction = trigger.getHandler();

		Command command = original.getRequest().getStorageManager().getCommand(targetAction);

		log.trace("[TRIGGER COMMAND] {}",command);
		String entryIdPointer = trigger.getCatalogEntryId();
		CatalogExcecutionContext context = original.getRequest().getStorageManager().spawn(original);
		Long stakeHolder = null;
		if (trigger.isRunAsStakeHolder()) {
			stakeHolder = (Long) trigger.getStakeHolder();

			context.getRequest().getSession().setStakeHolder(stakeHolder);
		}
		String targetCatalogId = trigger.getCatalogId();
		context.setCatalog(targetCatalogId);

		
		String expression = trigger.getExpression();
		boolean rollback = trigger.isRollbackOnFail();

		String rawSeed = trigger.getSeed();
		try {
			CatalogDescriptor targetCatalog = context.getCatalogDescriptor();
			CatalogEntry seed = null;
			CatalogEntry synthesizedEntry = null;
			if (rawSeed != null) {
				seed = deserializer.deserialize(rawSeed, targetCatalog, context);
			}
			if (seed != null) {
				synthesizedEntry = accessor.synthethize(catalog, seed, targetCatalog, (CatalogEntry) entry, (CatalogEntry) old, context, properties);
			}
			
			if (entryIdPointer != null) {

				Session session = accessor.newSession((CatalogEntry) entry);
				entryIdPointer = (String) accessor.synthethizeFieldValue(entryIdPointer, (CatalogEntry) entry, (CatalogEntry) old, catalog, context, session, null);
				if (entryIdPointer == null) {
					throw new IllegalArgumentException("Malformed trigger properties");
				} else {
				}
			}
			context.set(context.getDomain(), targetCatalogId, targetAction, entryIdPointer, synthesizedEntry, null);
			context.put(CatalogActionTrigger.OLD_ENTRY_CONTEXT_KEY, old);
			context.put(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY, entry);
			context.put(CatalogActionTrigger.CATALOG_CONTEXT_KEY, catalog);
			if (command != null) {

				if (rollback) {
					try {
						if (context.getRequest().getSession().hasPermissionsToProcessContext(context, manifest)) {
							
							
							log.debug("[EXCECUTING TRIGGER {}] CONTEXT= {} ",command,context);
							command.execute(context);
							Set<ConstraintViolation<?>> aggregate = context.getConstraintViolations();
							if ( aggregate != null && !aggregate.isEmpty()) {
								throw new IllegalArgumentException("Constraint validations encountered");
							}
						} else {
							throw new SecurityException("Not enough permissions to process request");
						}
					} catch (Exception e) {
						if (rollback) {
							throw e;
						} else {
							context.getRequest().addWarning("Trigger failed silently : " + e.getLocalizedMessage());
						}
					}
				} else {

				}

			}

			if (expression != null) {
				accessor.evaluate(expression, (CatalogEntry) entry, (CatalogEntry) old, context);
			}
		} finally {
			if (stakeHolder != null) {
				original.getRequest().getSession().releaseAuthority();
			}

		}
	}

	@Override
	public void configureContext(CatalogExcecutionContext context, CatalogTrigger trigger, Long domain, CatalogUserTransaction transaction) throws Exception {


		String targetAction = trigger.getHandler();
		String entryIdPointer = trigger.getCatalogEntryId();
		String targetCatalogId = trigger.getCatalogId();
		context.setCatalog(targetCatalogId);
		String rawSeed = trigger.getSeed();
		CatalogDescriptor targetCatalog =context.getCatalogDescriptor();
		CatalogEntry seed = null;
		if (rawSeed != null) {
			seed = deserializer.deserialize(rawSeed, targetCatalog, context);
			context.setEntryValue(seed);
		}
		
		context.set(domain, targetCatalogId, targetAction,entryIdPointer, seed, null);
		log.trace("[CONFIGURE TRIGGER {} ] {} ",trigger,context);

	}

}
