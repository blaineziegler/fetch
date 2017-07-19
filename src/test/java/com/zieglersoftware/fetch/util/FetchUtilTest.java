package com.zieglersoftware.fetch.util;

import org.junit.Assert;
import org.junit.Test;

public class FetchUtilTest
{
	@Test
	public void insertSql()
	{
		String tableName = "t";
		String columnName0 = "c0";
		String columnName1 = "c1";
		String columnName2 = "c2";

		String expectedOneColumnSql = "insert into t (c0) values (?)";
		String actualOneColumnSql = FetchUtil.insertSql(tableName, columnName0);
		Assert.assertEquals(expectedOneColumnSql, actualOneColumnSql);

		String expectedTwoColumnSql = "insert into t (c0, c1) values (?, ?)";
		String actualTwoColumnSql = FetchUtil.insertSql(tableName, columnName0, columnName1);
		Assert.assertEquals(expectedTwoColumnSql, actualTwoColumnSql);

		String expectedThreeColumnSql = "insert into t (c0, c1, c2) values (?, ?, ?)";
		String actualThreeColumnSql = FetchUtil.insertSql(tableName, columnName0, columnName1, columnName2);
		Assert.assertEquals(expectedThreeColumnSql, actualThreeColumnSql);
	}
}