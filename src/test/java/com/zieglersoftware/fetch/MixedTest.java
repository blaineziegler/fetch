package com.zieglersoftware.fetch;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Assert;
import org.junit.Test;

import com.zieglersoftware.fetch.async.SqlQueue;
import com.zieglersoftware.fetch.async.SqlQueueDefaultImpl;
import com.zieglersoftware.fetch.core.Fetch;
import com.zieglersoftware.fetch.core.FetchDefaultImpl;

public class MixedTest
{
	private static final String DRIVER_CLASS_NAME = "org.hsqldb.jdbc.JDBCDriver";
	private static final String URL = "jdbc:hsqldb:mem:testdb";
	private static final String USERNAME = "sa";
	private static final String PASSWORD = "";
	private static final DataSource DATA_SOURCE = getDataSource(DRIVER_CLASS_NAME, URL, USERNAME, PASSWORD);
	private static final Fetch FETCH = new FetchDefaultImpl(DATA_SOURCE);

	private static final int MAX_QUEUE_LENGTH = 1000;
	private static final long CALL_PERIOD = 100;
	private static final TimeUnit CALL_PERIOD_TIME_UNIT = TimeUnit.MILLISECONDS;
	private static final int MAX_CONCURRENT_CALLS = 3;
	private static final SqlQueue SQL_QUEUE = new SqlQueueDefaultImpl(MAX_QUEUE_LENGTH, CALL_PERIOD, CALL_PERIOD_TIME_UNIT,
			MAX_CONCURRENT_CALLS, FETCH);

	private static DataSource getDataSource(String jdbcDriverClassName, String jdbcUrl, String jdbcUsername, String jdbcPassword)
	{
		BasicDataSource basicDataSource = new BasicDataSource();
		basicDataSource.setDriverClassName(jdbcDriverClassName);
		basicDataSource.setUrl(jdbcUrl);
		basicDataSource.setUsername(jdbcUsername);
		basicDataSource.setPassword(jdbcPassword);
		return basicDataSource;
	}

	@Test
	public void test()
	{
		String insertQuery = "insert into test(timestamp) values(now())";
		String maxIdQuery = "select max(id) from test";

		FETCH.execute("drop table if exists test; create table test(id identity, timestamp timestamp);");
		FETCH.insert(insertQuery, "ID");
		FETCH.insert(insertQuery, "ID");
		FETCH.update(insertQuery);
		int maxId = FETCH.select(maxIdQuery).asInteger();
		Assert.assertEquals(2, maxId);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		SQL_QUEUE.submitUpdate(insertQuery);
		maxId = FETCH.select(maxIdQuery).asInteger();
		Assert.assertEquals(2, maxId);
		FETCH.update(insertQuery);
		FETCH.update(insertQuery);
		FETCH.update(insertQuery);
		maxId = FETCH.select(maxIdQuery).asInteger();
		Assert.assertTrue(maxId >= 5);
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		int maxIdAfterWaiting = FETCH.select(maxIdQuery).asInteger();
		Assert.assertTrue(maxIdAfterWaiting > maxId);
	}
}