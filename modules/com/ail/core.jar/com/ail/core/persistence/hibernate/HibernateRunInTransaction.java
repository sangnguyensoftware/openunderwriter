/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.ail.core.persistence.hibernate;

import static javax.transaction.Status.STATUS_ACTIVE;
import static javax.transaction.Status.STATUS_NO_TRANSACTION;
import static org.hibernate.FlushMode.MANUAL;

import java.lang.reflect.InvocationTargetException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.hibernate.Session;

import com.ail.core.persistence.OutsideTransactionContext;

public abstract class HibernateRunInTransaction<T> {
    private T result;
    private boolean beganTransaction = false;
    private UserTransaction userTransaction = null;
    private Session session = null;

    public abstract T run() throws Throwable;

    public T result() {
        return result;
    }

    public HibernateRunInTransaction<T> invoke() throws Throwable {
        return invoke(false);
    }

    public HibernateRunInTransaction<T> invoke(boolean readonly) throws Throwable {

        try {
            beginOrJoinTransaction(readonly);

            result = this.run();

            commitTransaction();
        } catch (ForceSilentRollbackError e) {
            rollbackTransaction();
        } catch (Throwable t) {
            rollbackTransaction();
            unwindException(t);
        } finally {
            closeTransaction();
        }

        return this;
    }

    void unwindException(Throwable t) throws Throwable {
        if (!(t.getCause() instanceof ForceSilentRollbackError)) {
            if (t instanceof InvocationTargetException) {
                throw ((InvocationTargetException) t).getTargetException();
            } else {
                throw t;
            }
        }
    }

    private void beginOrJoinTransaction(boolean readonly) throws NamingException, SystemException, NotSupportedException {
         try {
             userTransaction = (UserTransaction) InitialContext.doLookup("java:jboss/UserTransaction");

             if (userTransaction.getStatus() == STATUS_NO_TRANSACTION) {
                 userTransaction.begin();
                 beganTransaction = true;
             }
         }
         catch(NoInitialContextException e) {
             session = HibernateSessionBuilder.getSessionFactory().openSession();

             if (!session.getTransaction().isActive()) {
                 if (readonly) {
                     session.setFlushMode(MANUAL);
                     session.setDefaultReadOnly(true);
                 }
                 session.getTransaction().begin();
                 beganTransaction = true;
             }
         }

         if (beganTransaction) {
             OutsideTransactionContext.initialise();
         }
    }

    private void commitTransaction() throws Exception {
        if (beganTransaction) {
            if (userTransaction != null) {
                userTransaction.commit();
            }
            if (session != null) {
                session.getTransaction().commit();
            }

            OutsideTransactionContext.executePostCommitCommands();
        }
    }

    private void rollbackTransaction() throws SystemException {
        if (userTransaction != null && userTransaction.getStatus() == STATUS_ACTIVE) {
            userTransaction.rollback();
        }

        if (session != null && session.getTransaction().isActive()) {
            session.getTransaction().rollback();
        }
    }

    void closeTransaction() {
        if (beganTransaction) {
            if (session != null && session.isOpen()) {
                session.close();
            }

            OutsideTransactionContext.destroy();
        }
    }
}
