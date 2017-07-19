package com.zieglersoftware.fetch.core;

import java.util.Collection;

/**
 * Executes SQL statements including {@code select} and various forms of DML and DDL.
 * <p>
 * In {@link #select}, {@link #insert}, and {@link #update}, the SQL statement may be parameterized with placeholders ("?").
 * <p>
 * {@link #select} returns its results as a {@link SelectResult}.
 * <p>
 * {@link #insert} returns the primary key(s) of the newly inserted record(s) as a {@link PrimaryKeyHolder}.
 * <p>
 * {@link #update} and {@link #execute} do not return values.
 * <p>
 * {@code Fetch} implementations must be immutable.
 * 
 * @see FetchDefaultImpl
 */
public interface Fetch extends SqlExecutor
{
	/**
	 * Executes the given SQL, which must be a single {@code select} query.
	 * <p>
	 * Returns the results of the query as a {@link SelectResult}.
	 * <p>
	 * The SQL may contain placeholders ("?"), in which case parameters must be specified to replace the placeholders.
	 * 
	 * @param sql
	 *            the SQL {@code select} query to be executed. May contain placeholders in the form of "?".
	 * @param parameters
	 *            the parameter values to bind to the query, replacing the placeholders.
	 * @return the results of the query as a {@link SelectResult} (never {@code null}).
	 */
	public SelectResult select(String sql, Object... parameters);

	/**
	 * Executes the given SQL, which must be a single {@code insert} statement.
	 * <p>
	 * Returns the primary key(s) of the newly inserted record(s) as a {@link PrimaryKeyHolder}.
	 * <p>
	 * The SQL may contain placeholders ("?"), in which case parameters must be specified to replace the placeholders.
	 * 
	 * @param sql
	 *            the SQL {@code insert} statement to be executed. May contain placeholders in the form of "?".
	 * @param primaryKeyColumnName
	 *            the name of the primary key column of the table being inserted into.
	 * @param parameters
	 *            the parameter values to bind to the query, replacing the placeholders.
	 * @return the primary key(s) of the newly inserted record(s) as a {@link PrimaryKeyHolder} (never {@code null}).
	 * 
	 * @see #insert(String, Collection, Object...)
	 */
	public PrimaryKeyHolder insert(String sql, String primaryKeyColumnName, Object... parameters);

	/**
	 * Executes the given SQL, which must be a single {@code insert} statement.
	 * <p>
	 * Returns the primary key(s) of the newly inserted record(s) as a {@link PrimaryKeyHolder}.
	 * <p>
	 * The SQL may contain placeholders ("?"), in which case parameters must be specified to replace the placeholders.
	 * 
	 * @param sql
	 *            the SQL {@code insert} statement to be executed. May contain placeholders in the form of "?".
	 * @param primaryKeyColumnNames
	 *            the names of the primary key columns of the table being inserted into.
	 * @param parameters
	 *            the parameter values to bind to the query, replacing the placeholders.
	 * @return the primary key(s) of the newly inserted record(s) as a {@link PrimaryKeyHolder} (never {@code null}).
	 * 
	 * @see #insert(String, String, Object...)
	 */
	public PrimaryKeyHolder insert(String sql, Collection<String> primaryKeyColumnNames, Object... parameters);

	@Override
	public void update(String sql, Object... parameters);

	@Override
	public void execute(String sql);
}