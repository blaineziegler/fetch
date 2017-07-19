package com.zieglersoftware.fetch.exception;

import com.zieglersoftware.fetch.core.SelectResult;

/**
 * Unchecked exception to be thrown when a non-empty {@link SelectResult} was expected, but it is actually empty
 */
public class EmptyResultException extends RuntimeException
{
	private static final long serialVersionUID = 2447568197771052442L;

	/**
	 * Private constructor. Use static factory methods instead.
	 */
	private EmptyResultException(String message)
	{
		super(message);
	}

	public static EmptyResultException expectedTable()
	{
		return new EmptyResultException("Empty result when expecting a table");
	}

	public static EmptyResultException expectedRow()
	{
		return new EmptyResultException("Empty result when expecting a single row");
	}

	public static EmptyResultException expectedColumn()
	{
		return new EmptyResultException("Empty result when expecting a single column");
	}

	public static EmptyResultException expectedValue()
	{
		return new EmptyResultException("Empty result when expecting a single value");
	}
}
