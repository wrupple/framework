package com.wrupple.muba.cms.server.services.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.PersistentCatalogEntity;
import com.wrupple.muba.cms.domain.HumanTaskData;
import com.wrupple.muba.cms.server.domain.ContentContext;
import com.wrupple.muba.cms.server.domain.HumanTaskDataImpl;
import com.wrupple.muba.cms.server.services.ContentDeterminationService;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.domain.CatalogDescriptorImpl;
import com.wrupple.vegetate.server.domain.FieldDescriptorImpl;
import com.wrupple.vegetate.server.services.AbstractVegetateServiceManifest;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.server.services.RequestScopedContext;

@Singleton
public class ContentDeterminationServiceImpl extends AbstractVegetateServiceManifest implements ContentDeterminationService {

	// TODO si queremos que AI genere el valor final darle un valor reservado
	// tipo &{ai.generate}
	private final String[] tokens = new String[] { CatalogDescriptor.DOMAIN_TOKEN, CatalogActionRequest.CATALOG_ID_PARAMETER,
			CatalogActionRequest.CATALOG_ENTRY_PARAMETER, FieldDescriptor.FIELD, FINAL_VALUE, INITIAL_VALUE };
	private final CatalogDescriptor contract;

	@Inject
	public ContentDeterminationServiceImpl(ObjectMapper mapper) {
		super(mapper, HumanTaskDataImpl.class);
		FieldDescriptor[] fields = new FieldDescriptor[tokens.length];
		FieldDescriptorImpl field;
		String token;
		for (int i = 0; i < tokens.length; i++) {
			token = tokens[i];
			field = new FieldDescriptorImpl();
			field.makeDefault(token, token, "text", CatalogEntry.STRING_DATA_TYPE);
			fields[i] = field;
		}
		contract = new CatalogDescriptorImpl(HumanTaskData.CATALOG, PersistentCatalogEntity.class, -564512335l, "Content Determination Request", fields);
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public String getServiceVersion() {
		return "1.0";
	}

	@Override
	public String[] getUrlPathParameters() {
		return tokens;
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
		return contract;
	}

	@Override
	public String buildServiceRequestUri(char tokenSeparator, long domain, String catalog, String entry, String field, String finalValue, String initialValue) {
		StringBuilder builder = new StringBuilder(70);

		builder.append(SERVICE_NAME);
		builder.append(domain);
		builder.append(tokenSeparator);
		builder.append(catalog);
		builder.append(tokenSeparator);
		builder.append(entry);
		builder.append(tokenSeparator);
		builder.append(field);
		builder.append(tokenSeparator);
		builder.append(finalValue);
		builder.append(tokenSeparator);
		builder.append(initialValue);
		return builder.toString();
	}

	@Override
	protected Context createBlankContext(RequestScopedContext requestContext) {
		return new ContentContext(requestContext.getStorageManager().spawn(null));
	}

}
