package com.wrupple.muba.catalogs;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.server.service.SQLCompatibilityDelegate;
import com.wrupple.muba.catalogs.server.service.impl.HSQLDBCompatibilityDelegate;

@Singleton
public class HSQLDBModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Character.class).annotatedWith(Names.named("catalog.sql.delimiter")).toInstance('\"');
		bind(String.class).annotatedWith(Names.named("catalog.sql.createTable")).toInstance("CREATE TABLE IF NOT EXISTS");
		bind(String.class).annotatedWith(Names.named("catalog.sql.booleanColumnDef")).toInstance("BOOLEAN DEFAULT FALSE");
		bind(String.class).annotatedWith(Names.named("catalog.sql.primaryColumnDef")).toInstance("INT NOT NULL IDENTITY");
		bind(String.class).annotatedWith(Names.named("catalog.sql.foreignKeyColumnDef")).toInstance("INT");
		bind(String.class).annotatedWith(Names.named("catalog.sql.longStringType")).toInstance("LONGVARCHAR");
		bind(String.class).annotatedWith(Names.named("catalog.sql.blobType")).toInstance("LONGVARBINARY");
		
		
		bind(SQLCompatibilityDelegate.class).to(HSQLDBCompatibilityDelegate.class);
	}

}
