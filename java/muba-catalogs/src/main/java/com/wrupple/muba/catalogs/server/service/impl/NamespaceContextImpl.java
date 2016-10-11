package com.wrupple.muba.catalogs.server.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.chain.impl.ContextBase;

import com.google.inject.Provider;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogNamespace;
import com.wrupple.muba.catalogs.domain.NamespaceContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogReadTransaction;

public class NamespaceContextImpl extends ContextBase implements NamespaceContext {

	private static final long serialVersionUID = -5394153010938493948L;

	private CatalogNamespace namespace;
	private final Provider<CatalogReadTransaction> read;
	private final Provider<CatalogNamespace> defaultNamespace;
	private final String host;

	private final boolean multitenant;

	@Inject
	public NamespaceContextImpl(@Named("system.multitenant")boolean multitenant,@Named("host") String host,Provider<CatalogReadTransaction> read,
			Provider<CatalogNamespace> defaultNamespace) {
		super();
		this.multitenant=multitenant;
		this.read=read;
		this.host=host;
		this.defaultNamespace = defaultNamespace;
		if(!multitenant){
			this.namespace = defaultNamespace.get();
		}
		
	}

	@Override
	public void switchToUserDomain(CatalogActionContext context) throws NumberFormatException, Exception {
		if(multitenant){
			SessionContext session = context.getExcecutionContext().getSession();
			if(session.getStakeHolderValue()!=null){
				if(session.getStakeHolderValue().getProperties()!=null){
					List<String> props = session.getStakeHolderValue().getProperties();
					for(String p : props){
						if(p.startsWith(host+"_namespace")){
							setId(Long.parseLong(p.substring(p.indexOf('=')+1, p.length()-1)),context);
							return;
						}
					}
					this.namespace=defaultNamespace.get();
				}else{
					this.namespace=defaultNamespace.get();
				}
			}else{
				this.namespace=defaultNamespace.get();
			}
		}
		
	}

	@Override
	public void setId(long requestedDomain,CatalogActionContext context) throws Exception {
		if(multitenant){
			clear();
			CatalogActionContext spawn = context.getCatalogManager().spawn(context);
			spawn.setEntry(requestedDomain);
			spawn.setCatalog(CatalogNamespace.CATALOG);
			read.get().execute(spawn);
			this.namespace = spawn.getResult();
		}
	}
	@Override
	public void setDomain(Long domain) {
	}

	@Override
	public void setIdAsString(String id) {
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
	public String getImage() {
		return namespace.getImage();
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
	public String getIdAsString() {
		return namespace.getIdAsString();
	}

	@Override
	public boolean isMultitenant() {
		return multitenant;
	}

}
