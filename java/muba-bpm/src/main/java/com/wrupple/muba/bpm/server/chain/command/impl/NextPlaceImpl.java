package com.wrupple.muba.bpm.server.chain.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.bpm.domain.Workflow;
import com.wrupple.muba.bpm.domain.ApplicationState;
import com.wrupple.muba.bpm.domain.WorkflowFinishedIntent;
import com.wrupple.muba.desktop.client.services.command.NextPlace;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.Workflow;
import com.wrupple.muba.desktop.domain.overlay.JsNotification;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import org.apache.commons.chain.Context;

import java.util.List;

/**
 * 
 * This implementation of the Next Command traverses the application tree by
 * first attempting to go the the current node's first child, if no child is
 * available, it goes to the next sibling of the node, at the same level, if no
 * more nodes are available at the same level, it "exits" to the parent, which
 * will reset the application unless some condition in met to redirect someplace
 * else
 * 
 * @author japi
 *
 */
public class NextPlaceImpl implements com.wrupple.muba.bpm.server.chain.command.NextPlace {


	@Inject
	public NextPlaceImpl() {
	}

	@Override
	public boolean execute(Context ctx) throws Exception {

		RuntimeContext context = (RuntimeContext) ctx;
		WorkflowFinishedIntent event = (WorkflowFinishedIntent) context.getServiceContract();
		ApplicationState state= (ApplicationState) event.getStateValue();

        Workflow currentItem = state.getApplicationValue();
        currentItem = findNextTreeNode(currentItem);
		state.setApplicationValue(currentItem);


		return CONTINUE_PROCESSING;
	}




	/**
	 *
	 *
	 * @param currentItem
	 * @return the first child, or the next sibling, or the parent, in that
	 *         order
	 */
	Workflow findNextTreeNode(Workflow currentItem) {
		// attempt to find the first child
		List<ServiceManifest> children = currentItem.getChildrenValues();
		if (children == null) {
			// find the next brother
            ServiceManifest currentItemParent = currentItem.getParentValue();
			if (currentItemParent == null) {

			} else {
				// siblings
				children = currentItemParent.getChildItemsValuesArray();
				for (int i = 0; i < children.length(); i++) {
					if (currentItem == children.get(i)) {
						if ((i + 1) < children.length()) {
							// next brother
							return children.get(i + 1);
						} else {
							// item is the last child of it's parent.

							// default to go back to the parent
							return currentItemParent;
							/*
							 * another option would be to find the parents first
							 * child or next brother like so: if
							 * (currentItemParent.getParentValue() == null) { //
							 * no where to go now } else { return
							 * findNextTreeNode(currentItemParent); }
							 */
						}

					}
				}
			}

		} else {
			return children.get(0);
		}
		throw new NullPointerException("Unable to determine next activity");

	}


}
