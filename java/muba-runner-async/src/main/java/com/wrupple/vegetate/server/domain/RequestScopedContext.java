package com.wrupple.vegetate.server.domain;

import org.apache.commons.chain.web.servlet.ServletWebContext;

import com.wrupple.muba.bootstrap.domain.ExcecutionContext;

public interface RequestScopedContext extends ExcecutionContext {
	final String SERVICE_FIELD = "service";
	
	ServletWebContext getServletContext();
	void setServletContext(ServletWebContext ctx);
	
}
