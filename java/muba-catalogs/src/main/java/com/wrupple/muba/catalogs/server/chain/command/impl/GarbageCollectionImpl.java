package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.CatalogIdentification;
import com.wrupple.muba.catalogs.server.chain.command.GarbageCollection;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.impl.CatalogCommand;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasEntryId;
import com.wrupple.vegetate.server.domain.FilterCriteriaImpl;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

@Singleton
public class GarbageCollectionImpl extends CatalogCommand implements GarbageCollection {

	private final Provider<DatabasePlugin> moduleRegistryProvider;
	
	@Inject
	public GarbageCollectionImpl(Provider<DatabasePlugin> moduleRegistryProvider,CatalogQueryRewriter queryRewriter, Provider<CatalogResultCache> cacheProvider,
			Provider<CatalogActionTriggerHandler> trigererProvider, CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
		this.moduleRegistryProvider=moduleRegistryProvider;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		if (context.getDomainContext().isGarbageCollectionEnabled()) {

			CatalogEntry e = (CatalogEntry) context.get(CatalogActionTrigger.OLD_ENTRY_CONTEXT_KEY);
			if (e == null) {
				e = (CatalogEntry) context.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);
			}

			if (e == null) {
				// ??what now???

			} else {
				// FIXME delete all catalogs of a domain when domain is dropped

				// FIXME Clean entities with no corresponding catalog in
				// namespace
				// https://cloud.google.com/appengine/docs/java/datastore/metadataqueries?csw=1#Namespace_Queries
				CatalogDescriptor catalog = (CatalogDescriptor) context.get(CatalogActionTrigger.CATALOG_CONTEXT_KEY);

				
				DatabasePlugin database = moduleRegistryProvider.get();
				List<CatalogIdentification> names = database.getAvailableCatalogs(context);
				
				log.trace("[PROCESSING {} CATALOGS FOR GARBAGE COLLECTION IN REFERENCE TO {}/{}]",names.size(),catalog.getCatalogId(),e.getIdAsString());

				String parentCatalogId = catalog.getCatalogId();
				CatalogDescriptor temp;
				Collection<FieldDescriptor> tempFields;
				FilterData garbageFilter;
				FilterCriteria garbageCriteria;
				CatalogDataAccessObject<CatalogEntry> dao;
				List<CatalogEntry> collectedGarbage;
				for (CatalogIdentification idem : names) {
					
					garbageFilter = null;
					temp = database.getDescriptorForName(idem.getIdAsString(), context);
					tempFields = temp.getFieldsValues();

					log.trace("[PROCESSING {}]",idem.getIdAsString());
					// FIND SIMPLE KEYS
					for (FieldDescriptor linkingField : tempFields) {
						if (linkingField.isKey() && linkingField.isHardKey() && !linkingField.isMultiple()
								&& parentCatalogId.equals(linkingField.getForeignCatalogName())) {

							log.trace("field {}@{} is hard not-multiple key referncing {}  ",linkingField.getFieldId(),temp.getCatalogId(),parentCatalogId); 
							
							if (garbageFilter == null) {
								garbageFilter = FilterDataUtils.newFilterData();
								garbageFilter.setConstrained(false);
							}

							garbageCriteria = new FilterCriteriaImpl(linkingField.getFieldId(), FilterData.EQUALS, e.getId());
							garbageFilter.addFilter(garbageCriteria);
							
							// we found a catalog, with a field that is hard
							// linked to the entry just deleted
							// we must delete all entries that match the deleted
							// entry trhough this field

						}
					}

					if (temp.getFieldDescriptor(HasCatalogId.FIELD) != null && temp.getFieldDescriptor(HasEntryId.FIELD) != null) {
						// THIS CATALOG HAS A COMPOUND KEY GOING ON
						if (garbageFilter == null) {
							garbageFilter = FilterDataUtils.newFilterData();
							garbageFilter.setConstrained(false);
						}
						garbageCriteria = new FilterCriteriaImpl(HasCatalogId.FIELD, FilterData.EQUALS, parentCatalogId);
						garbageFilter.addFilter(garbageCriteria);
						garbageCriteria = new FilterCriteriaImpl(HasEntryId.FIELD, FilterData.EQUALS, e.getId());
						garbageFilter.addFilter(garbageCriteria);
						
						log.trace("Will search for dinamic Catalog key references of catalogEntryId and catalogId fields");
					}

					if (garbageFilter != null) {
						dao = getOrAssembleDataSource(temp, context, CatalogEntry.class);
						// FIXME Creates a Command/Event to be excecuted at a
						// later time
						collectedGarbage = dao.read(garbageFilter);
						context.addResuls(collectedGarbage);
						if (collectedGarbage != null) {
							dao.beginTransaction();
							for (CatalogEntry garbage : collectedGarbage) {
								garbage = dao.delete(garbage);
							}
							dao.commitTransaction();
						}
					}
				}

			}

		}

		return CONTINUE_PROCESSING;
	}

}
