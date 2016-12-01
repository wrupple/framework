package com.wrupple.muba.desktop.domain.overlay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class JsArrayList<T extends JavaScriptObject> extends JavaScriptObject {
    
    public final static <T extends JavaScriptObject> List<T> arrayAsList(JsArray<T> array){
    	if(array==null){
    		return null;
    	}
    	List<T> regreso = new ListImpl<T>(array);
		return regreso ;
    }
    
    public final static List<String> arrayAsListOfString(JsArrayString array){
    	if(array==null){
    		return null;
    	}
    	List<String> regreso = new ArrayList<String>(array.length());
    	for(int i = 0 ; i< array.length(); i++){
    		regreso.add(array.get(i));
    	}
    	return regreso;
    }
    
	public static <T extends JavaScriptObject> JsArray<T> unwrap(List<T> list) {
		if(list instanceof ListImpl){
			return ((ListImpl) list).jsArray.cast();
		}else{
			throw new IllegalArgumentException("This is not a JsArrayList");
		}
	}

    protected JsArrayList() {
    }
    
    public final java.util.List<T> asList() {
        return new ListImpl<T>(this);
    }

    public final native T get(int index) /*-{
        return this[index];
    }-*/;

    public final native int size() /*-{
        return this.length;
    }-*/;

    public final boolean add(T value) {
        add(size(), value);
        return true;
    }
    
    public final native void add(int index, T value) /*-{
        this[index] = value;
    }-*/;

    public final boolean addAll(JsArrayList<? extends T> a) {
        return addAll(a.asList());
    }
    
    public final boolean addAll(Collection<? extends T> c) {
        return addAll(size(), c);
    }

    public final boolean addAll(int index, Collection<? extends T> c) {
        Iterator<? extends T> iter = c.iterator();
        boolean changed = iter.hasNext();
        while (iter.hasNext()) {
            add(index++, iter.next());
        }
        return changed;
    }

    public final native void clear() /*-{
        this.length = 0;
    }-*/;

    public final boolean contains(Object o) {
        for (int i=0, length=size(); i < length; i++) {
            T element = get(i);
            if (o == null ? element == null : o.equals(element)) {
                return true;
            }
        }
        return false;
    }

    public final boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    public final int indexOf(Object o) {
        for (int i=0, length=size(); i < length; i++) {
            T element = get(i);
            if (o == null ? element == null : o.equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public final boolean isEmpty() {
        return size() == 0;
    }

    public final Iterator<T> iterator() {
        return new IteratorImpl<T>(this);
    }

    public final int lastIndexOf(Object o) {
        for (int i=size() - 1; i >= 0; i--) {
            T element = get(i);
            if (o == null ? element == null : o.equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public final ListIterator<T> listIterator() {
        return new ListIteratorImpl<T>(this);
    }

    public final ListIterator<T> listIterator(int index) {
        return new ListIteratorImpl<T>(this, index);
    }

    public final boolean remove(Object o) {
        int i = indexOf(o);
        if (i == -1) {
            return false;
        }
        remove(i);
        return true;
    }

    public final T remove(int index) {
        T previous = get(index);
        removeRangeImpl(index, 1);
        return previous;
    }
    
    private final native void removeRangeImpl(int index, int size) /*-{
        this.splice(index, size);
    }-*/;

    public final boolean removeAll(Collection<?> c) {
        Iterator<?> iter = c.iterator();
        boolean changed = false;
        while (iter.hasNext()) {
            if (remove(iter.next())) {
                changed = true;
            }
        }
        return changed;
    }

    public final boolean retainAll(Collection<?> c) {
        Iterator<?> iter = iterator();
        boolean changed = false;
        while (iter.hasNext()) {
            if (!c.contains(iter.next())) {
                iter.remove();
                changed = true;
            }
        }
        return changed;
    }

    public final T set(int index, T element) {
        T previous = get(index);
        setImpl(index, element);
        return previous;
    }
    
    private final native void setImpl(int index, T element) /*-{
        this[index] = element;
    }-*/;

    public final List<T> subList(int fromIndex, int toIndex) {
    	List<T> regreso = new ArrayList<T>(toIndex-fromIndex);
    	for(int i = fromIndex ; i<toIndex;i++){
    		regreso.add(get(i));
    	}
    	return regreso;
    }

    public final Object[] toArray() {
        return toArray(new Object[size()]);
    }

    @SuppressWarnings("unchecked")
    public final <T2> T2[] toArray(T2[] a) {
        int size= size();
        if (a.length < size) {
            a = (T2[]) new Object[size];
        }
        for (int i = 0; i < size; ++i) {
            a[i] = (T2) get(i);
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }
    
    private static class ListImpl<T extends JavaScriptObject> implements List<T> {
        private final JsArrayList<T> jsArray;
        
        ListImpl(JsArrayList<T> jsArray) {
            this.jsArray = jsArray;
        }
        
        @SuppressWarnings("unused")
		ListImpl(JsArray<T> jsArray) {
            this.jsArray = jsArray.cast();
        }

        public boolean add(T e) {
            return jsArray.add(e);
        }

        public void add(int index, T element) {
            jsArray.add(index, element);
        }

        public boolean addAll(Collection<? extends T> c) {
            return jsArray.addAll(c);
        }

        public boolean addAll(int index, Collection<? extends T> c) {
            return jsArray.addAll(index, c);
        }

        public void clear() {
            jsArray.clear();
        }

        public boolean contains(Object o) {
            return jsArray.contains(o);
        }

        public boolean containsAll(Collection<?> c) {
            return jsArray.containsAll(c);
        }

        public T get(int index) {
            return jsArray.get(index);
        }

        public int indexOf(Object o) {
            return jsArray.indexOf(o);
        }

        public boolean isEmpty() {
            return jsArray.isEmpty();
        }

        public Iterator<T> iterator() {
            return jsArray.iterator();
        }

        public int lastIndexOf(Object o) {
            return jsArray.lastIndexOf(o);
        }

        public ListIterator<T> listIterator() {
            return jsArray.listIterator();
        }

        public ListIterator<T> listIterator(int index) {
            return jsArray.listIterator(index);
        }

        public boolean remove(Object o) {
            return jsArray.remove(o);
        }

        public T remove(int index) {
            return jsArray.remove(index);
        }

        public boolean removeAll(Collection<?> c) {
            return jsArray.removeAll(c);
        }

        public boolean retainAll(Collection<?> c) {
            return jsArray.retainAll(c);
        }

        public T set(int index, T element) {
            return jsArray.set(index, element);
        }

        public int size() {
            return jsArray.size();
        }

        public List<T> subList(int fromIndex, int toIndex) {
            return jsArray.subList(fromIndex, toIndex);
        }

        public Object[] toArray() {
            return jsArray.toArray();
        }

        public <T2> T2[] toArray(T2[] a) {
            return jsArray.toArray(a);
        }
    }

    /**
     * Copied (almost) verbatim from super/com/google/gwt/emul/java/util/AbstractList.java
     * 
     * Changes involve making it a static class (JSO restriction)
     */
    private static class IteratorImpl<T extends JavaScriptObject> implements Iterator<T> {
        /*
         * i is the index of the item that will be returned on the next call to
         * next() last is the index of the item that was returned on the previous
         * call to next() or previous (for ListIterator), -1 if no such item exists.
         */
        protected int i = 0, last = -1;
        
        protected final JsArrayList<T> jsArray;
        
        public IteratorImpl(JsArrayList<T> jsArray) {
            this.jsArray = jsArray;
        }

        public boolean hasNext() {
            return i < jsArray.size();
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return jsArray.get(last = i++);
        }

        public void remove() {
            if (last < 0) {
                throw new IllegalStateException();
            }
            jsArray.remove(last);
            i = last;
            last = -1;
        }
    }

    /**
     * Copied (almost) verbatim from super/com/google/gwt/emul/java/util/AbstractList.java
     * 
     * Changes involve making it a static class (JSO restriction)
     */
    private static class ListIteratorImpl<T extends JavaScriptObject> extends IteratorImpl<T> implements
    ListIterator<T> {
        /*
         * i is the index of the item that will be returned on the next call to
         * next() last is the index of the item that was returned on the previous
         * call to next() or previous (for ListIterator), -1 if no such item exists.
         */

        ListIteratorImpl(JsArrayList<T> jsArray) {
            super(jsArray);
        }

        ListIteratorImpl(JsArrayList<T> jsArray, int start) {
            super(jsArray);
            int size = jsArray.size();
            if (start < 0 || start > size) {
                throw new IndexOutOfBoundsException("Index: " + start + ", Size: " + size);
            }
            i = start;
        }

        public void add(T o) {
            jsArray.add(i++, o);
            last = -1;
        }

        public boolean hasPrevious() {
            return i > 0;
        }

        public int nextIndex() {
            return i;
        }

        public T previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            return jsArray.get(last = --i);
        }

        public int previousIndex() {
            return i - 1;
        }

        public void set(T o) {
            if (last == -1) {
                throw new IllegalStateException();
            }
            jsArray.set(last, o);
        }
    }


}
