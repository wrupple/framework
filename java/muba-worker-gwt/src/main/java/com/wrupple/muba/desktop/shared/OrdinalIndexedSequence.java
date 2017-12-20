package com.wrupple.muba.desktop.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OrdinalIndexedSequence<T> implements Sequence<T> {

	public class SequenceIterator implements Iterator<T> {

		private int currentIndex = 0;

		@Override
		public boolean hasNext() {
			return currentIndex < list.size();
		}

		@Override
		public T next() {
			T next = list.get(currentIndex);
			currentIndex++;
			return next;
		}

		@Override
		public void remove() {
			list.remove(currentIndex);
		}

		/**
		 * the current index of the iterator, which is the index of the next
		 * entry to be returned
		 * 
		 * @return
		 */
		public int currentIndex() {
			return currentIndex;
		}

		public List<T> getRemainingElements() {
			return list.subList(currentIndex, list.size() - 1);

		}

	}

	private List<T> list;

	protected OrdinalIndexedSequence() {
		super();
		this.list = new ArrayList<T>();
	}

	@Override
	public SequenceIterator iterator() {
		return new SequenceIterator();
	}

	@Override
	public boolean add(T arg0) {
		return list.add(arg0);
	}

	@Override
	public boolean addAll(Collection<? extends T> arg0) {
		return addAll(arg0);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return list.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return list.containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean remove(Object arg0) {
		return list.remove(arg0);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return list.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return list.retainAll(arg0);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <A> A[] toArray(A[] arg0) {
		return list.toArray(arg0);
	}

}
