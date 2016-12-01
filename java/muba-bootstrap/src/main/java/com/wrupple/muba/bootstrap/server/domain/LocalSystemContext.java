package com.wrupple.muba.bootstrap.server.domain;

import java.io.OutputStream;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.impl.ContextBase;

import com.wrupple.muba.bootstrap.domain.ApplicationContext;
import com.wrupple.muba.bootstrap.domain.Bootstrap;

@Singleton
public class LocalSystemContext extends ContextBase implements ApplicationContext {

	private static final long serialVersionUID = -7144539787781019055L;
	private final Bootstrap rootService;
	private final PrintWriter outputWriter;

	@Inject
	public LocalSystemContext(Bootstrap rootService,@Named("System.out") OutputStream out) {
		super();
		this.rootService = rootService;
		this.outputWriter = new PrintWriter(out);
	}

	public Bootstrap getRootService() {
		return rootService;
	}

	public PrintWriter getOutputWriter() {
		return outputWriter;
	}

}
