package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.UpdateTreeLevelIndex;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasChildren;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class UpdateTreeLevelIndexImpl implements UpdateTreeLevelIndex {

    protected static final Logger log = LogManager.getLogger(UpdateTreeLevelIndexImpl.class);
    private final FieldAccessStrategy access;

	@Inject
	public UpdateTreeLevelIndexImpl(FieldAccessStrategy access) {
		super();
		this.access = access;
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		CatalogActionContext context = (CatalogActionContext) ctx;
        CatalogDescriptor catalog = context.getCatalogDescriptor();
        CatalogEntry newe = (CatalogEntry) context.getRequest().getEntryValue();
		CatalogEntry olde = context.getOldValue();
        Instrospection instrospection = access.newSession(newe);
        FieldDescriptor treeIndex = catalog.getFieldDescriptor(ContentNode.CHILDREN_TREE_LEVEL_INDEX);
		FieldDescriptor childrenField = catalog.getFieldDescriptor(HasChildren.FIELD);
		access.setPropertyValue(treeIndex, newe, 0, instrospection);

		log.trace("[UpdateTreeLevelIndex]");
        List<Object> newChildren = (List<Object>) access.getPropertyValue(childrenField,
                newe, null, instrospection);
		List<Object> oldChildren = (List<Object>) (olde == null ? null
                : access.getPropertyValue(childrenField, olde, null, instrospection));

        long childrenTreeIndex = (Long) access.getPropertyValue(treeIndex, newe, null,
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
				access.setPropertyValue(treeIndex, child, childrenTreeIndex, instrospection);
                // FIXME update bulk
				context.triggerWrite(catalog.getDistinguishedName(),childId,child);
			}

		}
		return CONTINUE_PROCESSING;
	}

}
