package com.wrupple.muba.desktop.client.services.presentation.impl;




public interface PanelTransition {

    void slideIn(int duration);

    void onBeforeAnimate();
	
	void setInitialState();
	
	void setFinalState();
	
	void onAnimationFinished();

    enum Orientation {
        TOP, BOTTOM, LEFT, RIGHT
    }

}
