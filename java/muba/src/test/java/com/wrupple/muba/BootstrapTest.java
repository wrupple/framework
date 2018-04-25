package com.wrupple.muba;

import com.wrupple.muba.event.server.domain.impl.SessionContextImpl;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.junit.Rule;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class BootstrapTest extends EasyMockSupport {

	
	protected Logger log = LogManager.getLogger(BootstrapTest.class);

	public static final String ADDITION = "add";
	public static final String MULTIPLICATION = "multiply";
	public static final String DEFAULT_VERSION = "1.0";
	public static final String UPGRADED_VERSION = "1.1";
	public static final String FIRST_OPERAND_NAME = "first";
	public static final String SECOND_OPERAND_NAME = "second";


	@Rule
	public final EasyMockRule rule = new EasyMockRule(this);
    protected SessionContextImpl session;

}
