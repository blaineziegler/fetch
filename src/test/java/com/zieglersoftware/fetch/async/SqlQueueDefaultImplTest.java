package com.zieglersoftware.fetch.async;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.zieglersoftware.fetch.core.SqlExecutor;

public class SqlQueueDefaultImplTest
{
	private static final boolean DEBUG_MODE = false;
	private static final int DEBUG_MODE_NUMBER_OF_RUNS = 20;
	private static StringBuilder DEBUG_MODE_LOG;

	/**
	 * Period-bound, so expect calls every 200ms after initial delay of 200/4 + 5 = 55.
	 * <p>
	 * 4 calls by 55, 5 calls by 255
	 **/
	@Test
	public void periodBound()
	{
		final int maxQueueLength = 100;
		final int period = 200;
		final int latency = 5;
		final int maxConcurrentCalls = 4;
		final int numberOfCalls = 5;

		MockSqlExecutor sqlExecutor;

		for (int i = 0; i < (DEBUG_MODE ? DEBUG_MODE_NUMBER_OF_RUNS : 1); i++)
		{
			DEBUG_MODE_LOG = new StringBuilder();

			logIfDebug("Test iteration " + i);
			sqlExecutor = new MockSqlExecutor(latency);
			logIfDebug("Submitting all calls");
			createQueueAndSubmitAllCalls(maxQueueLength, period, maxConcurrentCalls, numberOfCalls, sqlExecutor);
			logIfDebug("Main thread sleeping 20 milliseconds");
			sleep(20);
			int callCountAfter20 = sqlExecutor.getCallCount();
			logIfDebug("After main thread slept 20 milliseconds, call count is " + callCountAfter20 + ". Expected 0");
			logIfDebug("Main thread sleeping 130 milliseconds");
			sleep(130);
			int callCountAfter150 = sqlExecutor.getCallCount();
			logIfDebug("After main thread slept 130 milliseconds, call count is " + callCountAfter150 + ". Expected 4");
			logIfDebug("Main thread sleeping 200 milliseconds");
			sleep(200);
			int callCountAfter370 = sqlExecutor.getCallCount();
			logIfDebug("After main thread slept 200 milliseconds, call count is " + callCountAfter370 + ". Expected 5");
			sleepAndPrintLogIfDebug(100);
			Assert.assertEquals(0, callCountAfter20);
			Assert.assertEquals(4, callCountAfter150);
			Assert.assertEquals(5, callCountAfter370);
		}
	}

	/**
	 * Latency-bound, so expect calls every 200ms after initial delay of 60/4 + 200 = 215.
	 * <p>
	 * 4 calls by 215, 5 calls by 415
	 **/
	@Test
	public void latencyBound()
	{
		final int maxQueueLength = 100;
		final int period = 60;
		final int latency = 200;
		final int maxConcurrentCalls = 4;
		final int numberOfCalls = 5;

		MockSqlExecutor sqlExecutor;

		for (int i = 0; i < (DEBUG_MODE ? DEBUG_MODE_NUMBER_OF_RUNS : 1); i++)
		{
			DEBUG_MODE_LOG = new StringBuilder();

			logIfDebug("Test iteration " + i);
			sqlExecutor = new MockSqlExecutor(latency);
			logIfDebug("Submitting all calls");
			createQueueAndSubmitAllCalls(maxQueueLength, period, maxConcurrentCalls, numberOfCalls, sqlExecutor);
			logIfDebug("Main thread sleeping 100 milliseconds");
			sleep(100);
			int callCountAfter100 = sqlExecutor.getCallCount();
			logIfDebug("After main thread slept 100 milliseconds, call count is " + callCountAfter100 + ". Expected 0");
			logIfDebug("Main thread sleeping 215 milliseconds");
			sleep(215);
			int callCountAfter315 = sqlExecutor.getCallCount();
			logIfDebug("After main thread slept 215 milliseconds, call count is " + callCountAfter315 + ". Expected 4");
			logIfDebug("Main thread sleeping 200 milliseconds");
			sleep(200);
			int callCountAfter415 = sqlExecutor.getCallCount();
			logIfDebug("After main thread slept 200 milliseconds, call count is " + callCountAfter415 + ". Expected 5");
			sleepAndPrintLogIfDebug(100);
			Assert.assertEquals(0, callCountAfter100);
			Assert.assertEquals(4, callCountAfter315);
			Assert.assertEquals(5, callCountAfter415);
		}
	}

	private static void createQueueAndSubmitAllCalls(int maxQueueLength, long callPeriodMillis, int maxConcurrentCalls, int numberOfCalls,
		MockSqlExecutor sqlExecutor)
	{
		SqlQueue sqlQueue = new SqlQueueDefaultImpl(
			maxQueueLength, callPeriodMillis, TimeUnit.MILLISECONDS, maxConcurrentCalls, sqlExecutor);
		for (int i = 0; i < numberOfCalls; i++)
			sqlQueue.submitUpdate("not important");
	}

	private static class MockSqlExecutor implements SqlExecutor
	{
		private final int latencyMilliseconds;
		private final AtomicInteger callCount = new AtomicInteger();

		public MockSqlExecutor(int latencyMilliseconds)
		{
			this.latencyMilliseconds = latencyMilliseconds;
		}

		@Override
		public void update(String sql, Object... parameters)
		{
			execute(sql);
		}

		@Override
		public void execute(String sql)
		{
			logIfDebug("Entering execute(). Sleeping latency of " + latencyMilliseconds + " milliseconds");
			sleepForLatency();
			logIfDebug("Calling increment");
			callCount.incrementAndGet();
		}

		public int getCallCount()
		{
			return callCount.get();
		}

		private void sleepForLatency()
		{
			try
			{
				Thread.sleep(latencyMilliseconds);
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
	};

	private static void sleep(int millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static synchronized void logIfDebug(String msg)
	{
		if (DEBUG_MODE)
		{
			DEBUG_MODE_LOG.append(System.currentTimeMillis());
			DEBUG_MODE_LOG.append(" -- ");
			DEBUG_MODE_LOG.append(msg);
			DEBUG_MODE_LOG.append("\n");
		}
	}

	private static void sleepAndPrintLogIfDebug(int millis)
	{
		if (DEBUG_MODE)
		{
			sleep(millis);
			System.out.println(DEBUG_MODE_LOG);
		}
	}
}