package com.wrupple.muba.catalogs.server.domain;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.wrupple.muba.bootstrap.domain.CatalogActionRequest;
import com.wrupple.muba.bootstrap.domain.ServiceManifestImpl;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.chain.CatalogEngine;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;

@Singleton
public class CatalogServiceManifestImpl extends ServiceManifestImpl implements CatalogServiceManifest {


	private static final String[] GRAMMAR = new String[] { CatalogDescriptor.DOMAIN_TOKEN, CatalogActionRequest.LOCALE_FIELD,
						CatalogActionRequest.CATALOG_FIELD, CatalogActionRequest.CATALOG_ACTION_PARAMETER,
						CatalogActionRequest.ENTRY_ID_FIELD, CatalogActionRequest.FORMAT_PARAMETER };

	@Inject
	public CatalogServiceManifestImpl(
			@Named(CatalogActionRequest.CATALOG) CatalogDescriptor descriptor, CatalogRequestInterpret requestInterpret, CatalogEngine catalogEngine) {
		super(SERVICE_NAME, "1.0", descriptor, null,
				GRAMMAR, requestInterpret, catalogEngine);
	}

	@Override
	public String buildServiceRequestUri(char tokenSeparator, CatalogActionRequest request) {
		// FIXME expose as a REST service? or share logic with client side?
		int bufferSize = getServiceName().length();

		bufferSize += lengthOfProperty((String) request.getDomain());
		bufferSize += lengthOfProperty(request.getCatalogType());
		bufferSize += lengthOfProperty(request.getAction());
		bufferSize += lengthOfProperty(request.getEntry() == null ? null : request.getEntry().toString());
		bufferSize += lengthOfProperty(request.getFormat());

		bufferSize = bufferSize + getGrammar().length;
		StringBuffer buffer = new StringBuffer(bufferSize);
		buffer.append(getServiceName());

		if (request.getDomain() == null) {

		} else {
			buffer.append(tokenSeparator);
			buffer.append(request.getDomain());

			if (request.getLocale() == null) {

			} else {
				buffer.append(tokenSeparator);
				buffer.append(request.getLocale());
				if (request.getCatalogType() == null) {

				} else {
					buffer.append(tokenSeparator);
					buffer.append(request.getCatalogType());

					if (request.getAction() == null) {

					} else {
						buffer.append(tokenSeparator);
						buffer.append(request.getAction());

						if (request.getEntry() == null) {

						} else {
							buffer.append(tokenSeparator);
							buffer.append(request.getEntry());

							if (request.getFormat() == null) {

							} else {
								buffer.append(tokenSeparator);
								buffer.append(request.getFormat());
							}
						}

					}

				}
			}
		}

		String permission = buffer.toString();

		return permission;
	}

	private int lengthOfProperty(String p) {
		if (p == null) {
			return 0;
		} else {
			return p.length() + 1;
		}
	}

	

}
