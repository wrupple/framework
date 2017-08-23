package com.wrupple.muba.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import javax.validation.Validator;

import com.wrupple.muba.bootstrap.domain.*;
import com.wrupple.muba.bootstrap.server.service.EventRegistry;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasResult;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;
import com.wrupple.muba.bootstrap.server.service.ValidationGroupProvider;

public class ServiceRequestValidation extends MubaTest {

	protected static final String ADDITION = "add";
	protected static final String MULTIPLICATION = "multiply";
	protected static final String DEFAULT_VERSION = "1.0";
	protected static final String UPGRADED_VERSION = "1.1";
	protected static final String FIRST_OPERAND_NAME = "value";
	protected static final String SECOND_OPERAND_NAME = "value2";

	public ServiceRequestValidation() {
		super();
		init(new MockModule(), new ValidationModule(), new BootstrapModule());
	}
	
	protected void registerServices(Validator v, ValidationGroupProvider g,SystemContext switchs) {
		List<String> grammar = Arrays.asList(new String[] { FIRST_OPERAND_NAME, SECOND_OPERAND_NAME });
		ContractDescriptor operationContract = new ContractDescriptorImpl(
				Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME), ProblemRequest.class);
		ServiceManifest multiply = new ServiceManifestImpl(MULTIPLICATION, DEFAULT_VERSION, operationContract,grammar);
		ServiceManifest addInt = new ServiceManifestImpl(ADDITION, DEFAULT_VERSION, operationContract, grammar);
		ServiceManifest addDouble = new ServiceManifestImpl(ADDITION, UPGRADED_VERSION, operationContract, grammar);


		switchs.registerService(multiply, new UpdatedVersionService() {

			@Override
			protected Double operation(Double first, Double second) {
				log.trace("DEFAULT_VERSION multiply {}+{}", first, second);
				return first * second;
			}
		});
		switchs.registerService(addInt, new OldVesionService() {

			@Override
			protected int operation(int first, int second) {
				log.trace("DEFAULT_VERSION ADD {}+{}", first, second);
				return first + second;
			}

		});
		switchs.registerService(addDouble, new UpdatedVersionService() {

			@Override
			protected Double operation(Double first, Double second) {
				log.trace("UPGRADED_VERSION ADD {}+{}", first, second);
				return first + second;
			}
		});

	}

	@Before
	@Override
	public void setUp() throws Exception {
		runtimeContext = injector.getInstance(RuntimeContext.class);
	}

	@Test
	public void nesting() throws Exception {
		// first attempt with a constraint violation
		log.trace("[-nesting-]");

		// version 1.1, nesting return double (1 + ( 1.1*(1+1) ) )
		runtimeContext.setSentence(ADDITION, UPGRADED_VERSION, "1", MULTIPLICATION, "1.1", ADDITION,
				UPGRADED_VERSION, "1", "1");
		runtimeContext.process();
		Double result = runtimeContext.getConvertedResult();
		assertNotNull(result);
		assertEquals(result.doubleValue(), 3.2, 0);
	}

	@Test
	public void validation() throws Exception {
		// first attempt with a constraint violation
		log.trace("[-validation-]");
		// a service contract must be provided for request interpret to invoke
		// validation
		ProblemRequest problemRequest = new ProblemRequest();
		runtimeContext.setServiceContract(problemRequest);
		// version 1.1, nesting return double (1 + ( 1.1*(1+1) ) )
		runtimeContext.setSentence(ADDITION, UPGRADED_VERSION, "1", MULTIPLICATION, "1.1", ADDITION,
				UPGRADED_VERSION, "1", "malformedToken");
		runtimeContext.process();
		assertEquals(9, runtimeContext.nextIndex());
		//some error must be thrown or validation constraint shown when the application has no servicesregistered
		assertNotNull(runtimeContext.getConstraintViolations());
	}

	class MockModule extends AbstractModule {

		@Override
		protected void configure() {

			bind(Person.class).toInstance(mock(Person.class));
			bind(Host.class).toInstance(mock(Host.class));
			bind(UserTransaction.class).toInstance(mock(UserTransaction.class));
			bind(EventRegistry.class).toInstance(mock(EventRegistry.class));
			bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
			bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);


		}

		@Provides
		@Singleton
		public SessionContext session(Person person, Host host) {
			return new SessionContextImpl(1, person, "localhost", host, CatalogEntry.PUBLIC_ID);
		}

	}
	private abstract class UpdatedVersionService implements Command {

		@SuppressWarnings("unchecked")
		@Override
		public boolean execute(Context context) throws Exception {
			String first = (String) context.get(FIRST_OPERAND_NAME);
			String second = (String) context.get(SECOND_OPERAND_NAME);
			log.debug("Excecuting on {},{}", first, second);
			Map<String, ServiceManifest> versions = runtimeContext.getApplication().getRootService()
					.getVersions(second);
			// is there an operation named like this?
			if (versions == null) {

				log.trace("excecuting operation with operands {},{}", first, second);
				((HasResult) context).setResult(operation(Double.parseDouble(first), Double.parseDouble(second)));
				return CONTINUE_PROCESSING;
			} else {
				log.trace("will invoke nested service {}", second);

				runtimeContext.setNextWordIndex(runtimeContext.nextIndex() - 1);
				if (Command.CONTINUE_PROCESSING == runtimeContext.process()) {
					log.trace("RESUMING WITH OPERANDS {},{}", first, ((HasResult) context).getConvertedResult());
					((HasResult) context).setResult(operation(Double.parseDouble(first),
							(Double) ((HasResult) context).getConvertedResult()));
					return CONTINUE_PROCESSING;
				} else {
					return PROCESSING_COMPLETE;
				}

			}

		}

		protected abstract Double operation(Double first, Double second);
	}

	private abstract class OldVesionService implements Command {

		@SuppressWarnings("unchecked")
		@Override
		public boolean execute(Context context) throws Exception {
			log.debug("Excecuting {}", OldVesionService.class);
			String first = (String) context.get(FIRST_OPERAND_NAME);
			String second = (String) context.get(SECOND_OPERAND_NAME);

			log.trace("default OPERANDS {},{}", first, second);
			((RuntimeContext) context).setResult(operation(Integer.parseInt(first), Integer.parseInt(second)));
			return CONTINUE_PROCESSING;

		}

		protected abstract int operation(int first, int second);
	}
}
