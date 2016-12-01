package com.wrupple.muba.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import javax.validation.Validator;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ContractDescriptor;
import com.wrupple.muba.bootstrap.domain.ContractDescriptorImpl;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.Host;
import com.wrupple.muba.bootstrap.domain.Person;
import com.wrupple.muba.bootstrap.domain.ProblemRequest;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifestImpl;
import com.wrupple.muba.bootstrap.domain.SessionContext;
import com.wrupple.muba.bootstrap.domain.reserved.HasResult;
import com.wrupple.muba.bootstrap.server.chain.command.impl.SyntaxParsingCommang;
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

	@Before
	@Override
	public void setUp() throws Exception {
		excecutionContext = injector.getInstance(ExcecutionContext.class);
	}

	@Test
	public void nesting() throws Exception {
		// first attempt with a constraint violation
		log.trace("[-nesting-]");

		// version 1.1, nesting return double (1 + ( 1.1*(1+1) ) )
		excecutionContext.setSentence(ADDITION, UPGRADED_VERSION, "1", MULTIPLICATION, "1.1", ADDITION,
				UPGRADED_VERSION, "1", "1");
		excecutionContext.process();
		Double result = excecutionContext.getConvertedResult();
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
		excecutionContext.setServiceContract(problemRequest);
		// version 1.1, nesting return double (1 + ( 1.1*(1+1) ) )
		excecutionContext.setSentence(ADDITION, UPGRADED_VERSION, "1", MULTIPLICATION, "1.1", ADDITION,
				UPGRADED_VERSION, "1", "invalid");
		excecutionContext.process();
		assertEquals(9, excecutionContext.nextIndex());
		assertNotNull(excecutionContext.getConstraintViolations());
	}

	class MockModule extends AbstractModule {

		@Override
		protected void configure() {

			bind(Person.class).toInstance(mock(Person.class));
			bind(Host.class).toInstance(mock(Host.class));
			bind(UserTransaction.class).toInstance(mock(UserTransaction.class));
			bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);

		}

		@Provides
		@Singleton
		public SessionContext session(Person person, Host host) {
			return new SessionContextImpl(1, person, "localhost", host,  CatalogEntry.PUBLIC_ID);
		}

		@Provides
		@Inject
		public List<ServiceManifest> foos(Validator v, ValidationGroupProvider g) {

			ServiceManifest multiply = new MathServiceManifest(MULTIPLICATION, DEFAULT_VERSION, operationContract,
					new UpdatedVersionService() {

						@Override
						protected Double operation(Double first, Double second) {
							log.trace("DEFAULT_VERSION MULTIPLY {}*{}", first, second);
							return first * second;
						}
					}, v, g);
			ServiceManifest addInt = new MathServiceManifest(ADDITION, DEFAULT_VERSION, operationContract,
					new OldVesionService() {

						@Override
						protected int operation(int first, int second) {
							log.trace("DEFAULT_VERSION ADD {}+{}", first, second);
							return first + second;
						}

					}, v, g);
			ServiceManifest addDouble = new MathServiceManifest(ADDITION, UPGRADED_VERSION, operationContract,
					new UpdatedVersionService() {

						@Override
						protected Double operation(Double first, Double second) {
							log.trace("UPGRADED_VERSION ADD {}+{}", first, second);
							return first + second;
						}
					}, v, g);

			return Arrays.asList(multiply, addInt, addDouble);
		}

		protected ContractDescriptor operationContract = new ContractDescriptorImpl(
				Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME), ProblemRequest.class);

		private class MathServiceManifest extends ServiceManifestImpl {

			public MathServiceManifest(String service, String version, ContractDescriptor contract, Command command,
					Validator v, ValidationGroupProvider g) {
				super(service, version, contract, null, new String[] { FIRST_OPERAND_NAME, SECOND_OPERAND_NAME },
						new SyntaxParsingCommang(v, g) {
							@Override
							protected Context createBlankContext(ExcecutionContext requestContext) {
								return requestContext;
							}
						}, command);
			}

		}

		private abstract class UpdatedVersionService implements Command {

			@SuppressWarnings("unchecked")
			@Override
			public boolean execute(Context context) throws Exception {
				String first = (String) context.get(FIRST_OPERAND_NAME);
				String second = (String) context.get(SECOND_OPERAND_NAME);
				log.debug("Excecuting on {},{}", first, second);
				Map<String, ServiceManifest> versions = excecutionContext.getApplication().getRootService()
						.getVersions(second);
				// is there an operation named like this?
				if (versions == null) {

					log.trace("excecuting operation with operands {},{}", first, second);
					((HasResult) context).setResult(operation(Double.parseDouble(first), Double.parseDouble(second)));
					return CONTINUE_PROCESSING;
				} else {
					log.trace("will invoke nested service {}", second);

					excecutionContext.setNextWordIndex(excecutionContext.nextIndex() - 1);
					if (Command.CONTINUE_PROCESSING == excecutionContext.process()) {
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
				((ExcecutionContext) context).setResult(operation(Integer.parseInt(first), Integer.parseInt(second)));
				return CONTINUE_PROCESSING;

			}

			protected abstract int operation(int first, int second);
		}

	}

}
