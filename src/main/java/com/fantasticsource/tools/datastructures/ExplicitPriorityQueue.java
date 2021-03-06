package com.fantasticsource.tools.datastructures;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class ExplicitPriorityQueue<T>
{
    private PriorityQueue<Entry> queue;

    public ExplicitPriorityQueue(int initialSize)
    {
        queue = new PriorityQueue<>(Math.max(1, initialSize));
    }

    public ExplicitPriorityQueue()
    {
        this(11);
    }

    public void add(T object, double priority)
    {
        queue.add(new Entry(object, priority));
    }

    public boolean removeAll(T object)
    {
        return queue.removeIf(e -> e.object == object);
    }

    public boolean removeIf(Predicate<Entry> predicate)
    {
        return queue.removeIf(predicate);
    }

    public T peek()
    {
        Entry<T> result = queue.peek();
        return result == null ? null : result.object;
    }

    public double peekPriority()
    {
        Entry<T> result = queue.peek();
        return result == null ? Double.NaN : result.priority;
    }

    public T poll()
    {
        Entry<T> result = queue.poll();
        return result == null ? null : result.object;
    }

    public Object[] toArray()
    {
        Object[] result = new Object[queue.size()];
        int i = 0;
        for (Entry entry : queue.toArray(new Entry[0])) result[i++] = entry.object;

        return result;
    }

    public T[] toArray(T[] a)
    {
        final int size = this.size();
        if (a.length < size)
        {
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(toArray(), size, a.getClass());
        }

        System.arraycopy(toArray(), 0, a, 0, size);
        return a;
    }

    public Entry[] toEntryArray()
    {
        return queue.toArray(new Entry[0]);
    }

    public Entry<T>[] toEntryArray(Entry<T>[] a)
    {
        final int size = this.size();
        if (a.length < size)
        {
            // Make a new array of a's runtime type, but my contents:
            return (Entry<T>[]) Arrays.copyOf(toEntryArray(), size, a.getClass());
        }

        System.arraycopy(toEntryArray(), 0, a, 0, size);
        return a;
    }

    public void clear()
    {
        queue.clear();
    }

    public int size()
    {
        return queue.size();
    }

    public boolean isEmpty()
    {
        return queue.isEmpty();
    }

    public ExplicitPriorityQueue<T> clone()
    {
        ExplicitPriorityQueue<T> clone = new ExplicitPriorityQueue<>(size());
        Entry<T>[] array = queue.toArray(new Entry[size()]);
        for (Entry<T> entry : array) clone.add(entry);
        return clone;
    }

    private void add(Entry<T> entry)
    {
        queue.add(entry);
    }


    public class Entry<A> implements Comparable<Entry>
    {
        public A object;
        public double priority;

        Entry(A object, double priority)
        {
            this.object = object;
            this.priority = priority;
        }

        @Override
        public int compareTo(Entry o)
        {
            return Double.compare(priority, o.priority);
        }
    }
}
