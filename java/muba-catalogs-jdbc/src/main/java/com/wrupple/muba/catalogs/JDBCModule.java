package com.wrupple.muba.catalogs;

import com.google.inject.AbstractModule;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataCreationCommand;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataDeleteCommand;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataQueryCommand;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataReadCommand;
import com.wrupple.muba.catalogs.server.chain.command.JDBCDataWritingCommand;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataCreationCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataDeleteCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataQueryCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataReadCommandImpl;
import com.wrupple.muba.catalogs.server.chain.command.impl.JDBCDataWritingCommandImpl;
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
