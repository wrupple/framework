package com.wrupple.muba.event.server.service.impl;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.chain.command.EventDispatcher;
import com.wrupple.muba.event.server.chain.command.impl.EventDispatcherImpl;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Named;
import javax.transaction.*;
import javax.validation.ConstraintViolation;

public class ServiceInvocationThread extends Thread {
	private static final Logger log = LogManager.getLogger(EventDispatcherImpl.class);

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
        UserTransaction transaction = requestContext.getTransactionHistory();

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
