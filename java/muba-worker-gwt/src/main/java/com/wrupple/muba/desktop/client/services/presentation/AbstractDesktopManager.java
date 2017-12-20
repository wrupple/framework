package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.domain.DesktopLoadingStateHolder;
import com.wrupple.muba.desktop.domain.DesktopPlace;

public abstract class AbstractDesktopManager implements DesktopManager{

	protected JavaScriptObject userObject;
	protected EventBus eventBus;

	protected JavaScriptObject applicationHierarchy;

	@Inject
	public AbstractDesktopManager(EventBus bus) {
		super();
		this.eventBus = bus;
	}

	
	@Override
	public DesktopPlace getDefaultPlace() {
		//TODO
		return new DesktopPlace(DesktopLoadingStateHolder.homeActivity);
	}

	/**
	 * wether succesful or not, this is called after its finished
	 */
	static class AuthFinished implements Command {
		@Override
		public void execute() {

		}

	}

	@SuppressWarnings("unchecked")
	public final <T extends JavaScriptObject> T getUser() {
		return (T) userObject;
	}

	// REMEMBER SESSION PROPAGATION?
	
	@Override
	public void endUserSession() {
		//FIXME meta tag to configure logout url
		Window.Location.assign("j_spring_security_logout");
	}

	@Override
	public void setUserObject(JavaScriptObject userData) {
		this.userObject = userData;
	}
	
	@Override
	public boolean isLandscape() {
		return Window.getClientHeight() < Window.getClientWidth();
	}
	
	
}