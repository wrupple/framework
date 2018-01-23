package com.wrupple.muba.event;

import com.wrupple.muba.BootstrapTest;
import com.wrupple.muba.event.chain.impl.OldVesionService;
import com.wrupple.muba.event.chain.impl.UpdatedVersionService;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.ExplicitIntent;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.impl.*;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.chain.command.ValidateContract;
import com.wrupple.muba.event.server.chain.command.ValidateRequest;
import com.wrupple.muba.event.server.chain.command.impl.BindServiceImpl;
import com.wrupple.muba.event.server.chain.command.impl.DispatchImpl;
import com.wrupple.muba.event.server.chain.command.impl.EventDispatcherImpl;
import com.wrupple.muba.event.server.chain.command.impl.IncorporateImpl;
import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;
import com.wrupple.muba.event.server.service.*;
import com.wrupple.muba.event.server.service.impl.*;
import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Context;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class EventListenerTest extends BootstrapTest {

	private EventBus system;

	protected ContractDescriptor operationContract = new ContractDescriptorImpl(
            Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME), null);


    public EventListenerTest() {


		LargeStringFieldDataAccessObject largeStringDelegate= new LargeStringFieldDataAccessObjectImpl();
		ObjectNativeInterface oni= new JavaObjectNativeInterface(largeStringDelegate);
		FilterNativeInterface filterer=new JavaFilterNativeInterfaceImpl(oni);
		FieldAccessStrategy instrospector=new JavaFieldAccessStrategy(null,oni);


        EventDispatcher dispatcher = new EventDispatcherImpl(new ValidateRequest() {
            @Override
            public boolean execute(Context context) throws Exception {
                return CONTINUE_PROCESSING;
            }
        }, new BindServiceImpl(), new IncorporateImpl(), new ValidateContract() {
            @Override
            public boolean execute(Context context) throws Exception {
                return CONTINUE_PROCESSING;
            }
        }, new DispatchImpl());

        ParentServiceManifestImpl rootService = new ParentServiceManifestImpl();
        EventRegistry interpret = new EventRegistryImpl(rootService,CatalogFactory.getInstance());

        EventBusImpl.IntentDelegate delegate = new IntentDelegateImpl();
        this.system = new EventBusImpl(interpret, dispatcher, System.out, System.in, instrospector, null, delegate);


        List<String> grammar = Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME);

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

        // http://stackoverflow.com/questions/4796172/is-there-a-way-to-get-users-uid-on-linux-machine-using-java
        //new SessionContextImpl(1, person, "localhost", peerValue, CatalogEntry.PUBLIC_ID);
        SessionImpl sessionValue = new SessionImpl();
        sessionValue.setDomain(CatalogEntry.PUBLIC_ID);
        session = new SessionContextImpl(sessionValue);

	}

	@Test
	public void defaultVersion() throws Exception {
		log.trace("[-defaultVersion-]");

		ExplicitIntent event = new ExplicitIntentImpl(ADDITION, "1", "2" );

		system.fireHandler(event,session);

		Integer result = (Integer) ((List)event.getConvertedResult()).get(0);
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

		Double result = (Double) ((List)event.getConvertedResult()).get(0);
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
