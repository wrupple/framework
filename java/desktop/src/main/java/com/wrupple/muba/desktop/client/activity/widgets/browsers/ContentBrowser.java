package com.wrupple.muba.desktop.client.activity.widgets.browsers;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.view.client.HasData;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.widget.HumanTaskProcessor;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsFilterData;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;

public interface ContentBrowser extends HumanTaskProcessor<JsArray<JsCatalogEntry>, JsFilterData>,
		HasData<JsCatalogEntry>{

	// dont ever change
	String WIDGET = "widget";
	String COMMIT_ON_SELECT = "commitOnSelect";
	String NO_PAGER = "none";

	void setRuntimeParams(String catalog, JavaScriptObject properties,
			EventBus bus, JsTransactionActivityContext contextParameters,
			ProcessContextServices contextServices);
	
	public void setCustomJoins(String customJoins) ;
	public void setCumulative(String cumulative) ;
	public void setPostSortField(String postSortField) ;
	public void setNotificationsDue(String notificationsDue);
	public void setBackgroundColor(String backGroundColor);
	public void setTextColor(String textColor);
	public void setWidget(String widget);

}
