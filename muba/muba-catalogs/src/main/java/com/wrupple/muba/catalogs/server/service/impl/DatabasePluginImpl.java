package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.domain.CatalogActionTriggerImpl;
import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.domain.CatalogIdentificationImpl;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.domain.WebEventTrigger;
import com.wrupple.muba.catalogs.domain.WruppleLocalizedString;
import com.wrupple.muba.catalogs.server.chain.FieldDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.chain.command.CatalogDescriptorUpdateTrigger;
import com.wrupple.muba.catalogs.server.chain.command.EntryDeleteTrigger;
import com.wrupple.muba.catalogs.server.chain.command.GarbageCollection;
import com.wrupple.muba.catalogs.server.chain.command.TrashDeleteTrigger;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptionCacheManager;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.RestoreTrash;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldConstraint;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.server.chain.CatalogManager;
import com.wrupple.vegetate.server.chain.command.I18nProcessing.DistributiedLocalizedEntry;
import com.wrupple.vegetate.server.domain.ValidationExpression;

@Singleton
public class DatabasePluginImpl implements DatabasePlugin {

	protected static final Logger log = LoggerFactory.getLogger(DatabasePluginImpl.class);
	private final CatalogDescriptionCacheManager cache;
	private final String host;
	private final CatalogPlugin[] plugins;
	private final Provider<CatalogDescriptor> fieldProvider;
	private final Provider<CatalogDescriptor> catalogProvider;
	private final Provider<CatalogDescriptor> i18nProvider;
	private final Provider<CatalogDescriptor> triggerProvider;
	private final Provider<CatalogDescriptor> localizedStringProvider;
	private final Provider<CatalogDescriptor> constraintProvider;
	private final Provider<CatalogManager> dsm;
	private final Provider<CatalogPropertyAccesor> accessorP;
	private final Provider<CatalogDescriptor> trashP;

	public DatabasePluginImpl(@Named("host") String host, CatalogDescriptionCacheManager cache, Provider<CatalogPropertyAccesor> accessorP,
			Provider<CatalogManager> dsm, CatalogManager dictionary, GarbageCollection collect, RestoreTrash restore, TrashDeleteTrigger dump,
			FieldDescriptorUpdateTrigger invalidateAll, CatalogDescriptorUpdateTrigger invalidate, EntryDeleteTrigger trash,
			@Named(FieldDescriptor.CATALOG_ID) Provider<CatalogDescriptor> fieldProvider,
			@Named(CatalogDescriptor.CATALOG_ID) Provider<CatalogDescriptor> catalogProvider,
			@Named(DistributiedLocalizedEntry.CATALOG) Provider<CatalogDescriptor> i18nProvider,
			@Named(CatalogActionTrigger.CATALOG) Provider<CatalogDescriptor> triggerProvider,
			@Named(WruppleLocalizedString.CATALOG) Provider<CatalogDescriptor> localizedStringProvider,
			@Named(FieldConstraint.CATALOG_ID) Provider<CatalogDescriptor> constraintProvider, @Named(Trash.CATALOG) Provider<CatalogDescriptor> trashP,
			CatalogPlugin... plugins) {
		dictionary.addCommand(CatalogDescriptorUpdateTrigger.class.getSimpleName(), invalidate);
		dictionary.addCommand(FieldDescriptorUpdateTrigger.class.getSimpleName(), invalidateAll);
		dictionary.addCommand(EntryDeleteTrigger.class.getSimpleName(), trash);
		dictionary.addCommand(TrashDeleteTrigger.class.getSimpleName(), dump);
		dictionary.addCommand(RestoreTrash.class.getSimpleName(), restore);
		dictionary.addCommand(GarbageCollection.class.getSimpleName(), collect);
		this.cache = cache;
		this.host = host;
		this.fieldProvider = fieldProvider;
		this.catalogProvider = catalogProvider;
		this.i18nProvider = i18nProvider;
		this.triggerProvider = triggerProvider;
		this.constraintProvider = constraintProvider;
		this.localizedStringProvider = localizedStringProvider;
		this.dsm = dsm;
		this.accessorP = accessorP;
		this.plugins = plugins;
		this.trashP = trashP;
	}

	@Override
	public void modifyAvailableCatalogList(List<? super CatalogIdentification> names, CatalogExcecutionContext context) {
		names.add(new CatalogIdentificationImpl(CatalogDescriptor.CATALOG_ID, "Catalogs", "/static/img/catalog.png"));
		names.add(new CatalogIdentificationImpl(FieldDescriptor.CATALOG_ID, "Fields", "/static/img/fields.png"));
		names.add(new CatalogIdentificationImpl(FieldConstraint.CATALOG_ID, "Validation Data", "/static/img/check.png"));
		names.add(new CatalogIdentificationImpl(CatalogActionTrigger.CATALOG, "Action Triggers", "/static/img/excecute.png"));
		names.add(new CatalogIdentificationImpl(WebEventTrigger.CATALOG, "Web Triggers", "/static/img/excecute.png"));
		names.add(new CatalogIdentificationImpl(DistributiedLocalizedEntry.CATALOG, "Localized Entity", "/static/img/locale.png"));
		names.add(new CatalogIdentificationImpl(WruppleLocalizedString.CATALOG, "i18n", "/static/img/locale.png"));
	}

