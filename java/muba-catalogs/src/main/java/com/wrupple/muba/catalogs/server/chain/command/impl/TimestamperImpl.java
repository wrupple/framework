package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.reserved.HasTimestamp;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.Timestamper;
import com.wrupple.muba.catalogs.shared.service.FieldAccessStrategy.Session;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class TimestamperImpl implements Timestamper {

	
	@Inject
	public TimestamperImpl() {
		super();
	}



	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		Session session = context.getCatalogManager().access().newSession((CatalogEntry) context.getEntryValue());
		CatalogEntry entry = (CatalogEntry)context.getEntryValue();
		context.getCatalogManager().access().setPropertyValue(HasTimestamp.FIELD, entry, new Date(), session);
		return CONTINUE_PROCESSING;
	}

}
