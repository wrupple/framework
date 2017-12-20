package com.wrupple.muba.desktop.client.services.command;

public interface CatalogCommandService extends CommandService {

    void setCatalog(String type);

    void setEntry(String entryId);

    void setAction(String action);

    void setProviderField(String providerField);

    void setFactoryMethod(String factoryMethod);

}
