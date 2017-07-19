package com.zieglersoftware.fetch.core;

import static com.zieglersoftware.assertions.Assertions.notNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.zieglersoftware.fetch.converter.Converter;
import com.zieglersoftware.fetch.exception.ConversionException;
import com.zieglersoftware.fetch.exception.EmptyResultException;
import com.zieglersoftware.fetch.exception.ResultShapeException;

/**
 * Represents the result of a {@code select} query to the database. {@code SelectResult} provides a more convenient API than
 * {@link ResultSet}.
 * <p>
 * A result is either empty or has the "shape" of a single value, row, column, or table. {@code SelectResult} provides methods to extract
 * the query result as whichever shape is applicable. These methods come in two forms -- one that asserts that the result should be
 * non-empty and fails with an exception if it is in fact empty, and the other that treats an empty result as a valid case. The latter have
 * their method names suffixed with {@code "opt"}, for optional.
 * <p>
 * Tables and columns are generally viewed as variable-length lists that you may wish to iterate over, so their {@code opt} methods return
 * an empty {@code List} when the result is empty. Rows, in contrast, are generally viewed as fixed-length tuples whose values collectively
 * represent a single object, so their {@code opt} methods return an empty {@link Optional} rather than an empty {@code List} when the
 * result is empty. The {@code opt} methods for individual values also return an {@code Optional}. {@code Null} is never returned as a
 * representation of an empty result; however, {@link #asValue()} returns {@code null} if the result has a value which is itself
 * {@code null}.
 * <p>
 * Some methods return result values as {@code Object}, while some convenience methods return values as particular specified types, such as
 * {@code Integer} or {@code String}.
 * <p>
 * {@code SelectResult} is immutable.
 */
public final class SelectResult
{
	private final Converter converter;
	private final List<List<Object>> table;
	private final int numberOfRows;
	private final int numberOfColumns;
	private final boolean isTable;
	private final boolean isRow;
	private final boolean isColumn;
	private final boolean isValue;
	private final boolean isEmpty;

	/**
	 * Package-private constructor. Public clients have no need to instantiate this class.
	 */
	SelectResult(ResultSet resultSet, Converter converter)
	{
		this.converter = converter;
		int numberOfRows;
		int numberOfColumnsAssumingNonEmptyResult;
		try
		{
			numberOfColumnsAssumingNonEmptyResult = resultSet.getMetaData().getColumnCount();
			numberOfRows = 0;
			List<List<Object>> table = new ArrayList<>();
			while (resultSet.next())
			{
				numberOfRows++;
				List<Object> row = new ArrayList<>();
				// 1-based indexing
				for (int column = 1; column <= numberOfColumnsAssumingNonEmptyResult; column++)
					row.add(resultSet.getObject(column));
				table.add(row);
			}
			this.table = table;
		}
		catch (SQLException e)
		{
			throw new RuntimeException("Problem constructing SelectResult", e);
		}
		int actualNumberOfColumns = numberOfRows > 0 ? numberOfColumnsAssumingNonEmptyResult : 0;
		this.numberOfRows = numberOfRows;
		this.numberOfColumns = actualNumberOfColumns;
		this.isTable = numberOfRows > 1 && actualNumberOfColumns > 1;
		this.isRow = numberOfRows == 1 && actualNumberOfColumns > 1;
		this.isColumn = numberOfRows > 1 && actualNumberOfColumns == 1;
		this.isValue = numberOfRows == 1 && actualNumberOfColumns == 1;
		this.isEmpty = numberOfRows == 0 && actualNumberOfColumns == 0;
	}

	/**
	 * Returns {@code true} if the result is a table, i.e. it has more than one row and more than one column
	 */
	public boolean isTable()
	{
		return isTable;
	}

	/**
	 * Returns {@code true} if the result is a row, i.e. it has exactly one row and more than one column
	 */
	public boolean isRow()
	{
		return isRow;
	}

	/**
	 * Returns {@code true} if the result is a column, i.e. it has exactly one column and more than one row
	 */
	public boolean isColumn()
	{
		return isColumn;
	}

