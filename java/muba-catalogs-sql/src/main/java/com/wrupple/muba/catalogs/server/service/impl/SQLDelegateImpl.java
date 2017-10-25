package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.SQLDelegate;
import com.wrupple.muba.catalogs.server.service.TableMapper;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import org.apache.commons.collections.KeyValue;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class SQLDelegateImpl implements SQLDelegate {


    private final Character DELIMITER;

    @Inject
    public SQLDelegateImpl(@Named("catalog.sql.delimiter") Character delimiter) {
        DELIMITER = delimiter;
    }

    @Override
    public int buildQueryHeader(TableMapper tableNames, StringBuilder filterStringBuffer, int criteriaSize, CatalogDescriptor catalogDescriptor, CatalogActionContext context, List<KeyValue> partitions) {
        filterStringBuffer.append("SELECT * FROM ");
        filterStringBuffer.append(DELIMITER);
        tableNames.getTableNameForCatalog(catalogDescriptor, context, filterStringBuffer);
        filterStringBuffer.append(DELIMITER);

        //
        if (criteriaSize > 0 || (partitions != null && !partitions.isEmpty())) {
            filterStringBuffer.append(" WHERE ");
            if (partitions != null) {
                KeyValue kv;
                for (int i = 0; i < partitions.size(); i++) {
                    kv = partitions.get(i);
                    if (i > 0) {
                        filterStringBuffer.append(" AND ");
                    }
                    filterStringBuffer.append(DELIMITER);
                    filterStringBuffer.append(kv.getKey());
                    filterStringBuffer.append(DELIMITER);
                    filterStringBuffer.append("=");
                    filterStringBuffer.append(kv.getValue());
                }

                return partitions.size();
            }

        }
        // return the ammount of written criterias
        return 0;
    }
}
