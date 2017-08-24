package com.wrupple.vegetate.server.domain;

import com.wrupple.muba.event.domain.RuntimeContext;
import org.apache.commons.chain.web.servlet.ServletWebContext;

public interface RequestScopedContext extends RuntimeContext {
	final String SERVICE_FIELD = "service";
	
	ServletWebContext getServletContext();
	void setServletContext(ServletWebContext ctx);
	
}
