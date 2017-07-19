package com.zieglersoftware.fetch.async;

import com.zieglersoftware.fetch.core.SqlExecutor;

/**
 * Asynchronous counterpart to {@link SqlExecutor}. Statements can be submitted to the queue for future execution. Upon submission of a
 * statement to the queue, control immediately returns to the caller, likely before the statement begins execution.
 * <p>
 * Provides two methods: {@link #submitUpdate} to execute {@code insert}, {@code update}, or {@code delete} statements or stored procedure
 * calls, and {@link #submitExecute} to execute arbitrary commands. With {@code submitUpdate}, the SQL may be parameterized with
 * placeholders ("?").
 * <p>
 * Both methods return a {@code boolean} indicating whether the statement was actually added to the queue. Implementations of
 * {@code SqlQueue} are free to enforce a maximum size of the queue and reject excess submissions, returning {@code false} in that
 * situation. This policy should be documented by the implementing class.
 * <p>
 * Implementations of {@code SqlQueue} must be thread-safe.
 * 
 * @see SqlQueueDefaultImpl
 * @see SqlExecutor
 */
public interface SqlQueue
{
	/**
	 * Submits the given SQL for future asynchronous execution. The SQL must be a single {@code insert}, {@code update}, {@code delete}, or
	 * stored procedure call.
	 * <p>
	 * The SQL may contain placeholders ("?"), in which case parameters must be specified to replace the placeholders.
	 * <p>
	 * Control immediately returns to the caller, likely before the statement begins execution.
	 * <p>
	 * Returns {@code true} if the statement was actually added to the queue, and {@code false} otherwise. Implementations of
	 * {@code SqlQueue} are free to enforce a maximum size of the queue and reject excess submissions, returning {@code false} in that
	 * situation. This policy should be documented by the implementing class.
	 * 
	 * @param sql
	 *            the SQL statement to be executed. May contain placeholders in the form of "?".
	 * @param parameters
	 *            the parameter values to bind to the query, replacing the placeholders.
	 * @return {@code true} if the statement was actually added to the queue, {@code false} otherwise
	 */
	public boolean submitUpdate(String sql, Object... parameters);

	/**
	 * Submits the given SQL for future asynchronous execution. The SQL can be any arbitrary command(s) recognized by the database. For
	 * example, it may represent DDL or database configuration. It is allowed to consist of multiple SQL statements separated by ";", to be
	 * executed in their given order.
	 * <p>
	 * The SQL cannot contain placeholders ("?").
	 * <p>
	 * Control immediately returns to the caller, likely before the SQL begins execution.
	 * <p>
	 * Returns {@code true} if the SQL was actually added to the queue, and {@code false} otherwise. Implementations of {@code SqlQueue} are
	 * free to enforce a maximum size of the queue and reject excess submissions, returning {@code false} in that situation. This policy
	 * should be documented by the implementing class.
	 * 
	 * @param sql
	 *            the SQL to be executed
	 * @return {@code true} if the SQL was actually added to the queue, {@code false} otherwise
	 */
	public boolean submitExecute(String sql);
}