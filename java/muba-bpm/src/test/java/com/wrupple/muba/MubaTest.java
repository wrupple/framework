package com.wrupple.muba;

import com.google.inject.*;
import com.google.inject.name.Names;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.domain.SessionContextImpl;
import com.wrupple.muba.event.server.service.EventRegistry;
import com.wrupple.muba.event.server.service.FormatDictionary;
import com.wrupple.muba.bpm.server.service.BusinessPlugin;
import com.wrupple.muba.bpm.server.service.SolverCatalogPlugin;
import com.wrupple.muba.catalogs.domain.CatalogPeer;
import com.wrupple.muba.catalogs.domain.Trash;
import com.wrupple.muba.catalogs.server.chain.EventSuscriptionChain;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.service.CatalogDeserializationService;
import com.wrupple.muba.catalogs.server.service.CatalogPlugin;
import com.wrupple.muba.catalogs.server.service.UserCatalogPlugin;
import org.apache.commons.dbutils.QueryRunner;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.*;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;

public abstract class MubaTest extends EasyMockSupport {

    /*
	 * mocks
	 */

    protected WriteOutput mockWriter;

    protected WriteAuditTrails mockLogger;

    protected CatalogPeer peerValue;

    protected EventSuscriptionChain chainMock;

    protected FormatDictionary mockFormats;


	public class BPMTestModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(String.class).annotatedWith(Names.named("host")).toInstance("localhost");

            bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
            bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);
            // this makes JDBC the default storage unit
            bind(DataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
            bind(DataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
            bind(DataReadCommand.class).to(JDBCDataReadCommandImpl.class);
            bind(DataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
            bind(DataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);

            // mocks
            mockWriter = mock(WriteOutput.class);
            mockLogger = mock(WriteAuditTrails.class);
            peerValue = mock(CatalogPeer.class);
            chainMock = mock(EventSuscriptionChain.class);
            mockFormats = mock(FormatDictionary.class);
			bind(FormatDictionary.class).toInstance(mockFormats);
            bind(WriteAuditTrails.class).toInstance(mockLogger);
            bind(WriteOutput.class).toInstance(mockWriter);
            bind(EventSuscriptionChain.class).toInstance(chainMock);
			/*
			 * COMMANDS
			 */

            bind(CatalogFileUploadTransaction.class).toInstance(mock(CatalogFileUploadTransaction.class));
            bind(CatalogFileUploadUrlHandlerTransaction.class)
                    .toInstance(mock(CatalogFileUploadUrlHandlerTransaction.class));
		}


        @Provides
        @javax.inject.Inject
        @javax.inject.Singleton
        public SessionContext sessionContext(@Named("host") String peer) {
            long stakeHolder = 1;
            Person stakeHolderValue = mock(Person.class);

            return new SessionContextImpl(stakeHolder, stakeHolderValue, peer, peerValue, CatalogEntry.PUBLIC_ID);
        }

        @Provides
        public UserTransaction localTransaction() {
            return mock(UserTransaction.class);
        }

        @Provides
        public Trash trash() {
            return mock(Trash.class);
        }

        @Provides
        public CatalogDeserializationService catalogDeserializationService() {
            return mock(CatalogDeserializationService.class);
        }


		@Provides
		@javax.inject.Inject
		public QueryRunner queryRunner(DataSource ds) {
			return new QueryRunner(ds);
		}

		@Provides
		@javax.inject.Singleton
		@javax.inject.Inject
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

	static {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
	}

	protected Logger log = LoggerFactory.getLogger(MubaTest.class);

	@Rule
	public EasyMockRule rule = new EasyMockRule(this);

	protected Injector injector;

	protected RuntimeContext runtimeContext;

	public final  void init(Module... modules) {
		injector = Guice.createInjector(modules);
		registerServices(injector.getInstance(EventRegistry.class));
	}

	protected abstract void registerServices(EventRegistry switchs);

	protected abstract void setUp() throws Exception;



}