	/**
	 * Returns {@code true} if the result is a single value, i.e. it has exactly one row and exactly one column
	 */
	public boolean isValue()
	{
		return isValue;
	}

	/**
	 * Returns {@code true} if the result is empty
	 */
	public boolean isEmpty()
	{
		return isEmpty;
	}

	/**
	 * Returns the result as a {@code List<List<Object>>}, throwing an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * The outer list of the returned value represents the rows of the result, and the inner lists represent the columns.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asTableOpt()} instead.
	 * 
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @see #asTableOpt()
	 */
	public List<List<Object>> asTable()
	{
		if (isEmpty)
			throw EmptyResultException.expectedTable();
		return asTableOpt();
	}

	/**
	 * Returns the result as a {@code List<List<Object>>}, or an empty {@code List} if the result is empty.
	 * <p>
	 * The outer list of the returned value represents the rows of the result, and the inner lists represent the columns.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asTable()} instead.
	 * 
	 * @see #asTable()
	 */
	public List<List<Object>> asTableOpt()
	{
		List<List<Object>> tableCopy = new ArrayList<List<Object>>();
		for (List<Object> row : table)
			tableCopy.add(new ArrayList<>(row));
		return tableCopy;
	}

	/**
	 * Returns the result as a {@code List<T>}, throwing an {@link EmptyResultException} if the result is empty. The {@code T} objects are
	 * created by applying the given {@code listToObjectMapper} to each {@code List<Object>} row of the result.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asMappedObjectsOpt(Function)} instead.
	 * 
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @see #asMappedObjectsOpt(Function)
	 */
	public <T> List<T> asMappedObjects(Function<List<Object>, T> listToObjectMapper)
	{
		notNull(listToObjectMapper, "listToObjectMapper");
		return asTable().stream().map(listToObjectMapper).collect(Collectors.toList());
	}

	/**
	 * Returns the result as a {@code List<T>}, or an empty {@code List} if the result is empty. The {@code T} objects are created by
	 * applying the given {@code listToObjectMapper} to each {@code List<Object>} row of the result.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asMappedObjects(Function)} instead.
	 * 
	 * @see #asMappedObjects(Function)
	 */
	public <T> List<T> asMappedObjectsOpt(Function<List<Object>, T> listToObjectMapper)
	{
		notNull(listToObjectMapper, "listToObjectMapper");
		return asTableOpt().stream().map(listToObjectMapper).collect(Collectors.toList());
	}

	/**
	 * Returns the result as a {@code List<Object>} representing the single row of the result, throwing a {@link ResultShapeException} if
	 * the result has more than one row, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asRowOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row, i.e., if {@link #isTable()} or {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @see #asRowOpt()
	 */
	public List<Object> asRow()
	{
		if (isEmpty)
			throw EmptyResultException.expectedRow();
		return asRowOpt().get();
	}

	/**
	 * Returns the result as an {@code Optional<List<Object>>} representing the optionally present single row of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one row.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asRow()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row, i.e., if {@link #isTable()} or {@link #isColumn()} return {@code true}
	 * @see #asRow()
	 */
	public Optional<List<Object>> asRowOpt()
	{
		if (isEmpty)
			return Optional.empty();
		if (numberOfRows > 1)
			throw ResultShapeException.expectedSingleRow(numberOfRows);
		List<Object> rowCopy = new ArrayList<>(table.get(0));
		return Optional.of(rowCopy);
	}

	/**
	 * Returns the result as a {@code T} representing the single row of the result, throwing a {@link ResultShapeException} if the result
	 * has more than one row, and an {@link EmptyResultException} if the result is empty. The {@code T} object is created by applying the
	 * given {@code listToObjectMapper} to the {@code List<Object>} row of the result.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asMappedObjectOpt(Function)} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row, i.e., if {@link #isTable()} or {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @see #asMappedObjectOpt(Function)
	 */
	public <T> T asMappedObject(Function<List<Object>, T> listToObjectMapper)
	{
		notNull(listToObjectMapper, "listToObjectMapper");
		return listToObjectMapper.apply(asRow());
	}

