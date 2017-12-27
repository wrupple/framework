package com.wrupple.muba.desktop.client.service.impl;

import com.wrupple.muba.catalogs.domain.ApplicationItem;
import com.wrupple.muba.cms.domain.ProcessTaskDescriptor;
import com.wrupple.muba.desktop.server.service.DesktopServiceManifest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.VegetateServiceManifest;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class DesktopServiceManifestImpl implements DesktopServiceManifest {

	final String[] PATH_TOKENS = new String[] { ApplicationItem.CATALOG, REPEAT_LAST_TOKEN, ProcessTaskDescriptor.CATALOG, REPEAT_LAST_TOKEN,
			REPEAT_LAST_TOKEN };

	@Override
	public String getServiceName() {
		return NAME;
	}

	@Override
	public String getServiceVersion() {
		return "1.0";
	}

	@Override
	public String[] getUrlPathParameters() {
		return PATH_TOKENS;
	}

	@Override
	public String[] getChildServicePaths() {
		return null;
	}

	@Override
	public List<? extends VegetateServiceManifest> getChildServiceManifests() {
		return null;
	}

	@Override
	public CatalogDescriptor getContractDescriptor() {
		// each activity defines a distinct contract... so i dont think we can
		// provide a generic one... maybe?
		return null;
	}

}
