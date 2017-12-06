package com.wrupple.muba.event;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.wrupple.muba.MubaTest;
import com.wrupple.muba.ValidationModule;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.impl.ContractDescriptorImpl;
import com.wrupple.muba.event.domain.impl.ServiceManifestImpl;
import com.wrupple.muba.event.domain.impl.SessionImpl;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.event.server.chain.command.EventSuscriptionMapper;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;
import com.wrupple.muba.event.server.service.impl.LambdaModule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.transaction.UserTransaction;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.wrupple.muba.event.domain.SessionContext.SYSTEM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ServiceRequestValidation extends MubaTest {

	protected static final String ADDITION = "add";
	protected static final String MULTIPLICATION = "multiply";
	protected static final String DEFAULT_VERSION = "1.0";
	protected static final String UPGRADED_VERSION = "1.1";
	protected static final String FIRST_OPERAND_NAME = "value";
	protected static final String SECOND_OPERAND_NAME = "value2";

	public ServiceRequestValidation() {
		super();
		init(new MockModule(), new ValidationModule(), new LambdaModule(), new DispatcherModule(), new ApplicationModule());
	}
	
	protected void registerServices(EventBus switchs) {
        List<String> grammar = Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME);
        ContractDescriptor operationContract = new ContractDescriptorImpl(
                Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME), null);
        ServiceManifest multiply = new ServiceManifestImpl(MULTIPLICATION, DEFAULT_VERSION, operationContract, grammar);
        ServiceManifest addInt = new ServiceManifestImpl(ADDITION, DEFAULT_VERSION, operationContract, grammar);
		ServiceManifest addDouble = new ServiceManifestImpl(ADDITION, UPGRADED_VERSION, operationContract, grammar);


		switchs.getIntentInterpret().registerService(multiply, new UpdatedVersionService() {

			@Override
			protected Double operation(Double first, Double second) {
				log.trace("DEFAULT_VERSION multiply {}+{}", first, second);
				return first * second;
			}
		});
		switchs.getIntentInterpret().registerService(addInt, new OldVesionService() {

			@Override
			protected int operation(int first, int second) {
				log.trace("DEFAULT_VERSION ADD {}+{}", first, second);
				return first + second;
			}

		});
		switchs.getIntentInterpret().registerService(addDouble, new UpdatedVersionService() {

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
		runtimeContext = new RuntimeContextImpl(injector.getInstance(EventBus.class),injector.getInstance( Key.get(SessionContext.class,Names.named(SYSTEM))));

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
		try{
            runtimeContext.process();
        }catch (IllegalArgumentException e){
			log.info("expected exception thrown", e);
		}
		assertEquals(9, runtimeContext.nextIndex());
		//some error must be thrown or validation constraint shown when the application has no servicesregistered
		assertNotNull(runtimeContext.getConstraintViolations());
	}

	class MockModule extends AbstractModule {

		@Override
		protected void configure() {
            bind(Boolean.class).annotatedWith(Names.named("event.parallel")).toInstance(false);
            bind(UserTransaction.class).toInstance(mock(UserTransaction.class));
			bind(OutputStream.class).annotatedWith(Names.named("System.out")).toInstance(System.out);
			bind(InputStream.class).annotatedWith(Names.named("System.in")).toInstance(System.in);
			mockSuscriptor = mock(EventSuscriptionMapper.class);
			bind(EventSuscriptionMapper.class).toInstance(mockSuscriptor);
		}

		@Provides
		@Inject
		@Singleton
		@Named(SessionContext.SYSTEM)
		public SessionContext sessionContext(@Named(SessionContext.SYSTEM) Session stakeHolderValue) {


			return new SessionContextImpl(stakeHolderValue);
		}

		@Provides
		@Inject
		@Singleton
		@Named(SessionContext.SYSTEM)
		public Session sessionContext() {
			SessionImpl sessionValue= new SessionImpl();
			sessionValue.setDomain(CatalogEntry.PUBLIC_ID);
			sessionValue.setId(CatalogEntry.PUBLIC_ID);
			return sessionValue;
		}


	}

    private abstract static class UpdatedVersionService implements Command {

        protected Logger log = LoggerFactory.getLogger(UpdatedVersionService.class);


		@SuppressWarnings("unchecked")
		@Override
		public boolean execute(Context context) throws Exception {
			String first = (String) context.get(FIRST_OPERAND_NAME);
			String second = (String) context.get(SECOND_OPERAND_NAME);
            RuntimeContext runtimeContext = (RuntimeContext) context;
            log.debug("{} for: {},{}", this.getClass().getSimpleName(), first, second);
            Map<String, ServiceManifest> versions = runtimeContext.getEventBus().getIntentInterpret().getRootService()
					.getVersions(second);
			// is there an operation named like this?
			if (versions == null) {

                log.trace("excecuting operation with operands: {},{}", first, second);
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

    private abstract static class OldVesionService implements Command {

        protected Logger log = LoggerFactory.getLogger(UpdatedVersionService.class);


        @SuppressWarnings("unchecked")
        @Override
        public boolean execute(Context context) throws Exception {
			log.debug("Excecuting {}", OldVesionService.class);
			String first = (String) context.get(FIRST_OPERAND_NAME);
			String second = (String) context.get(SECOND_OPERAND_NAME);

            log.trace(" for: {},{}", first, second);
            ((RuntimeContext) context).setResult(operation(Integer.parseInt(first), Integer.parseInt(second)));
            return CONTINUE_PROCESSING;

		}

		protected abstract int operation(int first, int second);
	}
}
