package com.zieglersoftware.fetch.async;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Assert;
import org.junit.Test;

public class MaxQueueTest
{
	private static void confirm(MaxQueue<?> rq,
		int expectedMaxNumberOfElements, int expectedCurrentNumberOfElements,
		boolean expectedEmpty, boolean expectedFull)
	{
		rq.checkRep();
		Assert.assertEquals(expectedMaxNumberOfElements, rq.maxNumberOfElements());
		Assert.assertEquals(expectedCurrentNumberOfElements, rq.currentNumberOfElements());
		Assert.assertEquals(expectedEmpty, rq.empty());
		Assert.assertEquals(expectedFull, rq.full());
	}

	private static void exercise(MaxQueue<?> rq)
	{
		rq.checkRep();
		rq.maxNumberOfElements();
		rq.currentNumberOfElements();
		rq.empty();
		rq.full();
	}

	private static void assertIntEquals(int a, int b)
	{
		Assert.assertEquals(a, b);
	}

	@Test
	public void empty()
	{
		MaxQueue<Integer> rq1 = new MaxQueue<Integer>(1);
		confirm(rq1, 1, 0, true, false);

		MaxQueue<Integer> rq5 = new MaxQueue<Integer>(5);
		confirm(rq5, 5, 0, true, false);

		MaxQueue<Integer> rq10 = new MaxQueue<Integer>(10);
		confirm(rq10, 10, 0, true, false);
	}

	@Test
	public void constructorException()
	{
		boolean exceptionCaught = false;
		try
		{
			new MaxQueue<Integer>(0);
		}
		catch (IllegalStateException e)
		{
			exceptionCaught = true;
		}
		Assert.assertTrue(exceptionCaught);
		exceptionCaught = false;
		try
		{
			new MaxQueue<Integer>(-1);
		}
		catch (IllegalStateException e)
		{
			exceptionCaught = true;
		}
		Assert.assertTrue(exceptionCaught);
	}

	@Test
	public void addWithoutGet()
	{
		MaxQueue<Integer> rq1 = new MaxQueue<Integer>(1);
		confirm(rq1, 1, 0, true, false);

		Assert.assertTrue(rq1.add(0));
		confirm(rq1, 1, 1, false, true);
		Assert.assertFalse(rq1.add(3));
		confirm(rq1, 1, 1, false, true);
		Assert.assertFalse(rq1.add(2));
		confirm(rq1, 1, 1, false, true);
		Assert.assertFalse(rq1.add(3));
		confirm(rq1, 1, 1, false, true);

		MaxQueue<Integer> rq5 = new MaxQueue<Integer>(5);
		confirm(rq5, 5, 0, true, false);

		Assert.assertTrue(rq5.add(5));
		confirm(rq5, 5, 1, false, false);
		Assert.assertTrue(rq5.add(5));
		confirm(rq5, 5, 2, false, false);
		Assert.assertTrue(rq5.add(1));
		confirm(rq5, 5, 3, false, false);
		Assert.assertTrue(rq5.add(0));
		confirm(rq5, 5, 4, false, false);
		Assert.assertTrue(rq5.add(0));
		confirm(rq5, 5, 5, false, true);
		Assert.assertFalse(rq5.add(0));
		confirm(rq5, 5, 5, false, true);
		Assert.assertFalse(rq5.add(9));
		confirm(rq5, 5, 5, false, true);
		Assert.assertFalse(rq5.add(0));
		confirm(rq5, 5, 5, false, true);
		Assert.assertFalse(rq5.add(0));
		confirm(rq5, 5, 5, false, true);
		Assert.assertFalse(rq5.add(2));
		confirm(rq5, 5, 5, false, true);

		MaxQueue<Integer> rq10 = new MaxQueue<Integer>(10);
		confirm(rq10, 10, 0, true, false);

		Assert.assertTrue(rq10.add(9));
		confirm(rq10, 10, 1, false, false);
		Assert.assertTrue(rq10.add(0));
		confirm(rq10, 10, 2, false, false);
		Assert.assertTrue(rq10.add(0));
		confirm(rq10, 10, 3, false, false);
		Assert.assertTrue(rq10.add(4));
		confirm(rq10, 10, 4, false, false);
		Assert.assertTrue(rq10.add(4));
		confirm(rq10, 10, 5, false, false);
		Assert.assertTrue(rq10.add(4));
		confirm(rq10, 10, 6, false, false);
		Assert.assertTrue(rq10.add(0));
		confirm(rq10, 10, 7, false, false);
		Assert.assertTrue(rq10.add(0));
		confirm(rq10, 10, 8, false, false);
		Assert.assertTrue(rq10.add(0));
		confirm(rq10, 10, 9, false, false);
		Assert.assertTrue(rq10.add(0));
		confirm(rq10, 10, 10, false, true);
		Assert.assertFalse(rq10.add(0));
		confirm(rq10, 10, 10, false, true);
		Assert.assertFalse(rq10.add(0));
		confirm(rq10, 10, 10, false, true);
		Assert.assertFalse(rq10.add(0));
		confirm(rq10, 10, 10, false, true);
		Assert.assertFalse(rq10.add(0));
		confirm(rq10, 10, 10, false, true);
	}

