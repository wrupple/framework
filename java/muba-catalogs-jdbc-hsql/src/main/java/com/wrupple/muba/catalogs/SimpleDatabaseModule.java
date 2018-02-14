package com.wrupple.muba.catalogs;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.Person;
import com.wrupple.muba.event.domain.impl.ContentNodeImpl;
import com.wrupple.muba.event.server.chain.command.BindService;
import com.wrupple.muba.event.server.chain.command.Dispatch;
import com.wrupple.muba.event.server.chain.command.impl.BindServiceImpl;
import com.wrupple.muba.event.server.chain.command.impl.DispatchImpl;
import org.apache.commons.dbutils.QueryRunner;
import org.hsqldb.jdbc.JDBCDataSource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;

public class SimpleDatabaseModule extends AbstractModule {


    private final String location;

    public SimpleDatabaseModule(String location) {
        this.location = location;
    }


    protected void configure() {


        bind(String.class).annotatedWith(Names.named("worker.charset")).toInstance("UTF-8");
        bind(String.class).annotatedWith(Names.named("worker.importHandler.catalog")).toInstance("workerImportHandlers");
        bind(String.class).annotatedWith(Names.named("host")).toInstance("localhost");
        bind(Boolean.class).annotatedWith(Names.named("event.parallel")).toInstance(false);
        bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
        bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);

        bind(BindService.class).to(BindServiceImpl.class);
        bind(Dispatch.class).to(DispatchImpl.class);

        // this makes JDBC the default storage unit
        bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
        bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
        bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
        bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
        bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);
    }

    /*
     * CONFIGURATION
     */


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
        ds.setUrl("jdbc:hsqldb:file:"+location);
        return ds;
    }


}
