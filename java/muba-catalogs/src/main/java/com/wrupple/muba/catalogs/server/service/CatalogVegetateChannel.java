package com.wrupple.muba.catalogs.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.inject.Provider;

import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.domain.CatalogActionResultImpl;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.shared.services.SignatureGenerator;

public class CatalogVegetateChannel extends AbstractCatalogVegetateChannel< CatalogActionResultImpl> {

	public CatalogVegetateChannel(String host, String vegetateUrlBase, ObjectMapper mapper, CatalogServiceManifest serviceManifest,
			Provider<? extends SignatureGenerator> signatureGeneratorProvider) {
		super(host, vegetateUrlBase, mapper, serviceManifest, signatureGeneratorProvider);
	}

	public CatalogVegetateChannel(String host, String vegetateUrlBase, ObjectMapper mapper, CatalogServiceManifest serviceManifest,
			SignatureGenerator signatureGenerator) {
		super(host, vegetateUrlBase, mapper, serviceManifest, signatureGenerator);
	}


	@Override
	protected Map<String,CatalogActionResultImpl> readObject(InputStream inputStream) throws IOException  {
		Map<String,CatalogActionResultImpl> r = mapper.readMap(inputStream, CatalogActionResultImpl.class);
		return r;
	}


}
