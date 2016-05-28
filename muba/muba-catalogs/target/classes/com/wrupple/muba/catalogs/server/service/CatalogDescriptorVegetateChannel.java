package com.wrupple.muba.catalogs.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.inject.Provider;

import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.server.domain.CatalogDescriptorImpl;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.shared.services.SignatureGenerator;

public class CatalogDescriptorVegetateChannel extends AbstractCatalogVegetateChannel<CatalogDescriptor> {

	public CatalogDescriptorVegetateChannel(String host, String vegetateUrlBase, ObjectMapper mapper, CatalogServiceManifest serviceManifest,
			Provider<? extends SignatureGenerator> signatureGeneratorProvider) {
		super(host, vegetateUrlBase, mapper, serviceManifest, signatureGeneratorProvider);
	}

	public CatalogDescriptorVegetateChannel(String host, String vegetateUrlBase, ObjectMapper mapper, CatalogServiceManifest serviceManifest,
			SignatureGenerator signatureGenerator) {
		super(host, vegetateUrlBase, mapper, serviceManifest, signatureGenerator);
	}

	@Override
	protected Map<String, CatalogDescriptor> readObject(InputStream inputStream) throws IOException {
		Map<String,CatalogDescriptor> r = (Map)mapper.readMap(inputStream, CatalogDescriptorImpl.class);
		return r;
	}

}
