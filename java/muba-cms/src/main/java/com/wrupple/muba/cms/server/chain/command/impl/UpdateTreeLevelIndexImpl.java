package com.wrupple.muba.cms.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogQueryRewriter;
import com.wrupple.muba.catalogs.server.service.CatalogResultCache;
import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.impl.CatalogCommand;
import com.wrupple.muba.cms.server.chain.command.UpdateTreeLevelIndex;
import com.wrupple.vegetate.domain.CatalogActionTrigger;
import com.wrupple.vegetate.domain.CatalogActionTriggerHandler;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.structure.HasChildren;

@Singleton
public class UpdateTreeLevelIndexImpl extends CatalogCommand implements UpdateTreeLevelIndex {


	@Inject
	public UpdateTreeLevelIndexImpl(CatalogQueryRewriter queryRewriter, Provider<CatalogResultCache> cacheProvider,
			Provider<CatalogActionTriggerHandler> trigererProvider, CatalogPropertyAccesor accessor, DatabasePlugin daoFactory) {
		super(queryRewriter, cacheProvider, trigererProvider, accessor, daoFactory);
	}

	@Override
	public boolean execute(Context context) throws Exception {
		CatalogDescriptor catalog = (CatalogDescriptor) context.get(CatalogActionTrigger.CATALOG_CONTEXT_KEY);
		PersistentCatalogEntity newe = (PersistentCatalogEntity) context.get(CatalogActionTrigger.NEW_ENTRY_CONTEXT_KEY);
		newe.setPropertyValue(0, ContentNode.CHILDREN_TREE_LEVEL_INDEX);

		beforeWritingParent(catalog, null, newe, context);
		return CONTINUE_PROCESSING;
	}

	private  void beforeWritingParent(CatalogDescriptor catalog, PersistentCatalogEntity olde ,PersistentCatalogEntity newe, Context context)
			throws Exception {
		log.trace("[UpdateTreeLevelIndex]");
		List<String> newChildren = (List<String>) newe.getPropertyValue(HasChildren.FIELD);
		List<String> oldChildren = (List<String>) (olde == null ? null : olde.getPropertyValue(HasChildren.FIELD));
		long childrenTreeIndex = (Long) newe.getPropertyValue(ContentNode.CHILDREN_TREE_LEVEL_INDEX);
		if (newChildren != null && !newChildren.isEmpty()) {
			if (oldChildren != null) {
				if (oldChildren.equals(newChildren)) {
					return;
				}
			}
			CatalogDataAccessObject<PersistentCatalogEntity> dao = getOrAssembleDataSource(catalog, (CatalogExcecutionContext) context, null);
			childrenTreeIndex++;
			log.trace("[UpdateTreeLevelIndex] new children available {}",childrenTreeIndex );
			PersistentCatalogEntity child;
			for (String childId : newChildren) {
				child = dao.read(childId);
				child.setPropertyValue(childrenTreeIndex, ContentNode.CHILDREN_TREE_LEVEL_INDEX);
				dao.update(child, child);
			}

		}
	}
}
