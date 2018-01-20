package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.impl.FilterDataUtils;
import com.wrupple.muba.desktop.client.chain.command.BuildApplicationTree;
import com.wrupple.muba.desktop.domain.DesktopRequestContext;
import com.wrupple.muba.event.domain.*;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BuildApplicationTreeImpl implements BuildApplicationTree {
    @Override
    public boolean execute(Context c) throws Exception {
        DesktopRequestContext context = (DesktopRequestContext) c;

        String rootActivity = context.getWorkerOrderValue().getHomeActivity();
        Application domainRoot;
        try {
            domainRoot = triggerGet(rootActivity, context.getRuntimeContext(), true, null);
        } catch (IndexOutOfBoundsException e) {
            // Most likely because domain has not been set up
            domainRoot = null;
            context.getWorkerOrderValue().setSetupFlag(true);
        }

        /*if (domainRoot!=null && sliceWriters != null) {
            for (DesktopSliceWriter sliceWriter : sliceWriters) {
                sliceWriter.writeItems(domainRoot);
            }
        }*/
        context.getWorkerOrderValue().setApplicationTree(domainRoot);
        return CONTINUE_PROCESSING;
    }


    private Application triggerGet(Object key, RuntimeContext runtimeContext, boolean assemble, CatalogActionRequest parent) throws Exception {
        CatalogActionRequestImpl event = new CatalogActionRequestImpl();
        event.setCatalog(Application.CATALOG);
        event.setEntry(key);
        event.setParentValue(parent);
        event.setFilter(null);
        event.setFollowReferences(assemble);
        event.setName(DataEvent.READ_ACTION);
        event.setDomain(runtimeContext.getSession().getSessionValue().getDomain());
        List<Application> results = runtimeContext.getEventBus().fireEvent(event, runtimeContext, null);
        return results == null ? null : results.isEmpty() ? null : results.get(0);
    }


    protected Application buildCurrentDomainSlice(DesktopRequestContext context, String homeActivity) throws Exception {

        Application domainRoot;
        FilterData filter = FilterDataUtils.createSingleFieldFilter(Application.NAME_FIELD, homeActivity);

        CatalogActionRequestImpl event = new CatalogActionRequestImpl();
        event.setParentValue(null);
        event.setCatalog(Application.CATALOG);
        event.setFilter(filter);
        event.setFollowReferences(false);
        event.setName(DataEvent.READ_ACTION);
        event.setDomain(context.getRuntimeContext().getSession().getSessionValue().getDomain());

        List<Application> domainRoots = context.getRuntimeContext().getEventBus().fireEvent(event, context.getRuntimeContext(), null);
        domainRoot = domainRoots.get(0);
        if (domainRoot == null) {
            throw new IllegalArgumentException("no domain root");
        }
        domainRoot = buildItemTree(domainRoot, context, event);

        return domainRoot;
    }

    private Application buildItemTree(Application item, DesktopRequestContext context, CatalogActionRequestImpl parent) {
        Collection<Long> childItems = item.getChildren();
        String requiredRole;

        boolean rolIsGranted;

        Application child;

        if (childItems != null && !childItems.isEmpty()) {

            List<Application> childInstances = new ArrayList<Application>(childItems.size());

            for (Long childId : childItems) {
                try {
                    child = triggerGet(childId, context.getRuntimeContext(), false, parent);
                } catch (Exception e) {
                    child = null;
                }
                if (child != null) {
                    requiredRole = child.getRequiredRole();
                    if (requiredRole == null) {
                        childInstances.add(child);
                    } else {
                        rolIsGranted = context.getRuntimeContext().getSession().isGranted(requiredRole);
                        if (rolIsGranted) {
                            childInstances.add(child);
                        } else {
                            System.out.println("CHILD ITEM SKIPPED " + childId);
                        }
                    }
                } else {
                    // TODO generate a notification
                    System.err.println("MISSING ITEM " + childId);
                }

            }

            item.setChildrenValues((List) childInstances);

            for (Application grandChild : childInstances) {
                buildItemTree(grandChild, context, parent);
            }

        }
        return item;
    }
}
