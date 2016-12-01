package com.wrupple.muba.desktop.client.services.command;

public interface CatalogCommandService extends CommandService {
	
	public void setCatalog(String type);

	public void setEntry(String entryId) ;
	
	public void setAction(String action) ;

	public void setProviderField(String providerField);

	public void setFactoryMethod(String factoryMethod) ;

}
