package com.zieglersoftware.fetch.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import com.zieglersoftware.fetch.exception.ConversionException;

/**
 * An implementation of {@link Converter}.
 * <p>
 * {@code ConverterDefaultImpl} is immutable.
 */
public final class ConverterDefaultImpl implements Converter
{
	/**
	 * Returns a representation of the given object as an {@code Integer}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}. If the object is non-{@code null} and is not an instance of {@link Number},
	 * or if it is a number outside the range of {@code Integer}, or if it is infinite or {@code NaN}, a {@link ConversionException} is
	 * thrown. If the object is a number with a decimal component, a truncated integral value is returned.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Integer}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public Integer asInteger(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof Integer)
			return (Integer) object;
		if (object instanceof Long)
			return longToInt((Long) object, Integer.class);
		if (object instanceof BigInteger)
			return bigIntegerToInt((BigInteger) object, Integer.class);
		if (object instanceof BigDecimal)
			return bigIntegerToInt(((BigDecimal) object).toBigInteger(), Integer.class);
		if (object instanceof Double)
			return doubleToInt((Double) object, Integer.class);
		if (object instanceof Float)
			return floatToInt((Float) object, Integer.class);
		throw new ConversionException(object, Integer.class);
	}

	private static int longToInt(Long longVal, Class<?> targetClass)
	{
		if (longVal <= Integer.MIN_VALUE || longVal >= Integer.MAX_VALUE)
			throw new ConversionException(longVal, targetClass);
		else
			return longVal.intValue();
	}

	private static final BigInteger MIN_INT_BIG_INTEGER = BigInteger.valueOf(Integer.MIN_VALUE);
	private static final BigInteger MAX_INT_BIG_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);

	private static int bigIntegerToInt(BigInteger bigIntegerVal, Class<?> targetClass)
	{
		if (bigIntegerVal.compareTo(MIN_INT_BIG_INTEGER) >= 0 && bigIntegerVal.compareTo(MAX_INT_BIG_INTEGER) <= 0)
			return bigIntegerVal.intValue();
		else
			throw new ConversionException(bigIntegerVal, targetClass);
	}

	private static int doubleToInt(Double doubleVal, Class<?> targetClass)
	{
		if (doubleVal.isInfinite() || doubleVal.isNaN() || doubleVal <= Integer.MIN_VALUE || doubleVal >= Integer.MAX_VALUE)
			throw new ConversionException(doubleVal, targetClass);
		return doubleVal.intValue();
	}

	private static int floatToInt(Float floatVal, Class<?> targetClass)
	{
		if (floatVal.isInfinite() || floatVal.isNaN() || floatVal <= Integer.MIN_VALUE || floatVal >= Integer.MAX_VALUE)
			throw new ConversionException(floatVal, targetClass);
		return floatVal.intValue();
	}

	/**
	 * Returns a representation of the given object as a {@code Long}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}. If the object is non-{@code null} and is not an instance of {@link Number},
	 * or if it is a number outside the range of {@code Long}, or if it is infinite or {@code NaN}, a {@link ConversionException} is thrown.
	 * If the object is a number with a decimal component, a truncated integral value is returned.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Long}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public Long asLong(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof Long)
			return (Long) object;
		if (object instanceof Integer)
			return ((Integer) object).longValue();
		if (object instanceof BigInteger)
			return bigIntegerToLong((BigInteger) object, Long.class);
		if (object instanceof BigDecimal)
			return bigIntegerToLong(((BigDecimal) object).toBigInteger(), Long.class);
		if (object instanceof Double)
			return doubleToLong((Double) object, Long.class);
		if (object instanceof Float)
			return floatToLong((Float) object, Long.class);
		throw new ConversionException(object, Long.class);
	}

	private static final BigInteger MIN_LONG_BIG_INTEGER = BigInteger.valueOf(Long.MIN_VALUE);
	private static final BigInteger MAX_LONG_BIG_INTEGER = BigInteger.valueOf(Long.MAX_VALUE);

	private static long bigIntegerToLong(BigInteger bigIntegerVal, Class<?> targetClass)
	{
		if (bigIntegerVal.compareTo(MIN_LONG_BIG_INTEGER) >= 0 && bigIntegerVal.compareTo(MAX_LONG_BIG_INTEGER) <= 0)
			return bigIntegerVal.longValue();
		else
			throw new ConversionException(bigIntegerVal, targetClass);
	}

	private static long doubleToLong(Double doubleVal, Class<?> targetClass)
	{
		if (doubleVal.isInfinite() || doubleVal.isNaN() || doubleVal <= Long.MIN_VALUE || doubleVal >= Long.MAX_VALUE)
			throw new ConversionException(doubleVal, targetClass);
		return doubleVal.longValue();
	}

	private static long floatToLong(Float floatVal, Class<?> targetClass)
	{
		if (floatVal.isInfinite() || floatVal.isNaN() || floatVal <= Long.MIN_VALUE || floatVal >= Long.MAX_VALUE)
			throw new ConversionException(floatVal, targetClass);
		return floatVal.longValue();
	}

	/**
	 * Returns a representation of the given object as a {@code BigInteger}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}. If the object is non-{@code null} and is not an instance of {@link Number},
	 * or if it is infinite or {@code NaN}, a {@link ConversionException} is thrown. If the object is a number with a decimal component, a
	 * truncated integral value is returned.
	 * 
	 * @param object
	 *            The object to be expressed as {@code BigInteger}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public BigInteger asBigInteger(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof BigInteger)
			return (BigInteger) object;
		if (object instanceof Integer)
			return BigInteger.valueOf((Integer) object);
		if (object instanceof Long)
			return BigInteger.valueOf((Long) object);
		if (object instanceof BigDecimal)
			return ((BigDecimal) object).toBigInteger();
		if (object instanceof Double)
			return doubleToBigDecimal((Double) object, BigInteger.class).toBigInteger();
		if (object instanceof Float)
			return floatToBigDecimal((Float) object, BigInteger.class).toBigInteger();
		throw new ConversionException(object, BigInteger.class);
	}

	private static BigDecimal doubleToBigDecimal(Double doubleVal, Class<?> targetClass)
	{
		if (doubleVal.isInfinite() || doubleVal.isNaN())
			throw new ConversionException(doubleVal, targetClass);
		return BigDecimal.valueOf(doubleVal);
	}

	private static BigDecimal floatToBigDecimal(Float floatVal, Class<?> targetClass)
	{
		if (floatVal.isInfinite() || floatVal.isNaN())
			throw new ConversionException(floatVal, targetClass);
		return BigDecimal.valueOf(floatVal);
	}

	/**
	 * Returns a representation of the given object as a {@code Double}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}. If the object is non-{@code null} and is not an instance of {@link Number},
	 * or if it is a number outside the range of {@code Double}, or if it is infinite or {@code NaN}, a {@link ConversionException} is
	 * thrown.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Double}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public Double asDouble(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof Double)
			return doubleToDouble((Double) object, Double.class);
		if (object instanceof Integer)
			return ((Integer) object).doubleValue();
		if (object instanceof Long)
			return ((Long) object).doubleValue();
		if (object instanceof BigInteger)
			return bigIntegerToDouble((BigInteger) object, Double.class);
		if (object instanceof BigDecimal)
			return bigDecimalToDouble((BigDecimal) object, Double.class);
		if (object instanceof Float)
			return floatToDouble((Float) object, Double.class);
		throw new ConversionException(object, Double.class);
	}

	private static double doubleToDouble(Double doubleVal, Class<?> targetClass)
	{
		if (doubleVal.isInfinite() || doubleVal.isNaN())
			throw new ConversionException(doubleVal, targetClass);
		return doubleVal;
	}

	private static double floatToDouble(Float floatVal, Class<?> targetClass)
	{
		if (floatVal.isInfinite() || floatVal.isNaN())
			throw new ConversionException(floatVal, targetClass);
		return floatVal;
	}

	private static final BigInteger MIN_DOUBLE_BIG_INTEGER = BigDecimal.valueOf(Double.MIN_VALUE).toBigInteger();
	private static final BigInteger MAX_DOUBLE_BIG_INTEGER = BigDecimal.valueOf(Double.MAX_VALUE).toBigInteger();

	private static double bigIntegerToDouble(BigInteger bigIntegerVal, Class<?> targetClass)
	{
		if (bigIntegerVal.compareTo(MIN_DOUBLE_BIG_INTEGER) >= 0 && bigIntegerVal.compareTo(MAX_DOUBLE_BIG_INTEGER) <= 0)
			return bigIntegerVal.doubleValue();
		else
			throw new ConversionException(bigIntegerVal, targetClass);
	}

	private static final BigDecimal MIN_DOUBLE_BIG_DECIMAL = BigDecimal.valueOf(Double.MIN_VALUE);
	private static final BigDecimal MAX_DOUBLE_BIG_DECIMAL = BigDecimal.valueOf(Double.MAX_VALUE);

	private static double bigDecimalToDouble(BigDecimal bigDecimalVal, Class<?> targetClass)
	{
		if (bigDecimalVal.compareTo(MIN_DOUBLE_BIG_DECIMAL) >= 0 && bigDecimalVal.compareTo(MAX_DOUBLE_BIG_DECIMAL) <= 0)
			return bigDecimalVal.doubleValue();
		else
			throw new ConversionException(bigDecimalVal, targetClass);
	}

	/**
	 * Returns a representation of the given object as a {@code BigDecimal}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}. If the object is non-{@code null} and is not an instance of {@link Number},
	 * or if it is infinite or {@code NaN}, a {@link ConversionException} is thrown.
	 * 
	 * @param object
	 *            The object to be expressed as {@code BigDecimal}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public BigDecimal asBigDecimal(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof BigDecimal)
			return (BigDecimal) object;
		if (object instanceof Integer)
			return BigDecimal.valueOf((Integer) object);
		if (object instanceof Long)
			return BigDecimal.valueOf((Long) object);
		if (object instanceof BigInteger)
			return new BigDecimal((BigInteger) object);
		if (object instanceof Double)
			return doubleToBigDecimal((Double) object, BigDecimal.class);
		if (object instanceof Float)
			return floatToBigDecimal((Float) object, BigDecimal.class);
		throw new ConversionException(object, BigDecimal.class);
	}

	/**
	 * Returns a representation of the given object as a {@code String}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code String}
	 */
	@Override
	public String asString(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof byte[])
			return Arrays.toString((byte[]) object);
		if (object instanceof Byte[])
			return Arrays.toString((Byte[]) object);
		return object.toString();
	}

	/**
	 * Returns a representation of the given object as a {@code Boolean}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Returns {@code true} if the object is boolean {@code true}, or a {@code Number} equal to {@code 1}, or a {@code String} that is
	 * case-insensitive equal to {@code "true"}, {@code "t"}, {@code "yes"}, {@code "y"}, or {@code "1"}.
	 * <p>
	 * Returns {@code false} if the object is boolean {@code false}, or a {@code Number} equal to {@code 0}, or a {@code String} that is
	 * case-insensitive equal to {@code "false"}, {@code "f"}, {@code "no"}, {@code "n"}, or {@code "0"}.
	 * <p>
	 * Otherwise, throws a {@link ConversionException}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Boolean}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public Boolean asBoolean(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof Boolean)
			return (Boolean) object;
		if (object instanceof String)
			return stringToBoolean((String) object, Boolean.class);
		if (object instanceof Number)
			return numberToBoolean((Number) object, Boolean.class);
		throw new ConversionException(object, Boolean.class);
	}

	private static final String TRUE = "true";
	private static final String T = "t";
	private static final String YES = "yes";
	private static final String Y = "y";
	private static final String ONE = "1";
	private static final String FALSE = "false";
	private static final String F = "f";
	private static final String NO = "no";
	private static final String N = "n";
	private static final String ZERO = "0";

	private static boolean stringToBoolean(String string, Class<?> targetClass)
	{
		if (string.equalsIgnoreCase(TRUE) || string.equalsIgnoreCase(T) ||
				string.equalsIgnoreCase(YES) || string.equalsIgnoreCase(Y) ||
				string.equals(ONE))
			return true;
		if (string.equalsIgnoreCase(FALSE) || string.equalsIgnoreCase(F) ||
				string.equalsIgnoreCase(NO) || string.equalsIgnoreCase(N) ||
				string.equals(ZERO))
			return false;
		throw new ConversionException(string, targetClass);
	}

	private static Boolean numberToBoolean(Number number, Class<?> targetClass)
	{
		int intValue;
		if (number instanceof Integer)
			intValue = (Integer) number;
		else if (number instanceof Long)
			intValue = longToInt((Long) number, targetClass);
		else if (number instanceof BigInteger)
			intValue = bigIntegerToInt((BigInteger) number, targetClass);
		else if (number instanceof BigDecimal)
			intValue = bigIntegerToInt(((BigDecimal) number).toBigInteger(), targetClass);
		else if (number instanceof Double)
			intValue = doubleToInt((Double) number, targetClass);
		else if (number instanceof Float)
			intValue = floatToInt((Float) number, targetClass);
		else
			throw new ConversionException(number, targetClass);
		if (intValue == 1)
			return true;
		if (intValue == 0)
			return false;
		throw new ConversionException(number, targetClass);
	}

	/**
	 * Returns a representation of the given object as a {@code LocalDate}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Returns a {@code LocalDate} if the given object is of one of the following types:
	 * <ul>
	 * <li>{@link LocalDate}</li>
	 * <li>{@link LocalDateTime}</li>
	 * <li>{@link Instant}</li>
	 * <li>{@link ZonedDateTime}</li>
	 * <li>{@link java.util.Date} (including its subclasses {@link java.sql.Date} and {@link java.util.Timestamp})</li>
	 * <li>{@link String} that can be parsed as {@link LocalDate}, {@link LocalDateTime}, {@link Instant}, or {@link ZonedDateTime} by the
	 * {@code ISO-8601} standard</li>
	 * </ul>
	 * <p>
	 * Otherwise, throws a {@link ConversionException}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code LocalDate}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public LocalDate asLocalDate(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof LocalDate)
			return (LocalDate) object;
		if (object instanceof LocalDateTime)
			return localDateTimeToLocalDate((LocalDateTime) object);
		if (object instanceof Instant)
			return instantToLocalDate((Instant) object);
		if (object instanceof ZonedDateTime)
			return zonedDateTimeToLocalDate((ZonedDateTime) object);
		if (object instanceof java.sql.Timestamp)
			return sqlTimestampToLocalDate((java.sql.Timestamp) object);
		if (object instanceof java.sql.Date)
			return sqlDateToLocalDate((java.sql.Date) object);
		if (object instanceof java.util.Date)
			return utilDateToLocalDate((java.util.Date) object);
		if (object instanceof String)
		{
			String string = (String) object;
			try
			{
				ZonedDateTime zonedDateTime = ZonedDateTime.parse(string);
				return zonedDateTimeToLocalDate(zonedDateTime);
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
			try
			{
				Instant instant = Instant.parse(string);
				return instantToLocalDate(instant);
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
			try
			{
				LocalDateTime localDateTime = LocalDateTime.parse(string);
				return localDateTimeToLocalDate(localDateTime);
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
			try
			{
				LocalDate localDate = LocalDate.parse(string);
				return localDate;
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
		}
		throw new ConversionException(object, LocalDate.class);
	}

	/**
	 * Returns a representation of the given object as a {@code LocalDateTime}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Returns a {@code LocalDateTime} if the given object is of one of the following types:
	 * <ul>
	 * <li>{@link LocalDateTime}</li>
	 * <li>{@link LocalDate}</li>
	 * <li>{@link Instant}</li>
	 * <li>{@link ZonedDateTime}</li>
	 * <li>{@link java.util.Date} (including its subclasses {@link java.sql.Date} and {@link java.util.Timestamp})</li>
	 * <li>{@link String} that can be parsed as {@link LocalDateTime}, {@link LocalDate}, {@link Instant}, or {@link ZonedDateTime} by the
	 * {@code ISO-8601} standard</li>
	 * </ul>
	 * <p>
	 * Otherwise, throws a {@link ConversionException}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code LocalDateTime}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public LocalDateTime asLocalDateTime(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof LocalDateTime)
			return (LocalDateTime) object;
		if (object instanceof LocalDate)
			return localDateToLocalDateTime((LocalDate) object);
		if (object instanceof Instant)
			return instantToLocalDateTime((Instant) object);
		if (object instanceof ZonedDateTime)
			return zonedDateTimeToLocalDateTime((ZonedDateTime) object);
		if (object instanceof java.sql.Timestamp)
			return sqlTimestampToLocalDateTime((java.sql.Timestamp) object);
		if (object instanceof java.sql.Date)
			return sqlDateToLocalDateTime((java.sql.Date) object);
		if (object instanceof java.util.Date)
			return utilDateToLocalDateTime((java.util.Date) object);
		if (object instanceof String)
		{
			String string = (String) object;
			try
			{
				ZonedDateTime zonedDateTime = ZonedDateTime.parse(string);
				return zonedDateTimeToLocalDateTime(zonedDateTime);
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
			try
			{
				Instant instant = Instant.parse(string);
				return instantToLocalDateTime(instant);
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
			try
			{
				LocalDateTime localDateTime = LocalDateTime.parse(string);
				return localDateTime;
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
			try
			{
				LocalDate localDate = LocalDate.parse(string);
				return localDateToLocalDateTime(localDate);
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
		}
		throw new ConversionException(object, LocalDateTime.class);
	}

	/**
	 * Returns a representation of the given object as an {@code Instant}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}.
	 * <p>
	 * Returns an {@code Instant} if the given object is of one of the following types:
	 * <ul>
	 * <li>{@link Instant}</li>
	 * <li>{@link LocalDate}</li>
	 * <li>{@link LocalDateTime}</li>
	 * <li>{@link ZonedDateTime}</li>
	 * <li>{@link java.util.Date} (including its subclasses {@link java.sql.Date} and {@link java.util.Timestamp})</li>
	 * <li>{@link String} that can be parsed as {@link Instant}, {@link LocalDate}, {@link LocalDateTime}, or {@link ZonedDateTime} by the
	 * {@code ISO-8601} standard</li>
	 * </ul>
	 * <p>
	 * Otherwise, throws a {@link ConversionException}.
	 * 
	 * @param object
	 *            The object to be expressed as {@code Instant}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public Instant asInstant(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof Instant)
			return (Instant) object;
		if (object instanceof LocalDate)
			return localDateToInstant((LocalDate) object);
		if (object instanceof LocalDateTime)
			return localDateTimeToInstant((LocalDateTime) object);
		if (object instanceof ZonedDateTime)
			return zonedDateTimeToInstant((ZonedDateTime) object);
		if (object instanceof java.sql.Timestamp)
			return sqlTimestampToInstant((java.sql.Timestamp) object);
		if (object instanceof java.sql.Date)
			return sqlDateToInstant((java.sql.Date) object);
		if (object instanceof java.util.Date)
			return utilDateToInstant((java.util.Date) object);
		if (object instanceof String)
		{
			String string = (String) object;
			try
			{
				ZonedDateTime zonedDateTime = ZonedDateTime.parse(string);
				return zonedDateTimeToInstant(zonedDateTime);
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
			try
			{
				Instant instant = Instant.parse(string);
				return instant;
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
			try
			{
				LocalDateTime localDateTime = LocalDateTime.parse(string);
				return localDateTimeToInstant(localDateTime);
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
			try
			{
				LocalDate localDate = LocalDate.parse(string);
				return localDateToInstant(localDate);
			}
			catch (DateTimeParseException e)
			{ // Nothing to do here, just move on and try something else
			}
		}
		throw new ConversionException(object, Instant.class);
	}

	private static Instant zonedDateTimeToInstant(ZonedDateTime zonedDateTime)
	{
		Instant instant = zonedDateTime.toInstant();
		return instant;
	}

	private static LocalDate zonedDateTimeToLocalDate(ZonedDateTime zonedDateTime)
	{
		LocalDate localDate = zonedDateTime.toLocalDate();
		return localDate;
	}

	private static LocalDateTime instantToLocalDateTime(Instant instant)
	{
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return localDateTime;
	}

	private static Instant localDateTimeToInstant(LocalDateTime localDateTime)
	{
		ZonedDateTime zonedDateTime = localDateTimeToZonedDateTime(localDateTime);
		Instant instant = zonedDateTimeToInstant(zonedDateTime);
		return instant;
	}

	private static LocalDateTime zonedDateTimeToLocalDateTime(ZonedDateTime zonedDateTime)
	{
		Instant instant = zonedDateTimeToInstant(zonedDateTime);
		LocalDateTime localDateTime = instantToLocalDateTime(instant);
		return localDateTime;
	}

	private static ZonedDateTime localDateTimeToZonedDateTime(LocalDateTime localDateTime)
	{
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
		return zonedDateTime;
	}

	private static LocalDateTime localDateToLocalDateTime(LocalDate localDate)
	{
		LocalDateTime localDateTime = localDate.atStartOfDay();
		return localDateTime;
	}

	private static LocalDate localDateTimeToLocalDate(LocalDateTime localDateTime)
	{
		LocalDate localDate = localDateTime.toLocalDate();
		return localDate;
	}

	private static LocalDate instantToLocalDate(Instant instant)
	{
		LocalDateTime localDateTime = instantToLocalDateTime(instant);
		LocalDate localDate = localDateTimeToLocalDate(localDateTime);
		return localDate;
	}

	private static Instant localDateToInstant(LocalDate localDate)
	{
		LocalDateTime localDateTime = localDateToLocalDateTime(localDate);
		Instant instant = localDateTimeToInstant(localDateTime);
		return instant;
	}

	private static Instant utilDateToInstant(java.util.Date date)
	{
		Instant instant = date.toInstant();
		return instant;
	}

	private static LocalDate utilDateToLocalDate(java.util.Date date)
	{
		Instant instant = utilDateToInstant(date);
		LocalDate localDate = instantToLocalDate(instant);
		return localDate;
	}

	private static LocalDateTime utilDateToLocalDateTime(java.util.Date date)
	{
		Instant instant = utilDateToInstant(date);
		LocalDateTime localDateTime = instantToLocalDateTime(instant);
		return localDateTime;
	}

	private static LocalDate sqlDateToLocalDate(java.sql.Date date)
	{
		LocalDate localDate = date.toLocalDate();
		return localDate;
	}

	private static LocalDateTime sqlDateToLocalDateTime(java.sql.Date date)
	{
		LocalDate localDate = sqlDateToLocalDate(date);
		LocalDateTime localDateTime = localDateToLocalDateTime(localDate);
		return localDateTime;
	}

	private static Instant sqlDateToInstant(java.sql.Date date)
	{
		LocalDateTime localDateTime = sqlDateToLocalDateTime(date);
		Instant instant = localDateTimeToInstant(localDateTime);
		return instant;
	}

	private static Instant sqlTimestampToInstant(java.sql.Timestamp timestamp)
	{
		Instant instant = timestamp.toInstant();
		return instant;
	}

	private static LocalDateTime sqlTimestampToLocalDateTime(java.sql.Timestamp timestamp)
	{
		LocalDateTime localDateTime = timestamp.toLocalDateTime();
		return localDateTime;
	}

	private static LocalDate sqlTimestampToLocalDate(java.sql.Timestamp timestamp)
	{
		LocalDateTime localDateTime = sqlTimestampToLocalDateTime(timestamp);
		LocalDate localDate = localDateTimeToLocalDate(localDateTime);
		return localDate;
	}

	/**
	 * Returns a representation of the given object as a {@code byte[]}.
	 * <p>
	 * Returns {@code null} if the given object is {@code null}. If the object is non-{@code null} and is not an instance of {@code byte[]}
	 * or {@code Byte[]}, or if it is a {@code Byte[]} with one or more {@code null} elements, a {@link ConversionException} is thrown.
	 * 
	 * @param object
	 *            The object to be expressed as {@code byte[]}
	 * @throws ConversionException
	 *             if the conversion cannot be made due to data incompatability
	 */
	@Override
	public byte[] asByteArray(Object object)
	{
		if (object == null)
			return null;
		if (object instanceof byte[])
			return (byte[]) object;
		if (object instanceof Byte[])
		{
			Byte[] boxedArray = (Byte[]) object;
			byte[] primitiveArray = new byte[boxedArray.length];
			int i = 0;
			for (Byte b : boxedArray)
			{
				if (b == null)
					throw new ConversionException(object, Byte[].class);
				primitiveArray[i++] = b;
			}
			return primitiveArray;
		}
		throw new ConversionException(object, Byte[].class);
	}
}