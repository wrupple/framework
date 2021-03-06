package com.wrupple.muba.catalogs.server.domain;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;

@Singleton
public class CatalogServiceManifestImpl extends ServiceManifestImpl implements CatalogServiceManifest {

	private static final long serialVersionUID = 5444635742533027017L;

	@Inject
	public CatalogServiceManifestImpl(@Named(CatalogActionRequest.CATALOG) CatalogDescriptor descriptor) {
		super(SERVICE_NAME, "1.0", descriptor, Arrays.asList(new String[] {
				CatalogDescriptor.DOMAIN_FIELD, CatalogActionRequest.LOCALE_FIELD,
				CatalogActionRequest.CATALOG_FIELD, CatalogActionRequest.NAME_FIELD,
				CatalogActionRequest.ENTRY_ID_FIELD, CatalogActionRequest.FORMAT_PARAMETER }));
	}

}
