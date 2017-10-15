package com.wrupple.muba.catalogs;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;

public class CatalogTestModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(String.class).annotatedWith(Names.named("host")).toInstance("localhost");
		
	}


	@Provides
	@Inject
	@Singleton
	@Named("catalog.plugins")
	public Object plugins() {
		// this is what makes it purr but not as much
		CatalogPlugin[] plugins = new CatalogPlugin[] {  };
		return plugins;
	}

}
