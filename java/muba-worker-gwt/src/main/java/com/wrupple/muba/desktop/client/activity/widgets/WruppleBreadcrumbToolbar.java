package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.event.ContextSwitchEvent;
import com.wrupple.muba.desktop.client.event.ProcessExitEvent;
import com.wrupple.muba.desktop.client.event.ProcessSwitchEvent;
import com.wrupple.muba.desktop.client.service.data.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.DesktopTheme;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.shared.domain.PanelTransformationConfig;
import com.wrupple.muba.worker.shared.event.EntriesDeletedEvent;
import com.wrupple.muba.worker.shared.event.EntriesRetrivedEvent;
import com.wrupple.muba.worker.shared.event.EntryCreatedEvent;
import com.wrupple.muba.worker.shared.event.EntryUpdatedEvent;

import java.util.HashMap;
import java.util.Map;

public class WruppleBreadcrumbToolbar extends Composite implements BreadcrumbToolbar {

	FlowPanel main;
	Image homeButton;
	final Map<String[], Image> imageMap;
	private int homeButtonHeight;
	private int size;
	private DesktopTheme resources;
	String metaContent = null;
	private PlaceController placeController;
	private JsApplicationItem current;
	private StorageManager sm;
	private DesktopManager dm;

	@Inject
	public WruppleBreadcrumbToolbar(final DesktopManager dm,final PlaceController placeController, DesktopTheme resources, StorageManager sm) {
		super();
		this.dm=dm;
		this.sm = sm;
		this.placeController = placeController;
		main = new FlowPanel();

		NodeList<Element> metaTags = Document.get().getElementsByTagName("meta");
		MetaElement meta;
		String metaTagName;

		for (int i = 0; i < metaTags.getLength(); i++) {
			meta = metaTags.getItem(i).cast();
			metaTagName = meta.getName();
			if (DesktopManager.HOME_BUTTON.equals(metaTagName)) {
				metaContent = meta.getContent();
			}
		}
		this.resources = resources;

		initWidget(main);

		imageMap = new HashMap<String[], Image>();
	}

	protected void toggleFullscreen() {

		// FIXME suggest how to install on ios and android
		// FIXME
		// https://developer.mozilla.org/en-US/Apps/Build/Manifest#fullscreen
		fakeFullScreen();
		requestElementFullscreen();
	}

	// from
	// https://developer.mozilla.org/en-US/docs/Web/Guide/API/DOM/Using_full_screen_mode
	private native void requestElementFullscreen()/*-{
		if (!$doc.fullscreenElement && // alternative standard method
		!$doc.mozFullScreenElement && !$doc.webkitFullscreenElement
				&& !$doc.msFullscreenElement) { // current working methods
			if ($doc.documentElement.requestFullscreen) {
				$doc.documentElement.requestFullscreen();
			} else if ($doc.documentElement.msRequestFullscreen) {
				$doc.documentElement.msRequestFullscreen();
			} else if ($doc.documentElement.mozRequestFullScreen) {
				$doc.documentElement.mozRequestFullScreen();
			} else if ($doc.documentElement.webkitRequestFullscreen) {
				$doc.documentElement
						.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
			}
		} else {
			if ($doc.exitFullscreen) {
				$doc.exitFullscreen();
			} else if ($doc.msExitFullscreen) {
				$doc.msExitFullscreen();
			} else if ($doc.mozCancelFullScreen) {
				$doc.mozCancelFullScreen();
			} else if ($doc.webkitExitFullscreen) {
				$doc.webkitExitFullscreen();
			}
		}
	}-*/;

	private native void fakeFullScreen()/*-{
		$wnd.scrollTo(0, 1);
	}-*/;

