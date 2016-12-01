package com.wrupple.base.server.domain;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.VegetateAuthenticationToken;
import com.wrupple.muba.catalogs.domain.ServiceManifest;
import com.wrupple.muba.catalogs.server.domain.VegetateAuthenticationTokenDescriptor;
import com.wrupple.muba.catalogs.server.services.AbstractServiceManifest;
import com.wrupple.muba.catalogs.server.services.ObjectMapper;

@Singleton
public class OAuthServiceManifest  extends AbstractServiceManifest  implements ServiceManifest {
	public static final String OAUTH_REQUEST_TOKEN = "session.desktopcallback";
	private VegetateAuthenticationTokenDescriptor catalog;
	private final String[] path;

	@Inject
	public OAuthServiceManifest(VegetateAuthenticationTokenDescriptor catalog,ObjectMapper mapper, Provider<UserAuthenticationContext> provider) {
		super(mapper, VegetateAuthenticationTokenImpl.class, provider);
		this.catalog=catalog;
		path =new String[]{CatalogDescriptor.DOMAIN_TOKEN,VegetateAuthenticationToken.REALM_PARAMETER,VegetateAuthenticationToken.ACTION_PARAMETER};
	}

	@Override
	public String getServiceName() {
		return VegetateAuthenticationToken.OAUTH_SERVICE;
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
	public String[] getChildServicePaths() {
		return null;
	}

	@Override
	public List<? extends ServiceManifest> getChildServiceManifests() {
		return null;
	}

	@Override
	public CatalogDescriptor getContractDescriptor() {
		return catalog;
	}

	public String getCallbackUrl(HttpServletRequest request,String realm) {
		// TODO support https?
		int port = request.getServerPort();
		return "http://" + request.getServerName() + (port == 80 ? "/" : ":" + port + "/") + VegetateAuthenticationToken.OAUTH_SERVICE
				+ "/" + realm + "/callback";
	}

	public String getDefaultSuccessUrl(HttpServletRequest request) {
		//TODO does shiro support getting it?
		return request.getContextPath() + "/";
	}

}
