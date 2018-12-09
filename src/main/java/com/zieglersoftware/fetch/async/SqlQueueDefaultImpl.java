package com.zieglersoftware.fetch.async;

import static com.zieglersoftware.assertions.Assertions.notEmpty;
import static com.zieglersoftware.assertions.Assertions.notLess;
import static com.zieglersoftware.assertions.Assertions.notNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.zieglersoftware.fetch.core.SqlExecutor;

/**
 * An implementation of {@link SqlQueue}.
 * <p>
 * On a recurring, periodic basis, {@code SqlQueueDefaultImpl} executes the statements in the queue that were added earliest (FIFO). The
 * number of such statements to execute concurrently at each "tick" is configurable at construction time. The duration between ticks is also
 * configurable at construction time.
 * <p>
 * {@code SqlQueueDefaultImpl} enforces a maximum queue length, which is configurable at construction time. If the queue is full of
 * statements waiting to be executed, a call to {@link #submitUpdate} or {@link #submitExecute} will result in nothing being added to the
 * queue and {@code false} being returned.
 * <p>
 * {@code SqlQueueDefaultImpl} is thread-safe.
 */
public final class SqlQueueDefaultImpl implements SqlQueue
{
	private static final int INITIAL_DELAY_DIVISOR = 4;

	private final MaxQueue<SqlCall> queue;
	private final SqlExecutor sqlExecutor;
	private final long initialDelay;
	private final long callPeriod;
	private final TimeUnit callPeriodTimeUnit;

	/**
	 * Constructs a new {@code SqlQueueDefaultImpl}.
	 * <p>
	 * The queue will be ready to accept submissions for execution immediately upon construction.
	 * <p>
	 * {@code maxQueueLength}, {@code callPeriod}, and {@code maxConcurrentCalls} must be greater than 0.
	 * 
	 * @param maxQueueLength
	 *            the maximum number of statements that can be awaiting execution in the queue at one time
	 * @param callPeriod
	 *            the amount of time, in the given {@link TimeUnit}, between statement executions by a particular thread against this queue.
	 *            With multiple threads operating against this queue, each thread will execute a statement every {@code callPeriod}.
	 * @param callPeriodTimeUnit
	 *            the unit of time corresponding to the given call period
	 * @param maxConcurrentCalls
	 *            the maximum number of statements that may be executing concurrently at any given time
	 * @param sqlExecutor
	 *            the {@link SqlExecutor} to execute the statements in this queue
	 */
	public SqlQueueDefaultImpl(int maxQueueLength, long callPeriod,
		TimeUnit callPeriodTimeUnit, int maxConcurrentCalls, SqlExecutor sqlExecutor)
	{
		notLess(maxQueueLength, 1, "maxQueueLength");
		notLess(callPeriod, 1, "callPeriod");
		notLess(maxConcurrentCalls, 1, "maxConcurrentCalls");
		notNull(callPeriodTimeUnit, "callPeriodTimeUnit");
		notNull(sqlExecutor, "sqlExecutor");
		this.queue = new MaxQueue<SqlCall>(maxQueueLength);
		this.sqlExecutor = sqlExecutor;
		this.initialDelay = (long) ((double) callPeriod / (double) INITIAL_DELAY_DIVISOR);
		this.callPeriod = callPeriod;
		this.callPeriodTimeUnit = callPeriodTimeUnit;
		ExecutorService executorService = Executors.newFixedThreadPool(maxConcurrentCalls);
		for (int i = 0; i < maxConcurrentCalls; i++)
		{
			final int finalI = i;
			String threadName = this.getClass().getSimpleName() + "_thread_" + finalI;
			ThreadFactory threadFactory = r -> new Thread(r, threadName);
			executorService.submit(new QueueConsumerScheduler(threadName), threadFactory);
		}
	}

	/**
	 * Submits the given SQL to the queue to be executed in a FIFO manner. The SQL must be a single {@code insert}, {@code update},
	 * {@code delete}, or stored procedure call.
	 * <p>
	 * The SQL may contain placeholders ("?"), in which case parameters must be specified to replace the placeholders.
	 * <p>
	 * Control immediately returns to the caller, likely before the statement begins execution.
	 * <p>
	 * Returns {@code true} if the queue was not already full so the statement was added to the queue, and {@code false} if the queue was
	 * already full so the statement was not added.
	 * 
	 * @param sql
	 *            the SQL statement to be executed. May contain placeholders in the form of "?".
	 * @param parameters
	 *            the parameter values to bind to the query, replacing the placeholders.
	 * @return {@code true} if the statement was actually added to the queue, {@code false} otherwise
	 */
	@Override
	public boolean submitUpdate(String sql, Object... parameters)
	{
		notEmpty(sql, "sql");
		notNull(parameters, "parameters");
		return queue.add(new SqlCall(sql, parameters, true));
	}

	/**
	 * Submits the given SQL to the queue to be executed in a FIFO manner. The SQL can be any arbitrary command(s) recognized by the
	 * database. For example, it may represent DDL or database configuration. It is allowed to consist of multiple SQL statements separated
	 * by ";", to be executed in their given order.
	 * <p>
	 * The SQL cannot contain placeholders ("?").
	 * <p>
	 * Control immediately returns to the caller, likely before the SQL begins execution.
	 * <p>
	 * Returns {@code true} if the queue was not already full so the SQL was added to the queue, and {@code false} if the queue was already
	 * full so the SQL was not added.
	 * 
	 * @param sql
	 *            the SQL to be executed
	 * @return {@code true} if the SQL was actually added to the queue, {@code false} otherwise
	 */
	@Override
	public boolean submitExecute(String sql)
	{
		notEmpty(sql, "sql");
		return queue.add(new SqlCall(sql, null, false));
	}

	private final class QueueConsumerScheduler implements Runnable
	{
		private final ThreadFactory threadFactory;

		public QueueConsumerScheduler(String parentThreadName)
		{
			this.threadFactory = r -> new Thread(r, parentThreadName + "_scheduler");
		}

		@Override
		public void run()
		{
			ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(threadFactory);
			scheduledExecutorService.scheduleAtFixedRate(new QueueConsumer(), initialDelay, callPeriod, callPeriodTimeUnit);
		}
	}

	private final class QueueConsumer implements Runnable
	{
		@Override
		public void run()
		{
			SqlCall nextQueueElement = queue.get();
			if (nextQueueElement != null)
			{
				String sql = nextQueueElement.sql;
				Object[] parameters = nextQueueElement.parameters;
				boolean isUpdate = nextQueueElement.isUpdate;
				if (isUpdate)
					sqlExecutor.update(sql, parameters);
				else
					sqlExecutor.execute(sql);
			}
		}
	}

	private static final class SqlCall
	{
		public final String sql;
		public final Object[] parameters;
		public final boolean isUpdate;

		public SqlCall(String sql, Object[] parameters, boolean isUpdate)
		{
			this.sql = sql;
			this.parameters = parameters;
			this.isUpdate = isUpdate;
		}
	}
}