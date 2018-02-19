package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.desktop.client.chain.command.BuildApplicationTree;
import com.wrupple.muba.desktop.domain.WorkerRequestContext;
import com.wrupple.muba.event.domain.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BuildApplicationTreeImpl implements BuildApplicationTree {

    @Override
    public boolean execute(WorkerRequestContext context) throws Exception {

        String rootActivity = context.getWorkerState().getHomeActivity();
        Application domainRoot;
            domainRoot = triggerGet(rootActivity, context.getRuntimeContext());

            if(domainRoot==null){
                // Most likely because domain has not been set up
                domainRoot = null;
                throw new IllegalStateException("Application tree root is not found with name "+rootActivity);
                //FIXME context.getWorkerStateValue().setSetupFlag(true);
            }

        /*if (domainRoot!=null && sliceWriters != null) {
            for (DesktopSliceWriter sliceWriter : sliceWriters) {
                sliceWriter.writeItems(domainRoot);
            }
        }*/
        domainRoot =  buildItemTree(domainRoot, context, null);
        context.getWorkerState().setApplicationTree(domainRoot);
        return CONTINUE_PROCESSING;
    }

    private Application triggerGet(Object key, RuntimeContext runtimeContext) throws Exception {
        CatalogActionRequestImpl event = new CatalogActionRequestImpl();
        event.setCatalog(Application.CATALOG);
        event.setEntry(key);
        event.setFollowReferences(true);
        event.setName(DataContract.READ_ACTION);
        event.setDomain(runtimeContext.getSession().getSessionValue().getDomain());
        return runtimeContext.getServiceBus().fireEvent(event, runtimeContext, null);
    }


    private Application buildItemTree(Application item, WorkerRequestContext context, CatalogActionRequestImpl parent) {
        Collection<Long> childItems = item.getChildren();
        String requiredRole;

        boolean rolIsGranted;

        Application child;

        if (childItems != null && !childItems.isEmpty()) {

            List<Application> childInstances = new ArrayList<Application>(childItems.size());

            for (Long childId : childItems) {
                try {
                    child = triggerGet(childId, context.getRuntimeContext());
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
