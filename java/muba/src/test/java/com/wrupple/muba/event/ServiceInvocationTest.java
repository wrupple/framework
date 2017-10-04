package com.wrupple.muba.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import com.wrupple.muba.event.chain.impl.OldVesionService;
import com.wrupple.muba.event.chain.impl.UpdatedVersionService;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.chain.command.impl.EventDispatcherImpl;
import com.wrupple.muba.event.server.service.*;
import com.wrupple.muba.event.server.service.impl.*;
import org.apache.commons.chain.CatalogFactory;
import org.junit.Before;
import org.junit.Test;

import com.wrupple.muba.BootstrapTest;
import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;

public class ServiceInvocationTest extends BootstrapTest {

	private EventBus system;

	protected ContractDescriptor operationContract = new ContractDescriptorImpl(
			Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME), CatalogEntryImpl.class);


	public ServiceInvocationTest() {


		LargeStringFieldDataAccessObject largeStringDelegate= new LargeStringFieldDataAccessObjectImpl();
		ObjectNativeInterface oni= new JavaObjectNativeInterface(largeStringDelegate);
		FilterNativeInterface filterer=new JavaFilterNativeInterfaceImpl(oni);
		FieldAccessStrategy instrospector=new JavaFieldAccessStrategy(null,oni);

        EventDispatcher dispatcher = new EventDispatcherImpl(null,null);
        ParentServiceManifestImpl rootService = new ParentServiceManifestImpl();
        EventRegistry interpret = new EventRegistryImpl(rootService,CatalogFactory.getInstance());

        this.system = new EventBusImpl(interpret,dispatcher, System.out,System.in,false,null,filterer,null,instrospector, null);


		List<String> grammar = Arrays.asList(new String[] { FIRST_OPERAND_NAME, SECOND_OPERAND_NAME });

		ServiceManifest multiply = new ServiceManifestImpl(MULTIPLICATION, DEFAULT_VERSION, operationContract, grammar);
		ServiceManifest addInt = new ServiceManifestImpl(ADDITION, DEFAULT_VERSION, operationContract, grammar);
		ServiceManifest addDouble = new ServiceManifestImpl(ADDITION, UPGRADED_VERSION, operationContract, grammar);

		interpret.registerService(addInt, new OldVesionService() {

			@Override
			protected int operation(int first, int second) {
				log.trace("DEFAULT_VERSION ADD {}+{}", first, second);
				return first + second;
			}

		});

		interpret.registerService( addDouble, new UpdatedVersionService() {

			@Override
			protected Double operation(Double first, Double second) {
				log.trace("UPGRADED_VERSION ADD {}+{}", first, second);
				return first + second;
			}
		});

		interpret.registerService( multiply, new UpdatedVersionService() {

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

		ExplicitIntent event = new ExplicitIntentImpl(ADDITION, "1", "2" );

		system.fireHandler(event,session);

		Integer result = event.getConvertedResult();
		assertNotNull(result);
		assertEquals(result.intValue(), 3);
	}

	@Test(expected = NumberFormatException.class)
	public void conflict() throws Exception {
		log.trace("[-conflict-] insufficient and/or malformed arguments were provided,should fail");
		// conflicting input data with version, fails

        ExplicitIntent event = new ExplicitIntentImpl(ADDITION, "1.0", "1");

        system.fireHandler/*TODO Async*/(event, session);
		// check rollback?
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidService() throws Exception {
		log.trace("[-invalidService-]");

        ExplicitIntent event = new ExplicitIntentImpl("invalidService", "input");

        system.fireHandler/*TODO Async*/(event, session);

		fail("No exception thrown when creating invalid service context");
	}

	@Test(expected = NumberFormatException.class)
	public void invalidInput() throws Exception {
		log.trace("[-invalidInput-]");

        ExplicitIntent event = new ExplicitIntentImpl(ADDITION, "one", "1.5");

        system.fireHandler/*TODO Async*/(event, session);

		fail("No exception thrown when processing invalid context");
	}

	@Test
	public void specificVersion() throws Exception {
		log.trace("[-specificVersion-]");

        ExplicitIntent event = new ExplicitIntentImpl(ADDITION, UPGRADED_VERSION, "1", "1.5");

        system.fireHandler/*TODO Async*/(event, session);

		Double result = event.getConvertedResult();
		assertNotNull(result);
		assertEquals(result.doubleValue(), 2.5, 0);
	}

	@Test(expected = NumberFormatException.class) // "1..0"
	public void invalidVersion() throws Exception {
		log.trace("[-invalidVersion-]");

        ExplicitIntent event = new ExplicitIntentImpl(ADDITION, "1..0", "2", "1.5");

        system.fireHandler/*TODO Async*/(event, session);

	}

}
