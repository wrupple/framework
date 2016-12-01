package com.wrupple.muba.desktop.client.factory;

import java.util.Set;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.factory.help.UserAssistanceProvider;
import com.wrupple.muba.desktop.domain.PanelTransformationConfig;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;


/**
 * 
 * Maps a map to a map so you can map it
 * 
 * @author japi
 */
public interface ServiceDictionary<T> extends UserAssistanceProvider {


	/**
	 * Instantiates a new Service/Object on run-time
	 * 
	 * @param id
	 *            id of the Widget Creator
	 * @return new Instance of the widget
	 */
	public T get(String id);
	
	/**
	 * Instantiates a new WIdget on run-time
	 * @param services 
	 * @param bus 
	 * @param ctx 
	 * @param serviceLocator 
	 * @param id
	 *            id of the Widget Creator
	 * @return new Instance of the widget
	 */
	public T getConfigured(JavaScriptObject configuration, ProcessContextServices services, EventBus bus, JsTransactionActivityContext ctx);
	
	void reconfigure(PanelTransformationConfig properties,
			T wruppleActivityToolbarBase, ProcessContextServices contextServices, EventBus eventBus, JsTransactionActivityContext contextParameters);
	
	public Set<String> keySet();
	
	String getDefault();
	
	String getPropertyName();

}
