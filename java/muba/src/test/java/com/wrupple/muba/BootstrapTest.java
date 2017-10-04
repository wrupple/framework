package com.wrupple.muba;

import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;

public class BootstrapTest extends EasyMockSupport {

	
	protected Logger log = LoggerFactory.getLogger(BootstrapTest.class);

	public static final String ADDITION = "add";
	public static final String MULTIPLICATION = "multiply";
	public static final String DEFAULT_VERSION = "1.0";
	public static final String UPGRADED_VERSION = "1.1";
	public static final String FIRST_OPERAND_NAME = "first";
	public static final String SECOND_OPERAND_NAME = "second";

	static final String validSerializedContext = "a valid context";

	static final String invalidSerializedContext = "{some context i dont know}";

	@Rule
	public final EasyMockRule rule = new EasyMockRule(this);
	// @Mock
	protected SessionContextImpl session;


	// @TestSubject protected ParentServiceManifest muba;

	

	
}
