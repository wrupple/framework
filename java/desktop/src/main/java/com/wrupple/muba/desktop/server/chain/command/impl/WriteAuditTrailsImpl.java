package com.wrupple.muba.desktop.server.chain.command.impl;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.chain.command.WriteAuditTrails;

public class WriteAuditTrailsImpl implements WriteAuditTrails {

	@Override
	public boolean execute(Context context) throws Exception {
		return CONTINUE_PROCESSING;
	}

}
