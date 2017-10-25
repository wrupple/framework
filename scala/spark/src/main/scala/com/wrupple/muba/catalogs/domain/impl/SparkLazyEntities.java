package com.wrupple.muba.catalogs.domain.impl;

import com.wrupple.muba.catalogs.server.service.TableMapper;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.server.domain.impl.PersistentCatalogEntityImpl;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;

import java.util.*;
import java.util.stream.Collectors;

public class SparkLazyEntities extends SparkLazyResults<HasAccesablePropertyValues> {


    private final CatalogDescriptor catalogDescriptor;

    public SparkLazyEntities(DataFrame subject, CatalogDescriptor catalogDescriptor, TableMapper mapper) {
        super(subject, catalogDescriptor, mapper);
        this.catalogDescriptor = catalogDescriptor;
    }

    @Override
    protected List<HasAccesablePropertyValues> assertCollection() {
//FIXME CUSTOM ENCODER
        //https://jaceklaskowski.gitbooks.io/mastering-apache-spark/spark-sql-Encoder.html
        //https://stackoverflow.com/questions/43113585/create-a-dataframe-from-a-hashmap-with-keys-as-column-names-and-values-as-rows-i

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


        return subject.collectAsList().
                stream().
                map(row -> row2Map(row, fields, columnSize, columnIndexes)).
                map(map -> {
                    return new PersistentCatalogEntityImpl(catalogDescriptor, map);
                }).collect(Collectors.toList());

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
}
