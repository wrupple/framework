package com.wrupple.muba.desktop.client.bootstrap;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractDesktopEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		Widget first = getSplashScreen();
		RootLayoutPanel.get().add(first);
		WruppleDesktopLoader module = getDesktopLoader();
		module.setSplashScreen(first);
		GWT.runAsync(module);
	}

	protected abstract WruppleDesktopLoader getDesktopLoader() ;

	/**
	 * Intentionally left out any support for loading indicators cuz the loading
	 * is intended to be really really fast and letting the splash screen be too
	 * complex would make it need more code.
	 * 
	 * @return
	 */
	protected abstract Widget getSplashScreen();

}
