package com.wrupple.muba.catalogs.domain.impl;

import com.wrupple.muba.catalogs.server.domain.LazyList;
import com.wrupple.muba.catalogs.server.service.TableMapper;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SparkLazyResults<T extends CatalogEntry> implements LazyList<T> {

    private final Encoder<T> entryEncoder;
    protected final TableMapper mapper;
    protected final DataFrame subject;
    protected List<T> collection;

    public SparkLazyResults(DataFrame subject, CatalogDescriptor catalogDescriptor, TableMapper mapper) {
        this.mapper = mapper;
        entryEncoder = Encoders.bean((Class<T>) catalogDescriptor.getClazz());
        this.subject = subject;
    }

    protected List<T> assertCollection() {
        if (collection == null) {
            collection = subject.as(entryEncoder).collectAsList();
        }
        return collection;
    }

    public DataFrame getSubject() {
        return subject;
    }

    @Override
    public int size() {
        return assertCollection().size();
    }

    @Override
    public boolean isEmpty() {
        return assertCollection().isEmpty();
    }

    @Override
    public ListIterator<T> listIterator() {
        return assertCollection().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return assertCollection().listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return assertCollection().subList(fromIndex, toIndex);
    }

    @Override
    public boolean contains(Object o) {
        return assertCollection().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return assertCollection().iterator();
    }

    @Override
    public Object[] toArray() {
        return assertCollection().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return assertCollection().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return assertCollection().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return assertCollection().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return assertCollection().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return assertCollection().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return assertCollection().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return assertCollection().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return assertCollection().retainAll(c);
    }

    @Override
    public void clear() {
        assertCollection().clear();
    }

    @Override
    public T get(int index) {
        return assertCollection().get(index);
    }

    @Override
    public T set(int index, T element) {
        return assertCollection().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        assertCollection().add(index, element);
    }

    @Override
    public T remove(int index) {
        return assertCollection().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return assertCollection().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return assertCollection().lastIndexOf(o);
    }
}
