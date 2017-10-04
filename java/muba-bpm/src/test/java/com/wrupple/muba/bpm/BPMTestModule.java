package com.wrupple.muba.bpm;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.bpm.server.service.BusinessPlugin;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.UserCatalogPlugin;
import org.apache.commons.dbutils.QueryRunner;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.SQLException;

public class BPMTestModule extends AbstractModule {



    @Override
    protected void configure() {

        bind(String.class).annotatedWith(Names.named("host")).toInstance("localhost");

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
    @javax.inject.Inject
    @javax.inject.Singleton
    @Named("catalog.plugins")
    public Object plugins(BusinessPlugin bpm, SolverCatalogPlugin /* domain driven */ runner, UserCatalogPlugin /* domain driven */ user) {
        CatalogPlugin[] plugins = new CatalogPlugin[] { runner,bpm ,user};
        return plugins;
    }


}
