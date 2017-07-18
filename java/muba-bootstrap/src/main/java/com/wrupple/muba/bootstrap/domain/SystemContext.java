package com.wrupple.muba.bootstrap.domain;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.server.chain.command.RequestInterpret;

public interface SystemContext
		/* basic implementation: LocalSystemContext */ extends Context {

    OutputStream getOutput();
    InputStream getInput();

	PrintWriter getOutputWriter();

	RootServiceManifest getRootService();

	CatalogFactory getDictionaryFactory();
	
	//contract interpret per type? bpm?
	void registerContractInterpret(ServiceManifest manifest, RequestInterpret contractInterpret) ;
	void registerService(ServiceManifest manifest,Command service);

	/**
	 * this is very much like widgetters/lookupcommands
	 * 
	 * @param context
	 * @return reads properties from context, aswell as context property depen
	 */
	RequestInterpret getRequestInterpret(RuntimeContext context);
}