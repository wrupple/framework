package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.wrupple.muba.desktop.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.FilterableDataProvider;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.vegetate.domain.FilterData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SimpleFilterableDataProvider<T extends JavaScriptObject> extends AsyncDataProvider<T> implements FilterableDataProvider<T> {
	// USED TO FLAG RetrivingService to attempt to use client-side cache
	public static final String LOCAL_FILTERING = "IncrementalCachingRetrivingService_INCREMENTAL_FETCH_FIELD";

	class RangeReceived extends DataCallback<List<JsCatalogEntry>> {

		FilterData filter;

		public RangeReceived(FilterData filter) {
			this.filter = filter;
		}

		@Override
		public void execute() {
			callbacks.remove(this);
			if (result != null) {
				List<T> processedResult = processRawEntries(result, filter);
				int rangeStart = filter.getStart();
				updateRowData(rangeStart, processedResult);
			}
		}
	}

	class FetchThrottle implements RepeatingCommand {
		JsFilterData filter;

		public FetchThrottle() {
			super();
			this.filter = null;
		}

		public boolean extendRangeToInclude(JsFilterData newf) {
			if (filter == null) {
				filter = newf;
			} else {
				// ALWAYS INCLUDE!
				return extendRange(newf);

			}
			return false;
		}

		private boolean extendRange(FilterData newf) {
			int oldEnd = getEnd(filter);
			int newEnd = getEnd(newf);
			if (newf.getStart() > 0 && newf.getStart() < filter.getStart()) {
				filter.setStart(newf.getStart());
			}
			if (newEnd > oldEnd) {
				setEnd(filter,newEnd);
			}
			return true;
		}

		/*private boolean conditionallyInclude(FilterData newf, int addedLenght) {
			int newStart, newEnd;
			int diferenciaAntes = filter.getStart() - newf.getStart();
			if (Math.abs(diferenciaAntes) < addedLenght) {
				if (filter.getStart() < newf.getStart()) {
					newStart = filter.getStart();
				} else {
					newStart = newf.getStart();
				}
			} else {
				return false;
			}
			
			int diferenciaDespues = filter.getEnd() - newf.getEnd();
			if (Math.abs(diferenciaDespues) < addedLenght) {
				if (filter.getEnd() > newf.getEnd()) {
					newEnd = filter.getEnd();
				} else {
					newEnd = newf.getEnd();
				}
			} else {
				return false;
			}
			filter.setStart(newStart);
			filter.setEnd(newEnd);
			return true;
		}*/

		@Override
		public boolean execute() {
			throttles = null;
			RangeReceived callback = new RangeReceived(filter);
			callbacks.add(callback);
			storageManager.read(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, filter, callback);
			return false;
		}
	}

	protected String catalog;
	protected final StorageManager storageManager;
	private JsFilterData filter;
	private List<RangeReceived> callbacks;
	private FetchThrottle throttles;
	private boolean useCache;
	protected final DesktopManager dm;

	public SimpleFilterableDataProvider(DesktopManager dm,StorageManager storageManager, ProvidesKey<T> keyprovider) {
		super(keyprovider);
		this.dm=dm;
		this.storageManager = storageManager;
		this.filter = JsFilterData.newFilterData();
		this.callbacks = new ArrayList<SimpleFilterableDataProvider<T>.RangeReceived>();
		this.useCache = true;
	}

	public List<T> processRawEntries(List<JsCatalogEntry> result, FilterData filter) {
		return (List<T>) result;
	}

	@Override
	protected void onRangeChanged(HasData<T> display) {
		final Range currentRange = display.getVisibleRange();
		int displayedItemCount = display.getVisibleItemCount();

		if (callbacks.isEmpty()) {
			calculateRangesAndGoFetch(displayedItemCount, currentRange);
		} else {
			boolean foundAtLeastOneMatch = false;
			for (RangeReceived callback : callbacks) {
				if (isRangeContained(callback.filter, currentRange)) {
					foundAtLeastOneMatch = true;
				}
			}
			if (!foundAtLeastOneMatch) {
				calculateRangesAndGoFetch(displayedItemCount, currentRange);
			}
		}

	}

	public void setUseCache(boolean b) {
		this.useCache = b;
	}

	private void calculateRangesAndGoFetch(int displayedItemCount, Range currentRange) {
		JsFilterData filter = JsFilterData.newFilterData();
		filter.setFiltersArray(this.filter.getFilterArray());
		filter.setJoins(this.filter.getJoinsArray(false));
		filter.setOrderArray(this.filter.getOrderArray());
		filter.setConstrained(true);

		calculateRangeToFetch(displayedItemCount, currentRange, filter);

		if (useCache) {
			GWTUtils.setAttribute(filter, LOCAL_FILTERING, true);
		}

		fetch(filter);
	}

	protected void fetch(JsFilterData filter) {
		if (catalog == null) {
			throw new IllegalStateException();
		}
		if (throttles == null) {
			throttles = new FetchThrottle();
			Scheduler.get().scheduleFixedDelay(throttles, 200);
		}

		throttles.extendRangeToInclude(filter);
	}

	private boolean isRangeContained(FilterData filter, Range range) {
		boolean topChecks = filter.getStart() <= range.getStart();
		boolean bottomChecks = getEnd(filter) >= (range.getStart() + range.getLength());
		return topChecks && bottomChecks;
	}

	private void calculateRangeToFetch(int displayedItemCount, Range currentRange, FilterData filter) {
		filter.setStart(displayedItemCount);
		filter.setLength(currentRange.getStart() + currentRange.getLength()-displayedItemCount);

	}

	public void forceUpdateOnDisplays() {
		Set<HasData<T>> displays = super.getDataDisplays();
		for (HasData<T> display : displays) {
			forceUpdate(display);
		}
	}

	public String getCatalog() {
		return catalog;
	}

	public FilterData getFilter() {
		return filter;
	}

	public void setCatalog(String catalog) {
		assert catalog != null;
		this.catalog = catalog;
	}

	public void setFilter(FilterData filter) {
		this.filter = (JsFilterData) filter;
	}

	private void forceUpdate(HasData<T> display) {
		Range range = display.getVisibleRange();
		display.setVisibleRangeAndClearData(range, true);
	}
	
	private int getEnd(FilterData data){
		return data.getStart()+data.getLength();
	}
	
	private void setEnd(FilterData data, int end){
		int start = data.getStart();
		data.setLength(end-start);
		
	}

}
