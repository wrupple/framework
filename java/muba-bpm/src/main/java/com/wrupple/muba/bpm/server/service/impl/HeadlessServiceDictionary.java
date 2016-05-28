package com.wrupple.muba.bpm.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.impl.CatalogBase;

import com.wrupple.muba.bpm.server.chain.BusinessEngine;
import com.wrupple.muba.bpm.server.service.WebEventServiceManifest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.cms.server.services.ContentDeterminationService;
import com.wrupple.vegetate.server.chain.command.VegetateService;
import com.wrupple.vegetate.server.chain.command.ServiceDictionary;
import com.wrupple.vegetate.server.services.RootServiceManifest;

@Singleton
public class HeadlessServiceDictionary extends CatalogBase implements ServiceDictionary {

	@Inject
	public HeadlessServiceDictionary(RootServiceManifest root,VegetateService vegetate,CatalogServiceManifest catalogManifest,CatalogEngine catalog,ContentDeterminationService processManifest,BusinessEngine bp,WebEventServiceManifest eventManifest) {
		addCommand(root.getServiceId(), vegetate);
		addCommand(catalogManifest.getServiceId(), catalog);
		addCommand(processManifest.getServiceId(), bp);
		addCommand(eventManifest.getServiceId(), catalog);
	}
	
}
