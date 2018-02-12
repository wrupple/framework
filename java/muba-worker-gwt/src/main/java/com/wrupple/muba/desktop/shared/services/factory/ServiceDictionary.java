package com.wrupple.muba.desktop.shared.services.factory;


import com.wrupple.muba.desktop.shared.services.factory.help.SolverConcensor;
import com.wrupple.muba.worker.domain.ApplicationContext;
import com.wrupple.muba.worker.shared.domain.ReconfigurationBroadcastEvent;

import java.util.Set;


/**
 * 
 * Maps a map to a map so you can map it
 * 
 * @author japi
 */
public interface ServiceDictionary<T> extends SolverConcensor {


	/**
	 * Instantiates a new Service/Object on run-time
	 * 
	 * @param id
	 *            id of the Widget Creator
	 * @return new Instance of the widget
	 */
    T get(String id);

    /**
	 * Instantiates a new WIdget on run-time
     * @param ctx
     *
     * @return new Instance of the widget
	 */
    T getConfigured(ApplicationContext ctx);

    void reconfigure(ReconfigurationBroadcastEvent properties,
                     T subject, ApplicationContext context);

    Set<String> keySet();

    String getDefault();
	
	String getPropertyName();

}
