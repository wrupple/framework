package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.chain.command.PluginConsensus;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.catalogs.server.service.CatalogTriggerInterpret;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasCatalogId;
import com.wrupple.muba.event.server.service.ActionsDictionary;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.impl.FilterDataUtils;
import org.apache.commons.chain.Command;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import java.util.*;

@Singleton
public class CatalogTriggerInterpretImpl implements CatalogTriggerInterpret {
	private static final Logger log = LogManager.getLogger(CatalogTriggerInterpretImpl.class);

	private final Provider<CatalogServiceManifest> manifestP;
	private final CatalogDeserializationService deserializer;
	private final ActionsDictionary dictionary;
    private final EntrySynthesizer synthetizationDelegate;
	private final FieldAccessStrategy access;
	private final List<Trigger> metadataTriggers;

	@Inject
	public CatalogTriggerInterpretImpl(FieldAccessStrategy access,ActionsDictionary dictionary, Provider<CatalogServiceManifest> manifestP,
									   CatalogDeserializationService deserializer, EntrySynthesizer synthetizationDelegate) {
		super();
		this.access=access;
		this.dictionary=dictionary;
		this.manifestP = manifestP;
		this.deserializer = deserializer;
        this.synthetizationDelegate = synthetizationDelegate;
        //FIXME this may be an indication that a static storage unit for triggers is necessary
        TriggerImpl[] triggg = new TriggerImpl[3];
		TriggerImpl trigger = new TriggerImpl(1,
				CatalogDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
		trigger.setFailSilence(true);
		trigger.setStopOnFail(true);
        triggg[0]= (trigger);
		trigger = new TriggerImpl(2, CatalogDescriptorUpdateTrigger.class.getSimpleName(), false,
				null, null, null);
		trigger.setFailSilence(true);
		trigger.setStopOnFail(true);
        triggg[1]=(trigger);

		trigger = new TriggerImpl(0, PluginConsensus.class.getSimpleName(), true, CatalogDescriptor.CATALOG_ID, null, null);
		trigger.setFailSilence(true);
		trigger.setStopOnFail(true);
        triggg[2]=(trigger);

        this.metadataTriggers = Arrays.asList(triggg);

	}


	@Override
    public void invokeTrigger(Map<String, String> properties, CatalogActionContext context, UserDefinedCatalogActionConstraint trigger)
            throws Exception {
		log.trace("[INVOKE] {}", trigger);
		String targetAction = trigger.getName();

		Command command = dictionary.getCommand(targetAction);

		log.trace("[TRIGGER COMMAND] {}", command);
		Object entryIdPointer = trigger.getEntry();
		Long stakeHolder = null;
		if (trigger.getRunAsStakeHolder()) {
			stakeHolder = (Long) trigger.getStakeHolder();
				//FIXME if trigger fails context is changed stake holders
			context.getRuntimeContext().getSession().setStakeHolder(stakeHolder);
		}
		Object targetCatalogId = trigger.getCatalog();
		context.getRequest().setCatalog((String) targetCatalogId);

		boolean rollback = trigger.getFailSilence();

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
                Instrospection instrospection = access.newSession((CatalogEntry) context.getRequest().getEntryValue());
                entryIdPointer = synthethizeKeyValue(entryIdPointer, context, instrospection,
						targetCatalog.getFieldDescriptor(targetCatalog.getKeyField()));
				context.getRequest().setEntry(entryIdPointer);
			}
			if (command != null) {

				if (rollback) {
					try {
						if (context.getRuntimeContext().getSession().hasPermissionsToProcessContext(context,
								manifestP.get())) {

							log.debug("[EXCECUTING TRIGGER {}] ", command);
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
					log.debug("[EXCECUTING TRIGGER {}]  ", command);
					command.execute(context);
				}

			}

		} finally {
			if (stakeHolder != null) {
				context.getRuntimeContext().getSession().releaseAuthority();
			}

		}
	}

	@Override
	public List<Trigger> getTriggersValues(CatalogActionContext context) throws Exception {
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		String catalogId = catalog.getDistinguishedName();

		if(CatalogDescriptor.CATALOG_ID.equals(catalogId)){
			return metadataTriggers;
		}

        String action = context.getRequest().getName();
        Long triggerAction ;
        if (CatalogActionRequest.CREATE_ACTION.equals(action)) {
            triggerAction=0l;
        }else if (CatalogActionRequest.WRITE_ACTION.equals(action)) {
            triggerAction=1l;
        }else  if (CatalogActionRequest.DELETE_ACTION.equals(action)) {
            triggerAction=2l;
        }else{
            return null;
        }

		List<String> allCatalogs = new ArrayList<>();
		allCatalogs.add(catalogId);

		if(!catalog.getConsolidated()){
			while(catalog.getParentValue()!=null){
				catalog = catalog.getParentValue();
				allCatalogs.add(catalog.getDistinguishedName());

			}
		}

        FilterData triggerFilter = FilterDataUtils.createSingleFieldFilter(HasCatalogId.CATALOG_FIELD,allCatalogs);
        triggerFilter.addFilter(FilterDataUtils.createSingleFieldFilter(Collections.singletonList(Trigger.ACTION_FIELD),triggerAction));

        return context.triggerRead(Trigger.CATALOG,triggerFilter);
	}




    private Object synthethizeKeyValue(Object entryIdPointer, CatalogActionContext context, Instrospection instrospection,
			FieldDescriptor field) throws Exception {
		if (entryIdPointer instanceof String) {
			return synthetizationDelegate.synthethizeFieldValue(((String) entryIdPointer).split("\\."), context);
		} else {
			return entryIdPointer;
		}
	}

	/*@Override
    public void configureContext(CatalogActionContext context, UserDefinedCatalogActionConstraint trigger, Long domain,
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

        Instrospection instrospection = access.newSession(synthesizedEntry);
        Collection<FieldDescriptor> fields = targetCatalog.getFieldsValues();
		String fieldId;
		String token;
		Object fieldValue;
        Instrospection lowInstrospection = access.newSession((CatalogEntry) context.getRequest().getEntryValue());

		for (FieldDescriptor field : fields) {
			fieldId = field.getFieldId();
			if (!CatalogEntry.ID_FIELD.equals(fieldId) && !field.isEphemeral()) {
				token = properties.get(fieldId);
				if (token != null) {
					fieldValue = synthetizationDelegate.synthethizeFieldValue(token.split(" "), context);
                    access.setPropertyValue(field, synthesizedEntry, fieldValue, instrospection);
                }
			}

		}
		return synthesizedEntry;
	}

}
