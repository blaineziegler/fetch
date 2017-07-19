package com.zieglersoftware.fetch.exception;

import com.zieglersoftware.fetch.core.SelectResult;

/**
 * Unchecked exception to be thrown when the actual "shape" of a {@link SelectResult}, i.e., table, row, column, or value, is incompatible
 * with the expected shape.
 * <p>
 * For example, if {@link SelectResult#asRow()} is called when the result actually contains multiple rows, a {@code ResultShapeException}
 * will be thrown.
 */
public final class ResultShapeException extends RuntimeException
{
	private static final long serialVersionUID = -326317805574475266L;

	/**
	 * Private constructor. Use static factory methods instead.
	 */
	private ResultShapeException(String message)
	{
		super(message);
	}

	/**
	 * Returns a {@code ResultShapeException} indicating that at most one row was expected, but multiple rows were found
	 */
	public static ResultShapeException expectedSingleRow(int actualNumberOfRows)
	{
		return new ResultShapeException("Expected at most one row. Actually " + actualNumberOfRows + " rows");
	}

	/**
	 * Returns a {@code ResultShapeException} indicating that at most one column was expected, but multiple columns were found
	 */
	public static ResultShapeException expectedSingleColumn(int actualNumberOfColumns)
	{
		return new ResultShapeException("Expected at most one column. Actually " + actualNumberOfColumns + " columns");
	}

	/**
	 * Returns a {@code ResultShapeException} indicating that at most one value was expected, but multiple rows and/or columns were found
	 */
	public static ResultShapeException expectedSingleValue(int actualNumberOfRows, int actualNumberOfColumns)
	{
		return new ResultShapeException("Expected at most one value. Actually " + actualNumberOfRows + " row(s) and " + actualNumberOfColumns + " column(s)");
	}
}