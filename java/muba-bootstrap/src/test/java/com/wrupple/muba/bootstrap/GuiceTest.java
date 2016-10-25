package com.wrupple.muba.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.BootstrapTest;
import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.ExcecutionContextImpl;
import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.ParentServiceManifest;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;

public class GuiceTest extends BootstrapTest {

	private final Injector injector;

	public GuiceTest() {
		super();
		injector = Guice.createInjector(new MockModule(), new BootstrapModule());
		muba = injector.getInstance(Bootstrap.class);
	}

	@Before
	public void prepare() {
		excecutionContext = injector.getInstance(ExcecutionContext.class);
	}

	@Test
	public void nesting() throws Exception {
		log.trace("[-nesting-]");
		// version 1.1, nesting return double (1 + ( 2*1.1 ) )
		excecutionContext.setSentence(ADDITION, UPGRADED_VERSION, "1", MULTIPLICATION, "1.1", ADDITION,
				UPGRADED_VERSION, "1", "1");
		muba.getContextProcessingCommand().execute(excecutionContext);
		Double result = (Double) excecutionContext.get(RESULT_PARAMETER_NAME);
		assertNotNull(result);
		assertEquals(result.doubleValue(), 3.2, 0);
	}

	class MockModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(Person.class).toInstance(mock(Person.class));
			bind(Host.class).toInstance(mock(Host.class));
			bind(ParentServiceManifest.class).annotatedWith(Names.named("bootstrap.seoAwareService"))
					.toInstance(mock(ParentServiceManifest.class));

		}

		@Provides
		@Inject
		public ExcecutionContext system(SessionContext session) {
			return new ExcecutionContextImpl(new PrintWriter(System.out), session, null);
		}

		@Provides
		@Singleton
		public SessionContext session(Person person, Host host) {
			return new SessionContextImpl(1, person, "localhost", host, true);
		}

		@Provides
		@Inject
		public List<ServiceManifest> foos() {
			// in traditional dependency injection this should be injected as
			// method parameters
			return Arrays.asList(multiply, addInt, addDouble);
		}
	}
}
