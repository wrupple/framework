package com.wrupple.vegetate.server.services;

import com.wrupple.vegetate.domain.VegetateServiceManifest;

public interface RootServiceManifest extends VegetateServiceManifest {

	String NAME = "vegetate";

	VegetateServiceManifest getChildServiceManifest(String service);

	VegetateServiceManifest getChildServiceManifest(RequestScopedContext requestContext, String[] pathTokens);

	String getUrl(VegetateServiceManifest manifest);

}
