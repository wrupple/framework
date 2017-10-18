package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.WritePublicTimelineEventDiscriminator;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class WritePublicTimelineEventDiscriminatorImpl implements WritePublicTimelineEventDiscriminator {

	protected static final Logger log = LoggerFactory.getLogger(CommitCatalogActionImpl.class);
	private final String discriminatorField, catalogField;
	private final FieldAccessStrategy access;

	@Inject
	public WritePublicTimelineEventDiscriminatorImpl(FieldAccessStrategy access,
			@Named("catalog.timeline.entryDiscriminator") String discriminatorField,
			@Named("catalog.timeline.typeDiscriminator") String catalogField) {
		super();
		this.discriminatorField = discriminatorField;
		this.catalogField = catalogField;
		this.access=access;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		CatalogEntry node = (CatalogEntry) context.getRequest().getEntryValue();
        Instrospection instrospection = access.newSession(node);
        CatalogDescriptor catalog = context.getCatalogDescriptor();
		access.setPropertyValue(getDiscriminatorField(), node, node.getId(), instrospection);
		access.setPropertyValue(getCatalogField(), node, catalog.getId(), instrospection);
        return CONTINUE_PROCESSING;
	}

	@Override
	public String getDiscriminatorField() {
		return discriminatorField;
	}

	@Override
	public String getCatalogField() {
		return catalogField;
	}

}
