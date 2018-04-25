package com.wrupple.muba.event.server.domain.impl;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.CatalogKey;
import com.wrupple.muba.event.domain.ServiceContext;
import com.wrupple.muba.event.domain.TransactionHistory;
import com.wrupple.muba.event.server.chain.command.UserCommand;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.transaction.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogUserTransactionImpl implements TransactionHistory {

	protected Logger log = LogManager.getLogger(CatalogUserTransactionImpl.class);

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
        if (localTransaction != null)
            localTransaction.begin();
	}

	@Override
	public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
			SecurityException, IllegalStateException, SystemException {
        if (localTransaction != null)
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
        if (localTransaction != null)
            localTransaction.rollback();
		status = Status.STATUS_ROLLING_BACK;

		try {
			history.rollback();
		} catch (Exception e) {
			// context.getRuntimeContext().getSession().getRequestContext().addWarning("unable to
			// completely rollback catalog action");
		}

		status = Status.STATUS_ROLLEDBACK;
	}

	@Override
	public void setRollbackOnly() throws IllegalStateException, SystemException {
        if (localTransaction != null)
            localTransaction.setRollbackOnly();
		status = javax.transaction.Status.STATUS_MARKED_ROLLBACK;
	}

	@Override
	public void setTransactionTimeout(int arg0) throws SystemException {
        if (localTransaction != null)
            localTransaction.setTransactionTimeout(arg0);
	}
	@Override
	public <T extends CatalogEntry> void didRead(ServiceContext catalog, List<T> r, UserCommand dao) {
        history.didRead(r, dao, catalog);
    }
	@Override
	public <T extends CatalogEntry> void didRead(ServiceContext catalog, T r, UserCommand dao) {
        history.didRead(catalog, r, dao);
    }
	@Override
	public <T extends CatalogEntry> void didCreate(ServiceContext catalog, CatalogEntry regreso,
			UserCommand createDao) {
        history.didCreate(catalog, regreso, createDao);
    }
	@Override
	public <T extends CatalogEntry> void didUpdate(ServiceContext catalog, T original, T outDatedEntry,
			UserCommand dao) {
        history.didUpdate(catalog, original, outDatedEntry, dao);
    }
	@Override
	public <T extends CatalogEntry> void didDelete(ServiceContext catalog, T r, UserCommand dao) {
        history.didDelete(catalog, r, dao);
    }

	public enum CatalogAction {
		CREATE,UPDATE,READ,DELETE
	}

	public UndoBasedTransactionDelegate getDelegate() {
		return history;
	}

	public List<UndoBasedTransactionDelegate.UndoToken> getAuditTrails() {
		return history.getAuditTrails();
	}

	static class UndoBasedTransactionDelegate {
        private static final Logger log = LogManager.getLogger(UndoBasedTransactionDelegate.class);

        public static class UndoToken
		{
			public final CatalogAction actionType;
			public final int affectedUnits;
			private final UserCommand dao;
			public final Context context;

			private UndoToken(CatalogAction actionType, int affectedUnits, UserCommand dao,Context context) {
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
						token.dao.undo((ServiceContext) token.context);
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

		public <T extends CatalogKey> void didRead(List<T> r, UserCommand dao, Context context) {
			int affectedUnits = r == null ? 0 : r.size();
			log.debug("[NEW READ HISTORY TOKEN] result size={}",affectedUnits);
			history.add(new UndoToken(CatalogAction.READ, affectedUnits, dao,context));
		}

		public <T extends CatalogKey> void didRead(Context catalog, T r, UserCommand dao) {
			log.debug("[NEW READ HISTORY TOKEN] result={}",r);
			history.add(new UndoToken(CatalogAction.READ, 1,  dao,catalog));
		}

		public <T extends CatalogKey> void didCreate(Context catalog, T r, UserCommand dao) {
			log.debug("[NEW CREATE HISTORY TOKEN] result={}",r);
			history.add(new UndoToken(CatalogAction.CREATE, 1,dao,catalog));
		}

		public <T extends CatalogEntry> void didUpdate(Context catalog, T entry, T outDatedEntry, UserCommand dao) {
			log.debug("[NEW UPDATE HISTORY TOKEN] result={}",entry);
			history.add(new UndoToken(CatalogAction.UPDATE, 1,dao,catalog));
		}

		public <T extends CatalogKey> void didDelete(Context catalog, T r, UserCommand dao) {
			log.debug("[NEW DELETE HISTORY TOKEN] result={}",r);
			history.add(new UndoToken(CatalogAction.DELETE, 1,dao,catalog));
		}


		public void commit() {
			if(parent!=null){
				history.addAll(parent.history);
			}
		}

	}


}
