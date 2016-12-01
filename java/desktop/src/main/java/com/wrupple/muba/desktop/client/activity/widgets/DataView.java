package com.wrupple.muba.desktop.client.activity.widgets;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.wrupple.muba.desktop.domain.HasUserActions;


public interface DataView<T> extends HasUserActions,IsWidget {
	public HasData<T> getDisplay();
	
	List<T> getVisibleEntries();
	

}
