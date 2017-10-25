package com.wrupple.muba.catalogs;

import com.google.inject.AbstractModule;
import com.wrupple.muba.catalogs.server.chain.command.*;
import com.wrupple.muba.catalogs.server.chain.command.impl.*;
import com.wrupple.muba.catalogs.server.service.JDBCMappingDelegate;
import com.wrupple.muba.catalogs.server.service.QueryResultHandler;
import com.wrupple.muba.catalogs.server.service.impl.JDBCMappingDelegateImpl;
import com.wrupple.muba.catalogs.server.service.impl.QueryResultHandlerImpl;

public class JDBCModule extends AbstractModule {

	@Override
	protected void configure() {
        bind(JDBCDataCreationCommand.class).to(JDBCDataCreationCommandImpl.class);
		bind(JDBCDataQueryCommand.class).to(JDBCDataQueryCommandImpl.class);
		bind(JDBCDataReadCommand.class).to(JDBCDataReadCommandImpl.class);
		bind(JDBCDataWritingCommand.class).to(JDBCDataWritingCommandImpl.class);
		bind(JDBCDataDeleteCommand.class).to(JDBCDataDeleteCommandImpl.class);
		
		bind(JDBCMappingDelegate.class).to(JDBCMappingDelegateImpl.class);
		bind(QueryResultHandler.class).to(QueryResultHandlerImpl.class);
	}

}
