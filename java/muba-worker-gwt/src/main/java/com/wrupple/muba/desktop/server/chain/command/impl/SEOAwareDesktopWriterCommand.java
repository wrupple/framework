package com.wrupple.muba.desktop.server.chain.command.impl;

import com.google.inject.Inject;
import com.wrupple.muba.desktop.server.chain.command.DesktopWriterCommand;
import com.wrupple.muba.desktop.server.chain.command.GWTDesktopWriterCommand;
import com.wrupple.muba.desktop.server.chain.command.SearchEngineOptimizedDesktopWriterCommand;
import com.wrupple.muba.desktop.server.domain.DesktopBuilderContext;
import org.apache.commons.chain.Context;

public class SEOAwareDesktopWriterCommand implements DesktopWriterCommand {
	
	private final GWTDesktopWriterCommand gwt;
	private final SearchEngineOptimizedDesktopWriterCommand seo;
	
	@Inject
	public SEOAwareDesktopWriterCommand(GWTDesktopWriterCommand gwt, SearchEngineOptimizedDesktopWriterCommand seo) {
		super();
		this.gwt = gwt;
		this.seo = seo;
	}



	@Override
	public boolean execute(Context c) throws Exception {
		DesktopBuilderContext context=(DesktopBuilderContext) c;
		if(context.isSearchEngine()){
			seo.execute(context);
		}else{
			gwt.execute(context);
		}
		return CONTINUE_PROCESSING;
	}

}
