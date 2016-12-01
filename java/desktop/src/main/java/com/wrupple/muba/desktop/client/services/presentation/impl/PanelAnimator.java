package com.wrupple.muba.desktop.client.services.presentation.impl;

import java.util.Collection;

import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.user.client.ui.LayoutPanel;

/**
 * 
 * All animated widgets should be added to the panel before  the animation begins
 * 
 * @author japi
 *
 */
public interface PanelAnimator {
	public void setLayoutPanel(LayoutPanel parent);
	
	public void addTransition(PanelTransition transition);
	
	public void addTrantisions(Collection<PanelTransition> transitions);
	
	public void animate(int duration);
	
	public <T extends PanelTransition> T getTransition();

	public void animate(int i, AnimationCallback callback);
}
