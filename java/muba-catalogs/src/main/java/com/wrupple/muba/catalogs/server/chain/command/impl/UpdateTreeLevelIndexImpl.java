package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.*;
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
		CatalogEntry newe = (CatalogEntry) context.getRequest().getEntryValue();
		CatalogEntry olde = context.getOldValue();
        Instrospection instrospection = context.getCatalogManager().access().newSession(newe);
        FieldDescriptor treeIndex = catalog.getFieldDescriptor(ContentNode.CHILDREN_TREE_LEVEL_INDEX);
		FieldDescriptor childrenField = catalog.getFieldDescriptor(HasChildren.FIELD);
        context.getCatalogManager().access().setPropertyValue(treeIndex, newe, 0, instrospection);

		log.trace("[UpdateTreeLevelIndex]");
        List<Object> newChildren = (List<Object>) context.getCatalogManager().access().getPropertyValue(childrenField,
                newe, null, instrospection);
		List<Object> oldChildren = (List<Object>) (olde == null ? null
                : context.getCatalogManager().access().getPropertyValue(childrenField, olde, null, instrospection));

        long childrenTreeIndex = (Long) context.getCatalogManager().access().getPropertyValue(treeIndex, newe, null,
                instrospection);
		if (newChildren != null && !newChildren.isEmpty()) {
			if (oldChildren != null) {
				if (oldChildren.equals(newChildren)) {
					return CONTINUE_PROCESSING;
				}
			}
			childrenTreeIndex++;
			log.trace("[UpdateTreeLevelIndex] new children available {}", childrenTreeIndex);
			PersistentCatalogEntity child;
			for (Object childId : newChildren) {
				child =context.triggerGet(catalog.getDistinguishedName(),childId);
                context.getCatalogManager().access().setPropertyValue(treeIndex, child, childrenTreeIndex, instrospection);
                // FIXME update bulk
				context.triggerWrite(catalog.getDistinguishedName(),childId,child);
			}

		}
		return CONTINUE_PROCESSING;
	}

}