	@Override
	public void onPlaceChange(DesktopPlace place, JsApplicationItem item) {
		main.clear();
		if (item == current) {
			return;
		}
		if (homeButton == null) {
			homeButton = imageMap.get(DesktopLoadingStateHolder.homeActivity);
			if (homeButton == null) {

				if (metaContent == null) {
					homeButton = new Image(resources.homeButton());
				} else {
					homeButton = new Image(metaContent);
				}
				imageMap.put(DesktopLoadingStateHolder.homeActivity, homeButton);
				homeButton.addDoubleClickHandler(new DoubleClickHandler() {

					@Override
					public void onDoubleClick(DoubleClickEvent event) {
						placeController.goTo(new DesktopPlace(DesktopLoadingStateHolder.homeActivity));
						toggleFullscreen();
					}
				});
				homeButton.addClickHandler(new GoToPlace(DesktopLoadingStateHolder.homeActivity));
			}
			homeButtonHeight = homeButton.getHeight() <= 0 ? homeButtonHeight : homeButton.getHeight();
		}
		if (homeButtonHeight <= 0) {
			homeButtonHeight = homeButton.getHeight() <= 0 ? homeButtonHeight : homeButton.getHeight();
			if (homeButtonHeight <= 0) {
				homeButtonHeight = size;
			}
		}
		current = item;

		JsArray<JsApplicationItem> breadcrumbs = item.getHierarchy();

		if (breadcrumbs == null || breadcrumbs.length() == 0) {

			main.add(homeButton);

		} else {
			JsApplicationItem crum;
			String[] activity;
			String image;
			Image found;
			for (int i = breadcrumbs.length() - 1; i >= 0; i--) {
				crum = breadcrumbs.get(i);
				activity = crum.getUri();
				image = crum.getImage();
				if (DesktopLoadingStateHolder.homeActivity.equals(activity)) {
					found = homeButton;
				} else {

					found = imageMap.get(activity);
					if (found == null) {

						if (image == null) {
							if (crum.getStaticImageUrl() == null) {
								// no image
								if (crum.getStaticImageUri() == null) {
									found = new Image("/static/img/no-image.png");
								} else {
									found = new Image(crum.getStaticImageUri());
								}
							} else {
								found = new Image(crum.getStaticImageUrl());
							}
						} else {
							if (image.equals(ImageTemplate.IMAGE_RESOURCE)) {
								// safe uri resource
								found = new Image(crum.getStaticImageUri());
							} else if (image.startsWith("/") || image.startsWith("http://")) {
								found = new Image(image);
							} else {
								found = new Image(sm.getRemoteStorageUnit(crum.isHijackDesktop()?crum.getHost():null).getImageUri(dm.getCurrentActivityDomain(), image, 0));
							}
						}
						found.addClickHandler(new GoToPlace(activity));
						found.setPixelSize(found.getWidth()* homeButtonHeight / found.getHeight(),homeButtonHeight );

						imageMap.put(activity, found);
					}
				}

				main.add(found);
			}
		}
	}

	class GoToPlace implements ClickHandler {
		final String[] activity;

		public GoToPlace(String[] activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(ClickEvent event) {
			placeController.goTo(new DesktopPlace(activity));
		}

	}

	@Override
	public void onProcessSwitch(ProcessSwitchEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onContextSwitch(ContextSwitchEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProcessDone(ProcessExitEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor, JsProcessTaskDescriptor parameter, JsTransactionApplicationContext contextParameters,
			EventBus bus, ProcessContextServices contextServices) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setType(String s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyAlterations(PanelTransformationConfig properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParamenters) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(JavaScriptObject value) {
		// TODO Auto-generated method stub

	}

	@Override
	public JavaScriptObject getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSize(int height) {
		this.size = height;
	}

	@Override
	public void onEntriesDeleted(EntriesDeletedEvent entriesDeletedEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntriesRetrived(EntriesRetrivedEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryUpdated(EntryUpdatedEvent entryUpdatedEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEntryCreated(EntryCreatedEvent entryCreatedEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<JavaScriptObject> handler) {
		// TODO Auto-generated method stub
		return null;
	}

}
