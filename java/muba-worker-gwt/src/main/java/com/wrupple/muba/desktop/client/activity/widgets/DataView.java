package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.HasData;
import com.wrupple.muba.desktop.domain.HasUserActions;

import java.util.List;


public interface DataView<T> extends HasUserActions,IsWidget {
    HasData<T> getDisplay();

    List<T> getVisibleEntries();
	

}
