package com.wrupple.muba.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.junit.Before;
import org.junit.Test;

import com.wrupple.muba.BootstrapTest;
import com.wrupple.muba.bootstrap.domain.ApplicationContext;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.CatalogEntryImpl;
import com.wrupple.muba.bootstrap.domain.ContractDescriptor;
import com.wrupple.muba.bootstrap.domain.ContractDescriptorImpl;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.RootServiceManifestImpl;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifestImpl;
import com.wrupple.muba.bootstrap.domain.reserved.HasResult;
import com.wrupple.muba.bootstrap.server.chain.command.ContextSwitchCommand;
import com.wrupple.muba.bootstrap.server.chain.command.impl.ContextSwitchCommandImpl;
import com.wrupple.muba.bootstrap.server.domain.ExcecutionContextImpl;
import com.wrupple.muba.bootstrap.server.domain.LocalSystemContext;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;

public class ServiceInvocationTest extends BootstrapTest {

	private ApplicationContext application;

	protected ContractDescriptor operationContract = new ContractDescriptorImpl(
			Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME), CatalogEntryImpl.class);

	private ContextSwitchCommand cwtich;

	public abstract class OldVesionService implements Command {

		@SuppressWarnings("unchecked")
		@Override
		public boolean execute(Context context) throws Exception {
			String first = (String) context.get(FIRST_OPERAND_NAME);
			String second = (String) context.get(SECOND_OPERAND_NAME);
			log.trace("default OPERANDS {},{}", first, second);
			((ExcecutionContext) context).setResult(operation(Integer.parseInt(first), Integer.parseInt(second)));
			return CONTINUE_PROCESSING;

		}

		protected abstract int operation(int first, int second);
	}

	private abstract class UpdatedVersionService implements Command {

		@SuppressWarnings("unchecked")
		@Override
		public boolean execute(Context context) throws Exception {
			String first = (String) context.get(FIRST_OPERAND_NAME);
			String second = (String) context.get(SECOND_OPERAND_NAME);
			// is there an operation named like this?
			if (excecutionContext.getApplication().getRootService().getVersions(second) != null) {
				log.trace("delegating to {}, to find the second term", second);

				excecutionContext.setNextWordIndex(excecutionContext.nextIndex() - 1);
				excecutionContext.process();

				log.trace("RESUMING WITH OPERANDS {},{}", first, ((HasResult) context).getConvertedResult());
				((HasResult) context).setResult(
						operation(Double.parseDouble(first), (Double) ((HasResult) context).getConvertedResult()));
				return CONTINUE_PROCESSING;
			} else {
				log.trace("new OPERANDS {},{}", first, second);
				((HasResult) context).setResult(operation(Double.parseDouble(first), Double.parseDouble(second)));
				return CONTINUE_PROCESSING;
			}

		}

		protected abstract Double operation(Double first, Double second);
	}

	public ServiceInvocationTest() {
		Host peerValue = createNiceMock(Host.class);
		Person person = createNiceMock(Person.class);

		RootServiceManifestImpl rootService = new RootServiceManifestImpl();


		this.application = new LocalSystemContext(rootService, System.out,System.in, CatalogFactory.getInstance());
		// http://stackoverflow.com/questions/4796172/is-there-a-way-to-get-users-uid-on-linux-machine-using-java
		session = new SessionContextImpl(1, person, "localhost", peerValue, CatalogEntry.PUBLIC_ID);

		cwtich = new ContextSwitchCommandImpl(null, null);

		List<String> grammar = Arrays.asList(new String[] { FIRST_OPERAND_NAME, SECOND_OPERAND_NAME });

		ServiceManifest multiply = new ServiceManifestImpl(MULTIPLICATION, DEFAULT_VERSION, operationContract, grammar);
		ServiceManifest addInt = new ServiceManifestImpl(ADDITION, DEFAULT_VERSION, operationContract, grammar);
		ServiceManifest addDouble = new ServiceManifestImpl(ADDITION, UPGRADED_VERSION, operationContract, grammar);

		application.registerService(addInt, new OldVesionService() {

			@Override
			protected int operation(int first, int second) {
				log.trace("DEFAULT_VERSION ADD {}+{}", first, second);
				return first + second;
			}

		});
		
		application.registerService( addDouble, new UpdatedVersionService() {

			@Override
			protected Double operation(Double first, Double second) {
				log.trace("UPGRADED_VERSION ADD {}+{}", first, second);
				return first + second;
			}
		});
	
		application.registerService( multiply, new UpdatedVersionService() {

			@Override
			protected Double operation(Double first, Double second) {
				log.trace("DEFAULT_VERSION multiply {}+{}", first, second);
				return first * second;
			}
		});
	}

	@Before
	public void prepare() {
		excecutionContext = new ExcecutionContextImpl(cwtich, application, session, null, null);
	}

	@Test
	public void defaultVersion() throws Exception {
		log.trace("[-defaultVersion-]");
		String[] tokenValues = new String[] { ADDITION, "1", "2" };
		excecutionContext.setSentence(tokenValues);
		excecutionContext.process();
		Integer result = excecutionContext.getConvertedResult();
		assertNotNull(result);
		assertEquals(result.intValue(), 3);
	}

	@Test(expected = NumberFormatException.class)
	public void conflict() throws Exception {
		log.trace("[-conflict-] insufficient and/or malformed arguments were provided,should fail");
		// conflicting input data with version, fails

		excecutionContext.setSentence(ADDITION, "1.0", "1");
		excecutionContext.process();
		// check rollback?
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidService() throws Exception {
		log.trace("[-invalidService-]");

		excecutionContext.setSentence("invalidService", "input");
		excecutionContext.process();

		fail("No exception thrown when creating invalid service context");
	}

	@Test(expected = NumberFormatException.class)
	public void invalidInput() throws Exception {
		log.trace("[-invalidInput-]");
		excecutionContext.setSentence(ADDITION, "one", "1.5");
		excecutionContext.process();

		fail("No exception thrown when processing invalid context");

	}

	@Test
	public void specificVersion() throws Exception {
		log.trace("[-specificVersion-]");

		excecutionContext.setSentence(ADDITION, UPGRADED_VERSION, "1", "1.5");
		excecutionContext.process();
		Double result = excecutionContext.getConvertedResult();
		assertNotNull(result);
		assertEquals(result.doubleValue(), 2.5, 0);
	}

	@Test(expected = NumberFormatException.class) // "1..0"
	public void invalidVersion() throws Exception {
		log.trace("[-invalidVersion-]");

		excecutionContext.setSentence(ADDITION, "1..0", "2", "1.5");
		excecutionContext.process();

	}

}
