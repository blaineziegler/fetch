package com.zieglersoftware.fetch.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.zieglersoftware.fetch.exception.ConversionException;

/**
 * Provides methods to express instances of {@code Object} as instances of common classes such as {@code Integer}, {@code String}, etc.
 * <p>
 * Methods may simply return a cast of the given object to the desired type, or return a logically equivalent conversion of that object to
 * an instance of the desired type.
 * <p>
 * All methods are {@code null}-safe. Passing in a {@code null} argument will result in a {@code null} returned value.
 * <p>
 * All methods throw a {@link ConversionException} if the conversion cannot be made due to data incompatability.
 * <p>
 * {@code Converter} implementations must be immutable.
 * 
 * @see ConverterDefaultImpl
 */
public interface Converter
{
	/**
	 * Returns a representation of the given object as an {@code Integer}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code Integer}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Integer}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public Integer asInteger(Object object);

	/**
	 * Returns a representation of the given object as a {@code Long}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code Long}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Long}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public Long asLong(Object object);

	/**
	 * Returns a representation of the given object as a {@code BigInteger}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code BigInteger}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code BigInteger}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public BigInteger asBigInteger(Object object);

	/**
	 * Returns a representation of the given object as a {@code Double}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code Double}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Double}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public Double asDouble(Object object);

	/**
	 * Returns a representation of the given object as a {@code BigDecimal}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code BigDecimal}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code BigDecimal}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public BigDecimal asBigDecimal(Object object);

	/**
	 * Returns a representation of the given object as a {@code String}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code String}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code String}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public String asString(Object object);

	/**
	 * Returns a representation of the given object as a {@code Boolean}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code Boolean}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Boolean}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public Boolean asBoolean(Object object);

	/**
	 * Returns a representation of the given object as a {@link LocalDate}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code LocalDate}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code LocalDate}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public LocalDate asLocalDate(Object object);

	/**
	 * Returns a representation of the given object as a {@link LocalDateTime}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code LocalDateTime}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code LocalDateTime}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public LocalDateTime asLocalDateTime(Object object);

	/**
	 * Returns a representation of the given object as an {@link Instant}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code Instant}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Instant}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public Instant asInstant(Object object);

	/**
	 * Returns a representation of the given object as a {@code byte[]}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Further specification is left to the implementing class. The implementing class may throw a {@link ConversionException} if it deems
	 * the object incompatible with {@code byte[]}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code byte[]}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	public byte[] asByteArray(Object object);
}