	@Test
	public void addException()
	{
		boolean exceptionCaught = false;
		try
		{
			new MaxQueue<Integer>(1).add(null);
		}
		catch (NullPointerException e)
		{
			exceptionCaught = true;
		}
		Assert.assertTrue(exceptionCaught);
	}

	@Test
	public void getWithoutAdd()
	{
		MaxQueue<Integer> rq1 = new MaxQueue<Integer>(1);
		confirm(rq1, 1, 0, true, false);

		Assert.assertNull(rq1.get());
		confirm(rq1, 1, 0, true, false);
		Assert.assertNull(rq1.get());
		confirm(rq1, 1, 0, true, false);

		MaxQueue<Integer> rq5 = new MaxQueue<Integer>(5);
		confirm(rq5, 5, 0, true, false);

		Assert.assertNull(rq5.get());
		confirm(rq5, 5, 0, true, false);
		Assert.assertNull(rq5.get());
		confirm(rq5, 5, 0, true, false);

		MaxQueue<Integer> rq10 = new MaxQueue<Integer>(10);
		confirm(rq10, 10, 0, true, false);

		Assert.assertNull(rq10.get());
		confirm(rq10, 10, 0, true, false);
		Assert.assertNull(rq10.get());
		confirm(rq10, 10, 0, true, false);
	}

