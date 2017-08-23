package com.wrupple.muba.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import com.wrupple.muba.bootstrap.chain.impl.OldVesionService;
import com.wrupple.muba.bootstrap.chain.impl.UpdatedVersionService;
import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bootstrap.server.chain.command.EventDispatcher;
import com.wrupple.muba.bootstrap.server.chain.command.impl.EventDispatcherImpl;
import com.wrupple.muba.bootstrap.server.domain.JavaSystemContext;
import org.apache.commons.chain.CatalogFactory;
import org.junit.Before;
import org.junit.Test;

import com.wrupple.muba.BootstrapTest;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;

public class ServiceInvocationTest extends BootstrapTest {

	private SystemContext system;

	protected ContractDescriptor operationContract = new ContractDescriptorImpl(
			Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME), CatalogEntryImpl.class);


	public ServiceInvocationTest() {

		RootServiceManifestImpl rootService = new RootServiceManifestImpl();


        EventDispatcher dispatcher = new EventDispatcherImpl(null,null);
        this.system = new JavaSystemContext(dispatcher,rootService, System.out,System.in, CatalogFactory.getInstance(),/*FIXME test implicit intents*/ null,null);


		List<String> grammar = Arrays.asList(new String[] { FIRST_OPERAND_NAME, SECOND_OPERAND_NAME });

		ServiceManifest multiply = new ServiceManifestImpl(MULTIPLICATION, DEFAULT_VERSION, operationContract, grammar);
		ServiceManifest addInt = new ServiceManifestImpl(ADDITION, DEFAULT_VERSION, operationContract, grammar);
		ServiceManifest addDouble = new ServiceManifestImpl(ADDITION, UPGRADED_VERSION, operationContract, grammar);

		system.registerService(addInt, new OldVesionService() {

			@Override
			protected int operation(int first, int second) {
				log.trace("DEFAULT_VERSION ADD {}+{}", first, second);
				return first + second;
			}

		});
		
		system.registerService( addDouble, new UpdatedVersionService() {

			@Override
			protected Double operation(Double first, Double second) {
				log.trace("UPGRADED_VERSION ADD {}+{}", first, second);
				return first + second;
			}
		});
	
		system.registerService( multiply, new UpdatedVersionService() {

			@Override
			protected Double operation(Double first, Double second) {
				log.trace("DEFAULT_VERSION multiply {}+{}", first, second);
				return first * second;
			}
		});
	}

	@Before
	public void prepare() {
        Host peerValue = createNiceMock(Host.class);
        Person person = createNiceMock(Person.class);
        // http://stackoverflow.com/questions/4796172/is-there-a-way-to-get-users-uid-on-linux-machine-using-java
        session = new SessionContextImpl(1, person, "localhost", peerValue, CatalogEntry.PUBLIC_ID);
	}

	@Test
	public void defaultVersion() throws Exception {
		log.trace("[-defaultVersion-]");

		UserEvent event = new UserEventImpl(ADDITION, "1", "2" );

		system.fireEvent/*TODO Async*/(event,session);

		Integer result = event.getConvertedResult();
		assertNotNull(result);
		assertEquals(result.intValue(), 3);
	}

	@Test(expected = NumberFormatException.class)
	public void conflict() throws Exception {
		log.trace("[-conflict-] insufficient and/or malformed arguments were provided,should fail");
		// conflicting input data with version, fails

        UserEvent event = new UserEventImpl(ADDITION, "1.0", "1");

        system.fireEvent/*TODO Async*/(event, session);
		// check rollback?
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidService() throws Exception {
		log.trace("[-invalidService-]");

        UserEvent event = new UserEventImpl("invalidService", "input");

        system.fireEvent/*TODO Async*/(event, session);

		fail("No exception thrown when creating invalid service context");
	}

	@Test(expected = NumberFormatException.class)
	public void invalidInput() throws Exception {
		log.trace("[-invalidInput-]");

        UserEvent event = new UserEventImpl(ADDITION, "one", "1.5");

        system.fireEvent/*TODO Async*/(event, session);

		fail("No exception thrown when processing invalid context");
	}

	@Test
	public void specificVersion() throws Exception {
		log.trace("[-specificVersion-]");

        UserEvent event = new UserEventImpl(ADDITION, UPGRADED_VERSION, "1", "1.5");

        system.fireEvent/*TODO Async*/(event, session);

		Double result = event.getConvertedResult();
		assertNotNull(result);
		assertEquals(result.doubleValue(), 2.5, 0);
	}

	@Test(expected = NumberFormatException.class) // "1..0"
	public void invalidVersion() throws Exception {
		log.trace("[-invalidVersion-]");

        UserEvent event = new UserEventImpl(ADDITION, "1..0", "2", "1.5");

        system.fireEvent/*TODO Async*/(event, session);

	}

}
