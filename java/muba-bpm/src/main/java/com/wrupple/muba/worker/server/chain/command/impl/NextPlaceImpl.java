package com.wrupple.muba.worker.server.chain.command.impl;

import com.google.inject.Inject;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.domain.ApplicationState;
import com.wrupple.muba.worker.domain.Workflow;
import com.wrupple.muba.worker.server.chain.command.NextPlace;
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
public class NextPlaceImpl implements NextPlace {


	@Inject
	public NextPlaceImpl() {
	}

	@Override
	public boolean execute(Context ctx) throws Exception {

		ApplicationContext context = (ApplicationContext) ctx;
        ApplicationState state = context.getStateValue();

        Workflow currentItem = (Workflow) state.getHandleValue();
        currentItem = findNextTreeNode(currentItem);
		state.setHandleValue(currentItem);


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
				children = currentItemParent.getChildrenValues();
				for (int i = 0; i < children.size(); i++) {
					if (currentItem == children.get(i)) {
						if ((i + 1) < children.size()) {
							// next brother
							return (Workflow) children.get(i + 1);
						} else {
							// item is the last child of it's parent.

							// default to go back to the parent
							return (Workflow) currentItemParent;
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
			return (Workflow) children.get(0);
		}
		throw new NullPointerException("Unable to determine next activity");

	}


}