	@Override
	public CatalogDescriptor getDescriptorForName(String catalogId, CatalogExcecutionContext context) {

		CatalogDescriptor regreso = cache.get(catalogId, context);
		if (regreso == null) {
			log.info("read catalog descriptor {} ", catalogId);
			if (FieldDescriptor.CATALOG_ID.equals(catalogId)) {
				regreso = fieldProvider.get();
				CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1, FieldDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
				trigger.setRollbackOnFail(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
				trigger = new CatalogActionTriggerImpl(2, FieldDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
				trigger.setRollbackOnFail(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
				return regreso;
			} else if (CatalogDescriptor.CATALOG_ID.equals(catalogId)) {
				regreso = catalogProvider.get();
				CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1, CatalogDescriptorUpdateTrigger.class.getSimpleName(), false, null, null,
						null);
				trigger.setRollbackOnFail(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
				trigger = new CatalogActionTriggerImpl(2, CatalogDescriptorUpdateTrigger.class.getSimpleName(), false, null, null, null);
				trigger.setRollbackOnFail(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
			} else if (DistributiedLocalizedEntry.CATALOG.equals(catalogId)) {
				regreso = i18nProvider.get();
			} else if (CatalogActionTrigger.CATALOG.equals(catalogId)) {
				regreso = triggerProvider.get();
			} else if (WruppleLocalizedString.CATALOG.equals(catalogId)) {
				regreso = localizedStringProvider.get();
			} else if (FieldConstraint.CATALOG_ID.equals(catalogId)) {
				regreso = constraintProvider.get();
			} else if (Trash.CATALOG.equals(catalogId)) {
				regreso = trashP.get();
				CatalogActionTriggerImpl trigger = new CatalogActionTriggerImpl(1, RestoreTrash.class.getSimpleName(), true, null, null, null);
				trigger.setRollbackOnFail(false);
				trigger.setStopOnFail(false);
				regreso.getTriggersValues().add(trigger);
				trigger = new CatalogActionTriggerImpl(2, TrashDeleteTrigger.class.getSimpleName(), true, null, null, null);
				trigger.setRollbackOnFail(true);
				trigger.setStopOnFail(true);
				regreso.getTriggersValues().add(trigger);
			} else {
				// POLLING plugins
				for (CatalogPlugin plugin : plugins) {
					regreso = plugin.getDescriptorForName(catalogId, context);
					if (regreso != null) {
						break;
					}
				}
			}

			return processDescriptor(catalogId, regreso, context);

		} else {
			return regreso;
		}
	}

	private CatalogDescriptor processDescriptor(String name, CatalogDescriptor regreso, CatalogExcecutionContext context) {
		if(regreso==null){
			throw new IllegalArgumentException(name);
		}
		for (CatalogPlugin interpret2 : plugins) {
			log.debug("POST process {} IN {}", name, interpret2);
			interpret2.postProcessCatalogDescriptor(regreso);
		}
		if (regreso.getHost() == null) {
			log.debug("locally bound catalog {} @ {}", name, host);
			regreso.setHost(host);
		}
		cache.put(name, regreso, context);
		CatalogUserTransaction transaction = (CatalogUserTransaction) context.getRequest().getTransaction(context);
		if (transaction != null) {
			transaction.didMetadataRead(regreso);
		}
		log.debug("BUILT catalog {}={}", name, regreso);
		return regreso;
	}

	@Override
	public List<CatalogIdentification> getAvailableCatalogs(CatalogExcecutionContext context) throws Exception {
		List<CatalogIdentification> names = new ArrayList<CatalogIdentification>();

		modifyAvailableCatalogList(names, context);

		CatalogPlugin[] modules = getPlugins();
		if (modules != null) {
			for (CatalogPlugin module : modules) {
				module.modifyAvailableCatalogList(names, context);
			}
		}
		return names;
	}

	@Override
	public void postProcessCatalogDescriptor(CatalogDescriptor regreso) {

		CatalogActionTriggerImpl trigger;

		trigger = new CatalogActionTriggerImpl(2, GarbageCollection.class.getSimpleName(), true, null, null, null);
		trigger.setRollbackOnFail(true);
		trigger.setStopOnFail(true);

		regreso.getTriggersValues().add(trigger);

		FieldDescriptor field = regreso.getFieldDescriptor(Trash.TRASH_FIELD);
		if (field != null && field.getDataType() == CatalogEntry.BOOLEAN_DATA_TYPE) {

			trigger = new CatalogActionTriggerImpl(1, EntryDeleteTrigger.class.getSimpleName(), false, null, null, null);
			trigger.setRollbackOnFail(true);
			trigger.setStopOnFail(true);
			regreso.getTriggersValues().add(trigger);
		}

	}

	@Override
	public CatalogDataAccessObject<? extends CatalogEntry> getOrAssembleDataSource(CatalogDescriptor catalog, Class<? extends CatalogEntry> clazz,
			CatalogExcecutionContext context) throws Exception {
		for (CatalogPlugin plugin : plugins) {
			CatalogDataAccessObject<? extends CatalogEntry> regreso = plugin.getOrAssembleDataSource(catalog,clazz, context);
			if (regreso != null) {
				return regreso;
			}
		}
		throw new IllegalArgumentException(catalog.getCatalogId());
	}

	@Override
	public ValidationExpression[] getValidations() {// TODO catalog field
													// consistency
													// and inheritance loop
													// detection
		return null;
	}

	@Override
	public CatalogPlugin[] getPlugins() {
		return plugins;
	}

}
