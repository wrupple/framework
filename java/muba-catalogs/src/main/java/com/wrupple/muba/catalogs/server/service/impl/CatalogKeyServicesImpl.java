package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogRelation;
import com.wrupple.muba.event.domain.PersistentImageMetadata;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.event.domain.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Singleton
public class CatalogKeyServicesImpl implements CatalogKeyServices {
    protected static final Logger log = LogManager.getLogger(CatalogKeyServicesImpl.class);


    @Inject
    public CatalogKeyServicesImpl(){

    }


	/*
	 * KEY SERVICES
	 */
    @Override
    public boolean isJoinableValueField(FieldDescriptor field) {
        //data that lives on another layer
        return (field.getCatalog() != null && (field.isEphemeral() || field.isKey() && !isFileField(field)));
    }


    public boolean isFileField(FieldDescriptor field) {
        String catalog = field.getCatalog();
        return (field.isKey() && catalog != null
                && (catalog.equals(PersistentImageMetadata.CATALOG) ))/* FIXME or contained in format dictionary || catalog.equals(WrupleSVGDocument.CATALOG_TIMELINE)
				|| catalog.equals(WruppleFileMetadata.CATALOG_TIMELINE) || catalog.equals(WruppleAudioMetadata.CATALOG_TIMELINE)
				|| catalog.equals(WruppleVideoMetadata.CATALOG_TIMELINE)))*/;
    }

    @Override
    public Object encodeClientPrimaryKeyFieldValue(Object rawValue, FieldDescriptor field, CatalogDescriptor catalog) {
        if (rawValue == null) {
            return null;
        }
        if (field != null && field.isMultiple()) {
            List<Object> rawCollection = (List<Object>) rawValue;
            List<String> encodedValues = new ArrayList<String>(rawCollection.size());
            boolean wasTested = false;
            boolean expectLongValues = false;
            for (Object rawCollectionValue : rawCollection) {
                if (!wasTested) {
                    wasTested = true;
                    expectLongValues = rawCollectionValue instanceof Long;
                }
                if (expectLongValues) {
                    encodedValues.add(encodeLongKey((Long) rawCollectionValue));
                } else {
                    encodedValues.add(String.valueOf(rawCollectionValue));
                }
            }
            return encodedValues;
        } else {
            if (field == null || field.isKey() || field.getDataType() == CatalogEntry.INTEGER_DATA_TYPE) {
                try {
                    return encodeLongKey((Long) rawValue);
                } catch (Exception e) {
                    System.err.println("Unable to encode numeric key most likely already a String");
                    return String.valueOf(rawValue);
                }

            } else {
                return rawValue;
            }
        }
    }

    @Override
    public List<Object> decodePrimaryKeyFilters(List<Object> values) {
        if (values != null && !values.isEmpty()) {
            try {
                List<Object> ids = new ArrayList<Object>(values.size());
                String raw;
                for (int i = 0; i < values.size(); i++) {
                    raw = (String) values.get(i);
                    ids.add(decodePrimaryKeyToken(raw));
                }
                return ids;
            } catch (NumberFormatException e) {
                return values;
            } catch (ClassCastException e) {
                return values;
            }
        }
        return values;
    }

    @Override
    public boolean qualifiesForEncoding(FieldDescriptor field, CatalogDescriptor catalog) {
        return field.isKey();
    }

    @Override
    public boolean isInheritedField(FieldDescriptor field, CatalogDescriptor owner) {
        if(owner.getParent()==null){
            return false;
        }else if(owner.getKeyField().equals(field.getFieldId())){
            return false;
        }else{
            CatalogDescriptor parent = owner.getParentValue();
            return parent.getFieldDescriptor(field.getFieldId())!=null;
        }
    }

    @Override
    public Object decodePrimaryKeyToken(Object targetEntryId) {
        if (targetEntryId == null) {
            return null;
        }

        if (targetEntryId instanceof Long) {
            return (Long) targetEntryId;
        } else {
            String rawKey = (String) targetEntryId;
            if(rawKey.charAt(0)==':'&&rawKey.length()>1){
                return decodeKey( ((String) targetEntryId).substring(1));
            }else{
                return rawKey;
            }

        }
    }

    private String encodeLongKey(Long key) {
        return ":"+Long.toString(key, 36);
    }

    private Long decodeKey(String key) {
        return Long.parseLong(key, 36);
    }

    /**
     * if (StringUtils.isEmpty(str)) { return false; } for (int i = 0; i <
     * str.length(); i++) { if (!Character.isDigit(str.charAt(i))) { return
     * false; } }
     */
    @Override
    public boolean isPrimaryKey(String vanityId) {

        // lets try, shall we?
        return true;
    }

