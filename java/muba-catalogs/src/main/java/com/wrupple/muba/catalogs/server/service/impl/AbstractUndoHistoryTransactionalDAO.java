package com.wrupple.muba.catalogs.server.service.impl;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.server.service.CatalogDataAccessObject;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.CatalogKey;

public abstract class AbstractUndoHistoryTransactionalDAO<T extends CatalogKey> implements CatalogDataAccessObject<T> {
	
	protected static final Logger log = LoggerFactory.getLogger(AbstractUndoHistoryTransactionalDAO.class);


	private  CatalogUserTransactionUndoHistoryDelegate history;
	protected CatalogExcecutionContext context;
	
	@Override
	public void beginTransaction() throws NotSupportedException, SystemException {
		if(history== null){
			log.debug("[start log]");
			history = spawn();
		}
	}
	
	private CatalogUserTransactionUndoHistoryDelegate spawn() {
		return new CatalogUserTransactionUndoHistoryDelegate(((CatalogUserTransaction)context.getRequest().getTransaction(context)).getDelegate());
	}

	@Override
	public void commitTransaction() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException,
			SystemException {
		if(history!=null){
			log.debug("[commit log]");
			history.commit();
		}
	}

	@Override
	public void rollbackTransaction() throws IllegalStateException, SecurityException, SystemException {
		if(history!= null){
			try {
				log.debug("[rollback log]");
				history.rollback();
			} catch (Exception e) {
				throw new SystemException(e.getMessage());
			}
			history=null;
		}
	}

	@Override
	public void setContext(CatalogExcecutionContext context) {
		this.context=context;
	}

	@Override
	public CatalogExcecutionContext getContext() {
		return context;
	}
}
