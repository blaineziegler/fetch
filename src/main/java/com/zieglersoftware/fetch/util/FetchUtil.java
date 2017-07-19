package com.zieglersoftware.fetch.util;

import static com.zieglersoftware.assertions.Assertions.notNull;

import com.zieglersoftware.fetch.core.Fetch;

/**
 * Provides static utility methods to assist with {@link Fetch} usage
 */
public final class FetchUtil
{
	private FetchUtil()
	{
		// Cannot instantiate
	}

	/**
	 * Returns a {@code String} that is an {@code insert} query for the given table and column names, containing "?" placeholders as the
	 * values to be inserted into each given column.
	 * <p>
	 * For example, if {@code tableName} is "person", {@code firstColumnName} is "first_name", and {@code remainingColumnNames} is
	 * ["last_name", "age", "hometown"], this method will return<br>
	 * {@code insert into person(first_name, last_name, age, hometown) values (?, ?, ?, ?)}
	 * <p>
	 * This method does <b>not</b> verify that the given table and column names are simple identifiers as opposed to executable SQL code.
	 * That verification is left to the caller; failure to do so could clearly result in unexpected behavior or intentional malice via SQL
	 * injection. In typical usage, the caller will define these names as string literals, making such verification trivial.
	 * 
	 * @param tableName
	 *            the name of the table into which to insert a record (not {@code null})
	 * @param firstColumnName
	 *            the name of the first column of the table into which to insert a record (not {@code null})
	 * @param remainingColumnNames
	 *            the names of all columns after the first column of the table into which to insert a record (may be empty, may not be
	 *            {@code null}, may not contain {@code null} elements)
	 * @return an {@code insert} query of the form <br>
	 *         {@code insert into $tableName ($firstColumnName, $remainingColumnNames[0], $remainingColumnNames[1], ...) values (?, ?, ?, ...)}
	 */
	public static String insertSql(String tableName, String firstColumnName, String... remainingColumnNames)
	{
		notNull(tableName, "tableName");
		notNull(firstColumnName, "firstColumnName");
		notNull(remainingColumnNames, "remainingColumnNames");
		StringBuilder sqlBuilder = new StringBuilder("insert into ").append(tableName).append(" (").append(firstColumnName);
		for (String columnName : remainingColumnNames)
			sqlBuilder.append(", ").append(columnName);
		sqlBuilder.append(") values (?");
		for (int i = 0; i < remainingColumnNames.length; i++)
			sqlBuilder.append(", ?");
		sqlBuilder.append(")");
		return sqlBuilder.toString();
	}
}