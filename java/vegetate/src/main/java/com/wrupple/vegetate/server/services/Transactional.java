package com.wrupple.vegetate.server.services;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

public interface Transactional {

	 /**
    * Create a new transaction and associate it with the current thread.
    *
    * @exception NotSupportedException Thrown if the thread is already
    *    associated with a transaction and the Transaction Manager
    *    implementation does not support nested transactions.
    *
    * @exception SystemException Thrown if the transaction manager
    *    encounters an unexpected error condition.
    *
    */
   void beginTransaction() throws NotSupportedException, SystemException;

   /**
    * Complete the transaction associated with the current thread. When this
    * method completes, the thread is no longer associated with a transaction.
    *
    * @exception RollbackException Thrown to indicate that
    *    the transaction has been rolled back rather than committed.
    *
    * @exception HeuristicMixedException Thrown to indicate that a heuristic
    *    decision was made and that some relevant updates have been committed
    *    while others have been rolled back.
    *
    * @exception HeuristicRollbackException Thrown to indicate that a
    *    heuristic decision was made and that all relevant updates have been
    *    rolled back.
    *
    * @exception SecurityException Thrown to indicate that the thread is
    *    not allowed to commit the transaction.
    *
    * @exception IllegalStateException Thrown if the current thread is
    *    not associated with a transaction.
    *
    * @exception SystemException Thrown if the transaction manager
    *    encounters an unexpected error condition.
   */
   void commitTransaction() throws RollbackException,
	HeuristicMixedException, HeuristicRollbackException, SecurityException,
	IllegalStateException, SystemException;

   /**
    * Roll back the transaction associated with the current thread. When this
    * method completes, the thread is no longer associated with a transaction.
    *
    * @exception SecurityException Thrown to indicate that the thread is
    *    not allowed to roll back the transaction.
    *
    * @exception IllegalStateException Thrown if the current thread is
    *    not associated with a transaction.
    *
    * @exception SystemException Thrown if the transaction manager
    *    encounters an unexpected error condition.
    *
    */
   void rollbackTransaction() throws IllegalStateException, SecurityException,
       SystemException;
}
