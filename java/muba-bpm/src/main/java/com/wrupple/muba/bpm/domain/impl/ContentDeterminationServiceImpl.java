package com.wrupple.muba.bpm.domain.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.ServiceManifestImpl;
import com.wrupple.muba.bpm.domain.ContentDeterminationManifest;
import com.wrupple.muba.bpm.domain.HumanTaskData;
import com.wrupple.muba.bpm.server.chain.command.ContentDeterminationRequestInterpret;
import com.wrupple.muba.bpm.server.chain.command.FindSignificantFieldValue;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.FieldDescriptor;

@Singleton
public class ContentDeterminationServiceImpl extends ServiceManifestImpl implements ContentDeterminationManifest {

	private static final String[] tokens = new String[] { CatalogDescriptor.DOMAIN_TOKEN,
			CatalogActionRequest.CATALOG_FIELD, CatalogActionRequest.ENTRY_ID_FIELD, FieldDescriptor.FIELD, FINAL_VALUE,
			INITIAL_VALUE };

	@Inject
	public ContentDeterminationServiceImpl(
			/* HumanTaskData.CATALOG "Content Determination Request" */@Named(HumanTaskData.CATALOG)CatalogDescriptor contractDescriptor, ContentDeterminationRequestInterpret contextParsingCommand, FindSignificantFieldValue contextProcessingCommand) {
		super(SERVICE_NAME, "1.0", contractDescriptor, null, tokens, contextParsingCommand, contextProcessingCommand);
	}

	@Override
	public String buildServiceRequestUri(char tokenSeparator, long domain, String catalog, String entry, String field,
			String finalValue, String initialValue) {
		return super.buildUri(getGrammar(),tokenSeparator,String.valueOf(domain),catalog,entry,field,finalValue,initialValue);
	}

}
