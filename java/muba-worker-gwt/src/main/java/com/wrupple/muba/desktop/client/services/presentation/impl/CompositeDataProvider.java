package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.view.client.ProvidesKey;
import com.wrupple.muba.catalogs.client.services.ClientCatalogCacheManager;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.muba.worker.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.FilterData;

import java.util.ArrayList;
import java.util.List;

public class CompositeDataProvider<T extends JavaScriptObject> extends SimpleFilterableDataProvider<T> {

	private void dispatchQueued() {
		for (JsFilterData node : queue) {
			actuallyPerformFetch(node);
		}
	}

	public void addToQueue(JsFilterData filter) {
		if (!isDuplicate(filter)) {
			queue.add(filter);
		}
	}

	private boolean isDuplicate(FilterData filter) {
		for (FilterData contained : queue) {
			if (contained.equals(filter)) {
				return true;
			}
		}

		return false;
	}

	class OnLoadDispatch extends DataCallback<CatalogDescriptor> {

		public OnLoadDispatch() {
		}

		@Override
		public void execute() {
			descriptor = result;
			try {
				// totally weird, kinda worth it cuz we share the same joining
				// logic in server and client this way
				joins = ImplicitJoinUtils.getJoins(null,storageManager, descriptor, customJoins, dm.getCurrentActivityDomain(),dm.getCurrentActivityHost());
			} catch (Exception e) {
				GWT.log("unable to get joins ", e);
				if (customJoins == null) {
					joins = new String[0][0];
				} else {
					joins = customJoins;
				}
			}

			dispatchQueued();
		}

	}

	protected final ClientCatalogCacheManager ccm;
	protected CatalogDescriptor descriptor;
	private String[][] joins;
	final List<JsFilterData> queue;
	private String[][] customJoins;

	public CompositeDataProvider(ClientCatalogCacheManager ccm,DesktopManager dm, StorageManager storageManager, ProvidesKey<T> keyprovider) {
		super(dm,storageManager, keyprovider);
		this.ccm = ccm;
		descriptor = null;
		queue = new ArrayList<JsFilterData>();
	}

	@Override
	public void setCatalog(String catalog) {
		if (this.catalog == null || !this.catalog.equals(catalog)) {
			super.setCatalog(catalog);
			descriptor = null;
			joins = null;
			storageManager.loadGraphDescription(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, new OnLoadDispatch());
		}
	}

	@Override
	protected void fetch(JsFilterData filter) {
		if (catalog == null || descriptor == null || joins == null) {
			addToQueue(filter);
		} else {
			actuallyPerformFetch(filter);
		}
	}

	public void setCustomJoins(String[][] customJoins) {
		this.customJoins = customJoins;
	}

	private void actuallyPerformFetch(JsFilterData filter) {
		if (catalog == null || joins == null || descriptor == null) {
			throw new IllegalStateException();
		}
		String[][] joins = getJoins();
		if (joins == null || joins.length == 0) {
			filter.setJoins((String[][])null);
		} else {
			filter.setJoins(joins);
		}
		super.fetch(filter);
	}

	private String[][] getJoins() {
		return joins;
	}

}
