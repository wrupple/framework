package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.AbstractHasData;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.DefaultSelectionEventManager.EventTranslator;
import com.google.gwt.view.client.DefaultSelectionEventManager.SelectAction;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.client.services.evaluation.impl.JsComparator;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.ContentBrowser;
import com.wrupple.muba.desktop.client.activity.widgets.panels.BackAndForthPager;
import com.wrupple.muba.desktop.client.activity.widgets.panels.InfiniteScrollPager;
import com.wrupple.muba.desktop.client.factory.dictionary.CatalogEntryBrowserMap;
import com.wrupple.muba.desktop.client.services.logic.CatalogEntryKeyProvider;
import com.wrupple.muba.desktop.client.services.logic.GenericDataProvider;
import com.wrupple.muba.desktop.client.services.logic.ModifyUserInteractionStateModelCommand;
import com.wrupple.muba.desktop.client.services.presentation.BrowserSelectionModel;
import com.wrupple.muba.desktop.client.services.presentation.ContentStyleDelegate;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.ModelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.JsArrayList;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.shared.domain.PanelTransformationConfig;
import com.wrupple.muba.worker.shared.event.EntriesDeletedEvent;
import com.wrupple.muba.worker.shared.event.EntriesRetrivedEvent;
import com.wrupple.muba.worker.shared.event.EntryCreatedEvent;
import com.wrupple.muba.worker.shared.event.EntryUpdatedEvent;
import com.wrupple.vegetate.domain.FilterData;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractContentBrowser extends ResizeComposite implements ContentBrowser {

	public static final String INFINITE_PAGER = "infinite";
	public static final String SIMPLE_PAGER = "simple";
	public static final String CARROUSEL_PAGER = "carrousel";

	private AbstractDataProvider<JsCatalogEntry> abstractDataProvider;
	private Provider<GenericDataProvider> dinamicDataProviderProvider;
	private CatalogEntryKeyProvider keyProvider;
	protected EventBus eventBus;
	private String catalog;
	String[][] customJoins;

	private ContentStyleDelegate styleDelegate;
	// TODO all pagers should be layoutPanels so resize isn't an issue
	protected boolean pagingEnabled;
	private boolean cumulative;
	private int previousCumulativeItemCount;

	String postSortField;
	protected JsTransactionApplicationContext contextParameters;
	protected ProcessContextServices contextServices;
	private String notificationsDue;
	private CatalogEntryBrowserMap browserMap;
	protected final  HasData<JsCatalogEntry> hasData;
	public AbstractContentBrowser(HasData<JsCatalogEntry> browser, Provider<GenericDataProvider> dinamicDataProviderProvider,
			CatalogEntryKeyProvider keyProviderProvider, ContentStyleDelegate styleDelegate, CatalogEntryBrowserMap browserMap) {
		super();
		this.hasData=browser;
		this.browserMap = browserMap;
		previousCumulativeItemCount = 0;
		pagingEnabled = true;
		this.dinamicDataProviderProvider = dinamicDataProviderProvider;
		this.keyProvider = keyProviderProvider;
		this.styleDelegate = styleDelegate;
	}
	
	
	

	@Override
	public HandlerRegistration addRowCountChangeHandler(com.google.gwt.view.client.RowCountChangeEvent.Handler handler) {
		return hasData.addRowCountChangeHandler(handler);
	}

	@Override
	public int getRowCount() {
		return hasData.getRowCount();
	}

	@Override
	public Range getVisibleRange() {
		return hasData.getVisibleRange();
	}

	@Override
	public boolean isRowCountExact() {
		return hasData.isRowCountExact();
	}

	@Override
	public void setRowCount(int count) {
		hasData.setRowCount(count);
	}

	@Override
	public void setRowCount(int count, boolean isExact) {
		hasData.setRowCount(count,isExact);
	}

	@Override
	public void setVisibleRange(int start, int length) {
		hasData.setVisibleRange(start, length);
	}

	@Override
	public void setVisibleRange(Range range) {
		hasData.setVisibleRange(range)
		;
	}

	@Override
	public HandlerRegistration addCellPreviewHandler(com.google.gwt.view.client.CellPreviewEvent.Handler<JsCatalogEntry> handler) {
		return hasData.addCellPreviewHandler(handler);
	}

	@Override
	public SelectionModel<? super JsCatalogEntry> getSelectionModel() {
		return hasData.getSelectionModel();
	}

	@Override
	public JsCatalogEntry getVisibleItem(int indexOnPage) {
		return hasData.getVisibleItem(indexOnPage);
	}

	@Override
	public int getVisibleItemCount() {
		return hasData.getVisibleItemCount();
	}

	@Override
	public Iterable<JsCatalogEntry> getVisibleItems() {
		return hasData.getVisibleItems();
	}



	@Override
	public void setVisibleRangeAndClearData(Range range, boolean forceRangeChangeEvent) {
		hasData.setVisibleRangeAndClearData(range, forceRangeChangeEvent);
	}

	public void setCustomJoins(String customJoins) {
		this.customJoins = GWTUtils.getCustomJoins(customJoins);
	}

	public void setCumulative(String cumulative) {
		if (cumulative == null) {
			this.cumulative = false;
		} else {
            this.cumulative = Boolean.parseBoolean(cumulative);
        }
	}

	public void setPostSortField(String postSortField) {
		this.postSortField = postSortField;
	}

	public void setNotificationsDue(String notificationsDue) {
		this.notificationsDue = notificationsDue;
	}

	@Override
	public void setRuntimeParams(String catalog, JavaScriptObject properties, EventBus bus, JsTransactionApplicationContext contextParameters,
			ProcessContextServices contextServices) {
		this.eventBus = bus;
		this.contextParameters = contextParameters;
		this.contextServices = contextServices;
		this.catalog = catalog;

		// TODO really similar functionality in ForeignValueRelationEditorImpl
		// to determine paging, BOTH SHOULD USE configuration framework
		Widget initWidget = null;
		// cumulative data show all available data hence disallow the existence
		// of a wrapping pager.
		if (!cumulative && pagingEnabled) {
			String pager = getPager(properties);
			if (pager == null) {
				// TODO fix infinite scroller to work on DataGrid, use simple
				// pager (so usser can jump from page 1 to 999 , but increaze
				// the page size (and change page number accordingly) when user
				// reaches bottom
				//DATAGRID.tableDataScroller
				//https://github.com/arteezy/infinite-scroll-gwt
				//http://stackoverflow.com/questions/25972758/gwt-infinite-scroll-with-discarding-start-of-list-results
				pager = SIMPLE_PAGER;
			}
			if (NO_PAGER.equals(pager)) {
				initWidget = ((IsWidget) hasData).asWidget();
			} else if (SIMPLE_PAGER.equals(pager)) {
				SimplePager pagerWidget = new SimplePager();
				pagerWidget.setDisplay(hasData);
				LayoutPanel container = new LayoutPanel();
				container.add(pagerWidget);
				container.setWidgetBottomHeight(pagerWidget, 0, Unit.PX, 2.5, Unit.EM);
				container.add(((IsWidget) hasData).asWidget());
				container.setWidgetTopBottom(((IsWidget) hasData).asWidget(), 0, Unit.PX, 2.5, Unit.EM);
				initWidget = container;
			} else if (INFINITE_PAGER.equals(pager)) {
				InfiniteScrollPager pagerWidget = new InfiniteScrollPager();
				pagerWidget.setDisplay(hasData);
				SimpleLayoutPanel container = new SimpleLayoutPanel();
				container.setWidget(pagerWidget);
				initWidget = container;
			} else if (CARROUSEL_PAGER.equals(pager)) {
				// TODO configurable values
				BackAndForthPager pagerWidget = new BackAndForthPager(0, 1, 1);
				pagerWidget.setDisplay(hasData);
				initWidget = pagerWidget;
			} else {
				initWidget = ((IsWidget) hasData).asWidget();
			}
		} else {
			initWidget = ((IsWidget) hasData).asWidget();
		}

		initWidget(initWidget);
	}

	private native String getPager(JavaScriptObject o) /*-{
		return o.pager;
	}-*/;

	@Override
	public void setRowData(int start, List<? extends JsCatalogEntry> values) {
		if (values.size() > 0) {

			if (cumulative) {
				Range visibleRange = getVisibleRange();
				int visibleItemCount = getVisibleItemCount();
				List<JsCatalogEntry> newDataSet;
				if (start == 0) {
					newDataSet = new ArrayList<JsCatalogEntry>(values);
				} else {
					Iterable<JsCatalogEntry> visibleItems = getVisibleItems();
					newDataSet = new ArrayList<JsCatalogEntry>(visibleItemCount + values.size());
					for (JsCatalogEntry entry : visibleItems) {
						newDataSet.add(entry);
					}
					newDataSet.addAll(start, values);
				}

				// TODO let the storage manager handle all sortings

				// post sorting
				if (postSortField != null) {
					Collections.sort(newDataSet, new JsComparator(postSortField, true));
				}
				// TODO this causes a row count change event, that causes
				// conflict if a cumulative dataWidget is wrapped in a Pager
				hasData.setRowData(visibleRange.getStart(), newDataSet);

				visibleItemCount = getVisibleItemCount();
				if (visibleItemCount == previousCumulativeItemCount) {
					// stop
				} else if (visibleItemCount > previousCumulativeItemCount) {
					visibleRange = getVisibleRange();
					previousCumulativeItemCount = visibleItemCount;
					setVisibleRangeAndClearData(new Range(visibleRange.getStart(), visibleRange.getLength() + FilterData .DEFAULT_INCREMENT), true);
				}
			} else {
				hasData.setRowData(start, values);
			}
			// TODO apply a similar mechanism on catalog forms
			if (notificationsDue != null) {
				String[] notifications = notificationsDue.split(",");
				Iterable<JsCatalogEntry> visibleItems = getVisibleItems();
				JsArray<JsCatalogEntry> visibleItemArray = JavaScriptObject.createArray().cast();

				for (JsCatalogEntry e : visibleItems) {
					visibleItemArray.push(e);
				}

				ModelTransformationConfig modelTransformation;

				for (String element : notifications) {
					modelTransformation = JavaScriptObject.createObject().cast();
					modelTransformation.setSourceData(visibleItemArray);
					modelTransformation.setTarget(element);
					contextServices.getServiceBus().excecuteCommand(ModifyUserInteractionStateModelCommand.COMMAND, modelTransformation, eventBus,
							contextServices, contextParameters, null);
				}
			}
		}
	}

	@Override
	public void setValue(JsArray<JsCatalogEntry> value) {
		if (value == null) {
			throw new NullPointerException("Attempting to set a null value to a Browser");
		}
		if (catalog == null) {
			throw new IllegalStateException("Catalog is not set");
		}

		boolean isArray = GWTUtils.isArray(value);
		if (isArray) {
			for (int i = 0; i < value.length(); i++) {
				if (value.get(i).getId() == null) {
					value.get(i).setId(String.valueOf(i));
				}
			}
			List<JsCatalogEntry> listToWrap = JsArrayList.arrayAsList(value);
			// static data

			ListDataProvider<JsCatalogEntry> staticDataProvider = (ListDataProvider<JsCatalogEntry>) (abstractDataProvider == null ? new ListDataProvider<JsCatalogEntry>(
					keyProvider) : abstractDataProvider);

			staticDataProvider.setList(listToWrap);

			if (this.abstractDataProvider == null) {
				this.abstractDataProvider = staticDataProvider;
				this.abstractDataProvider.addDataDisplay(this);
			}

		} else {
			// dinamic data

			JsFilterData filter = value.cast();
			setValue(filter);
		}

	}

	public void setValue(JsFilterData filter) {
		GWT.log("[content table] new filter value");
		GenericDataProvider dinamicDataProvider = dinamicDataProviderProvider.get();
		
		
		if (this.abstractDataProvider == null) {
			 dinamicDataProvider = dinamicDataProviderProvider.get();
			this.abstractDataProvider = (AbstractDataProvider<JsCatalogEntry>) dinamicDataProvider;
			this.abstractDataProvider.addDataDisplay(this);
		} else {
			dinamicDataProvider = (GenericDataProvider) abstractDataProvider;
			dinamicDataProvider.forceUpdateOnDisplays();
		}
		
		dinamicDataProvider.setCatalog(catalog);
		// properties.isFetchSummary() TODO retrive summary fields only

		dinamicDataProvider.setCustomJoins(customJoins);

		dinamicDataProvider.setFilter(filter);
		ValueChangeEvent.fire(this, filter);
	}

	protected AbstractDataProvider<JsCatalogEntry> getDataProvider() {
		return abstractDataProvider;
	}

	@Override
	public JsArray<JsCatalogEntry> getValue() {
		BrowserSelectionModel selectionModel = (BrowserSelectionModel) getSelectionModel();
		if (selectionModel == null) {
			return null;
		} else {
			return selectionModel.getSelectedItems();
		}
	}

	public JsFilterData getFilterData() {
		if(getDataProvider()==null){
			return null;
		}else{
			return (JsFilterData) ((GenericDataProvider) getDataProvider()).getFilter();
		}
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public String getCatalog() {
		return catalog;
	}

	protected abstract void upateValue(int visibleIndex, JsCatalogEntry receivedUpdate);



	@Override
	public void setBackgroundColor(String backGroundColor) {
		if (styleDelegate != null) {
			styleDelegate.setBackgroundColor(backGroundColor);
		}
	}

	@Override
	public void setTextColor(String textColor) {
		if (styleDelegate != null) {
			styleDelegate.setTextColor(textColor);
		}

	}

	private void invalidateCurrentContentAfterEventLoopResumes() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				Range visibleRange = getVisibleRange();
				setVisibleRangeAndClearData(new Range(visibleRange.getStart(), visibleRange.getLength() + FilterData.DEFAULT_INCREMENT), true);
			}
		});
	}

	@Override
	public void applyAlterations(PanelTransformationConfig properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParamenters) {
		properties.setWidget(widget);
		browserMap.reconfigure(properties, this, contextServices, eventBus, contextParameters);
		if (properties.getFireReset()) {
			setRowData(0, (List<? extends JsCatalogEntry>) getVisibleItems());
		}
	}

	private String widget;

	public void setWidget(String s) {
		this.widget = s;
	}

	@Override
	public void setSelectionModel(SelectionModel<? super JsCatalogEntry> selectionModel) {
		EventTranslator<JsCatalogEntry> translator = new EventTranslator<JsCatalogEntry>() {

			@Override
			public boolean clearCurrentSelection(CellPreviewEvent<JsCatalogEntry> event) {
				return false;
			}

			@Override
			public SelectAction translateSelectionEvent(CellPreviewEvent<JsCatalogEntry> event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				String type = nativeEvent.getType();
				if (BrowserEvents.CLICK.equals(type)) {
					return SelectAction.TOGGLE;
				} else {
					return SelectAction.IGNORE;
				}

			}
		};
		Handler<JsCatalogEntry> h = DefaultSelectionEventManager.createCustomManager(translator);
		if (hasData instanceof AbstractHasData) {
			((AbstractHasData<JsCatalogEntry>) hasData).setSelectionModel(selectionModel, h);
		} else {
			addCellPreviewHandler(h);
		}
		hasData.setSelectionModel(selectionModel);
	}
	
	/*
	 * USER INTERACTION WIDGET
	 */

	@Override
	public HandlerRegistration addRangeChangeHandler(com.google.gwt.view.client.RangeChangeEvent.Handler handler) {
		return hasData.addRangeChangeHandler( handler);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<JsFilterData> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
	
	@Override
	public void onEntryCreated(EntryCreatedEvent e) {
		if (catalog != null && catalog.equals(e.entry.getCatalog())) {
			invalidateCurrentContentAfterEventLoopResumes();
		}
	}

	@Override
	public void onEntriesDeleted(EntriesDeletedEvent e) {
		if (catalog != null && catalog.equals(e.catalog)) {
			invalidateCurrentContentAfterEventLoopResumes();
		}
	}

	@Override
	public void onEntryUpdated(EntryUpdatedEvent e) {
		// asumes underlying functionality retrives entry after it has been
		// updated
		if (catalog != null && catalog.equals(e.entry.getCatalog())) {
			invalidateCurrentContentAfterEventLoopResumes();
		}
	}

	@Override
	public void onEntriesRetrived(EntriesRetrivedEvent e) {
		
	}
}
