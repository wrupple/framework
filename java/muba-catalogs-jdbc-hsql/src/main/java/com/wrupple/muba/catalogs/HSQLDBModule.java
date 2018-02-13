package com.wrupple.muba.catalogs;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.server.service.SQLCompatibilityDelegate;
import com.wrupple.muba.catalogs.server.service.impl.HSQLDBCompatibilityDelegate;

@Singleton
public class HSQLDBModule extends AbstractModule {

	private final String location;

	public HSQLDBModule(String location) {
		this.location = location;
	}

	@Override
	protected void configure() {

		bind(Integer.class).annotatedWith(Names.named("catalog.missingTableErrorCode")).toInstance(
				/*org.hsqldb.error.ErrorCode.X_42501*/5501 * -1/* 1146 in MySQL */);
		
		bind(Character.class).annotatedWith(Names.named("catalog.sql.delimiter")).toInstance('\"');
		if(location==null){
			bind(String.class).annotatedWith(Names.named("catalog.sql.createTable")).toInstance("CREATE TABLE IF NOT EXISTS");
		}else{
			bind(String.class).annotatedWith(Names.named("catalog.sql.createTable")).toInstance("CREATE TEXT TABLE IF NOT EXISTS");
		}
		bind(String.class).annotatedWith(Names.named("catalog.sql.booleanColumnDef")).toInstance("BOOLEAN DEFAULT FALSE");
		bind(String.class).annotatedWith(Names.named("catalog.sql.primaryColumnDef")).toInstance("INT NOT NULL IDENTITY");
		bind(String.class).annotatedWith(Names.named("catalog.sql.foreignKeyColumnDef")).toInstance("INT");
		bind(String.class).annotatedWith(Names.named("catalog.sql.longStringType")).toInstance("LONGVARCHAR");
		bind(String.class).annotatedWith(Names.named("catalog.sql.blobType")).toInstance("LONGVARBINARY");
		bind(String.class).annotatedWith(Names.named("catalog.hsqldb.path")).toInstance(location);


		bind(SQLCompatibilityDelegate.class).to(HSQLDBCompatibilityDelegate.class);
	}

}
