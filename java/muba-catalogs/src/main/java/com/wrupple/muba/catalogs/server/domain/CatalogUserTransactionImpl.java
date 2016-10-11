package com.wrupple.muba.catalogs.server.domain;

import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.MubaTest;
import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ContractDescriptor;
import com.wrupple.muba.bootstrap.domain.TransactionHistory;
import com.wrupple.muba.bootstrap.domain.UserContext;
import com.wrupple.muba.bootstrap.server.chain.command.UserCommand;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.server.service.impl.UndoBasedTransactionDelegate;
import com.wrupple.muba.catalogs.server.service.impl.UndoBasedTransactionDelegate.UndoToken;

public class CatalogUserTransactionImpl implements TransactionHistory {
	protected Logger log = LoggerFactory.getLogger(MubaTest.class);

	private int status ;
	private final UndoBasedTransactionDelegate history;
	private final UserTransaction localTransaction;

	
	public CatalogUserTransactionImpl(UserTransaction localTransaction) {
		super();
		this.status = javax.transaction.Status.STATUS_PREPARED;
		this.localTransaction = localTransaction;
		history = new UndoBasedTransactionDelegate(null);
	}

	@Override
	public void begin() throws NotSupportedException, SystemException {
		localTransaction.begin();
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
			SecurityException, IllegalStateException, SystemException {
		localTransaction.commit();
		status = javax.transaction.Status.STATUS_COMMITTING;
		status = javax.transaction.Status.STATUS_COMMITTED;
	}

	@Override
	public int getStatus() throws SystemException {
		return status;
	}

	@Override
	public void rollback() throws IllegalStateException, SecurityException, SystemException {
		localTransaction.rollback();
		status = Status.STATUS_ROLLING_BACK;

		try {
			history.rollback();
		} catch (Exception e) {
			// context.getSession().getRequestContext().addWarning("unable to
			// completely rollback catalog action");
		}

		status = Status.STATUS_ROLLEDBACK;
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
		localTransaction.setRollbackOnly();
		status = javax.transaction.Status.STATUS_MARKED_ROLLBACK;
	}

	@Override
	public void setTransactionTimeout(int arg0) throws SystemException {
		localTransaction.setTransactionTimeout(arg0);
	}
	@Override
	public <T extends CatalogEntry> void didRead(UserContext catalog, List<T> r, UserCommand dao) {
		history.didRead(r, dao, (CatalogActionContext)catalog);
	}
	@Override
	public <T extends CatalogEntry> void didRead(UserContext catalog, T r, UserCommand dao) {
		history.didRead((CatalogActionContext)catalog, r, dao);
	}
	@Override
	public <T extends CatalogEntry> void didCreate(UserContext catalog, CatalogEntry regreso,
			UserCommand createDao) {
		history.didCreate((CatalogActionContext)catalog, regreso, createDao);
	}
	@Override
	public <T extends CatalogEntry> void didUpdate(UserContext catalog, T original, T outDatedEntry,
			UserCommand dao) {
		history.didUpdate((CatalogActionContext)catalog, original, outDatedEntry, dao);
	}
	@Override
	public <T extends CatalogEntry> void didDelete(UserContext catalog, T r, UserCommand dao) {
		history.didDelete((CatalogActionContext)catalog, r, dao);
	}
	@Override
	public void didMetadataRead(ContractDescriptor regreso) {
		if (regreso != null && regreso.getCatalog() != null) {
			history.didMetadataRead((CatalogDescriptor) regreso);
		}
	}

	public UndoBasedTransactionDelegate getDelegate() {
		return history;
	}

	public List<UndoToken> getAuditTrails() {
		return history.getAuditTrails();
	}

}
