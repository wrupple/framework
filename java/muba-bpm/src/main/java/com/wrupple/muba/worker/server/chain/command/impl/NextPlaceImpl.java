package com.wrupple.muba.worker.server.chain.command.impl;

import com.google.inject.Inject;
import com.wrupple.muba.event.domain.Application;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.Workflow;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.event.domain.ApplicationState;
import com.wrupple.muba.worker.server.chain.command.NextPlace;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	protected static final Logger log = LogManager.getLogger(NextPlaceImpl.class);


	@Inject
	public NextPlaceImpl() {
	}

	@Override
	public boolean execute(Context ctx) throws Exception {

		ApplicationContext context = (ApplicationContext) ctx;
        ApplicationState state = context.getStateValue();

		Application currentItem = (Application) state.getApplicationValue();
        currentItem = findNextTreeNode(currentItem);
		log.info(currentItem.getName()==null?currentItem.getDistinguishedName():currentItem.getName());
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
	Application findNextTreeNode(Application currentItem) {
		// attempt to find the first child
		List children = currentItem.getChildrenValues();
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
							return (Application) children.get(i + 1);
						} else {
							// item is the last child of it's parent.

							// default to go back to the parent
							return (Application) currentItemParent;
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
			return (Application) children.get(0);
		}
		throw new NullPointerException("Unable to determine next activity");

	}


}
