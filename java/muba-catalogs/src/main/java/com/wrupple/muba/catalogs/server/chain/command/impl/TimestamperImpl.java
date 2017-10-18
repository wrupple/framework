package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.reserved.HasTimestamp;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.Timestamper;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;

@Singleton
public class TimestamperImpl implements Timestamper {

	private final FieldAccessStrategy access;
	@Inject
	public TimestamperImpl(FieldAccessStrategy access) {
		super();
		this.access = access;
	}



	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		Instrospection instrospection = access.newSession((CatalogEntry) context.getRequest().getEntryValue());
		CatalogEntry entry = (CatalogEntry)context.getRequest().getEntryValue();
		access.setPropertyValue(HasTimestamp.FIELD, entry, new Date(), instrospection);
		return CONTINUE_PROCESSING;
	}

}
