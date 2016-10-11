package com.wrupple.muba.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.ExcecutionContextImpl;
import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.server.chain.command.ServiceInvocationCommand;
import com.wrupple.muba.bootstrap.server.chain.command.impl.ServiceInvocationCommandImpl;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;
import com.wrupple.muba.bootstrap.server.service.impl.BootstrapImpl;

public class ServiceInvocationTest extends BootstrapTest {
	

	public ServiceInvocationTest() {
		

		Bootstrap fallbackService = createNiceMock(Bootstrap.class);

		Person person = createNiceMock(Person.class);

		Host peerValue = createNiceMock(Host.class);
		
		List<ServiceManifest> childServiceVersions = Arrays.asList(multiply, addInt, addDouble);

		// http://stackoverflow.com/questions/4796172/is-there-a-way-to-get-users-uid-on-linux-machine-using-java
		session = new SessionContextImpl(1, person, "localhost", peerValue, true);

		ServiceInvocationCommand serviceInvocationCommand = new ServiceInvocationCommandImpl();
		muba = new BootstrapImpl(childServiceVersions, fallbackService, serviceInvocationCommand);

	}

	@Before
	public void prepare() {
		excecutionContext = new ExcecutionContextImpl(new PrintWriter(System.out), session, null);
	}

	// TODO //semantic service manifest // deposit 500 in AX, 200 in MC and show
	// balance.
	@Test
	public void defaultVersion() throws Exception {
		log.trace("[-defaultVersion-]");
		String[] tokenValues = new String[] { ADDITION, "1", "2" };
		excecutionContext.setSentence(tokenValues);
		muba.getContextProcessingCommand().execute(excecutionContext);
		Integer result = (Integer) excecutionContext.get(RESULT_PARAMETER_NAME);
		assertNotNull(result);
		assertEquals(result.intValue(), 3);
	}

	@Test
	public void conflict() throws Exception {
		log.trace("[-conflict-]");
		// conflicting input data with version, fails

		excecutionContext.setSentence(ADDITION, "1.0", "1");
		muba.getContextProcessingCommand().execute(excecutionContext);
		// check rollback?
		assertTrue("insufficient and/or malformed arguments were provided,should have failed",
				excecutionContext.getCaughtException() != null
						&& excecutionContext.getCaughtException().getClass() == NumberFormatException.class);
	}

	@Test(expected = NullPointerException.class)
	public void invalidService() throws Exception {
		log.trace("[-invalidService-]");

		excecutionContext.setSentence("invalidService", "input");
		muba.getContextProcessingCommand().execute(excecutionContext);

		fail("No exception thrown when creating invalid service context");
	}

	@Test
	public void invalidInput() throws Exception {
		log.trace("[-invalidInput-]");
		excecutionContext.setSentence(ADDITION, "one", "1.5");
		muba.getContextProcessingCommand().execute(excecutionContext);

		assertTrue("No exception thrown when processing invalid context", excecutionContext.getCaughtException() != null
				&& excecutionContext.getCaughtException().getClass() == NumberFormatException.class);

	}

	@Test
	public void specificVersion() throws Exception {
		log.trace("[-specificVersion-]");

		excecutionContext.setSentence(ADDITION, UPGRADED_VERSION, "1", "1.5");
		muba.getContextProcessingCommand().execute(excecutionContext);
		Double result = (Double) excecutionContext.get(RESULT_PARAMETER_NAME);
		assertNotNull(result);
		assertEquals(result.doubleValue(), 2.5, 0);
	}

	@Test // "1..0"
	public void invalidVersion() throws Exception {
		log.trace("[-invalidVersion-]");

		excecutionContext.setSentence(ADDITION, "1..0", "2", "1.5");
		muba.getContextProcessingCommand().execute(excecutionContext);
		assertTrue("malformed arguments were provided,should have failed",
				excecutionContext.getCaughtException() != null
						&& excecutionContext.getCaughtException().getClass() == NumberFormatException.class);

	}




}
