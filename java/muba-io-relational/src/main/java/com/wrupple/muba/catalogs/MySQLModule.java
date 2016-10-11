package com.wrupple.muba.catalogs;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.server.service.SQLCompatibilityDelegate;
import com.wrupple.muba.catalogs.server.service.impl.MySQLCompatibilityDelegate;

public class MySQLModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Character.class).annotatedWith(Names.named("catalog.sql.delimiter")).toInstance('`');
		bind(String.class).annotatedWith(Names.named("catalog.sql.createTable")).toInstance("CREATE TABLE IF NOT EXISTS");
		bind(String.class).annotatedWith(Names.named("catalog.sql.booleanColumnDef")).toInstance("BOOL DEFAULT FALSE");
		bind(String.class).annotatedWith(Names.named("catalog.sql.primaryColumnDef")).toInstance("INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY");
		bind(String.class).annotatedWith(Names.named("catalog.sql.foreignKeyColumnDef")).toInstance("INT UNSIGNED NOT NULL");
		bind(String.class).annotatedWith(Names.named("catalog.sql.longStringType")).toInstance("TEXT");
		bind(String.class).annotatedWith(Names.named("catalog.sql.longStringType")).toInstance("BLOB");
		
		
		//bind(String.class).annotatedWith(Names.named("catalog.sql.")).toInstance("");
		
		bind(SQLCompatibilityDelegate.class).to(MySQLCompatibilityDelegate.class);
		
	}

}
