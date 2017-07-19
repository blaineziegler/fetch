package com.zieglersoftware.fetch.core;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Assert;
import org.junit.Test;

import com.zieglersoftware.fetch.exception.EmptyResultException;

public class FetchDefaultImplTest
{
	private static final String DRIVER_CLASS_NAME = "org.hsqldb.jdbc.JDBCDriver";
	private static final String URL = "jdbc:hsqldb:mem:testdb";
	private static final String USERNAME = "sa";
	private static final String PASSWORD = "";
	private static final DataSource DATA_SOURCE = getDataSource(DRIVER_CLASS_NAME, URL, USERNAME, PASSWORD);
	private static final Fetch FETCH = new FetchDefaultImpl(DATA_SOURCE);

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
		String insertQuery = "insert into test(a) values(?)";

		int a = 10;
		FETCH.execute("drop table if exists test; create table test(id identity, a integer);");
		FETCH.update(insertQuery, a++);
		FETCH.update(insertQuery, a++);
		FETCH.update(insertQuery, a++);
		FETCH.update(insertQuery, a++);
		FETCH.update(insertQuery, a++);
		int newPrimaryKey = FETCH.insert(insertQuery, "ID", a++).keyAsInteger();
		int maxId = FETCH.select("select max(id) from test").asInteger();
		Assert.assertEquals(5, newPrimaryKey);
		Assert.assertEquals(5, maxId);
		boolean emptyResultExceptionThrown = false;
		try
		{
			FETCH.select("select id, a from test where a < ? order by id", 1).asTable();
		}
		catch (EmptyResultException e)
		{
			emptyResultExceptionThrown = true;
		}
		Assert.assertTrue(emptyResultExceptionThrown);
		List<List<Object>> tableWhereALessThan1 = FETCH.select("select id, a from test where a < ? order by id", 1).asTableOpt();
		Assert.assertTrue(tableWhereALessThan1.isEmpty());
		List<List<Object>> tableWhereAGreaterThan12 = FETCH.select("select id, a from test where a > ? order by id", 12).asTable();
		Assert.assertEquals(3, tableWhereAGreaterThan12.size());
		Assert.assertEquals(Arrays.asList(3, 13), tableWhereAGreaterThan12.get(0));
		Assert.assertEquals(Arrays.asList(4, 14), tableWhereAGreaterThan12.get(1));
		Assert.assertEquals(Arrays.asList(5, 15), tableWhereAGreaterThan12.get(2));
		List<Integer> allAs = FETCH.select("select a from test").asIntegerColumn();
		Assert.assertEquals(Arrays.asList(10, 11, 12, 13, 14, 15), allAs);
		List<Object> rowWhereA11 = FETCH.select("select id, a from test where a = 11").asRow();
		Assert.assertEquals(Arrays.asList(1, 11), rowWhereA11);
		FETCH.update("update test set a = a + ? where a < ?", 10, 12);
		List<List<Object>> tableAll = FETCH.select("select id, a from test order by id").asTable();
		Assert.assertEquals(6, tableAll.size());
		Assert.assertEquals(Arrays.asList(0, 20), tableAll.get(0));
		Assert.assertEquals(Arrays.asList(1, 21), tableAll.get(1));
		Assert.assertEquals(Arrays.asList(2, 12), tableAll.get(2));
		Assert.assertEquals(Arrays.asList(3, 13), tableAll.get(3));
		Assert.assertEquals(Arrays.asList(4, 14), tableAll.get(4));
		Assert.assertEquals(Arrays.asList(5, 15), tableAll.get(5));
		FETCH.update("delete from test where id in (?,?) and a in (?,?)", 2, 3, 13, 20);
		tableAll = FETCH.select("select id, a from test order by id").asTable();
		Assert.assertEquals(5, tableAll.size());
		Assert.assertEquals(Arrays.asList(0, 20), tableAll.get(0));
		Assert.assertEquals(Arrays.asList(1, 21), tableAll.get(1));
		Assert.assertEquals(Arrays.asList(2, 12), tableAll.get(2));
		Assert.assertEquals(Arrays.asList(4, 14), tableAll.get(3));
		Assert.assertEquals(Arrays.asList(5, 15), tableAll.get(4));
	}
}