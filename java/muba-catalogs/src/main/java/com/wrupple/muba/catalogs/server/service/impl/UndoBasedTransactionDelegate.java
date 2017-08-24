package com.wrupple.muba.catalogs.server.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.CatalogKey;
import com.wrupple.muba.event.server.chain.command.UserCommand;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;

public class UndoBasedTransactionDelegate {
	private static final Logger log = LoggerFactory.getLogger(UndoBasedTransactionDelegate.class);
	public static class UndoToken
	{
		public final CatalogAction actionType;
		public final int affectedUnits;
		private final UserCommand dao;
		public final CatalogActionContext context;

		private UndoToken(CatalogAction actionType, int affectedUnits, UserCommand dao,CatalogActionContext context) {
			super();
			this.actionType = actionType;
			this.affectedUnits = affectedUnits;
			this.context=context;
			this.dao = dao;
		}
	}

	private final List<UndoToken> history = new ArrayList<UndoToken>();
	private final UndoBasedTransactionDelegate parent;

	public UndoBasedTransactionDelegate(UndoBasedTransactionDelegate parent) {
		this.parent=parent;
	}

	public void rollback() throws Exception {
		if (history != null && !history.isEmpty()) {
			for (UndoToken token : history) {
				if (token.actionType != CatalogAction.READ) {
					token.dao.undo(token.context);
				}
			}
		}
	}

	public List<UndoToken> getAuditTrails() {
		if (history == null) {
			return Collections.EMPTY_LIST;
		} else {
			return Collections.unmodifiableList(history);
		}
	}
	
	public <T extends CatalogKey> void didRead( List<T> r, UserCommand dao, CatalogActionContext context) {
		int affectedUnits = r == null ? 0 : r.size();
		log.debug("[NEW READ HISTORY TOKEN] result size={}",affectedUnits);
		history.add(new UndoToken(CatalogAction.READ, affectedUnits, dao,context));
	}

	public <T extends CatalogKey> void didRead(CatalogActionContext catalog, T r, UserCommand dao) {
		log.debug("[NEW READ HISTORY TOKEN] result={}",r);
		history.add(new UndoToken(CatalogAction.READ, 1,  dao,catalog));
	}

	public <T extends CatalogKey> void didCreate(CatalogActionContext catalog, T r, UserCommand dao) {
		log.debug("[NEW CREATE HISTORY TOKEN] result={}",r);
		history.add(new UndoToken(CatalogAction.CREATE, 1,dao,catalog));
	}

	public <T extends CatalogEntry> void didUpdate(CatalogActionContext catalog, T entry, T outDatedEntry, UserCommand dao) {
		log.debug("[NEW UPDATE HISTORY TOKEN] result={}",entry);
		history.add(new UndoToken(CatalogAction.UPDATE, 1,dao,catalog));
	}

	public <T extends CatalogKey> void didDelete(CatalogActionContext catalog, T r, UserCommand dao) {
		log.debug("[NEW DELETE HISTORY TOKEN] result={}",r);
		history.add(new UndoToken(CatalogAction.DELETE, 1,dao,catalog));
	}

	public void didMetadataRead(CatalogDescriptor regreso) {
		log.debug("[NEW INSPECT HISTORY TOKEN] result={}",regreso);
		if (regreso != null && regreso.getDistinguishedName() != null) {
			history.add(new UndoToken(CatalogAction.READ, 1,null,null));
		}
	}

	public void commit() {
		if(parent!=null){
			history.addAll(parent.history);
		}
	}

}
