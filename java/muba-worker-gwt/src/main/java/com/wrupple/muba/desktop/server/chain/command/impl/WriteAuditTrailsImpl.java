package com.wrupple.muba.desktop.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.chain.command.WriteAuditTrails;
import org.apache.commons.chain.Context;

public class WriteAuditTrailsImpl implements WriteAuditTrails {

	@Override
	public boolean execute(Context context) throws Exception {
		return CONTINUE_PROCESSING;
	}

}
