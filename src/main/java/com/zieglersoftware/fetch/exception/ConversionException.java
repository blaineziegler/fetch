package com.zieglersoftware.fetch.exception;

import com.zieglersoftware.fetch.converter.Converter;

/**
 * Unchecked exception to be thrown when a {@link Converter} method is unable to perform a conversion due to data incompatability
 */
public final class ConversionException extends RuntimeException
{
	private static final long serialVersionUID = -7322707093231278028L;

	/**
	 * Constructs a new {@code ConversionException}
	 * 
	 * @param sourceObject
	 *            the source object that was attempted to be converted
	 * @param destinationClass
	 *            the class that the source object was attempted be converted to
	 */
	public ConversionException(Object sourceObject, Class<?> destinationClass)
	{
		super("Cannot convert " + sourceObject + " from " + sourceObject.getClass() + " to " + destinationClass);
	}
}