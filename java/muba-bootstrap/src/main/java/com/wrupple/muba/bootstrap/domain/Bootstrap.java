package com.wrupple.muba.bootstrap.domain;

import java.util.Map;

/**
 * mutating unified business adaptor
 * @author japi
 *
 */
public interface Bootstrap extends ParentServiceManifest {

	final String NAME = "root",THREAD = "vegetate.thread",MANIFEST_HOLDER = "manifestObj";
	

	ParentServiceManifest getFallbackService();


	Map<String, ServiceManifest> getVersions(String service);





}
