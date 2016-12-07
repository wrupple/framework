package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasChildren;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.chain.command.CatalogUpdateTransaction;
import com.wrupple.muba.catalogs.server.chain.command.UpdateTreeLevelIndex;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin.Session;

@Singleton
public class UpdateTreeLevelIndexImpl implements UpdateTreeLevelIndex {

	protected static final Logger log = LoggerFactory.getLogger(CommitCatalogActionImpl.class);

	@Inject
	public UpdateTreeLevelIndexImpl() {
		super();
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
		CatalogDescriptor catalog = (CatalogDescriptor) context.getCatalogDescriptor();
		CatalogEntry newe = (CatalogEntry) context.getEntryValue();
		CatalogEntry olde = context.getOldValue();
		Session session = context.getCatalogManager().newSession(newe);
		FieldDescriptor treeIndex = catalog.getFieldDescriptor(ContentNode.CHILDREN_TREE_LEVEL_INDEX);
		FieldDescriptor childrenField = catalog.getFieldDescriptor(HasChildren.FIELD);
		context.getCatalogManager().setPropertyValue(catalog, treeIndex, newe, 0, session);

		log.trace("[UpdateTreeLevelIndex]");
		List<Object> newChildren = (List<Object>) context.getCatalogManager().getPropertyValue(catalog, childrenField,
				newe, null, session);
		List<Object> oldChildren = (List<Object>) (olde == null ? null
				: context.getCatalogManager().getPropertyValue(catalog, childrenField, olde, null, session));

		long childrenTreeIndex = (Long) context.getCatalogManager().getPropertyValue(catalog, treeIndex, newe, null,
				session);
		if (newChildren != null && !newChildren.isEmpty()) {
			if (oldChildren != null) {
				if (oldChildren.equals(newChildren)) {
					return CONTINUE_PROCESSING;
				}
			}
			childrenTreeIndex++;
			log.trace("[UpdateTreeLevelIndex] new children available {}", childrenTreeIndex);
			PersistentCatalogEntity child;
			CatalogActionContext updateContext = context.getCatalogManager().spawn(context);
			updateContext.setCatalogDescriptor(catalog);
			for (Object childId : newChildren) {
				updateContext.setEntry(childId);
				updateContext.getCatalogManager().getRead().execute(updateContext);
				child = updateContext.getEntryResult();
				context.getCatalogManager().setPropertyValue(catalog, treeIndex, child, childrenTreeIndex, session);
				// FIXME update bulk
				updateContext.setEntryValue(child);
				updateContext.getCatalogManager().getWrite().execute(updateContext);
			}

		}
		return CONTINUE_PROCESSING;
	}

}
