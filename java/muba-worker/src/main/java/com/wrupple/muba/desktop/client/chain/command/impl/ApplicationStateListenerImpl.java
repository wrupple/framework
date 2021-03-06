package com.wrupple.muba.desktop.client.chain.command.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogEventHandler;
import com.wrupple.muba.desktop.client.chain.command.ApplicationStateListener;
import com.wrupple.muba.event.domain.ApplicationState;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

/**
 * after update trigger installed by com.wrupple.muba.desktop.client.chain.command.impl.InstallActivityEventHandlerImpl
 *
 * When a catalog update event is fired for an application state we update desktop place
 * Created by japi on 24/08/17.
 */
public class ApplicationStateListenerImpl implements ApplicationStateListener {


    @Override
    public boolean execute(CatalogActionContext context) throws Exception {

        ApplicationState state = (ApplicationState) context.getResult();

        context.
                getRuntimeContext().
                getServiceBus().
                fireEvent(state
                        ,
                        context.getRuntimeContext(),
                        null
                );

        return CONTINUE_PROCESSING;
    }


    public void onApplicationStateChanged(ApplicationState e) {
        //FIXME set worker, fire state


        //activity manager set display new NestedActivityPresenter(dm)
        //add display to root layoutpanel
        // fire  state  postSolutionToApplication(applicationState);


        /*
        final PlaceController pc = context.getPlaceController();

        StandardActivityCommand.determineFieldUrlParameters(result,p,properties,output);

        //conditions from GWT desktop (AbstractCommitUserTransactionImpl CommitEditTransaction CommitSelectTransaction)... do commit

        if (userOutput == null) {*/
                /*
                 *
                 *  esta llamad en realidad solo tiene sentido en el cliente (paquete runner human), pero esta logica no se debe perder, y debe realizarse aqui mismo
                 *
                 * on ApplicationState Update Catalog Contract
                 *
                 * the original implementation of user catalog invalidation heartbeat socket channel is in workspace/src/desktop/server/java/GAE/src/main/java/com/wrupple/base/client/state/impl/StartUserHeartBeatImpl.java and CacheInvalidationBroadcasterImpl
                 *
                 *
                 * Buscar "History url" en
                 */
/*
            context.getHumanSolver().putPlaceParameter(saveTo, null);
        }else{
            String unencodedString;
            if (GWTUtils.isArray(userOutput)) {

                JsArray<JsCatalogEntry> out = userOutput.cast();
                if (out.length() == 0) {
                    unencodedString = null;
                } else {
                    StringBuilder builder = new StringBuilder(out.length()*20);
                    for (int i = 0; i < out.length(); i++) {
                        if (i > 0) {
                            builder.append(',');
                        }
                        builder.append(out.get(i).getId());

                    }
                    unencodedString = builder.toString();
                }
            } else {
                unencodedString = userOutput.getId();
            }
            context.getDesktopManager().putPlaceParameter(saveTo, unencodedString);
        }

*/

    }

    /*
    @Override
	CacheInvalidationBroadcasterImpl:: public void broadcastInvalidationEventToUpperLayers(JsCacheInvalidationData e, ServiceBus eventBus) {
		String targetCatalog = e.getCatalog();
		String sourceAction = e.getName();
		JsCatalogEntry key=e.getEntry();
		key.setCatalog(targetCatalog);
		String targetEntryId = e.getEntryId();

		if (CatalogActionRequest.WRITE_ACTION.equals(sourceAction) && key != null) {
			// FIXME server should send event keys setRuntimeContext id encodede as String
			if (targetEntryId == null) {
				targetEntryId = getIdAsString(key);
			}
			if (targetEntryId != null) {
				key.setId(targetEntryId);
			}
			key.setCatalog(targetCatalog);
			eventBus.fireHandler(new EntryUpdatedEvent(key, resolvedHost, resolvedDomain));
		} else if (CatalogActionRequest.CREATE_ACTION.equals(sourceAction) && key != null) {
			if (targetEntryId == null) {
				targetEntryId = getIdAsString(key);
			}
			if (targetEntryId != null) {
				key.setId(targetEntryId);
			}
			key.setCatalog(targetCatalog);
			eventBus.fireHandler(new EntryCreatedEvent(key, resolvedHost, resolvedDomain));
		} else if (CatalogActionRequest.DELETE_ACTION.equals(sourceAction)) {
			Collection<String> entries = Collections.singletonList(targetEntryId);
			eventBus.fireHandler(new EntriesDeletedEvent(entries, targetCatalog, resolvedDomain, resolvedHost));
		}
	}
     */
}
