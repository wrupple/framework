package com.wrupple.muba.bootstrap.server.service.impl;

import javax.inject.Named;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolation;

import com.wrupple.muba.bootstrap.domain.RuntimeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.server.chain.command.EventDispatcher;
import com.wrupple.muba.bootstrap.server.chain.command.impl.EventDispatcherImpl;

public class ServiceInvocationThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(EventDispatcherImpl.class);

	private final RuntimeContext requestContext;
	private final EventDispatcher command;
	private  final boolean rollbackOnViolations;
	
	
	public ServiceInvocationThread(RuntimeContext context, EventDispatcher command, @Named("rollbackOnViolations") Boolean rollbackOnViolations) {
		super();
		this.command = command;
		this.requestContext = context;
		this.rollbackOnViolations=rollbackOnViolations;
	}

	@Override
	public void run() {
		UserTransaction transaction = requestContext.getApplication().getTransaction();

		try {
			try {

				if (transaction != null) {
					transaction.begin();
				}

				command.execute(requestContext);

				if (requestContext.getConstraintViolations() != null
						&& !requestContext.getConstraintViolations().isEmpty()) {
					log.error("Constraint Violations found in {}={}", requestContext.getId(), requestContext);
					if(log.isTraceEnabled()){
						for(ConstraintViolation< ?> viol: requestContext.getConstraintViolations()){
							log.trace("{}",viol);
						}
					}
					if (rollbackOnViolations && transaction != null) {
						transaction.rollback();
					}
				}

			} catch (Exception e) {
				log.error("Unknown Error while processing {}={}", requestContext.getId(), requestContext);
				if (transaction != null) {
					transaction.rollback();
				}
				requestContext.setCaughtException(e);
			} finally {
				if (transaction != null) {
					log.trace("commit transaction");
					transaction.commit();
				}

			}
		} catch (IllegalStateException | SecurityException | SystemException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException e1) {
			log.error("Unknown error while handling transaction", e1);
		}
		super.run();
	}
}
