package com.wrupple.muba.desktop.client.bootstrap;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel;
import com.wrupple.muba.desktop.client.activity.widgets.ContentPanel.ToolbarDirection;
import com.wrupple.muba.desktop.client.activity.widgets.panels.NestedActivityPresenter;
import com.wrupple.muba.desktop.client.activity.widgets.toolbar.HomeToolbar;
import com.wrupple.muba.desktop.client.bootstrap.state.DesktopLoadingState;
import com.wrupple.muba.desktop.client.bootstrap.state.ReadDesktopMetadata;
import com.wrupple.muba.desktop.client.event.DesktopInitializationDoneEvent;
import com.wrupple.muba.desktop.client.event.DesktopProcessEvent;
import com.wrupple.muba.desktop.client.event.VegetateEvent;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;

/**
 * 
 * @author japi
 * 
 */
public final class WruppleDesktopLoader implements RunAsyncCallback {

	static class PlaceChangeListener implements com.google.gwt.place.shared.PlaceChangeEvent.Handler{
		final NestedActivityPresenter main ;
		
		final HomeToolbar toolbar;
		
		final DesktopManager dm;
		
		
		public PlaceChangeListener(NestedActivityPresenter main, HomeToolbar toolbar,DesktopManager dm) {
			super();
			this.dm=dm;
			this.main = main;
			this.toolbar = toolbar;
			
			NodeList<Element> metaTags = Document.get().getElementsByTagName("meta");
			MetaElement meta;
			String metaTagName;
			String metaContent = null;
			for (int i = 0; i < metaTags.getLength(); i++) {
				meta = metaTags.getItem(i).cast();
				metaTagName = meta.getName();
				if (ContentPanel.ActivityPresenterToolbarHeight.equals(metaTagName)) {
					metaContent = meta.getContent();
				}
			}
			int height;
			if (metaContent == null) {
				height = 30;
			} else {
				height = Integer.parseInt(metaContent);
			}
			toolbar.setSize(height);
			main.addToolbarAndRedraw(toolbar, false, false, ToolbarDirection.NORTH, "home", height);
		}


		@Override
		public void onPlaceChange(PlaceChangeEvent event) {
			DesktopPlace place = (DesktopPlace) event.getNewPlace();
			String[] newActivity = place.getTokens();
			JsApplicationItem item = (JsApplicationItem) dm.getApplicationItem(place);
			toolbar.onPlaceChange(place,item);
			if(DesktopLoadingStateHolder.homeActivity.equals(newActivity)){
				main.hideToolbar(toolbar);
			}else{
				main.showToolbar(toolbar);
			}
		}
		
	}
	
	public interface FactoryLoader {
		WruppleDesktopModule getFactory();
		DesktopLoadingState[] getLoadingStates();
	}
	
	private static WruppleDesktopModule factory;
	private Widget first;
	private final FactoryLoader factoryLoader;

	
	public WruppleDesktopLoader(FactoryLoader factoryLoader) {
		this.factoryLoader=factoryLoader;
	}
	

	public static WruppleDesktopModule getBaseModule(){
		return WruppleDesktopLoader.factory;
	}
	
	@Override
	public void onFailure(Throwable reason) {
		Window.prompt("You broke me, are you happy?", reason.toString());
	}
	

	@Override
	public void onSuccess() {
		factory = factoryLoader.getFactory();
		DesktopLoadingState[] loadingStates = factoryLoader.getLoadingStates();
		/*
		 * Load Basic Strictures
		 */

		final EventBus eventBus = factory.getEventBus();
		
		/*
		 * Create Process
		 */
		SequentialProcess<DesktopLoadingStateHolder, DesktopLoadingStateHolder> loading = new SequentialProcess<DesktopLoadingStateHolder, DesktopLoadingStateHolder>();

		// register places
		ReadDesktopMetadata registerPlaces = factory.getPlaceRegister();
		loading.addState(registerPlaces);
		
		if(loadingStates!=null){
			for(DesktopLoadingState s: loadingStates){
				loading.add(s);
			}
		}
		
		DesktopLoadingStateHolder grandNew = new DesktopLoadingStateHolder();
		loading.start(grandNew, new DataCallback<DesktopLoadingStateHolder>() {

			@Override
			public void execute() {
				//remove splash screen
				final PlaceController placeController = factory.getPlaceController();
				final PlaceHistoryMapper historyMapper=factory.getPlaceHistoryMapper();
				final DesktopManager dm = factory.getDesktopManager();
				if(first!=null){
					RootLayoutPanel.get().remove(first);
					first=null;
				}
				final ActivityManager activityManager = new ActivityManager(
						factory.getActivityMapper(), eventBus);
				
				NestedActivityPresenter main = new NestedActivityPresenter(dm);
				main.setStyleName("desktop");
				HomeToolbar toolbar = factory.getHomeToolbar();
				activityManager.setDisplay(main.getRootTaskPresenter());
				eventBus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeListener(main, toolbar, dm));
				eventBus.addHandler(VegetateEvent.TYPE, toolbar);
				eventBus.addHandler(DesktopProcessEvent.TYPE,toolbar);
				RootLayoutPanel.get().add(main);
				// Fire Event
				eventBus.fireEvent(new DesktopInitializationDoneEvent());
				// Goes to the place represented on URL else default place
				final PlaceHistoryHandler paceHistoryHandler = new PlaceHistoryHandler(historyMapper);
				paceHistoryHandler.register(placeController, eventBus,
						dm.getDefaultPlace());
				GWT.log("Desktop Loading finished, handling current url history state");
				paceHistoryHandler.handleCurrentHistory();
			}
		}, eventBus);
	}
	

	public void setSplashScreen(Widget first) {
		this.first=first;
	}


	
}
