package com.wrupple.muba.desktop.server;

import com.wrupple.muba.bpm.server.service.impl.ProcessManagerServerModule;
import com.wrupple.muba.catalogs.server.service.CatalogServerModule;
import com.wrupple.muba.catalogs.server.service.WruppleServerModule;
import com.wrupple.muba.catalogs.server.service.WruppleServerModuleRegistry;
import com.wrupple.muba.cms.server.ContentServerModule;
import com.wrupple.muba.cms.server.services.DataDrivenServerModule;
import com.wrupple.muba.desktop.client.service.impl.DesktopServerModule;
import com.wrupple.muba.desktop.server.service.CredentialsServerModule;
import com.wrupple.muba.desktop.server.service.SocialModule;

import javax.inject.Inject;

public class DesktopModuleRegistry implements WruppleServerModuleRegistry {

    private WruppleServerModule[] modules;

    @Inject
    public DesktopModuleRegistry(CredentialsServerModule credentials, CatalogServerModule catalog, ContentServerModule content, DesktopServerModule desktop,
                                 ProcessManagerServerModule process, SocialModule social, DataDrivenServerModule multitenancy) {
        super();
        this.modules = new WruppleServerModule[]{desktop, credentials, process, catalog, content, social, multitenancy};
    }

    @Override
    public WruppleServerModule[] getModules() {
        return modules;
    }

}