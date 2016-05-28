package com.wrupple.muba.bpm.server.chain.impl;

import javax.inject.Inject;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.impl.ChainBase;

import com.wrupple.muba.bpm.server.chain.BusinessEngine;
import com.wrupple.muba.bpm.server.chain.command.FindQuickResult;
import com.wrupple.muba.bpm.server.chain.command.FindSignificantFields;
import com.wrupple.muba.bpm.server.chain.command.GenerateOutput;
import com.wrupple.muba.bpm.server.chain.command.ValidateRequest;
import com.wrupple.muba.bpm.server.chain.command.WriteOutput;

public class BusinessEngineImpl extends ChainBase implements BusinessEngine {

	// String CONSTANT = CatalogAuditTrail.CATALOG;

	@Inject
	public BusinessEngineImpl(ValidateRequest validateUserData, FindQuickResult commitQuick, FindSignificantFields findFields, GenerateOutput findValue,
			WriteOutput write) {
		super(new Command[] { validateUserData, commitQuick, findFields, findValue, write });
	}

}
