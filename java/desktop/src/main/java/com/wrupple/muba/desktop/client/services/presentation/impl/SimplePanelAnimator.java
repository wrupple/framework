package com.wrupple.muba.desktop.client.services.presentation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.LayoutPanel;

public class SimplePanelAnimator implements PanelAnimator {
	
	class AnimatorCallback implements AnimationCallback{

		@Override
		public void onAnimationComplete() {
			for(PanelTransition transition:transitions){
				transition.onAnimationFinished();
			}
		}

		@Override
		public void onLayout(Layer arg0, double arg1) {
			
		}
		
	}
	
	private LayoutPanel parent;
	private List<PanelTransition> transitions;
	
	
	public SimplePanelAnimator() {
		super();
		transitions = new ArrayList<PanelTransition>();
	}

	@Override
	public void setLayoutPanel(LayoutPanel parent) {
		this.parent=parent;
	}

	@Override
	public void addTransition(PanelTransition transition) {
		transitions.add(transition);
	}

	@Override
	public void addTrantisions(Collection<PanelTransition> transitions) {
		transitions.addAll(transitions);
	}

	@Override
	public void animate(int duration) {
		animate(duration,null);
	}
	
	@Override
	public void animate(int duration, AnimationCallback outtercallback) {
		for(PanelTransition transition:transitions){
			transition.onBeforeAnimate();
			transition.setInitialState();
		}
		parent.forceLayout();
		for(PanelTransition transition:transitions){
			transition.setFinalState();
		}
		parent.animate(duration,outtercallback);
	}

	@SuppressWarnings("unchecked")
	public <T extends PanelTransition> T getTransition() {
		return (T) transitions.get(0);
	}


}