    public List<CatalogRelation> getJoins(CatalogActionContext contexto, Object clientSide, CatalogDescriptor descriptor,
                                          String[][] customJoins, Object domain, String host) throws Exception {
        // joinable fields

        Collection<FieldDescriptor> thisCatalogFields = descriptor.getFieldsValues();
        List<FieldDescriptor> thisCatalogJoinableFields = new ArrayList<FieldDescriptor>();
        String foreignCatalogId;
        String foreignField;
        String localField;

        // GATHER JOINABLE FIELDS

        for (FieldDescriptor field : thisCatalogFields) {
            if (isJoinableValueField(field)) {
                thisCatalogJoinableFields.add(field);
            }
        }
        int size = thisCatalogJoinableFields.size();
        int customJoinsSize = customJoins == null ? 0 : customJoins.length;
        // Generate join sentence
        List<CatalogRelation> allJoinSentences = new ArrayList<>(size + customJoinsSize);
        FieldDescriptor currentJoinableField;
        int i;
        CatalogDescriptor foreign;
        for (i = 0; i < size; i++) {
            currentJoinableField = thisCatalogJoinableFields.get(i);
            foreignCatalogId = currentJoinableField.getCatalog();
            localField = null;
            foreignField = null;
            if (contexto == null) {
                throw new RuntimeException("not implemented");
                // foreign =clientSide.loadFromCache(host, (String)domain,
                // foreignCatalogId);
            } else {
                //TODO dismiss join from statement if result is already joined (by storage unit or some magic)
                log.trace("[{} requires metadata for foreign catalog] {}",descriptor.getDistinguishedName(),foreignCatalogId);
                foreign=null;
                if(CatalogDescriptor.CATALOG_ID.equals(descriptor.getDistinguishedName())  ){
                    //si estoy actualmente armando descriptores
                    if(CatalogDescriptor.CATALOG_ID.equals(foreignCatalogId)){

                        foreign= descriptor;
                    }

                }
                if(foreign==null) {
                    foreign = contexto.getDescriptorForName(foreignCatalogId);
                }

            }

            if (currentJoinableField.isKey()) {
                localField = currentJoinableField.getFieldId();
                foreignField = getCatalogKeyFieldId(foreign);
            } else if (currentJoinableField.isEphemeral()) {
                localField = descriptor.getKeyField();
                foreignField = getFieldWithForeignType(foreign, descriptor.getDistinguishedName());
            }
            if (localField == null) {
                localField = CatalogKey.ID_FIELD;
            }
            allJoinSentences.add(new CatalogRelation (foreignCatalogId, foreignField, localField ));
        }
        if (customJoins != null) {
            for (String[] customJoinStatement : customJoins) {
                allJoinSentences.add(i,(new CatalogRelation (customJoinStatement[0], customJoinStatement[1], grabLocalJoinField(customJoinStatement,customJoinStatement[2]))));
                i++;
            }
        }
        return allJoinSentences;
    }


    private String grabLocalJoinField(String[] join, String foreignField) {
        String localField;
        if (join.length<2) {
            // natural join (Using field)
            localField = foreignField;
        } else {
            // standard join (On field==foreignField)
            localField = join[2];
        }
        return localField;
    }

    public String getFieldWithForeignType(CatalogDescriptor foreignDescriptor, String foreignType) {

        Collection<FieldDescriptor> fields = foreignDescriptor.getFieldsValues();
        String fieldsForeignCatalog;
        for (FieldDescriptor field : fields) {
            fieldsForeignCatalog = field.getCatalog();
            if (foreignType.equals(fieldsForeignCatalog)) {
                return field.getFieldId();
            }
        }
        throw new IllegalArgumentException(
                "No fields in " + foreignDescriptor.getDistinguishedName() + " point to " + foreignType);

    }

    @Override
    public boolean isFieldOwnedBy(FieldDescriptor fieldDescriptor, CatalogDescriptor catalogDescriptor) {
        //a field is owned by a catalog if it is present in the catalog and not the parent, or is consolidated

        return  catalogDescriptor.getFieldDescriptor(fieldDescriptor.getFieldId())!=null && (catalogDescriptor.getConsolidated() || (catalogDescriptor.getParent()==null||catalogDescriptor.getParentValue().getFieldDescriptor(fieldDescriptor.getFieldId())==null));

    }

    private String getCatalogKeyFieldId(CatalogDescriptor descriptor) {
        if (descriptor == null) {
            return CatalogEntry.ID_FIELD;
        } else {
            String keyField = descriptor.getKeyField();
            if (keyField == null) {
                return CatalogEntry.ID_FIELD;
            } else {
                return keyField;
            }
        }
    }

}
