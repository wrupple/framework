package com.wrupple.muba.desktop.client.services.logic;

import com.google.gwt.core.client.JavaScriptObject;
import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.desktop.domain.DesktopPlace;

public interface DesktopManager {
    String APPLICATION_HIERARCHY = "mubaHierarchy";
    String PUBLIC_TOKEN = "swarm";
    String[] RECOVERY_ACTIVITY = {"recovery"};
    String HOME_BUTTON = "homeButton";
	
	String getCurrentActivityDomain();
	
	String getCurrentActivityHost();
	
	void endUserSession();

	DesktopPlace getDefaultPlace();

    <T extends JavaScriptObject> T getUser();

	void setUserObject(JavaScriptObject userData);
	
	/**
	 * @param desktopPlace
	 * @return never null
	 */
	JavaScriptObject getApplicationItem(
			DesktopPlace desktopPlace);
	
	JavaScriptObject getCurrentApplicationItem();

	void putPlaceParameter(String parameter, String unencodedString);

    boolean isLandscape();

	boolean isDesktopyConfigured();
	
	boolean isSSL();

	/**
	 * in web desktops this updates the url
	 * 
	 * @param currentPlace
	 */
	void updatePlace(DesktopPlace currentPlace);

	ApplicationItem getApplicationItem(String itemId);

}
