package com.zieglersoftware.fetch.core;

import static com.zieglersoftware.assertions.Assertions.notEmpty;
import static com.zieglersoftware.assertions.Assertions.notEmptyAllNotEmpty;
import static com.zieglersoftware.assertions.Assertions.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.zieglersoftware.fetch.converter.Converter;
import com.zieglersoftware.fetch.converter.ConverterDefaultImpl;

/**
 * An implementation of {@link Fetch}.
 * <p>
 * Requires a {@link DataSource} to be provided at construction time. Utilizes a {@link Converter} to process data, which may optionally be
 * provided at construction time. If it is not provided, a {@link ConverterDefaultImpl} will be used.
 * <p>
 * {@code FetchDefaultImpl} is immutable.
 */
public final class FetchDefaultImpl implements Fetch
{
	private static final Logger LOGGER = LoggerFactory.getLogger(FetchDefaultImpl.class);

	private static final Converter DEFAULT_CONVERTER = new ConverterDefaultImpl();

	private final JdbcTemplate jdbcTemplate;
	private final Converter converter;
	private final ResultSetExtractor<SelectResult> selectResultExtractor;

	public FetchDefaultImpl(DataSource dataSource)
	{
		this(dataSource, DEFAULT_CONVERTER);
	}

	public FetchDefaultImpl(DataSource dataSource, Converter converter)
	{
		notNull(dataSource, "dataSource");
		notNull(converter, "converter");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.converter = converter;
		selectResultExtractor = resultSet -> new SelectResult(resultSet, converter);
	}

	@Override
	public SelectResult select(String sql, Object... parameters)
	{
		notEmpty(sql, "sql");
		notNull(parameters, "parameters");
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Executing: {}; Parameters: {}", sql, parametersToString(parameters));
		Object[] parametersCleaned = parametersWithoutNewDateClasses(parameters);
		SelectResult selectResult = jdbcTemplate.query(sql, selectResultExtractor, parametersCleaned);
		LOGGER.debug("Done");
		return selectResult;
	}

	@Override
	public PrimaryKeyHolder insert(String sql, String primaryKeyColumnName, Object... parameters)
	{
		notEmpty(sql, "sql");
		notEmpty(primaryKeyColumnName, "primaryKeyColumnName");
		notNull(parameters, "parameters");
		String[] primaryKeyColumnNamesArray = new String[] { primaryKeyColumnName };
		Object[] parametersCleaned = parametersWithoutNewDateClasses(parameters);
		PrimaryKeyHolder primaryKeyHolder = insert(sql, primaryKeyColumnNamesArray, parametersCleaned);
		return primaryKeyHolder;
	}

	@Override
	public PrimaryKeyHolder insert(String sql, Collection<String> primaryKeyColumnNames, Object... parameters)
	{
		notEmpty(sql, "sql");
		notEmptyAllNotEmpty(primaryKeyColumnNames, "primaryKeyColumnNames");
		notNull(parameters, "parameters");
		String[] primaryKeyColumnNamesArray = (String[]) primaryKeyColumnNames.toArray();
		Object[] parametersCleaned = parametersWithoutNewDateClasses(parameters);
		PrimaryKeyHolder primaryKeyHolder = insert(sql, primaryKeyColumnNamesArray, parametersCleaned);
		return primaryKeyHolder;
	}

	private PrimaryKeyHolder insert(String sql, String[] primaryKeyColumnNames, Object... parameters)
	{
		PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator()
		{
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException
			{
				PreparedStatement preparedStatement = con.prepareStatement(sql, primaryKeyColumnNames);
				int parameterIndex = 1; // 1-based indexing
				Object[] parametersCleaned = parametersWithoutNewDateClasses(parameters);
				for (Object parameterCleaned : parametersCleaned)
					preparedStatement.setObject(parameterIndex++, parameterCleaned);
				return preparedStatement;
			}
		};
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Executing: {}; Parameters: {}", sql, parametersToString(parameters));
		jdbcTemplate.update(preparedStatementCreator, keyHolder);
		LOGGER.debug("Done");
		PrimaryKeyHolder primaryKeyHolder = new PrimaryKeyHolder(keyHolder, converter);
		return primaryKeyHolder;
	}

	@Override
	public void update(String sql, Object... parameters)
	{
		notEmpty(sql, "sql");
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Executing: {}; Parameters: {}", sql, parametersToString(parameters));
		Object[] parametersCleaned = parametersWithoutNewDateClasses(parameters);
		jdbcTemplate.update(sql, parametersCleaned);
		LOGGER.debug("Done");
	}

	@Override
	public void execute(String sql)
	{
		notEmpty(sql, "sql");
		LOGGER.debug("Executing: {}", sql);
		jdbcTemplate.execute(sql);
		LOGGER.debug("Done");
	}

	/**
	 * Returns a copy of the given array, but with all {@link Instant}, {@link LocalDate}, {@link LocalDateTime}, and {@link ZonedDateTime}
	 * objects converted into {@link java.sql.Timestamp} instances
	 */
	private static Object[] parametersWithoutNewDateClasses(Object[] parameters)
	{
		return Arrays.stream(parameters).map(object -> parameterWithoutNewDateClasses(object)).toArray();
	}

	/**
	 * If the given object is of type {@link Instant}, {@link LocalDate}, {@link LocalDateTime}, or {@link ZonedDateTime}, returns the
	 * object as a {@link java.sql.Timestamp} instance. Otherwise, returns the given object.
	 */
	private static Object parameterWithoutNewDateClasses(Object parameter)
	{
		if (parameter == null)
			return null;
		Instant instant;
		if (parameter instanceof Instant)
			instant = (Instant) parameter;
		else if (parameter instanceof LocalDate)
			instant = ZonedDateTime.of(((LocalDate) parameter).atStartOfDay(), ZoneId.systemDefault()).toInstant();
		else if (parameter instanceof LocalDateTime)
			instant = ZonedDateTime.of((LocalDateTime) parameter, ZoneId.systemDefault()).toInstant();
		else if (parameter instanceof ZonedDateTime)
			instant = ((ZonedDateTime) parameter).toInstant();
		else
			return parameter;
		return Timestamp.from(instant);
	}

	private static String parametersToString(Object[] parameters)
	{
		int numberOfParams = parameters.length;
		if (numberOfParams == 0)
			return "[]";
		if (numberOfParams > 20)
			return "Many...";
		int maxLengthPerParam;
		if (numberOfParams <= 2)
			maxLengthPerParam = 200;
		else if (numberOfParams <= 5)
			maxLengthPerParam = 80;
		else if (numberOfParams <= 10)
			maxLengthPerParam = 40;
		else
			maxLengthPerParam = 20;
		StringBuilder stringBuilder = new StringBuilder("[");
		stringBuilder.append(parameterToString(parameters[0], maxLengthPerParam));
		for (int i = 1; i < parameters.length; i++)
			stringBuilder.append(", ").append(parameterToString(parameters[i], maxLengthPerParam));
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	private static String parameterToString(Object o, int maxLengthPerParam)
	{
		if (o == null)
			return "null";
		if (o instanceof byte[])
			return "{Byte array}";
		StringBuilder builder = new StringBuilder(o.toString());
		if (builder.length() > maxLengthPerParam)
			builder.replace(maxLengthPerParam, builder.length(), "...");
		return builder.toString();
	}
}