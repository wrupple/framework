package com.wrupple.muba.catalogs.domain.impl;

import com.wrupple.muba.catalogs.server.domain.LazyList;
import com.wrupple.muba.catalogs.server.service.TableMapper;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.server.domain.impl.PersistentCatalogEntityImpl;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;

import java.util.*;
import java.util.stream.Collectors;

public class SparkLazyList<T extends CatalogEntry> implements LazyList<T> {

    private final Encoder<T> entryEncoder;
    private final CatalogDescriptor catalogDescriptor;
    private final TableMapper mapper;
    private List<T> collection;
    private DataFrame subject;

    public SparkLazyList(CatalogDescriptor catalogDescriptor, TableMapper mapper) {
        this.catalogDescriptor = catalogDescriptor;
        this.mapper = mapper;
        if (HasAccesablePropertyValues.class.equals(catalogDescriptor.getClazz())) {
            //FIXME CUSTOM ENCODER
            //https://jaceklaskowski.gitbooks.io/mastering-apache-spark/spark-sql-Encoder.html
            //https://stackoverflow.com/questions/43113585/create-a-dataframe-from-a-hashmap-with-keys-as-column-names-and-values-as-rows-i
            entryEncoder = null;
        } else {
            entryEncoder = Encoders.bean((Class<T>) catalogDescriptor.getClazz());
        }
    }

    private List<T> assertCollection() {
        if (collection == null) {
            if (entryEncoder == null) {

                List<FieldDescriptor> fields = new ArrayList(catalogDescriptor.getFieldsValues());
                String[] physicalNames = subject.schema().fieldNames();
                int[] columnIndexes = new int[fields.size()];
                String physicalNamee;
                FieldDescriptor fieldd;
                for (int i = 0; i < fields.size(); i++) {
                    fieldd = fields.get(i);
                    physicalNamee = mapper.getColumnForField(null, catalogDescriptor, fieldd, false);
                    for (int j = 0; j < physicalNames.length; j++) {
                        if (physicalNamee.equals(physicalNames[i])) {
                            columnIndexes[i] = j;
                        }
                    }
                }


                final int columnSize = fields.size();


                collection = subject.collectAsList().
                        stream().
                        map(row -> row2Map(row, fields, columnSize, columnIndexes)).
                        map(map -> {
                            return new PersistentCatalogEntityImpl(catalogDescriptor, map);
                        }).collect(Collectors.toList());
            } else {
                collection = subject.as(entryEncoder).collectAsList();
            }
        }
        return collection;
    }

    private Map<String, Object> row2Map(Row row, List<FieldDescriptor> fields, int columnSize, int[] columnIndexes) {
        Map<String, Object> inner = new HashMap<>(columnSize);
        String physicalName;
        Object value;
        FieldDescriptor field;
        for (int i = 0; i < fields.size(); i++) {
            value = null;
            field = fields.get(i);
            physicalName = mapper.getColumnForField(null, catalogDescriptor, field, false);
            switch (field.getDataType()) {
                case CatalogEntry.BOOLEAN_DATA_TYPE:
                    value = row.getBoolean(columnIndexes[i]);
                    break;
                case CatalogEntry.DATE_DATA_TYPE:
                    value = new Date(row.getTimestamp(columnIndexes[i]).getTime());
                    break;
                case CatalogEntry.STRING_DATA_TYPE:
                    value = row.getString(columnIndexes[i]);
                    break;
                case CatalogEntry.INTEGER_DATA_TYPE:
                    value = row.getLong(columnIndexes[i]);
                    break;
                case CatalogEntry.NUMERIC_DATA_TYPE:
                    value = row.getDouble(columnIndexes[i]);
                    break;
            }

            inner.put(physicalName, value);
        }
        return inner;
    }

    public DataFrame getSubject() {
        return subject;
    }

    @Override
    public void setSubject(Object o) {
        collection = null;
        this.subject = ((DataFrame) o);
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
