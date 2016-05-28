package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogKey;

public class CatalogUserTransactionUndoHistoryDelegate {
	private static final Logger log = LoggerFactory.getLogger(CatalogUserTransactionUndoHistoryDelegate.class);
	public static class HistoryToken {
		public final CatalogAction actionType;
		public final int affectedUnits;
		private final CatalogKey entry;
		public final CatalogDescriptor catalog;
		private final CatalogDataAccessObject<CatalogKey> dao;
		public boolean metadata;
		private CatalogEntry original;

		private HistoryToken(CatalogAction actionType, int affectedUnits, CatalogKey entry, CatalogDescriptor catalog, CatalogDataAccessObject dao) {
			super();
			this.actionType = actionType;
			this.affectedUnits = affectedUnits;
			this.entry = entry;
			this.catalog = catalog;
			this.dao = dao;
		}

		public HistoryToken(CatalogDescriptor regreso) {
			this(CatalogAction.READ, 1, null, regreso, null);
			this.metadata = true;
		}

		public HistoryToken(CatalogEntry outDatedEntry, CatalogDescriptor catalog, CatalogDataAccessObject dao, CatalogEntry original) {
			this(CatalogAction.UPDATE, 1, outDatedEntry, catalog, dao);
			this.original = original;
		}

	}

	private final List<HistoryToken> history = new ArrayList<HistoryToken>();
	private final CatalogUserTransactionUndoHistoryDelegate parent;

	@Inject
	public CatalogUserTransactionUndoHistoryDelegate(CatalogUserTransactionUndoHistoryDelegate parent) {
		this.parent=parent;
	}

	public void rollback() throws Exception {
		if (history != null && !history.isEmpty()) {
			for (HistoryToken token : history) {
				if (token.actionType != CatalogAction.READ) {
					switch (token.actionType) {
					case UPDATE:
						token.dao.update(token.entry, token.original);
						break;
					case CREATE:
						token.dao.delete(token.entry);
						break;
					case DELETE:
						token.dao.create(token.entry);
						break;
					}
				}
			}
		}
	}

	public List<HistoryToken> getAuditTrails() {
		if (history == null) {
			return Collections.EMPTY_LIST;
		} else {
			return Collections.unmodifiableList(history);
		}
	}
	
	public <T extends CatalogKey> void didRead(CatalogDescriptor catalog, List<T> r, CatalogDataAccessObject<T> dao) {
		int affectedUnits = r == null ? 0 : r.size();
		log.debug("[NEW READ HISTORY TOKEN] result size={}",affectedUnits);
		history.add(new HistoryToken(CatalogAction.READ, affectedUnits, null, catalog, dao));
	}

	public <T extends CatalogKey> void didRead(CatalogDescriptor catalog, T r, CatalogDataAccessObject<T> dao) {
		log.debug("[NEW READ HISTORY TOKEN] result={}",r);
		history.add(new HistoryToken(CatalogAction.READ, 1, r, catalog, dao));
	}

	public <T extends CatalogKey> void didCreate(CatalogDescriptor catalog, T r, CatalogDataAccessObject<T> dao) {
		log.debug("[NEW CREATE HISTORY TOKEN] result={}",r);
		history.add(new HistoryToken(CatalogAction.CREATE, 1, r, catalog, dao));
	}

	public <T extends CatalogEntry> void didUpdate(CatalogDescriptor catalog, T entry, T outDatedEntry, CatalogDataAccessObject<T> dao) {
		log.debug("[NEW UPDATE HISTORY TOKEN] result={}",entry);
		history.add(new HistoryToken(outDatedEntry, catalog, dao, entry));
	}

	public <T extends CatalogKey> void didDelete(CatalogDescriptor catalog, T r, CatalogDataAccessObject<T> dao) {
		log.debug("[NEW DELETE HISTORY TOKEN] result={}",r);
		history.add(new HistoryToken(CatalogAction.DELETE, 1, r, catalog, dao));
	}

	public void didMetadataRead(CatalogDescriptor regreso) {
		log.debug("[NEW INSPECT HISTORY TOKEN] result={}",regreso);
		if (regreso != null && regreso.getCatalogId() != null) {
			history.add(new HistoryToken(regreso));
		}
	}

	public void commit() {
		if(parent!=null){
			history.addAll(parent.history);
		}
	}

}
