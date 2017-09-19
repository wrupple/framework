package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.Instrospector;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasChildren;
import com.wrupple.muba.catalogs.domain.*;
import com.wrupple.muba.catalogs.server.chain.command.UpdateTreeLevelIndex;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

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
        Instrospector instrospector = context.getCatalogManager().access().newSession(newe);
        FieldDescriptor treeIndex = catalog.getFieldDescriptor(ContentNode.CHILDREN_TREE_LEVEL_INDEX);
		FieldDescriptor childrenField = catalog.getFieldDescriptor(HasChildren.FIELD);
        context.getCatalogManager().access().setPropertyValue(treeIndex, newe, 0, instrospector);

		log.trace("[UpdateTreeLevelIndex]");
        List<Object> newChildren = (List<Object>) context.getCatalogManager().access().getPropertyValue(childrenField,
                newe, null, instrospector);
		List<Object> oldChildren = (List<Object>) (olde == null ? null
                : context.getCatalogManager().access().getPropertyValue(childrenField, olde, null, instrospector));

        long childrenTreeIndex = (Long) context.getCatalogManager().access().getPropertyValue(treeIndex, newe, null,
                instrospector);
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
                context.getCatalogManager().access().setPropertyValue(treeIndex, child, childrenTreeIndex, instrospector);
                // FIXME update bulk
				updateContext.setEntryValue(child);
				updateContext.getCatalogManager().getWrite().execute(updateContext);
			}

		}
		return CONTINUE_PROCESSING;
	}

}
