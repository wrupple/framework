package com.wrupple.muba.desktop.client.services.presentation.impl;

import javax.inject.Provider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.AbstractDesktopManager;
import com.wrupple.muba.desktop.domain.DesktopPlace;
import com.wrupple.muba.desktop.domain.overlay.JsApplicationItem;
import com.wrupple.vegetate.shared.services.PeerManager;

public class DesktopManagerImpl extends AbstractDesktopManager implements DesktopManager {

	private final Provider<PlaceController> placeControllerProvider;
	private final Provider<PlaceHistoryMapper> placeHistoryMapper;
	private final Provider<PeerManager> pmp;

	@Inject
	public DesktopManagerImpl(EventBus bus, Provider<PlaceController> placeControllerProvider, Provider<PlaceHistoryMapper> placeHistoryMapper,
			Provider<PeerManager> pmp) {
		super(bus);
		this.pmp = pmp;
		this.placeHistoryMapper = placeHistoryMapper;
		this.placeControllerProvider = placeControllerProvider;

	}

	private native JavaScriptObject get(String factory) /*-{
														var func = $wnd[factory];
														return func();
														}-*/;

	@Override
	public JsApplicationItem getApplicationItem(DesktopPlace desktopPlace) {
		JsApplicationItem match;
		if(desktopPlace.getApplicationItem()==null){
			String[] placeTokens = desktopPlace.getTokens();
			
			if (applicationHierarchy == null) {
				match = null;
			} else {
				JsApplicationItem root = applicationHierarchy.cast();
				desktopPlace.setLastActivityToken(0);
				match = findMatchingApplicationItem(root, placeTokens,desktopPlace);
				desktopPlace.setApplicationItem(match);
			}

		}else{
			match = desktopPlace.getApplicationItem().cast();
		}
		
		return match;
	}

	private JsApplicationItem findMatchingApplicationItem(JsApplicationItem parent,String[] placeTokens, DesktopPlace desktopPlace) {
		JsArray<JsApplicationItem> children = parent.getChildItemsValuesArray();
		int lastActivityToken = desktopPlace.getLastActivityToken();
		String token = placeTokens[lastActivityToken];
		String activity;
		JsApplicationItem child;
		for(int i = 0; i < children.length(); i++){
			child = children.get(i);
			activity = child.getActivity();
			if(token.equalsIgnoreCase(activity)){
				desktopPlace.setLastActivityToken(lastActivityToken+1);
				return findMatchingApplicationItem(child, placeTokens, desktopPlace);
			}
		}
		desktopPlace.setLastActivityToken(lastActivityToken-1);
		return parent;
	}

	@Override
	public void putPlaceParameter(String parameter, String unencodedString) {
		PlaceController pc = placeControllerProvider.get();
		DesktopPlace place = (DesktopPlace) pc.getWhere();
		place.setProperty(parameter, unencodedString);
		updatePlace(place);
	}

	@Override
	public void updatePlace(DesktopPlace currentPlace) {
		// MUST!!!! currentPlace == PlaceController.getWhere()
		PlaceHistoryMapper hp = placeHistoryMapper.get();
		String historyToken = hp.getToken(currentPlace);
		History.replaceItem(historyToken, false);
		//History.newItem(historyToken, false);
	}
	
	@Override
	public boolean isDesktopyConfigured() {
		try {
			if (applicationHierarchy == null) {
				applicationHierarchy = get(APPLICATION_HIERARCHY);
				JsApplicationItem root = applicationHierarchy.cast();
				init(root);
			}
		} catch (Exception e) {

		}

		return applicationHierarchy != null;
	}

	private void init(JsApplicationItem parent) {
		JsArray<JsApplicationItem> children = parent.getChildItemsValuesArray();

		if (children != null && children.length() > 0) {
			JsApplicationItem child;
			for (int i = 0; i < children.length(); i++) {
				child = children.get(i);
				child.setParentValue(parent);
				init(child);
			}

		}
	}

	@Override
	public String getCurrentActivityDomain() {
		JsApplicationItem item = getCurrentApplicationItem();
		boolean overrideUserDomain = item == null ? false : item.isHijackDesktop();
		if (overrideUserDomain) {
			String appDomain = item.getApplicationDomain();
			if(appDomain==null){
				return pmp.get().getDomain();
			}else{
				return appDomain;
			}
			
		} else {
			return pmp.get().getDomain();
		}
	}

	@Override
	public String getCurrentActivityHost() {
		JsApplicationItem item = getCurrentApplicationItem();
		boolean overrideUserDomain = item == null ? false : item.isHijackDesktop();
		if (overrideUserDomain) {
			String appPeer = item.getHost();
			if(appPeer==null){
				return pmp.get().getHost();
			}else{
				return appPeer;
			}
		} else {
			return pmp.get().getHost();
		}
	}

	@Override
	public JsApplicationItem getCurrentApplicationItem() {
		PlaceController pc = placeControllerProvider.get();
		DesktopPlace input = (DesktopPlace) pc.getWhere();
		return getApplicationItem(input);
	}

	@Override
	public boolean isSSL() {
		return Window.Location.getProtocol().equals("https");
	}

	@Override
	public ApplicationItem getApplicationItem(String itemId) {
		JsApplicationItem root = applicationHierarchy.cast();
		return findItemById(root,itemId);
	}

	private JsApplicationItem findItemById(JsApplicationItem parent, String itemId) {
		
		if(itemId.equals(parent.getId())){
			return parent;
		}else{
			JsArray<JsApplicationItem> children = parent.getChildItemsValuesArray();
			if(children==null){
				return null;
			}else{
				for (int i = 0; i < children.length(); i++) {
					parent = children.get(i);
					parent= findItemById(parent, itemId);
					if(parent!=null){
						return parent;
					}
					
				}
				return null;
			}
		}
		
		
	}


}
