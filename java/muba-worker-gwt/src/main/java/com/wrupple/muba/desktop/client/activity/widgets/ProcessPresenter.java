package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * An Activity Presenter provides control over how each step of a Workflow is
 * Presented.
 * 
 * This principle is the basis of the funtionality behind Having Data-oriented
 * records of what a wrokflow is. A Workflow is a series of steps that can be
 * described and that description can be saved and interpreted to reproduce the
 * activity with equal results given the same input.
 * 
 * Each of the workflow's steps requires some user interaction following some
 * predefined logical series of desicions that take place on a widget.
 * 
 * Implementing clases control the logical flow of user activities.
 * 
 * 
 * 
 * @author japi
 * 
 */
public interface ProcessPresenter extends IsWidget {
	
	/**
	 * @return exposed output feature so physical attach of widgets can be performed during Process sequence
	 */
	TaskPresenter getRootTaskPresenter();
	

}