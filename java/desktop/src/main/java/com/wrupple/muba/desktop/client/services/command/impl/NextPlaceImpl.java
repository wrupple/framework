package com.wrupple.muba.desktop.client.services.command.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.services.command.NextPlace;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsNotification;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogDescriptor;

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

	private JavaScriptObject properties;
	private JsNotification output;
	private ProcessContextServices context;

	@Inject
	public NextPlaceImpl() {
	}

	@Override
	public void prepare(String command, JavaScriptObject properties, EventBus eventBus, ProcessContextServices processContext,
                        JsTransactionApplicationContext processParameters, StateTransition<DesktopPlace> callback) {
		this.properties = properties;
		this.output = StandardActivityCommand.getUserOutputEntry(processParameters.getUserOutput());
		this.context = processContext;
	}

	@Override
	public void execute() {
		final PlaceController pc = context.getPlaceController();
		DesktopManager dm = context.getDesktopManager();
		StorageManager desc = context.getStorageManager();
		DesktopPlace current = (DesktopPlace) pc.getWhere();
		JsApplicationItem currentItem = (JsApplicationItem) dm.getApplicationItem(current);
		currentItem = findNextTreeNode(currentItem);

		JsArray<JsApplicationItem> hierarchy = currentItem.getHierarchy();
		int length = hierarchy.length();
		String [] targetActivity = new String[length];
		for(int i =0; i < length; i++){
			targetActivity[i]= hierarchy.get(i).getActivity();
		}
		
		final DesktopPlace place = StandardActivityCommand.determineExplicitPlaceIntentArguments(targetActivity, output, current, true);

		if (output == null || output.getCatalog() == null) {
			pc.goTo(place);
		} else {
			desc.loadCatalogDescriptor(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), output.getCatalog(), new DataCallback<CatalogDescriptor>() {
				@Override
				public void execute() {
					StandardActivityCommand.determineFieldUrlParameters(result, place, properties, (JsNotification) output);
					pc.goTo(place);
				}
			});
		}
	}

	/**
	 * 
	 * 
	 * @param currentItem
	 * @return the first child, or the next sibling, or the parent, in that
	 *         order
	 */
	JsApplicationItem findNextTreeNode(JsApplicationItem currentItem) {
		// attempt to find the first child
		JsArray<JsApplicationItem> children = currentItem.getChildItemsValuesArray();
		if (children == null) {
			// find the next brother
			JsApplicationItem currentItemParent = currentItem.getParentValue();
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
