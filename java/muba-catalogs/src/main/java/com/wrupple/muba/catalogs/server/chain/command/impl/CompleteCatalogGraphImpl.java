package com.wrupple.muba.catalogs.server.chain.command.impl;

import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.catalogs.server.chain.command.CompleteCatalogGraph;
import com.wrupple.muba.event.domain.Instrospection;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.commons.chain.Context;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class CompleteCatalogGraphImpl extends DataJoiner implements CompleteCatalogGraph {

	@Inject
	public CompleteCatalogGraphImpl(EntrySynthesizer inheritanceDelegate, CatalogKeyServices keydelegateValue, FieldAccessStrategy accessStrategy) {
		super(inheritanceDelegate, keydelegateValue, accessStrategy);
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		log.trace("<{}>",this.getClass().getSimpleName());
		CatalogActionContext context = (CatalogActionContext) ctx;
		if (!(context.getResults() == null || context.getResults().isEmpty())) {
			CatalogDescriptor descriptor = context.getCatalogDescriptor();
			String[][] joins = super.keydelegate.getJoins(context, null, descriptor, null,
					context, null);

			Map<JoinQueryKey, Set<Object>> filterMap = createFilterMap(joins, context);
			joinWithGivenJoinData(context.getResults(),descriptor, joins,
					context, filterMap, access.newSession(null));
		}
		log.trace("</{}>",this.getClass().getSimpleName());
		return CONTINUE_PROCESSING;
	}

	@Override
	protected void workJoinData(List<CatalogEntry> mainResults, CatalogDescriptor mainCatalog, List<CatalogEntry> joins,
			CatalogDescriptor joinCatalog, CatalogActionContext context, Instrospection instrospection) throws Exception {
		log.trace("Working Catalog Graph of {} and {}", mainCatalog.getDistinguishedName(),
				joinCatalog.getDistinguishedName());
		Collection<FieldDescriptor> fields = mainCatalog.getFieldsValues();
		CatalogEntry sample = mainResults.get(0);
		Collection<Object> needs;
		Object need;
		List<CatalogEntry> matches;
		CatalogEntry match;
		String reservedField;
		for (FieldDescriptor field : fields) {
			if ( super.keydelegate.isJoinableValueField(field)
					&& field.getCatalog().equals(joinCatalog.getDistinguishedName())) {
				if (field.isKey()) {

					Map<Object, CatalogEntry> key = null;
					if (field.isMultiple()) {
						reservedField = field.getFieldId() + CatalogEntry.MULTIPLE_FOREIGN_KEY;
						if (access.isWriteableProperty(reservedField, sample, instrospection)) {
							log.trace("Working field {}", field.getFieldId());
							for (CatalogEntry e : mainResults) {
								needs = (Collection<Object>) access.getPropertyValue(field, e, null, instrospection);
								if (needs != null) {
									if (key == null) {
										key = mapJoins(new HashMap<Object, CatalogEntry>(joins.size()), joins);
									}
									matches = new ArrayList<CatalogEntry>(needs.size());
									for (Object required : needs) {
										match = key.get(required);
										matches.add(match);
									}
									access.setPropertyValue(reservedField, e, matches, instrospection);
								}

							}

						}
					} else {
						reservedField = field.getFieldId() + CatalogEntry.FOREIGN_KEY;
						if (access.isWriteableProperty(reservedField, sample, instrospection)) {
							log.trace("Working one to many relationship {}", field.getFieldId());
							if (key == null) {
								key = mapJoins(new HashMap<Object, CatalogEntry>(joins.size()), joins);
							}
							for (CatalogEntry e : mainResults) {
								need = access.getPropertyValue(field, e, null, instrospection);
								match = key.get(need);
								access.setPropertyValue(reservedField, e, match, instrospection);
							}

						}
					}

				} else if (field.isEphemeral()) {
					if (field.getSentence() == null) {
						log.trace("Working many to one relationship {}", field.getFieldId());

						reservedField =  super.keydelegate.getFieldWithForeignType(joinCatalog,mainCatalog.getDistinguishedName());
						FieldDescriptor foreignField = joinCatalog.getFieldDescriptor(reservedField);
						Object temp;
						for (CatalogEntry e : mainResults) {
							matches = null;
							need = e.getId();
							for (CatalogEntry i : joins) {
								temp = access.getPropertyValue(foreignField, i, null, instrospection);
								if (need.equals(temp)) {
									if (matches == null) {
										matches = new ArrayList<CatalogEntry>();
									}
									matches.add(i);
								}
							}

							access.setPropertyValue(reservedField, e, matches, instrospection);
						}
					} else {

						// FIXME evaluation? or is evaluation at result set
						// construction a better deal?
					}

				}
			}
		}

	}

}
