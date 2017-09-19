package com.wrupple.muba.event.domain;

import java.util.Map;

/**
 *
 *
 *
 * mutating unified business adaptor
 * @author japi
 *
 */
public interface ParentServiceManifest extends ServiceManifest {

	final String NAME = "root",THREAD = "vegetate.thread",MANIFEST_HOLDER = "manifestObj";
	

	ServiceManifest getFallbackService();


	Map<String, ServiceManifest> getVersions(String service);


	void setFallBackService(ServiceManifest instance);


	void register(ServiceManifest manifest);

}
