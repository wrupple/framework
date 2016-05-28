package com.wrupple.muba.catalogs.server.service.impl;

import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.muba.catalogs.server.service.impl.CatalogUserTransactionUndoHistoryDelegate.HistoryToken;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogKey;

public class CatalogUserTransaction implements UserTransaction {

	private int status = javax.transaction.Status.STATUS_PREPARED;
	private final CatalogUserTransactionUndoHistoryDelegate history = new CatalogUserTransactionUndoHistoryDelegate(null);

	@Override
	public void begin() throws NotSupportedException, SystemException {
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException,
			SystemException {
		status = javax.transaction.Status.STATUS_COMMITTING;
		status = javax.transaction.Status.STATUS_COMMITTED;

	}

	@Override
	public int getStatus() throws SystemException {
		return status;
	}

	@Override
	public void rollback() throws IllegalStateException, SecurityException, SystemException {
		status = Status.STATUS_ROLLING_BACK;

		try {
			history.rollback();
		} catch (Exception e) {
			//context.getSession().getRequestContext().addWarning("unable to completely rollback catalog action");
		}

		status = Status.STATUS_ROLLEDBACK;
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		status = javax.transaction.Status.STATUS_MARKED_ROLLBACK;
	}

	@Override
	public void setTransactionTimeout(int arg0) throws SystemException {
		// TODO

	}

	protected <T extends CatalogKey> void didRead(CatalogDescriptor catalog, List<T> r, CatalogDataAccessObject<T> dao) {
		history.didRead(catalog, r, dao);
	}

	protected <T extends CatalogKey> void didRead(CatalogDescriptor catalog, T r, CatalogDataAccessObject<T> dao) {
		history.didRead(catalog, r, dao);
	}

	protected <T extends CatalogKey> void didCreate(CatalogDescriptor catalog, T r, CatalogDataAccessObject<T> dao) {
		history.didCreate(catalog, r, dao);
	}

	protected <T extends CatalogEntry> void didUpdate(CatalogDescriptor catalog, T original, T outDatedEntry, CatalogDataAccessObject<T> dao) {
		history.didUpdate(catalog, original, outDatedEntry, dao);
	}

	protected <T extends CatalogKey> void didDelete(CatalogDescriptor catalog, T r, CatalogDataAccessObject<T> dao) {
		history.didDelete(catalog, r, dao);
	}

	public void didMetadataRead(CatalogDescriptor regreso) {
		if (regreso != null && regreso.getCatalogId() != null) {
			history.didMetadataRead(regreso);
		}
	}


	public CatalogUserTransactionUndoHistoryDelegate getDelegate() {
		return history;
	}

	public List<HistoryToken> getAuditTrails() {
		return history.getAuditTrails();
	}


}
