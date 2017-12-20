package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.user.client.ui.LayoutPanel;

import java.util.Collection;

/**
 * 
 * All animated widgets should be added to the panel before  the animation begins
 * 
 * @author japi
 *
 */
public interface PanelAnimator {
    void setLayoutPanel(LayoutPanel parent);

    void addTransition(PanelTransition transition);

    void addTrantisions(Collection<PanelTransition> transitions);

    void animate(int duration);

    <T extends PanelTransition> T getTransition();

    void animate(int i, AnimationCallback callback);
}
