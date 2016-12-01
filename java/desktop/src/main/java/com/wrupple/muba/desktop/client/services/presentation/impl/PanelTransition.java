package com.wrupple.muba.desktop.client.services.presentation.impl;




public interface PanelTransition {
	
	public static enum Orientation {
		TOP, BOTTOM, LEFT, RIGHT
	}
	
	void onBeforeAnimate();
	
	void setInitialState();
	
	void setFinalState();
	
	void onAnimationFinished();
	
	public void slideIn(int duration) ;
	
}
