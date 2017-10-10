package com.wrupple.muba.catalogs.server.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.chain.impl.ContextBase;

import com.google.inject.Provider;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogNamespace;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;
import com.wrupple.muba.catalogs.server.domain.CatalogException;

public class NamespaceContextImpl extends ContextBase implements NamespaceContext {

	private static final long serialVersionUID = -5394153010938493948L;

	private CatalogNamespace namespace;
	private final Provider<CatalogReadTransaction> read;
	private final Provider<CatalogNamespace> defaultNamespace;

	private final boolean multitenant;

	@Inject
	public NamespaceContextImpl(@Named("system.multitenant")boolean multitenant,Provider<CatalogReadTransaction> read,
			Provider<CatalogNamespace> defaultNamespace) {
		super();
		this.multitenant=multitenant;
		this.read=read;
		this.defaultNamespace = defaultNamespace;
		if(!multitenant){
			this.namespace = defaultNamespace.get();
		}
		
	}
	

	@Override
	public void setId(long requestedDomain,CatalogActionContext context) throws CatalogException {
		if(multitenant){
			clear();
			try {
				this.namespace = context.triggerGet(CatalogNamespace.CATALOG,requestedDomain);
			} catch (Exception e) {
				throw new CatalogException(e);
			}
			if(namespace==null){
				this.namespace=defaultNamespace.get();
			}
		}
	}
	@Override
	public void setDomain(Object domain) {
	}



	@Override
	public String getAnonymousPrincipal() {
		return namespace.getAnonymousPrincipal();
	}

	@Override
	public boolean isRecycleBinEnabled() {
		return namespace.isRecycleBinEnabled();
	}

	@Override
	public String getCurrencyCode() {
		return namespace.getCurrencyCode();
	}

	@Override
	public boolean isGarbageCollectionEnabled() {
		return namespace.isGarbageCollectionEnabled();
	}

	@Override
	public String getLocale() {
		return namespace.getLocale();
	}

	@Override
	public List<String> getProperties() {
		return namespace.getProperties();
	}

	@Override
	public void setProperties(List<String> properties) {
		namespace.setProperties(properties);
	}

	@Override
	public List<String> getGlobalContextExpressions() {
		return namespace.getGlobalContextExpressions();
	}

	@Override
	public Object getDomain() {
		return namespace.getId();
	}

	@Override
	public boolean isAnonymouslyVisible() {
		return namespace.isAnonymouslyVisible();
	}

	@Override
	public void setAnonymouslyVisible(boolean p) {

	}

	@Override
	public Long getImage() {
		return (Long) namespace.getImage();
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public String getName() {
		return namespace.getName();
	}

	@Override
	public String getCatalogType() {
		return namespace.getCatalogType();
	}

	@Override
	public Object getId() {
		return namespace.getId();
	}

	@Override
	public boolean isMultitenant() {
		return multitenant;
	}

	@Override
	public void setNamespace(CatalogActionContext context) {
		
	}

	@Override
	public void unsetNamespace(CatalogActionContext context) {
		
	}



}
