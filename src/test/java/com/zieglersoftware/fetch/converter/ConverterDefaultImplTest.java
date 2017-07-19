package com.zieglersoftware.fetch.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

import com.zieglersoftware.fetch.exception.ConversionException;

public class ConverterDefaultImplTest
{
	@Test
	public void asInteger()
	{
		testAllForTargetClass(Integer.class);
	}

	@Test
	public void asLong()
	{
		testAllForTargetClass(Long.class);
	}

	@Test
	public void asBigInteger()
	{
		testAllForTargetClass(BigInteger.class);
	}

	@Test
	public void asDouble()
	{
		testAllForTargetClass(Double.class);
	}

	@Test
	public void asBigDecimal()
	{
		testAllForTargetClass(BigDecimal.class);
	}

	@Test
	public void asString()
	{
		testAllForTargetClass(String.class);
	}

	@Test
	public void asBoolean()
	{
		testAllForTargetClass(Boolean.class);
	}

	@Test
	public void asLocalDate()
	{
		testAllForTargetClass(LocalDate.class);
	}

	@Test
	public void asLocalDateTime()
	{
		testAllForTargetClass(LocalDateTime.class);
	}

	@Test
	public void asInstant()
	{
		testAllForTargetClass(Instant.class);
	}

	@Test
	public void asByteArray()
	{
		testAllForTargetClass(byte[].class);
	}

	private static final List<Conversion> CONVERSIONS = new ArrayList<ConverterDefaultImplTest.Conversion>();

