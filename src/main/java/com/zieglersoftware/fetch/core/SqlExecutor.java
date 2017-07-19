package com.zieglersoftware.fetch.core;

import com.zieglersoftware.fetch.async.SqlQueue;

/**
 * Executes SQL statements that do not return any data, i.e., DML and DDL.
 * <p>
 * Provides two methods: {@link #update} to execute {@code insert}, {@code update}, or {@code delete} statements or stored procedure calls,
 * and {@link #execute} to execute arbitrary commands. With {@code update}, the SQL may be parameterized with placeholders ("?").
 * <p>
 * {@code SqlExecutor} implementations must be immutable.
 * 
 * @see Fetch
 * @see SqlQueue
 */
public interface SqlExecutor
{
	/**
	 * Executes the given SQL, which must be a single {@code insert}, {@code update}, {@code delete}, or stored procedure call.
	 * <p>
	 * Does not return a value.
	 * <p>
	 * The SQL may contain placeholders ("?"), in which case parameters must be specified to replace the placeholders.
	 * 
	 * @param sql
	 *            the SQL statement to be executed. May contain placeholders in the form of "?".
	 * @param parameters
	 *            the parameter values to bind to the query, replacing the placeholders.
	 */
	public void update(String sql, Object... parameters);

	/**
	 * Executes the given SQL, which can be any arbitrary command(s) recognized by the database. For example, it may represent DDL or
	 * database configuration. It is allowed to consist of multiple SQL statements separated by ";", to be executed in their given order.
	 * <p>
	 * Does not return a value.
	 * <p>
	 * The SQL cannot contain placeholders ("?").
	 * <p>
	 * Care should be taken to avoid SQL injection vulnerabilities when using this method.
	 * 
	 * @param sql
	 *            the SQL to be executed
	 */
	public void execute(String sql);
}