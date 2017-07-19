package com.zieglersoftware.fetch.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.support.KeyHolder;

import com.zieglersoftware.fetch.converter.Converter;
import com.zieglersoftware.fetch.exception.ConversionException;

/**
 * Provides access to the primary keys(s) of record(s) that were inserted into a database. This is particularly useful when primary keys are
 * generated automatically by the database, so that a follow-up query to determine the newly generated key(s) is unnecessary.
 * <p>
 * {@code PrimaryKeyHolder} is immutable.
 */
public final class PrimaryKeyHolder
{
	private final Converter converter;
	private final List<List<Object>> keys;

	/**
	 * Package-private constructor. Public clients have no need to instantiate this class.
	 */
	PrimaryKeyHolder(KeyHolder keyHolder, Converter converter)
	{
		this.converter = converter;
		List<List<Object>> keys = new ArrayList<>();
		for (Map<String, Object> keyMapForRow : keyHolder.getKeyList())
			keys.add(new ArrayList<>(keyMapForRow.values()));
		this.keys = keys;
	}

	/**
	 * Returns the single inserted record's primary key, as an {@code Integer}. If no records were inserted, or if one record was inserted,
	 * but with no primary key values, {@code null} is returned. If more than one record was inserted, or if the record includes a compound
	 * (multi-column) primary key, an {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 *             if more than one record was inserted or if the record includes a compound primary key
	 * @throws ConversionException
	 *             if the key cannot be converted to {@code Integer}
	 */
	public Integer keyAsInteger()
	{
		Object key = singleValueKey();
		return converter.asInteger(key);
	}

	/**
	 * Returns the single inserted record's primary key, as a {@code Long}. If no records were inserted, or if one record was inserted, but
	 * with no primary key values, {@code null} is returned. If more than one record was inserted, or if the record includes a compound
	 * (multi-column) primary key, an {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 *             if more than one record was inserted or if the record includes a compound primary key
	 * @throws ConversionException
	 *             if the key cannot be converted to {@code Long}
	 */
	public Long keyAsLong()
	{
		Object key = singleValueKey();
		return converter.asLong(key);
	}

	/**
	 * Returns the single inserted record's primary key, as a {@code String}. If no records were inserted, or if one record was inserted,
	 * but with no primary key values, {@code null} is returned. If more than one record was inserted, or if the record includes a compound
	 * (multi-column) primary key, an {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 *             if more than one record was inserted or if the record includes a compound primary key
	 * @throws ConversionException
	 *             if the key cannot be converted to {@code String}
	 */
	public String keyAsString()
	{
		Object key = singleValueKey();
		return converter.asString(key);
	}

	/**
	 * Returns the single inserted record's primary key, which may be a compound (multi-column) key, as a {@code List<Object>}. If no
	 * records were inserted, returns {@code null}. If one record was inserted, but with no primary key values, an empty list is returned.
	 * If more than one record was inserted, an {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 *             if more than one record was inserted
	 */
	public List<Object> compoundKey()
	{
		List<Object> singleRowKey = singleRowKey();
		if (singleRowKey == null)
			return null;
		return singleRowKey;
	}

	private List<Object> singleRowKey()
	{
		if (keys.size() == 0)
			return null;
		if (keys.size() > 1)
			throw new IllegalStateException(keys.size() + " rows were inserted. Cannot retrieve single key.");
		return new ArrayList<>(keys.get(0));
	}

	private Object singleValueKey()
	{
		List<Object> singleRowKey = singleRowKey();
		if (singleRowKey == null)
			return null;
		if (singleRowKey.size() == 0)
			return null;
		if (singleRowKey.size() > 1)
			throw new IllegalStateException(singleRowKey.size() + " keys per row were inserted. Cannot retrieve single key.");
		return singleRowKey.get(0);
	}

	/**
	 * Returns the primary keys of all inserted records as {@code Integer}s. If no records were inserted, or if the inserted records contain
	 * no primary key values, an empty list is returned. If the inserted records include a compound (multi-column) primary key, an
	 * {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 *             if the inserted records include a compound primary key
	 * @throws ConversionException
	 *             if any keys cannot be converted to {@code Integer}
	 */
	public List<Integer> keysAsInteger()
	{
		List<Integer> integerKeys = new ArrayList<>();
		for (List<Object> keyRow : keys)
		{
			if (keyRow.size() > 0)
			{
				if (keyRow.size() > 1)
					throw new IllegalStateException(keyRow.size() + " keys per row were inserted. Cannot retrieve single key for row.");
				integerKeys.add(converter.asInteger(keyRow.get(0)));
			}
		}
		return integerKeys;
	}

	/**
	 * Returns the primary keys of all inserted records as {@code Long}s. If no records were inserted, or if the inserted records contain no
	 * primary key values, an empty list is returned. If the inserted records include a compound (multi-column) primary key, an
	 * {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 *             if the inserted records include a compound primary key
	 * @throws ConversionException
	 *             if any keys cannot be converted to {@code Long}
	 */
	public List<Long> keysAsLong()
	{
		List<Long> longKeys = new ArrayList<>();
		for (List<Object> keyRow : keys)
		{
			if (keyRow.size() > 0)
			{
				if (keyRow.size() > 1)
					throw new IllegalStateException(keyRow.size() + " keys per row were inserted. Cannot retrieve single key for row.");
				longKeys.add(converter.asLong(keyRow.get(0)));
			}
		}
		return longKeys;
	}

	/**
	 * Returns the primary keys of all inserted records as {@code String}s. If no records were inserted, or if the inserted records contain
	 * no primary key values, an empty list is returned. If the inserted records include a compound (multi-column) primary key, an
	 * {@link IllegalStateException} is thrown.
	 * 
	 * @throws IllegalStateException
	 *             if the inserted records include a compound primary key
	 * @throws ConversionException
	 *             if any keys cannot be converted to {@code String}
	 */
	public List<String> keysAsString()
	{
		List<String> stringKeys = new ArrayList<>();
		for (List<Object> keyRow : keys)
		{
			if (keyRow.size() > 0)
			{
				if (keyRow.size() > 1)
					throw new IllegalStateException(keyRow.size() + " keys per row were inserted. Cannot retrieve single key for row.");
				stringKeys.add(converter.asString(keyRow.get(0)));
			}
		}
		return stringKeys;
	}

	/**
	 * Returns the primary keys of all inserted records, which may be compound (multi-column) keys, as a {@code List<List<Object>>}. If no
	 * records were inserted, an empty list is returned. If records were inserted, but with no primary key values, a list of empty lists is
	 * returned.
	 */
	public List<List<Object>> compoundKeys()
	{
		List<List<Object>> keysCopy = new ArrayList<>();
		for (List<Object> keyRow : keys)
			keysCopy.add(new ArrayList<>(keyRow));
		return keysCopy;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object == null)
			return false;
		if (object == this)
			return true;
		if (!object.getClass().equals(PrimaryKeyHolder.class))
			return false;
		PrimaryKeyHolder other = (PrimaryKeyHolder) object;
		return this.keys.equals(other.keys);
	}

	@Override
	public int hashCode()
	{
		return keys.hashCode();
	}

	@Override
	public String toString()
	{
		return keys.toString();
	}
}