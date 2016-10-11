package com.wrupple.muba.catalogs;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.hsqldb.jdbc.JDBCDataSource;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.catalogs.domain.CatalogServiceManifest;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.UserCatalogPlugin;

public class JDBCHSQLTestModule extends AbstractModule {
	
	

	@Override
	protected void configure() {
		
		
	}
	
	

	@Provides
	@Inject
	public QueryRunner queryRunner(DataSource ds) {
		return new QueryRunner(ds);
	}

	@Provides
	@Singleton
	@Inject
	public DataSource dataSource() throws SQLException {

		/*
		 * Alternative
		 * http://www.exampit.com/blog/javahunter/9-8-2016-Connection-
		 * Pooling-using-Apache-common-DBCP-And-DBUtils
		 */
		JDBCDataSource ds = new JDBCDataSource();
		ds.setLogWriter(new PrintWriter(System.err));
		ds.setPassword("");
		ds.setUser("SA");
		ds.setUrl("jdbc:hsqldb:mem:aname");
		return ds;
	}
	

	@Provides
	@Inject
	@Singleton
	@Named("catalog.plugins")
	public Object plugins(UserCatalogPlugin /* domain driven */ user) {
		CatalogPlugin[] plugins = new CatalogPlugin[] { user };
		return plugins;
	}
	


	@Provides
	@Inject
	public List<ServiceManifest> foos(CatalogServiceManifest catalog) {
		// this is what makes it purr
		List<ServiceManifest> r = (List) Arrays.asList(catalog);
		return r;
	}
	



}
