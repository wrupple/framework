package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import org.apache.commons.chain.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import java.util.*;

@Singleton
public class CatalogTriggerInterpretImpl implements CatalogTriggerInterpret {
	private static final Logger log = LoggerFactory.getLogger(CatalogTriggerInterpretImpl.class);

	private final Provider<CatalogServiceManifest> manifestP;
	private final CatalogDeserializationService deserializer;

	@Inject
	public CatalogTriggerInterpretImpl(Provider<CatalogServiceManifest> manifestP,
                                       CatalogDeserializationService deserializer) {
		super();
		this.manifestP = manifestP;
		this.deserializer = deserializer;
    }


	@Override
	public void invokeTrigger(Map<String, String> properties, CatalogActionContext context, UserDefinedCatalogJob trigger)
			throws Exception {
		log.trace("[INVOKE] {}", trigger);
		String targetAction = trigger.getSentence();

		Command command = context.getCatalogManager().getCommand(targetAction);

		log.trace("[TRIGGER COMMAND] {}", command);
		Object entryIdPointer = trigger.getEntry();
		Long stakeHolder = null;
		if (trigger.isRunAsStakeHolder()) {
			stakeHolder = (Long) trigger.getStakeHolder();
				//FIXME if trigger fails context is changed stake holders
			context.getRuntimeContext().getSession().setStakeHolder(stakeHolder);
		}
		String targetCatalogId = trigger.getCatalog();
		context.getRequest().setCatalog(targetCatalogId);

		boolean rollback = trigger.isFailSilence();

		String rawSeed = trigger.getSeed();
		try {
			CatalogDescriptor targetCatalog = context.getCatalogDescriptor();
			CatalogEntry seed = null;
			if (rawSeed != null) {
				seed = deserializer.deserialize(rawSeed, targetCatalog, context);
				if (seed != null) {
					CatalogEntry synthesizedEntry = synthethize(seed, targetCatalog, context, properties);
					context.getRequest().setEntryValue(synthesizedEntry);
				}
			}

			if (entryIdPointer != null) {
                Instrospection instrospection = context.getCatalogManager().access().newSession((CatalogEntry) context.getRequest().getEntryValue());
                entryIdPointer = synthethizeKeyValue(entryIdPointer, context, instrospection,
						targetCatalog.getFieldDescriptor(targetCatalog.getKeyField()));
				context.getRequest().setEntry(entryIdPointer);
			}
			if (command != null) {

				if (rollback) {
					try {
						if (context.getRuntimeContext().getSession().hasPermissionsToProcessContext(context,
								manifestP.get())) {

							log.debug("[EXCECUTING TRIGGER {}] CONTEXT= {} ", command, context);
							command.execute(context);
							Set<ConstraintViolation<?>> aggregate = context.getRuntimeContext().getConstraintViolations();
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
							context.getRuntimeContext()
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
				context.getRuntimeContext().getSession().releaseAuthority();
			}

		}
	}

	private Object synthethizeKeyValue(Object entryIdPointer, CatalogActionContext context, Instrospection instrospection,
			FieldDescriptor field) throws Exception {
		if (entryIdPointer instanceof String) {
			return context.getCatalogManager().synthethizeFieldValue(((String) entryIdPointer).split("\\."), context);
		} else {
			return entryIdPointer;
		}
	}

	/*@Override
	public void configureContext(CatalogActionContext context, UserDefinedCatalogJob trigger, Long domain,
			TransactionHistory transaction) throws Exception {

		String targetAction = trigger.getSentence();
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
		context.setName(targetAction);
		context.setEntry(entryIdPointer);
		context.setEntryValue(seed);
		log.trace("[CONFIGURE TRIGGER {} ] {} ", trigger, context);

	}*/

	private CatalogEntry synthethize(CatalogEntry synthesizedEntry, CatalogDescriptor targetCatalog,
			CatalogActionContext context, Map<String, String> properties) throws Exception {

		context.getRequest().setCatalog(targetCatalog.getDistinguishedName());

        Instrospection instrospection = context.getCatalogManager().access().newSession(synthesizedEntry);
        Collection<FieldDescriptor> fields = targetCatalog.getFieldsValues();
		String fieldId;
		String token;
		Object fieldValue;
        Instrospection lowInstrospection = context.getCatalogManager().access().newSession((CatalogEntry) context.getRequest().getEntryValue());

		for (FieldDescriptor field : fields) {
			fieldId = field.getFieldId();
			if (!CatalogEntry.ID_FIELD.equals(fieldId) && !field.isEphemeral()) {
				token = properties.get(fieldId);
				if (token != null) {
					fieldValue = context.getCatalogManager().synthethizeFieldValue(token.split(" "), context);
                    context.getCatalogManager().access().setPropertyValue(field, synthesizedEntry, fieldValue, instrospection);
                }
			}

		}
		return synthesizedEntry;
	}

}
