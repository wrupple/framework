package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class FadingTransition {

	static class FadingAnimation extends Animation {
		IsWidget next; Widget current; AnimationCallback callback;

		protected FadingAnimation(IsWidget next, Widget current, AnimationCallback callback) {
			super();
			this.next = next;
			this.current = current;
			this.callback = callback;
		}

		@Override
		protected void onUpdate(double progress) {
			if(callback!=null){
				callback.onLayout(null, progress);
			}
			if(current!=null){
				current.getElement().getStyle().setOpacity((float) (1 - progress));
			}
			next.asWidget().getElement().getStyle().setOpacity( (float) progress);
		}

		@Override
		protected void onComplete() {
			super.onComplete();
			if(callback!=null){
				callback.onAnimationComplete();
			}
		}

	}

	/**
	 * @param next
	 * @param current
	 *            can be null
	 * @param callback
	 * @return
	 */
	public static Animation fade(IsWidget next, Widget current, AnimationCallback callback) {
		return new FadingAnimation(next, current, callback);
	}

}
