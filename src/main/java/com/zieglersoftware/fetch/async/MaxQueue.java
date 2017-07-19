package com.zieglersoftware.fetch.async;

import static com.zieglersoftware.assertions.Assertions.notLess;
import static com.zieglersoftware.assertions.Assertions.notNull;

/**
 * Package-private class that represents a queue of elements of type {@code E}.
 * <p>
 * The queue has a permanently fixed maximum size. An attempt to add an element to the queue when it is already full will result in the
 * element not being added and the {@code add} method returning {@code false}.
 * <p>
 * {@code MaxQueue} is thread-safe.
 */
final class MaxQueue<E>
{
	private final Object[] array;
	private boolean empty;
	private boolean full;
	private int nextGetIndex;
	private int nextAddIndex;
	private Object lock = new Object();

	/**
	 * Constructs a new, empty {@code EQueue} with the given max number of elements.
	 * <p>
	 * {@code maxNumberOfElements} must be at least 1.
	 */
	public MaxQueue(int maxNumberOfElements)
	{
		notLess(maxNumberOfElements, 1, "maxNumberOfElements");
		this.array = new Object[maxNumberOfElements];
		empty = true;
		full = false;
		this.nextGetIndex = 0;
		this.nextAddIndex = 0;
	}

	/**
	 * Adds the given {@code E} to this queue if this queue is not already full.
	 * <p>
	 * Returns {@code true} if this queue was not full so the element was added, and returns {@code false} if this queue was full so the
	 * element was not added.
	 * <p>
	 * {@code e} must not be {@code null}.
	 */
	public boolean add(E e)
	{
		notNull(e, "e");
		synchronized (lock)
		{
			if (full)
				return false;
			empty = false;
			array[nextAddIndex] = e;
			nextAddIndex--;
			if (nextAddIndex == -1)
				nextAddIndex = array.length - 1;
			if (nextAddIndex == nextGetIndex)
				full = true;
			return true;
		}
	}

	/**
	 * Returns the element at the head of this queue. If the queue is empty, {@code null} will be returned.
	 */
	public E get()
	{
		synchronized (lock)
		{
			if (empty)
				return null;
			full = false;
			@SuppressWarnings("unchecked")
			E nextElement = (E) array[nextGetIndex];
			array[nextGetIndex] = null;
			nextGetIndex--;
			if (nextGetIndex == -1)
				nextGetIndex = array.length - 1;
			if (nextGetIndex == nextAddIndex)
				empty = true;
			return nextElement;
		}
	}

	/**
	 * Returns the number of elements this queue can contain.
	 */
	public int maxNumberOfElements()
	{
		return array.length;
	}

	/**
	 * Returns the number of elements this queue currently contains.
	 */
	public int currentNumberOfElements()
	{
		if (empty)
			return 0;
		if (full)
			return array.length;
		if (nextGetIndex > nextAddIndex)
			return nextGetIndex - nextAddIndex;
		return nextGetIndex - nextAddIndex + array.length;
	}

	/**
	 * Returns {@code true} if this queue contains no elements.
	 */
	public boolean empty()
	{
		return empty;
	}

	/**
	 * Returns {@code true} if this queue contains its maximum number of elements.
	 */
	public boolean full()
	{
		return full;
	}

	/**
	 * For testing only.
	 */
	protected void checkRep()
	{
		synchronized (lock)
		{
			if (array.length == 0)
				throw new IllegalStateException("Array length is 0");
			if (nextGetIndex < 0)
				throw new IllegalStateException("nextGetIndex is less than 0, " + nextGetIndex);
			if (nextAddIndex < 0)
				throw new IllegalStateException("nextAddIndex is less than 0, " + nextAddIndex);
			if (nextGetIndex >= array.length)
				throw new IllegalStateException("nextGetIndex " + nextGetIndex + " is greater or equal to array length " + array.length);
			if (nextAddIndex >= array.length)
				throw new IllegalStateException("nextAddIndex " + nextAddIndex + " is greater or equal to array length " + array.length);
			if (empty && full)
				throw new IllegalStateException("empty and full are simultaneously true");
			if (empty && nextGetIndex != nextAddIndex)
				throw new IllegalStateException("Empty is true when nextGetIndex does not equal nextAddIndex. " +
					"nextGetIndex=" + nextGetIndex + ", nextAddIndex=" + nextAddIndex);
			if (full && nextGetIndex != nextAddIndex)
				throw new IllegalStateException("Full is true when nextGetIndex does not equal nextAddIndex. " +
					"nextGetIndex=" + nextGetIndex + ", nextAddIndex=" + nextAddIndex);
			if (!empty && !full && nextGetIndex == nextAddIndex)
				throw new IllegalStateException("Empty and full are both false when nextGetIndex equals nextAddIndex. " +
					"nextGetIndex=" + nextGetIndex + ", nextAddIndex=" + nextAddIndex);
			for (int i = 0; i < array.length; i++)
			{
				boolean shouldContainElement = false;
				if (full)
					shouldContainElement = true;
				else if (nextGetIndex > nextAddIndex && (i <= nextGetIndex && i > nextAddIndex))
					shouldContainElement = true;
				else if (nextGetIndex < nextAddIndex && (i <= nextGetIndex || i > nextAddIndex))
					shouldContainElement = true;
				@SuppressWarnings("unchecked")
				E element = (E) array[i];
				if (shouldContainElement && element == null)
					throw new IllegalStateException("Element at index " + i + " should not be null. " +
						"Array length=" + array.length + ", nextGetIndex=" + nextGetIndex + ", nextAddIndex=" + nextAddIndex);
				if (!shouldContainElement && element != null)
					throw new IllegalStateException("Element at index " + i + " should be null. " +
						"Array length=" + array.length + ", nextGetIndex=" + nextGetIndex + ", nextAddIndex=" + nextAddIndex);
			}
		}
	}
}