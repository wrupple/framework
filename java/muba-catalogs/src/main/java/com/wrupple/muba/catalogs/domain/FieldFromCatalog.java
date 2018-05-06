package com.wrupple.muba.catalogs.domain;

/**
 * Created by japi on 5/05/18.
 */
public class FieldFromCatalog {
    final String catalog;
    final String field;

    public FieldFromCatalog(String catalog, String field) {
        super();
        this.catalog = catalog;
        this.field = field;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
        result = prime * result + ((field == null) ? 0 : field.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        FieldFromCatalog other = (FieldFromCatalog) obj;
        if (catalog == null) {
            if (other.catalog != null)
                return false;
        } else if (!catalog.equals(other.catalog))
            return false;
        if (field == null) {
            if (other.field != null)
                return false;
        } else if (!field.equals(other.field))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "catalog=" + catalog + ", field=" + field;
    }

}
