package com.wrupple.muba.desktop.client.activity.widgets;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.cms.domain.WruppleDomainHTMLPage;
import com.wrupple.muba.desktop.client.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.desktop.domain.overlay.JsWruppleDomainHTMLPage;
import com.wrupple.vegetate.client.services.StorageManager;

public class HTMLToolbar extends WruppleActivityToolbarBase {

	class HtmlCallback extends DataCallback<JsWruppleDomainHTMLPage> {

		@Override
		public void execute() {
			if (result != null) {
				String html = result.getValue();
				HTMLPanel widget = new HTMLPanel(html);
				panel.setWidget(widget);
			}
		}

	}

	private final SimpleLayoutPanel panel;
	private String htmlId;

	@Inject
	public HTMLToolbar(ToolbarMap toolbarMap) {
		super(toolbarMap);
		panel = new SimpleLayoutPanel();
		initWidget(panel);
	}

	
	public void setHTML(String htmlId){
		this.htmlId=htmlId;
		if(super.contextServices!=null){
			//already initialized
			if (htmlId != null) {
				readAndSet(htmlId,contextServices.getStorageManager());
			}
		}
	}

	@Override
	public void setValue(JavaScriptObject value) {
		
	}

	@Override
	public JavaScriptObject getValue() {
		return null;
	}

	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor,
                           JsProcessTaskDescriptor parameter,
                           JsTransactionApplicationContext contextParameters, EventBus bus,
                           ProcessContextServices contextServices) {
		super.initialize(toolbarDescriptor, parameter, contextParameters, bus, contextServices);
		
		StorageManager sm = contextServices.getStorageManager();
		if (htmlId != null) {
			readAndSet(htmlId, sm);
		}
	}
	
	private void readAndSet(String htmlId,StorageManager sm) {
		HtmlCallback callback = new HtmlCallback();
		sm.read(contextServices.getDesktopManager().getCurrentActivityHost(), contextServices.getDesktopManager().getCurrentActivityDomain(), WruppleDomainHTMLPage.CATALOG, htmlId, callback);
	}

}
