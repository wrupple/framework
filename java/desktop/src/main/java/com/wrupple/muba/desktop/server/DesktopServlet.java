package com.wrupple.muba.desktop.server;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.wrupple.muba.desktop.server.chain.DesktopEngine;
import com.wrupple.muba.desktop.server.chain.command.DesktopResponseWriter;
import com.wrupple.muba.desktop.server.domain.DesktopBuilderContext;
import com.wrupple.muba.desktop.server.service.DesktopRequestTokenizer;
import com.wrupple.muba.desktop.server.service.DesktopServiceManifest;
import com.wrupple.vegetate.server.VegetateServlet;
import com.wrupple.vegetate.server.services.RequestScopedContext;
import com.wrupple.vegetate.server.services.SessionContext;

@Singleton
public class DesktopServlet extends VegetateServlet<DesktopBuilderContext> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1125078203927802079L;

	@Inject
	public DesktopServlet(DesktopResponseWriter writer, Provider<DesktopRequestTokenizer> requestTokenizer, Provider<DesktopEngine> commandProvider,
			Provider<SessionContext> usipp, DesktopServiceManifest manifest, Provider<RequestScopedContext> rsc) {
		super(writer, requestTokenizer, commandProvider, usipp, manifest, rsc);
	}

}
