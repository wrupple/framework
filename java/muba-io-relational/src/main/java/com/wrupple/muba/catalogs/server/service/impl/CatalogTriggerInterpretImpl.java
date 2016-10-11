package com.wrupple.muba.catalogs.server.service.impl;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;

import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.CatalogKey;
import com.wrupple.muba.bootstrap.domain.TransactionHistory;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.CatalogTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.shared.services.CatalogEvaluationService;

@Singleton
public class CatalogTriggerInterpretImpl implements CatalogTriggerInterpret {
	private static final Logger log = LoggerFactory.getLogger(CatalogTriggerInterpretImpl.class);

	private final CatalogServiceManifest manifest;
	private final CatalogEvaluationDelegate accessor;
	private final CatalogDeserializationService deserializer;
	private final CatalogEvaluationService evaluator;

	@Inject
	public CatalogTriggerInterpretImpl(CatalogServiceManifest manifest, CatalogEvaluationDelegate accessor, CatalogDeserializationService deserializer,CatalogEvaluationService evaluator) {
		super();
		this.manifest = manifest;
		this.accessor = accessor;
		this.evaluator=evaluator;
		this.deserializer = deserializer;
	}

	@Override
	public void invokeTrigger(CatalogDescriptor catalog, CatalogKey entry, CatalogKey old, Map<String, String> properties, CatalogActionContext original,
			CatalogTrigger trigger) throws Exception {
		log.trace("[INVOKE] {}",trigger);
		String targetAction = trigger.getHandler();

		Command command = original.getCatalogManager().getCommand(targetAction);

		log.trace("[TRIGGER COMMAND] {}",command);
		String entryIdPointer = trigger.getEntry();
		CatalogActionContext context = original.getCatalogManager().spawn(original);
		Long stakeHolder = null;
		if (trigger.isRunAsStakeHolder()) {
			stakeHolder = (Long) trigger.getStakeHolder();

			context.getExcecutionContext().getSession().setStakeHolder(stakeHolder);
		}
		String targetCatalogId = trigger.getCatalog();
		context.setCatalog(targetCatalogId);

		
		String expression = trigger.getExpression();
		boolean rollback = trigger.isFailSilence();

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
			context.setOldValues(original.getOldValues());
			context.setResults(original.getResults());
			if (command != null) {

				if (rollback) {
					try {
						if (context.getExcecutionContext().getSession().hasPermissionsToProcessContext(context, manifest)) {
							
							
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
							context.getExcecutionContext().addWarning("Trigger failed silently : " + e.getLocalizedMessage());
						}
					}
				} else {

				}

			}

			if (expression != null) {
				evaluator.evaluate(expression, (CatalogEntry) entry, (CatalogEntry) old, context);
			}
		} finally {
			if (stakeHolder != null) {
				original.getExcecutionContext().getSession().releaseAuthority();
			}

		}
	}

	@Override
	public void configureContext(CatalogActionContext context, CatalogTrigger trigger, Long domain, TransactionHistory transaction) throws Exception {


		String targetAction = trigger.getHandler();
		String entryIdPointer = trigger.getEntry();
		String targetCatalogId = trigger.getCatalog();
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
