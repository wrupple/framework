package com.wrupple.muba;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import com.wrupple.muba.bootstrap.server.domain.SessionContextImpl;

public class BootstrapTest extends EasyMockSupport {

	
	protected Logger log = LoggerFactory.getLogger(BootstrapTest.class);

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

	protected RuntimeContext runtimeContext;

	// @TestSubject protected RootServiceManifest muba;

	

	
}
