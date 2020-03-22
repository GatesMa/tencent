/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq;


/**
 * A context object that is used to pass arguments to the various methods of
 * {@link TransactionProvider}.
 *
 * @author Lukas Eder
 */
public interface TransactionContext extends Scope {

    /**
     * A user-defined transaction object, possibly obtained from
     * {@link TransactionProvider#begin(TransactionContext)}.
     *
     * @return The transaction object. May be <code>null</code>.
     */
    Transaction transaction();

    /**
     * Set the user-defined transaction object to the current transaction
     * context.
     */
    TransactionContext transaction(Transaction transaction);

    /**
     * The exception that has caused the rollback.
     *
     * @return The exception. May be <code>null</code>, in particular if the
     *         cause is a {@link Throwable}, in case of which
     *         {@link #causeThrowable()} should be called.
     */
    Exception cause();

    /**
     * The throwable that has caused the rollback.
     *
     * @return The throwable. May be <code>null</code>.
     */
    Throwable causeThrowable();

    /**
     * Set the exception that has caused the rollback to the current transaction
     * context.
     */
    TransactionContext cause(Exception cause);

    /**
     * Set the throwable that has caused the rollback to the current transaction
     * context.
     */
    TransactionContext causeThrowable(Throwable cause);
}