	@Test
	public void addAndGet()
	{
		MaxQueue<Integer> rq1 = new MaxQueue<Integer>(1);
		confirm(rq1, 1, 0, true, false);
		Assert.assertNull(rq1.get());
		confirm(rq1, 1, 0, true, false);
		Assert.assertTrue(rq1.add(0));
		confirm(rq1, 1, 1, false, true);
		assertIntEquals(0, rq1.get());
		confirm(rq1, 1, 0, true, false);
		Assert.assertTrue(rq1.add(0));
		confirm(rq1, 1, 1, false, true);
		assertIntEquals(0, rq1.get());
		confirm(rq1, 1, 0, true, false);
		Assert.assertTrue(rq1.add(1));
		confirm(rq1, 1, 1, false, true);
		Assert.assertFalse(rq1.add(2));
		confirm(rq1, 1, 1, false, true);
		Assert.assertFalse(rq1.add(3));
		confirm(rq1, 1, 1, false, true);
		assertIntEquals(1, rq1.get());
		confirm(rq1, 1, 0, true, false);
		Assert.assertNull(rq1.get());
		confirm(rq1, 1, 0, true, false);
		Assert.assertTrue(rq1.add(2));
		confirm(rq1, 1, 1, false, true);
		Assert.assertFalse(rq1.add(3));
		confirm(rq1, 1, 1, false, true);
		assertIntEquals(2, rq1.get());
		confirm(rq1, 1, 0, true, false);
		Assert.assertNull(rq1.get());
		confirm(rq1, 1, 0, true, false);

		MaxQueue<Integer> rq5 = new MaxQueue<Integer>(5);
		confirm(rq5, 5, 0, true, false);
		Assert.assertNull(rq1.get());
		confirm(rq5, 5, 0, true, false);
		Assert.assertTrue(rq5.add(0));
		confirm(rq5, 5, 1, false, false);
		Assert.assertTrue(rq5.add(1));
		confirm(rq5, 5, 2, false, false);
		Assert.assertTrue(rq5.add(2));
		confirm(rq5, 5, 3, false, false);
		assertIntEquals(0, rq5.get());
		confirm(rq5, 5, 2, false, false);
		assertIntEquals(1, rq5.get());
		confirm(rq5, 5, 1, false, false);
		assertIntEquals(2, rq5.get());
		confirm(rq5, 5, 0, true, false);
		Assert.assertTrue(rq5.add(0));
		confirm(rq5, 5, 1, false, false);
		Assert.assertTrue(rq5.add(1));
		confirm(rq5, 5, 2, false, false);
		Assert.assertTrue(rq5.add(2));
		confirm(rq5, 5, 3, false, false);
		Assert.assertTrue(rq5.add(3));
		confirm(rq5, 5, 4, false, false);
		Assert.assertTrue(rq5.add(4));
		confirm(rq5, 5, 5, false, true);
		Assert.assertFalse(rq5.add(5));
		confirm(rq5, 5, 5, false, true);
		Assert.assertFalse(rq5.add(6));
		confirm(rq5, 5, 5, false, true);
		Assert.assertFalse(rq5.add(7));
		confirm(rq5, 5, 5, false, true);
		assertIntEquals(0, rq5.get());
		confirm(rq5, 5, 4, false, false);
		assertIntEquals(1, rq5.get());
		confirm(rq5, 5, 3, false, false);
		Assert.assertTrue(rq5.add(0));
		confirm(rq5, 5, 4, false, false);
		assertIntEquals(2, rq5.get());
		confirm(rq5, 5, 3, false, false);
		Assert.assertTrue(rq5.add(6));
		confirm(rq5, 5, 4, false, false);
		Assert.assertTrue(rq5.add(7));
		confirm(rq5, 5, 5, false, true);
		Assert.assertFalse(rq5.add(7));
		confirm(rq5, 5, 5, false, true);
		assertIntEquals(3, rq5.get());
		confirm(rq5, 5, 4, false, false);
		assertIntEquals(4, rq5.get());
		confirm(rq5, 5, 3, false, false);
		assertIntEquals(0, rq5.get());
		confirm(rq5, 5, 2, false, false);
		assertIntEquals(6, rq5.get());
		confirm(rq5, 5, 1, false, false);
		assertIntEquals(7, rq5.get());
		confirm(rq5, 5, 0, true, false);
		Assert.assertTrue(rq5.add(7));
		confirm(rq5, 5, 1, false, false);
		assertIntEquals(7, rq5.get());
		confirm(rq5, 5, 0, true, false);
	}

	@Test
	public void concurrency()
	{
		MaxQueue<Integer> rq5 = new MaxQueue<Integer>(5);
		confirm(rq5, 5, 0, true, false);
		final List<Error> error = new CopyOnWriteArrayList<Error>();
		final List<RuntimeException> runtimeException = new CopyOnWriteArrayList<RuntimeException>();
		Runnable queueOperations = new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					for (int i = 0; i < 100; i++)
					{
						rq5.add(0);
						exercise(rq5);
						rq5.get();
						exercise(rq5);
						rq5.add(0);
						exercise(rq5);
						rq5.add(0);
						exercise(rq5);
						rq5.get();
						exercise(rq5);
						rq5.get();
						exercise(rq5);
						rq5.get();
						exercise(rq5);
						rq5.add(0);
						exercise(rq5);
						rq5.add(0);
						exercise(rq5);
						rq5.add(0);
						exercise(rq5);
						rq5.get();
						exercise(rq5);
						rq5.add(0);
						exercise(rq5);
					}
				}
				catch (Error e)
				{
					e.printStackTrace();
					error.add(e);
				}
				catch (RuntimeException e)
				{
					e.printStackTrace();
					runtimeException.add(e);
				}
			}
		};
		Thread[] threads = {
			new Thread(queueOperations), new Thread(queueOperations),
			new Thread(queueOperations), new Thread(queueOperations),
			new Thread(queueOperations), new Thread(queueOperations),
			new Thread(queueOperations), new Thread(queueOperations),
			new Thread(queueOperations), new Thread(queueOperations)
		};
		for (Thread thread : threads)
			thread.start();
		try
		{
			for (Thread thread : threads)
				thread.join();
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		if (!error.isEmpty())
			throw new RuntimeException(error.get(0));
		if (!runtimeException.isEmpty())
			throw new RuntimeException(runtimeException.get(0));
	}
}