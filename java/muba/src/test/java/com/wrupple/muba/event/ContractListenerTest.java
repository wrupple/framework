package com.wrupple.muba.event;

import com.wrupple.muba.BootstrapTest;
import com.wrupple.muba.event.chain.impl.OldVesionService;
import com.wrupple.muba.event.chain.impl.UpdatedVersionService;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ContractDescriptor;
import com.wrupple.muba.event.domain.Invocation;
import com.wrupple.muba.event.domain.ServiceManifest;
import com.wrupple.muba.event.domain.impl.*;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.chain.command.ValidateContract;
import com.wrupple.muba.event.server.chain.command.ValidateContext;
import com.wrupple.muba.event.server.chain.command.impl.BindServiceImpl;
import com.wrupple.muba.event.server.chain.command.impl.RunImpl;
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

public class ContractListenerTest extends BootstrapTest {

	private ServiceBus system;

	protected ContractDescriptor operationContract = new ContractDescriptorImpl(
            Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME), null);


    public ContractListenerTest() {


		LargeStringFieldDataAccessObject largeStringDelegate= new LargeStringFieldDataAccessObjectImpl();
		ObjectNativeInterface oni= new JavaObjectNativeInterface(largeStringDelegate);
		FieldAccessStrategy instrospector=new JavaFieldAccessStrategy(null,oni);


        EventDispatcher dispatcher = new EventDispatcherImpl(new ValidateContext() {
            @Override
            public boolean execute(Context context) throws Exception {
                return CONTINUE_PROCESSING;
            }
        }, new BindServiceImpl(), new IncorporateImpl(), new ValidateContract() {
            @Override
            public boolean execute(Context context) throws Exception {
                return CONTINUE_PROCESSING;
            }
        }, new RunImpl());

        ParentServiceManifestImpl rootService = new ParentServiceManifestImpl();
        EventRegistry interpret = new EventRegistryImpl(rootService,CatalogFactory.getInstance());

        ServiceBusImpl.IntentDelegate delegate = new IntentDelegateImpl();
        this.system = new ServiceBusImpl(interpret, dispatcher, System.out, System.in, instrospector, null, delegate);


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

        SessionImpl sessionValue = new SessionImpl();
        sessionValue.setDomain(CatalogEntry.PUBLIC_ID);
        session = new SessionContextImpl(sessionValue);

	}

	@Test
	public void defaultVersion() throws Exception {
		log.trace("[-defaultVersion-]");

		Invocation event = new InvocationImpl(ADDITION, "1", "2" );



		Integer result = system.fireHandler(event,session);
		assertNotNull(result);
		assertEquals(result.intValue(), 3);
	}

	@Test(expected = NumberFormatException.class)
	public void conflict() throws Exception {
		log.trace("[-conflict-] insufficient and/or malformed arguments were provided,should fail");
		// conflicting input data with version, fails

        Invocation event = new InvocationImpl(ADDITION, "1.0", "1");

        system.fireHandler/*TODO Async*/(event, session);
		// check rollback?
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidService() throws Exception {
		log.trace("[-invalidService-]");

        Invocation event = new InvocationImpl("invalidService", "input");

        system.fireHandler/*TODO Async*/(event, session);

		fail("No exception thrown when creating invalid service context");
	}

	@Test(expected = NumberFormatException.class)
	public void invalidInput() throws Exception {
		log.trace("[-invalidInput-]");

        Invocation event = new InvocationImpl(ADDITION, "one", "1.5");

        system.fireHandler/*TODO Async*/(event, session);

		fail("No exception thrown when processing invalid context");
	}

	@Test
	public void specificVersion() throws Exception {
		log.trace("[-specificVersion-]");

        Invocation event = new InvocationImpl(ADDITION, UPGRADED_VERSION, "1", "1.5");



		Double result = system.fireHandler/*TODO Async*/(event, session);
		assertNotNull(result);
		assertEquals(result.doubleValue(), 2.5, 0);
	}

	@Test(expected = NumberFormatException.class) // "1..0"
	public void invalidVersion() throws Exception {
		log.trace("[-invalidVersion-]");

        Invocation event = new InvocationImpl(ADDITION, "1..0", "2", "1.5");

        system.fireHandler/*TODO Async*/(event, session);

	}

}
