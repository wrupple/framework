package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.wrupple.muba.bpm.client.activity.widget.HumanTaskProcessor;
import com.wrupple.muba.bpm.client.activity.widget.Toolbar;

public interface ContentPanel extends AcceptsOneWidget,IsWidget {
	String REDRAW_FLAG="toolbarRedraw";
	String ActivityPresenterToolbarHeight= "activityPresenterToolbarHeight";
	public enum ToolbarDirection {
		NORTH, EAST, SOUTH, WEST, CENTER, LINE_START, LINE_END,WIDE_START,WIDE_END,SHORT_START,SHORT_END
	}	
	

	

	/**
	 * @return the main user interaction widget for the current task
	 */
	HumanTaskProcessor<? extends JavaScriptObject,?> getMainTaskProcessor();
	void setMainTaskProcessor(HumanTaskProcessor<? extends JavaScriptObject,?> ui);
	
	Toolbar getToolbarById(String toolbarId);

	public void setUnit(String layoutUnit);

	void focusToolbar(String toolbarId);
	
	public boolean isToolbarVisible(String toolbarId);
	
	void focusToolbar(Toolbar activityToolbar);

	void addToolbar(Toolbar toolbar, JavaScriptObject properties);

}
