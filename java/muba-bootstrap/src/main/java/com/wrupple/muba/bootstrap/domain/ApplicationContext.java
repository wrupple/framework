package com.wrupple.muba.bootstrap.domain;

import java.io.PrintWriter;

import org.apache.commons.chain.Context;

public interface ApplicationContext /*basic implementation: LocalSystemContext*/ extends Context {
	
	//new PrintWriter(System.out)
	public PrintWriter getOutputWriter();
	
	Bootstrap getRootService();
}
