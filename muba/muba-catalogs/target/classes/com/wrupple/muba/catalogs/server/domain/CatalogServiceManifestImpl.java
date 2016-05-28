package com.wrupple.muba.catalogs.server.domain;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.HasStakeHolder;
import com.wrupple.vegetate.domain.VegetateServiceManifest;
import com.wrupple.vegetate.server.services.AbstractVegetateServiceManifest;
import com.wrupple.vegetate.server.services.ObjectMapper;
import com.wrupple.vegetate.server.services.RequestScopedContext;

@Singleton
public class CatalogServiceManifestImpl extends AbstractVegetateServiceManifest implements CatalogServiceManifest {

	@Inject
	public CatalogServiceManifestImpl(ObjectMapper mapper,CatalogActionRequestDescriptor descriptor) {
		super(mapper, CatalogActionRequestImpl.class);
		this.descriptor = descriptor;
		this.path=new String[] { CatalogDescriptor.DOMAIN_TOKEN,HasStakeHolder.STAKE_HOLDER_FIELD,CatalogActionRequest.LOCALE_FIELD, CatalogActionRequest.CATALOG_ID_PARAMETER, CatalogActionRequest.CATALOG_ACTION_PARAMETER,
				CatalogActionRequest.CATALOG_ENTRY_PARAMETER, CatalogActionRequest.FORMAT_PARAMETER };
	}

	private final CatalogActionRequestDescriptor descriptor;
	private final String[] path;


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
		return path;
	}

	@Override
	public String buildServiceRequestUri(char tokenSeparator, CatalogActionRequest request) {
		// FIXME expose as a REST service? or share logic with client side?
		int bufferSize = getServiceName().length();

		bufferSize += lengthOfProperty((String)request.getDomain());
		bufferSize += lengthOfProperty(request.getCatalog());
		bufferSize += lengthOfProperty(request.getAction());
		bufferSize += lengthOfProperty(request.getEntry()==null?null:request.getEntry().toString());
		bufferSize += lengthOfProperty(request.getFormat());

		bufferSize = bufferSize + getUrlPathParameters().length;
		StringBuffer buffer = new StringBuffer(bufferSize);
		buffer.append(getServiceName());

		if (request.getDomain() == null) {

		} else {
			buffer.append(tokenSeparator);
			buffer.append(request.getDomain());

			if (request.getCatalog() == null) {

			} else {
				buffer.append(tokenSeparator);
				buffer.append(request.getCatalog());

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

	@Override
	public String[] getChildServicePaths() {
		return null;
	}

	@Override
	public CatalogDescriptor getContractDescriptor() {
		return descriptor;
	}

	@Override
	public List<VegetateServiceManifest> getChildServiceManifests() {
		return null;
	}

	@Override
	protected Context createBlankContext(RequestScopedContext requestContext) {
		return requestContext.getStorageManager().spawn(null);
	}



}
