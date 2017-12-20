package com.wrupple.muba.desktop.client.activity.widgets.browsers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.view.client.HasData;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.widget.HumanTaskProcessor;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;

public interface ContentBrowser extends HumanTaskProcessor<JsArray<JsCatalogEntry>, JsFilterData>,
		HasData<JsCatalogEntry>{

	// dont ever change
	String WIDGET = "widget";
	String COMMIT_ON_SELECT = "commitOnSelect";
	String NO_PAGER = "none";

	void setRuntimeParams(String catalog, JavaScriptObject properties,
			EventBus bus, JsTransactionApplicationContext contextParameters,
			ProcessContextServices contextServices);

    void setCustomJoins(String customJoins);

    void setCumulative(String cumulative);

    void setPostSortField(String postSortField);

    void setNotificationsDue(String notificationsDue);

    void setBackgroundColor(String backGroundColor);

    void setTextColor(String textColor);

    void setWidget(String widget);

}
