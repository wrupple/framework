package com.wrupple.muba;

import java.util.Arrays;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ContextBase;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.TestSubject;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.Bootstrap;
import com.wrupple.muba.bootstrap.domain.ContractDescriptor;
import com.wrupple.muba.bootstrap.domain.ContractDescriptorImpl;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.domain.ServiceManifest;
import com.wrupple.muba.bootstrap.domain.ServiceManifestImpl;
import com.wrupple.muba.bootstrap.server.chain.command.impl.PathParsingCommand;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;

public class BootstrapTest extends EasyMockSupport {

	protected ContractDescriptor operationContract = new ContractDescriptorImpl(
			Arrays.asList(FIRST_OPERAND_NAME, SECOND_OPERAND_NAME), ContextBase.class.getCanonicalName());

	protected ServiceManifest multiply = new MathServiceManifest(MULTIPLICATION, DEFAULT_VERSION, operationContract,
			new UpdatedVersionService() {

				@Override
				protected Double operation(Double first, Double second) {
					log.trace("DEFAULT_VERSION MULTIPLY {}*{}", first, second);
					return first * second;
				}
			});
	protected ServiceManifest addInt = new MathServiceManifest(ADDITION, DEFAULT_VERSION, operationContract,
			new OldVesionService() {

				@Override
				protected int operation(int first, int second) {
					log.trace("DEFAULT_VERSION ADD {}+{}", first, second);
					return first + second;
				}

			});
	protected ServiceManifest addDouble = new MathServiceManifest(ADDITION, UPGRADED_VERSION, operationContract,
			new UpdatedVersionService() {

				@Override
				protected Double operation(Double first, Double second) {
					log.trace("UPGRADED_VERSION ADD {}+{}", first, second);
					return first + second;
				}
			});

	protected Logger log = LoggerFactory.getLogger(BootstrapTest.class);

	protected static final String RESULT_PARAMETER_NAME = "result";
	protected static final String ADDITION = "add";
	protected static final String MULTIPLICATION = "multiply";
	protected static final String DEFAULT_VERSION = "1.0";
	protected static final String UPGRADED_VERSION = "1.1";
	protected static final String FIRST_OPERAND_NAME = "first";
	protected static final String SECOND_OPERAND_NAME = "second";

	static final String validSerializedContext = "a valid context";

	static final String invalidSerializedContext = "{some context i dont know}";

	@Rule
	public final EasyMockRule rule = new EasyMockRule(this);
	// @Mock
	protected SessionContextImpl session;

	protected ExcecutionContext excecutionContext;

	@TestSubject
	protected
	Bootstrap muba;

	private abstract class OldVesionService implements Command {

		@SuppressWarnings("unchecked")
		@Override
		public boolean execute(Context context) throws Exception {
			String first = (String) context.get(FIRST_OPERAND_NAME);
			String second = (String) context.get(SECOND_OPERAND_NAME);
			log.trace("default OPERANDS {},{}", first, second);
			context.put(RESULT_PARAMETER_NAME, operation(Integer.parseInt(first), Integer.parseInt(second)));
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
			if (muba.getVersions(second) != null) {
				log.trace("delegating to {}, to find the second term", second);

				excecutionContext.setNextWordIndex(excecutionContext.nextIndex() - 1);
				muba.getContextProcessingCommand().execute(excecutionContext);

				log.trace("RESUMING WITH OPERANDS {},{}", first, context.get(RESULT_PARAMETER_NAME));
				context.put(RESULT_PARAMETER_NAME,
						operation(Double.parseDouble(first), (Double) context.get(RESULT_PARAMETER_NAME)));
				return CONTINUE_PROCESSING;
			} else {
				log.trace("new OPERANDS {},{}", first, second);
				context.put(RESULT_PARAMETER_NAME, operation(Double.parseDouble(first), Double.parseDouble(second)));
				return CONTINUE_PROCESSING;
			}

		}

		protected abstract Double operation(Double first, Double second);
	}

	private class MathServiceManifest extends ServiceManifestImpl {

		public MathServiceManifest(String service, String version, ContractDescriptor contract, Command command) {
			super(service, version, contract, null, new String[] { FIRST_OPERAND_NAME, SECOND_OPERAND_NAME },
					new PathParsingCommand() {

						@Override
						protected Context createBlankContext(ExcecutionContext requestContext) {
							return excecutionContext;
						}
					}, command);
		}

	}
}