	/**
	 * Returns the result as an {@code Optional<T>} representing the optionally present single row of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one row. The {@code T} object is created by applying the given
	 * {@code listToObjectMapper} to the {@code List<Object>} row of the result.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asMappedObject(Function)} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row, i.e., if {@link #isTable()} or {@link #isColumn()} return {@code true}
	 * @see #asMappedObject(Function)
	 */
	public <T> Optional<T> asMappedObjectOpt(Function<List<Object>, T> listToObjectMapper)
	{
		notNull(listToObjectMapper, "listToObjectMapper");
		Optional<List<Object>> optionalList = asRowOpt();
		if (optionalList.isPresent())
			return Optional.of(listToObjectMapper.apply(optionalList.get()));
		else
			return Optional.empty();
	}

	/**
	 * Returns the result as a {@code List<Object>} representing the single column of the result, throwing a {@link ResultShapeException} if
	 * the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @see #asColumnOpt()
	 */
	public List<Object> asColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<Object>} representing the single column of the result, or an empty {@code List} if the result is
	 * empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @see #asColumn()
	 */
	public List<Object> asColumnOpt()
	{
		assertColumn();
		List<Object> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(row.get(0));
		return column;
	}

	/**
	 * Returns the result as an {@code Object} representing the single value of the result, throwing a {@link ResultShapeException} if the
	 * result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asValueOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @see #asValueOpt()
	 */
	public Object asValue()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		return asValueOpt().get();
	}

	/**
	 * Returns the result as an {@code Optional<Object>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asValue()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @see #asValue()
	 */
	public Optional<Object> asValueOpt()
	{
		if (isEmpty)
			return Optional.empty();
		if (numberOfRows > 1 || numberOfColumns > 1)
			throw ResultShapeException.expectedSingleValue(numberOfRows, numberOfColumns);
		return Optional.of(table.get(0).get(0));
	}

	private void assertColumn()
	{
		if (numberOfColumns > 1)
			throw ResultShapeException.expectedSingleColumn(numberOfColumns);
	}

	/**
	 * Returns the result as a {@code List<Integer>} representing the single column of the result, throwing a {@link ResultShapeException}
	 * if the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asIntegerColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Integer}
	 * @see #asIntegerColumnOpt()
	 */
	public List<Integer> asIntegerColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asIntegerColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<Integer>} representing the single column of the result, or an empty {@code List} if the result is
	 * empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asIntegerColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Integer}
	 * @see #asIntegerColumn()
	 */
	public List<Integer> asIntegerColumnOpt()
	{
		assertColumn();
		List<Integer> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asInteger(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<Long>} representing the single column of the result, throwing a {@link ResultShapeException} if
	 * the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asLongColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Long}
	 * @see #asLongColumnOpt()
	 */
	public List<Long> asLongColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asLongColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<Long>} representing the single column of the result, or an empty {@code List} if the result is
	 * empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asLongColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Long}
	 * @see #asLongColumn()
	 */
	public List<Long> asLongColumnOpt()
	{
		assertColumn();
		List<Long> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asLong(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<BigInteger>} representing the single column of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asBigIntegerColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code BigInteger}
	 * @see #asBigIntegerColumnOpt()
	 */
	public List<BigInteger> asBigIntegerColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asBigIntegerColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<BigInteger>} representing the single column of the result, or an empty {@code List} if the result
	 * is empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asBigIntegerColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code BigInteger}
	 * @see #asBigIntegerColumn()
	 */
	public List<BigInteger> asBigIntegerColumnOpt()
	{
		assertColumn();
		List<BigInteger> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asBigInteger(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<Double>} representing the single column of the result, throwing a {@link ResultShapeException} if
	 * the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asDoubleColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Double}
	 * @see #asDoubleColumnOpt()
	 */
	public List<Double> asDoubleColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asDoubleColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<Double>} representing the single column of the result, or an empty {@code List} if the result is
	 * empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asDoubleColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Double}
	 * @see #asDoubleColumn()
	 */
	public List<Double> asDoubleColumnOpt()
	{
		assertColumn();
		List<Double> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asDouble(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<BigDecimal>} representing the single column of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asBigDecimalColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code BigDecimal}
	 * @see #asBigDecimalColumnOpt()
	 */
	public List<BigDecimal> asBigDecimalColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asBigDecimalColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<BigDecimal>} representing the single column of the result, or an empty {@code List} if the result
	 * is empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asBigDecimalColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code BigDecimal}
	 * @see #asBigDecimalColumn()
	 */
	public List<BigDecimal> asBigDecimalColumnOpt()
	{
		assertColumn();
		List<BigDecimal> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asBigDecimal(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<String>} representing the single column of the result, throwing a {@link ResultShapeException} if
	 * the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asStringColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code String}
	 * @see #asStringColumnOpt()
	 */
	public List<String> asStringColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asStringColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<String>} representing the single column of the result, or an empty {@code List} if the result is
	 * empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asStringColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code String}
	 * @see #asStringColumn()
	 */
	public List<String> asStringColumnOpt()
	{
		assertColumn();
		List<String> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asString(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<Boolean>} representing the single column of the result, throwing a {@link ResultShapeException}
	 * if the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asBooleanColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Boolean}
	 * @see #asBooleanColumnOpt()
	 */
	public List<Boolean> asBooleanColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asBooleanColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<Boolean>} representing the single column of the result, or an empty {@code List} if the result is
	 * empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asBooleanColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Boolean}
	 * @see #asBooleanColumn()
	 */
	public List<Boolean> asBooleanColumnOpt()
	{
		assertColumn();
		List<Boolean> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asBoolean(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<LocalDate>} representing the single column of the result, throwing a {@link ResultShapeException}
	 * if the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asLocalDateColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code LocalDate}
	 * @see #asLocalDateColumnOpt()
	 */
	public List<LocalDate> asLocalDateColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asLocalDateColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<LocalDate>} representing the single column of the result, or an empty {@code List} if the result
	 * is empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asLocalDateColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code LocalDate}
	 * @see #asLocalDateColumn()
	 */
	public List<LocalDate> asLocalDateColumnOpt()
	{
		assertColumn();
		List<LocalDate> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asLocalDate(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<LocalDateTime>} representing the single column of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asLocalDateTimeColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code LocalDateTime}
	 * @see #asLocalDateTimeColumnOpt()
	 */
	public List<LocalDateTime> asLocalDateTimeColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asLocalDateTimeColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<LocalDateTime>} representing the single column of the result, or an empty {@code List} if the
	 * result is empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asLocalDateTimeColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code LocalDateTime}
	 * @see #asLocalDateTimeColumn()
	 */
	public List<LocalDateTime> asLocalDateTimeColumnOpt()
	{
		assertColumn();
		List<LocalDateTime> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asLocalDateTime(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<Instant>} representing the single column of the result, throwing a {@link ResultShapeException}
	 * if the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asInstantColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Instant}
	 * @see #asInstantColumnOpt()
	 */
	public List<Instant> asInstantColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asInstantColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<Instant>} representing the single column of the result, or an empty {@code List} if the result is
	 * empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asInstantColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code Instant}
	 * @see #asInstantColumn()
	 */
	public List<Instant> asInstantColumnOpt()
	{
		assertColumn();
		List<Instant> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asInstant(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as a {@code List<byte[]>} representing the single column of the result, throwing a {@link ResultShapeException} if
	 * the result has more than one column, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asByteArrayColumnOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code byte[]}
	 * @see #asByteArrayColumnOpt()
	 */
	public List<byte[]> asByteArrayColumn()
	{
		if (isEmpty)
			throw EmptyResultException.expectedColumn();
		return asByteArrayColumnOpt();
	}

	/**
	 * Returns the result as a {@code List<byte[]>} representing the single column of the result, or an empty {@code List} if the result is
	 * empty, throwing a {@link ResultShapeException} if the result has more than one column.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asByteArrayColumn()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one column, i.e., if {@link #isTable()} or {@link #isRow()} return {@code true}
	 * @throws ConversionException
	 *             if any objects in the column cannot be represented as {@code byte[]}
	 * @see #asByteArrayColumn()
	 */
	public List<byte[]> asByteArrayColumnOpt()
	{
		assertColumn();
		List<byte[]> column = new ArrayList<>();
		for (List<Object> row : table)
			column.add(converter.asByteArray(row.get(0)));
		return column;
	}

	/**
	 * Returns the result as an {@code Integer} representing the single value of the result, throwing a {@link ResultShapeException} if the
	 * result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asIntegerOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as an {@code Integer}
	 * @see #asIntegerOpt()
	 */
	public Integer asInteger()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asInteger(value);
	}

	/**
	 * Returns the result as an {@code Optional<Integer>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asInteger()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as an {@code Integer}
	 * @see #asInteger()
	 */
	public Optional<Integer> asIntegerOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asInteger(value));
	}

	/**
	 * Returns the result as a {@code Long} representing the single value of the result, throwing a {@link ResultShapeException} if the
	 * result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asLongOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code Long}
	 * @see #asLongOpt()
	 */
	public Long asLong()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asLong(value);
	}

	/**
	 * Returns the result as an {@code Optional<Long>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asLong()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code Long}
	 * @see #asLong()
	 */
	public Optional<Long> asLongOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asLong(value));
	}

	/**
	 * Returns the result as a {@code BigInteger} representing the single value of the result, throwing a {@link ResultShapeException} if
	 * the result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asBigIntegerOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code BigInteger}
	 * @see #asBigIntegerOpt()
	 */
	public BigInteger asBigInteger()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asBigInteger(value);
	}

	/**
	 * Returns the result as an {@code Optional<BigInteger>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asBigInteger()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code BigInteger}
	 * @see #asBigInteger()
	 */
	public Optional<BigInteger> asBigIntegerOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asBigInteger(value));
	}

	/**
	 * Returns the result as a {@code Double} representing the single value of the result, throwing a {@link ResultShapeException} if the
	 * result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asDoubleOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code Double}
	 * @see #asDoubleOpt()
	 */
	public Double asDouble()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asDouble(value);
	}

	/**
	 * Returns the result as an {@code Optional<Double>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asDouble()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code Double}
	 * @see #asDouble()
	 */
	public Optional<Double> asDoubleOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asDouble(value));
	}

	/**
	 * Returns the result as a {@code BigDecimal} representing the single value of the result, throwing a {@link ResultShapeException} if
	 * the result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asBigDecimalOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code BigDecimal}
	 * @see #asBigDecimalOpt()
	 */
	public BigDecimal asBigDecimal()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asBigDecimal(value);
	}

	/**
	 * Returns the result as an {@code Optional<BigDecimal>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asBigDecimal()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code BigDecimal}
	 * @see #asBigDecimal()
	 */
	public Optional<BigDecimal> asBigDecimalOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asBigDecimal(value));
	}

	/**
	 * Returns the result as a {@code String} representing the single value of the result, throwing a {@link ResultShapeException} if the
	 * result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asStringOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code String}
	 * @see #asStringOpt()
	 */
	public String asString()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asString(value);
	}

	/**
	 * Returns the result as an {@code Optional<String>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asString()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code String}
	 * @see #asString()
	 */
	public Optional<String> asStringOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asString(value));
	}

	/**
	 * Returns the result as a {@code Boolean} representing the single value of the result, throwing a {@link ResultShapeException} if the
	 * result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asBooleanOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code Boolean}
	 * @see #asBooleanOpt()
	 */
	public Boolean asBoolean()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asBoolean(value);
	}

	/**
	 * Returns the result as an {@code Optional<Boolean>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asBoolean()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code Boolean}
	 * @see #asBoolean()
	 */
	public Optional<Boolean> asBooleanOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asBoolean(value));
	}

	/**
	 * Returns the result as a {@code LocalDate} representing the single value of the result, throwing a {@link ResultShapeException} if the
	 * result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asLocalDateOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code LocalDate}
	 * @see #asLocalDateOpt()
	 */
	public LocalDate asLocalDate()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asLocalDate(value);
	}

	/**
	 * Returns the result as an {@code Optional<LocalDate>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asLocalDate()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code LocalDate}
	 * @see #asLocalDate()
	 */
	public Optional<LocalDate> asLocalDateOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asLocalDate(value));
	}

	/**
	 * Returns the result as a {@code LocalDateTime} representing the single value of the result, throwing a {@link ResultShapeException} if
	 * the result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asLocalDateTimeOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code LocalDateTime}
	 * @see #asLocalDateTimeOpt()
	 */
	public LocalDateTime asLocalDateTime()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asLocalDateTime(value);
	}

	/**
	 * Returns the result as an {@code Optional<LocalDateTime>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asLocalDateTime()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code LocalDateTime}
	 * @see #asLocalDateTime()
	 */
	public Optional<LocalDateTime> asLocalDateTimeOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asLocalDateTime(value));
	}

	/**
	 * Returns the result as an {@code Instant} representing the single value of the result, throwing a {@link ResultShapeException} if the
	 * result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asInstantOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code Instant}
	 * @see #asInstantOpt()
	 */
	public Instant asInstant()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asInstant(value);
	}

	/**
	 * Returns the result as an {@code Optional<Instant>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asInstant()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code Instant}
	 * @see #asInstant()
	 */
	public Optional<Instant> asInstantOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asInstant(value));
	}

	/**
	 * Returns the result as a {@code byte[]} representing the single value of the result, throwing a {@link ResultShapeException} if the
	 * result has more than one value, and an {@link EmptyResultException} if the result is empty.
	 * <p>
	 * If an empty result should not cause an exception to be thrown, use {@link #asByteArrayOpt()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws EmptyResultException
	 *             if the result is empty, i.e., if {@link #isEmpty()} returns {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code byte[]}
	 * @see #asByteArrayOpt()
	 */
	public byte[] asByteArray()
	{
		if (isEmpty)
			throw EmptyResultException.expectedValue();
		Object value = asValue();
		return converter.asByteArray(value);
	}

	/**
	 * Returns the result as an {@code Optional<byte[]>} representing the optionally present single value of the result, throwing a
	 * {@link ResultShapeException} if the result has more than one value.
	 * <p>
	 * If an empty result is an invalid case and should cause an exception to be thrown, use {@link #asByteArray()} instead.
	 * 
	 * @throws ResultShapeException
	 *             if the result has more than one row or more than one column, i.e. {@link #isTable()}, {@link #isRow()}, or
	 *             {@link #isColumn()} return {@code true}
	 * @throws ConversionException
	 *             if the value cannot be represented as a {@code byte[]}
	 * @see #asByteArray()
	 */
	public Optional<byte[]> asByteArrayOpt()
	{
		if (isEmpty)
			return Optional.empty();
		Object value = asValue();
		return Optional.of(converter.asByteArray(value));
	}

	/**
	 * Returns true if the given object is a {@code SelectResult} and has equal elements in an equal shape as this {@code SelectResult}.
	 */
	@Override
	public boolean equals(Object object)
	{
		if (object == null)
			return false;
		if (object == this)
			return true;
		if (!object.getClass().equals(SelectResult.class))
			return false;
		SelectResult other = (SelectResult) object;
		if (this.numberOfRows != other.numberOfRows || this.numberOfColumns != other.numberOfColumns)
			return false;
		return this.table.equals(other.table);
	}

	@Override
	public int hashCode()
	{
		return table.hashCode();
	}

	@Override
	public String toString()
	{
		if (isEmpty)
			return "Empty";
		if (isValue)
			return "Value: " + asValue();
		if (isRow)
			return "Row: " + asRow();
		if (isColumn)
			return "Column: " + asColumn();
		StringBuilder sb = new StringBuilder("Table:");
		for (List<Object> row : table)
			sb.append("\n").append(row);
		return sb.toString();
	}
}