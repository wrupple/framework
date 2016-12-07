package com.wrupple.muba.bootstrap.domain;

import java.io.PrintWriter;

import org.apache.commons.chain.CatalogFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.server.chain.command.RequestInterpret;

public interface ApplicationContext
		/* basic implementation: LocalSystemContext */ extends Context {

	// new PrintWriter(System.out)
	public PrintWriter getOutputWriter();

	RootServiceManifest getRootService();

	CatalogFactory getDictionaryFactory();
	
	//contract interpret per type? bpm?
	public void registerContractInterpret(ServiceManifest manifest, RequestInterpret contractInterpret) ;
	void registerService(ServiceManifest manifest,Command service);

	/**
	 * this is very much like widgetters/lookupcommands
	 * 
	 * @param context
	 * @return reads properties from context, aswell as context property depen
	 */
	public RequestInterpret getRequestInterpret(ExcecutionContext context);
}