package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

public class WidgetWithAnimation extends SimplePanel {
	
	class AnimationStepper extends Timer {
		int step;
		
		public AnimationStepper() {
			super();
			step =0;
		}

		@Override
		public void run() {
			if(animationSteps==null || animationSteps.length==0){
				//do nothing
			}else{
				int stepIndex = step % animationSteps.length;
				
				changeAnimatedWidget(animationSteps[stepIndex]);
				
				step ++;
			}
		}
		
	}
	
	
	IsWidget[] animationSteps;
	IsWidget staticStateWidget;
	Timer animationTimer;

	public WidgetWithAnimation() {
		super();
	}

	public WidgetWithAnimation(IsWidget widget) {
		super();
		this.setWidget(widget);
	}

	public IsWidget[] getAnimationSteps() {
		return animationSteps;
	}

	public void setAnimationSteps(IsWidget[] animationSteps) {
		this.animationSteps = animationSteps;
	}
	
	
	public IsWidget getStaticStateWidget() {
		return staticStateWidget;
	}

	public void setStaticStateWidget(IsWidget staticStateWidget) {
		setWidget(staticStateWidget);
	}
	
	@Override
	public void setWidget(IsWidget w) {
		this.staticStateWidget=w;
		super.setWidget(w);
    }


    protected void changeAnimatedWidget(IsWidget isWidget) {
        super.setWidget(isWidget);
	}


	public void startAnimation(int step){
		animationTimer = new AnimationStepper();
		animationTimer.scheduleRepeating(step);
	}
	
	public void stopAnimation(){
		animationTimer.cancel();
		animationTimer= null;
		this.setWidget(staticStateWidget);
	}
	
}