	static
	{
		// Null

		CONVERSIONS.add(new Conversion(null,
				null, null, null, null, null, null, null, null, null, null, null));

		long big = ((long) Integer.MAX_VALUE) * 2;
		long negativeBig = ((long) Integer.MIN_VALUE) * 2;
		double huge = ((double) Long.MAX_VALUE) * 2;
		double negativeHuge = ((double) Long.MIN_VALUE) * 2;
		BigInteger gigantic = new BigDecimal("1e500").toBigInteger();
		BigInteger negativeGigantic = new BigDecimal("-1e500").toBigInteger();

		// Numbers

		CONVERSIONS.add(new Conversion(0,
				0, 0L, BigInteger.ZERO, 0D, BigDecimal.ZERO, "0", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion(1,
				1, 1L, BigInteger.ONE, 1D, BigDecimal.ONE, "1", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion(-1,
				-1, -1L, BigInteger.ONE.negate(), -1D, BigDecimal.ONE.negate(), "-1", ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion(big,
				ConversionException.class, big, BigInteger.valueOf(big), (double) big, BigDecimal.valueOf(big),
				big + "", ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion(negativeBig,
				ConversionException.class, negativeBig, BigInteger.valueOf(negativeBig),
				(double) negativeBig, BigDecimal.valueOf(negativeBig), negativeBig + "",
				ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion(huge,
				ConversionException.class, ConversionException.class, BigDecimal.valueOf(huge).toBigInteger(),
				huge, BigDecimal.valueOf(huge), huge + "", ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion(negativeHuge,
				ConversionException.class, ConversionException.class, BigDecimal.valueOf(negativeHuge).toBigInteger(),
				negativeHuge, BigDecimal.valueOf(negativeHuge), negativeHuge + "",
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class));
		CONVERSIONS.add(new Conversion(gigantic,
				ConversionException.class, ConversionException.class, gigantic,
				ConversionException.class, new BigDecimal(gigantic), gigantic + "", ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion(negativeGigantic,
				ConversionException.class, ConversionException.class, negativeGigantic,
				ConversionException.class, new BigDecimal(negativeGigantic), negativeGigantic + "",
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class));
		CONVERSIONS.add(new Conversion(Float.NaN,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, Float.NaN + "",
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class));
		CONVERSIONS.add(new Conversion(Float.POSITIVE_INFINITY,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, Float.POSITIVE_INFINITY + "",
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class));
		CONVERSIONS.add(new Conversion(Float.NEGATIVE_INFINITY,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, Float.NEGATIVE_INFINITY + "",
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class));
		CONVERSIONS.add(new Conversion(Double.NaN,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, Double.NaN + "",
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class));
		CONVERSIONS.add(new Conversion(Double.POSITIVE_INFINITY,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, Double.POSITIVE_INFINITY + "",
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class));
		CONVERSIONS.add(new Conversion(Double.NEGATIVE_INFINITY,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, Double.NEGATIVE_INFINITY + "",
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class));

		// Strings

		CONVERSIONS.add(new Conversion("",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "", ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("hello",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "hello", ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));

		// Booleans

		CONVERSIONS.add(new Conversion("0",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "0", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("1",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "1", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("true",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "true", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("false",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "false", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("trUe",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "trUe", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("faLse",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "faLse", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("t",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "t", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("f",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "f", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("T",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "T", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("F",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "F", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("yes",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "yes", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("no",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "no", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("yEs",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "yEs", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("nO",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "nO", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("y",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "y", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("n",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "n", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("Y",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "Y", true,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
		CONVERSIONS.add(new Conversion("N",
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, "N", false,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));

		// Dates

		LocalDateTime localDateTime = LocalDateTime.of(2005, Month.FEBRUARY, 15, 7, 25, 50, 100);
		LocalDateTime localDateTimeRoundedToSeconds = LocalDateTime.of(2005, Month.FEBRUARY, 15, 7, 25, 50);
		LocalDateTime localDateTimeRoundedToDay = LocalDateTime.of(2005, Month.FEBRUARY, 15, 0, 0);
		LocalDate localDate = LocalDate.of(2005, Month.FEBRUARY, 15);
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
		ZonedDateTime zonedDateTimeRoundedToSeconds = ZonedDateTime.of(localDateTimeRoundedToSeconds, ZoneId.systemDefault());
		ZonedDateTime zonedDateTimeRoundedToDay = ZonedDateTime.of(localDateTimeRoundedToDay, ZoneId.systemDefault());
		Instant instant = zonedDateTime.toInstant();
		Instant instantRoundedToSeconds = zonedDateTimeRoundedToSeconds.toInstant();
		Instant instantRoundedToDay = zonedDateTimeRoundedToDay.toInstant();
		java.util.Date utilDate = java.util.Date.from(instant);
		java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
		java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(localDateTime);
		String localDateTimeString = localDateTime.toString();
		String localDateString = localDate.toString();
		String instantString = instant.toString();
		String zonedDateTimeString = zonedDateTime.toString();

		CONVERSIONS.add(new Conversion(localDateTime,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, localDateTime.toString(), ConversionException.class,
				localDate, localDateTime, instant, ConversionException.class));
		CONVERSIONS.add(new Conversion(localDate,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, localDate.toString(), ConversionException.class,
				localDate, localDateTimeRoundedToDay, instantRoundedToDay, ConversionException.class));
		CONVERSIONS.add(new Conversion(instant,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, instant.toString(), ConversionException.class,
				localDate, localDateTime, instant, ConversionException.class));
		CONVERSIONS.add(new Conversion(zonedDateTime,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, zonedDateTime.toString(), ConversionException.class,
				localDate, localDateTime, instant, ConversionException.class));
		CONVERSIONS.add(new Conversion(utilDate,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, utilDate.toString(), ConversionException.class,
				localDate, localDateTimeRoundedToSeconds, instantRoundedToSeconds, ConversionException.class));
		CONVERSIONS.add(new Conversion(sqlDate,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, sqlDate.toString(), ConversionException.class,
				localDate, localDateTimeRoundedToDay, instantRoundedToDay, ConversionException.class));
		CONVERSIONS.add(new Conversion(sqlTimestamp,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, sqlTimestamp.toString(), ConversionException.class,
				localDate, localDateTime, instant, ConversionException.class));
		CONVERSIONS.add(new Conversion(localDateTimeString,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, localDateTimeString.toString(), ConversionException.class,
				localDate, localDateTime, instant, ConversionException.class));
		CONVERSIONS.add(new Conversion(localDateString,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, localDateString.toString(), ConversionException.class,
				localDate, localDateTimeRoundedToDay, instantRoundedToDay, ConversionException.class));
		CONVERSIONS.add(new Conversion(instantString,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, instantString.toString(), ConversionException.class,
				localDate, localDateTime, instant, ConversionException.class));
		CONVERSIONS.add(new Conversion(zonedDateTimeString,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, zonedDateTimeString.toString(), ConversionException.class,
				localDate, localDateTime, instant, ConversionException.class));

		// Byte arrays

		byte[] primitiveByteArray = new byte[] { 1, 2, 3 };
		Byte[] boxedByteArray = new Byte[] { 4, 5, 6 };
		byte[] boxedByteArrayAsPrimitive = new byte[] { 4, 5, 6 };
		Byte[] boxedByteArrayWithNull = new Byte[] { 7, null, 9 };

		CONVERSIONS.add(new Conversion(primitiveByteArray,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, Arrays.toString(primitiveByteArray), ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, primitiveByteArray));
		CONVERSIONS.add(new Conversion(boxedByteArray,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, Arrays.toString(boxedByteArray), ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, boxedByteArrayAsPrimitive));
		CONVERSIONS.add(new Conversion(boxedByteArrayWithNull,
				ConversionException.class, ConversionException.class, ConversionException.class,
				ConversionException.class, ConversionException.class, Arrays.toString(boxedByteArrayWithNull), ConversionException.class,
				ConversionException.class, ConversionException.class, ConversionException.class, ConversionException.class));
	}

	private static class Conversion
	{
		Object objectValue;
		Object integerValue;
		Object longValue;
		Object bigIntegerValue;
		Object doubleValue;
		Object bigDecimalValue;
		Object stringValue;
		Object booleanValue;
		Object localDateValue;
		Object localDateTimeValue;
		Object instantValue;
		Object byteArrayValue;

		public Conversion(Object objectValue, Object integerValue, Object longValue,
				Object bigIntegerValue, Object doubleValue, Object bigDecimalValue, Object stringValue,
				Object booleanValue, Object localDateValue, Object localDateTimeValue, Object instantValue,
				Object byteArrayValue)
		{
			this.objectValue = objectValue;
			this.integerValue = integerValue;
			this.longValue = longValue;
			this.bigIntegerValue = bigIntegerValue;
			this.doubleValue = doubleValue;
			this.bigDecimalValue = bigDecimalValue;
			this.stringValue = stringValue;
			this.booleanValue = booleanValue;
			this.localDateValue = localDateValue;
			this.localDateTimeValue = localDateTimeValue;
			this.instantValue = instantValue;
			this.byteArrayValue = byteArrayValue;
		}

		@Override
		public String toString()
		{
			return Objects.toString(objectValue);
		}
	}

	private static final ConverterDefaultImpl CONVERTER = new ConverterDefaultImpl();

	private static void testAllForTargetClass(Class<?> targetClass)
	{
		for (Conversion conversion : CONVERSIONS)
		{
			Object objectValue = conversion.objectValue;
			Object expectedConversion = null;
			Object actualConversion = null;
			try
			{
				if (targetClass == Integer.class)
				{
					expectedConversion = conversion.integerValue;
					actualConversion = CONVERTER.asInteger(objectValue);
				}
				else if (targetClass == Long.class)
				{
					expectedConversion = conversion.longValue;
					actualConversion = CONVERTER.asLong(objectValue);
				}
				else if (targetClass == BigInteger.class)
				{
					expectedConversion = conversion.bigIntegerValue;
					actualConversion = CONVERTER.asBigInteger(objectValue);
				}
				else if (targetClass == Double.class)
				{
					expectedConversion = conversion.doubleValue;
					actualConversion = CONVERTER.asDouble(objectValue);
				}
				else if (targetClass == BigDecimal.class)
				{
					expectedConversion = conversion.bigDecimalValue;
					actualConversion = CONVERTER.asBigDecimal(objectValue);
				}
				else if (targetClass == String.class)
				{
					expectedConversion = conversion.stringValue;
					actualConversion = CONVERTER.asString(objectValue);
				}
				else if (targetClass == Boolean.class)
				{
					expectedConversion = conversion.booleanValue;
					actualConversion = CONVERTER.asBoolean(objectValue);
				}
				else if (targetClass == LocalDate.class)
				{
					expectedConversion = conversion.localDateValue;
					actualConversion = CONVERTER.asLocalDate(objectValue);
				}
				else if (targetClass == LocalDateTime.class)
				{
					expectedConversion = conversion.localDateTimeValue;
					actualConversion = CONVERTER.asLocalDateTime(objectValue);
				}
				else if (targetClass == Instant.class)
				{
					expectedConversion = conversion.instantValue;
					actualConversion = CONVERTER.asInstant(objectValue);
				}
				else if (targetClass == byte[].class)
				{
					expectedConversion = conversion.byteArrayValue;
					actualConversion = CONVERTER.asByteArray(objectValue);
				}
			}
			catch (ConversionException e)
			{
				actualConversion = ConversionException.class;
			}
			if (expectedConversion instanceof byte[])
				Assert.assertArrayEquals((byte[]) expectedConversion, (byte[]) actualConversion);
			else
				Assert.assertEquals(expectedConversion, actualConversion);
		}
	}
}