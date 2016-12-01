package com.wrupple.muba.catalogs.server.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;

import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.TransactionHistory;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.domain.CatalogTrigger;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate;
import com.wrupple.muba.catalogs.server.service.CatalogEvaluationDelegate.Session;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;

@Singleton
public class CatalogTriggerInterpretImpl implements CatalogTriggerInterpret {
	private static final Logger log = LoggerFactory.getLogger(CatalogTriggerInterpretImpl.class);

	private final CatalogServiceManifest manifest;
	private final CatalogEvaluationDelegate accessor;
	private final CatalogDeserializationService deserializer;

	@Inject
	public CatalogTriggerInterpretImpl(CatalogServiceManifest manifest, CatalogEvaluationDelegate accessor,
			CatalogDeserializationService deserializer) {
		super();
		this.manifest = manifest;
		this.accessor = accessor;
		this.deserializer = deserializer;
	}

	@Override
	public void invokeTrigger(Map<String, String> properties, CatalogActionContext context, CatalogTrigger trigger)
			throws Exception {
		log.trace("[INVOKE] {}", trigger);
		String targetAction = trigger.getHandler();

		Command command = context.getCatalogManager().getCommand(targetAction);

		log.trace("[TRIGGER COMMAND] {}", command);
		Object entryIdPointer = trigger.getEntry();
		Long stakeHolder = null;
		if (trigger.isRunAsStakeHolder()) {
			stakeHolder = (Long) trigger.getStakeHolder();

			context.getExcecutionContext().getSession().setStakeHolder(stakeHolder);
		}
		String targetCatalogId = trigger.getCatalog();
		context.setCatalog(targetCatalogId);

		boolean rollback = trigger.isFailSilence();

		String rawSeed = trigger.getSeed();
		try {
			CatalogDescriptor targetCatalog = context.getCatalogDescriptor();
			CatalogEntry seed = null;
			if (rawSeed != null) {
				seed = deserializer.deserialize(rawSeed, targetCatalog, context);
				if (seed != null) {
					CatalogEntry synthesizedEntry = synthethize(seed, targetCatalog, context, properties);
					context.setEntryValue(synthesizedEntry);
				}
			}

			if (entryIdPointer != null) {
				Session session = accessor.newSession((CatalogEntry) context.getEntryValue());
				entryIdPointer = synthethizeKeyValue(entryIdPointer, context, session,
						targetCatalog.getFieldDescriptor(targetCatalog.getKeyField()));
				context.setEntry(entryIdPointer);
			}
			if (command != null) {

				if (rollback) {
					try {
						if (context.getExcecutionContext().getSession().hasPermissionsToProcessContext(context,
								manifest)) {

							log.debug("[EXCECUTING TRIGGER {}] CONTEXT= {} ", command, context);
							command.execute(context);
							Set<ConstraintViolation<?>> aggregate = context.getExcecutionContext().getConstraintViolations();
							if (aggregate != null && !aggregate.isEmpty()) {
								log.error("Constraint validations encountered");
								throw new IllegalArgumentException("Constraint validations encountered");
							}
						} else {
							throw new SecurityException("Not enough permissions to process request");
						}
					} catch (Exception e) {
						if (rollback) {
							throw e;
						} else {
							context.getExcecutionContext()
									.addWarning("Trigger failed silently : " + e.getLocalizedMessage());
						}
					}
				} else {
					log.debug("[EXCECUTING TRIGGER {}] CONTEXT= {} ", command, context);
					command.execute(context);
				}

			}

		} finally {
			if (stakeHolder != null) {
				context.getExcecutionContext().getSession().releaseAuthority();
			}

		}
	}

	private Object synthethizeKeyValue(Object entryIdPointer, CatalogActionContext context, Session session,
			FieldDescriptor field) throws Exception {
		if (entryIdPointer instanceof String) {
			return accessor.synthethizeFieldValue((String) entryIdPointer, context);
		} else {
			return entryIdPointer;
		}
	}

	@Override
	public void configureContext(CatalogActionContext context, CatalogTrigger trigger, Long domain,
			TransactionHistory transaction) throws Exception {

		String targetAction = trigger.getHandler();
		String entryIdPointer = trigger.getEntry();
		String targetCatalogId = trigger.getCatalog();
		context.setCatalog(targetCatalogId);
		String rawSeed = trigger.getSeed();
		CatalogDescriptor targetCatalog = context.getCatalogDescriptor();
		CatalogEntry seed = null;
		if (rawSeed != null) {
			seed = deserializer.deserialize(rawSeed, targetCatalog, context);
			context.setEntryValue(seed);
		}

		context.setDomain(domain);
		context.setCatalog(targetCatalogId);
		context.setAction(targetAction);
		context.setEntry(entryIdPointer);
		context.setEntryValue(seed);
		log.trace("[CONFIGURE TRIGGER {} ] {} ", trigger, context);

	}

	private CatalogEntry synthethize(CatalogEntry synthesizedEntry, CatalogDescriptor targetCatalog,
			CatalogActionContext context, Map<String, String> properties) throws Exception {

		context.setCatalog(targetCatalog.getDistinguishedName());

		Session session = accessor.newSession(synthesizedEntry);
		Collection<FieldDescriptor> fields = targetCatalog.getFieldsValues();
		String fieldId;
		String token;
		Object fieldValue;
		Session lowSession = accessor.newSession((CatalogEntry) context.getEntryValue());

		for (FieldDescriptor field : fields) {
			fieldId = field.getFieldId();
			if (!CatalogEntry.ID_FIELD.equals(fieldId) && !field.isEphemeral()) {
				token = properties.get(fieldId);
				if (token != null) {
					fieldValue = accessor.synthethizeFieldValue(token, context);
					accessor.setPropertyValue(targetCatalog, field, synthesizedEntry, fieldValue, session);
				}
			}

		}
		return synthesizedEntry;
	}

}
