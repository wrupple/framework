package com.wrupple.muba.catalogs.server.chain.command.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.chain.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.WritePublicTimelineEventDiscriminator;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin.Session;

@Singleton
public class WritePublicTimelineEventDiscriminatorImpl implements WritePublicTimelineEventDiscriminator {

	protected static final Logger log = LoggerFactory.getLogger(CommitCatalogActionImpl.class);
	private final String discriminatorField, catalogField;

	@Inject
	public WritePublicTimelineEventDiscriminatorImpl(
			@Named("catalog.timeline.entryDiscriminator") String discriminatorField,
			@Named("catalog.timeline.typeDiscriminator") String catalogField) {
		super();
		this.discriminatorField = discriminatorField;
		this.catalogField = catalogField;
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogActionContext context = (CatalogActionContext) c;
		CatalogEntry node = (CatalogEntry) context.getEntryValue();
		Session session = context.getCatalogManager().newSession(node);
		CatalogDescriptor catalog = context.getCatalogDescriptor();
		context.getCatalogManager().setPropertyValue(catalog, getDiscriminatorField(), node, node.getId(), session);
		context.getCatalogManager().setPropertyValue(catalog, getCatalogField(), node, catalog.getId(), session);
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
