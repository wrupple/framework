package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorService;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasResult;
import com.wrupple.muba.event.server.domain.impl.EvaluationContext;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import com.wrupple.muba.event.server.service.NaturalLanguageInterpret;
import org.apache.commons.chain.Context;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class EntrySynthesizerImpl implements EntrySynthesizer {
    protected static final Logger log = LogManager.getLogger(EntrySynthesizerImpl.class);


    private final String ancestorIdField;
    private final FieldAccessStrategy access;
    private final CatalogKeyServices keyDelgeate;
    private final CatalogDescriptorService catalogService;


    @Inject
    public EntrySynthesizerImpl(@Named("catalog.ancestorKeyField") String ancestorIdField,FieldAccessStrategy access, CatalogKeyServices keyDelgeate, CatalogDescriptorService catalogService, ServiceBus serviceBus) {
        this.pattern = pattern;
        this.ancestorIdField=ancestorIdField;
        this.access = access;
        this.keyDelgeate = keyDelgeate;
        this.catalogService = catalogService;
        this.serviceBus = serviceBus;
    }
	/*
	 * INHERITANCE
	 */

    @Override
    public String evaluateGreatAncestor(CatalogActionContext realContext, CatalogDescriptor result, CatalogActionRequest tempNotRealContract) throws Exception {
        if (result.getParent()==null) {
            return null;
        }else{
            // find great ancestor
            if(result.getRootAncestor()!=null){
                if(result.getRootAncestor()==result){
                    return null;
                }else{
                    return result.getRootAncestor().getDistinguishedName();
                }
            }else{

                CatalogDescriptor parentValue = result.getParentValue();
                if(parentValue==null){
                    catalogService.getDescriptorForKey(result.getParent(),realContext);
                    result.setParentValue(parentValue);
                }
                String greatAncestor = parentValue.getDistinguishedName();
                while (parentValue != null) {
                     greatAncestor = parentValue.getDistinguishedName();
                     if(parentValue.getParentValue()==null){
                        if(parentValue.getId()!=null){
                            parentValue.setParentValue(catalogService.getDescriptorForKey(result.getParent(),realContext));
                            if(parentValue.getParentValue()==null){
                                throw new IllegalStateException("unknown catalog key "+result.getParent());
                            }
                        }
                     }else {
                         parentValue.setParent(parentValue.getParentValue().getId());
                     }
                    parentValue = parentValue.getParentValue();
                }
                return greatAncestor;
            }
        }
    }



    @Override
    public CatalogEntry synthesizeCatalogObject(CatalogEntry source, CatalogDescriptor catalog,
                                                boolean excludeInherited, Instrospection instrospection, CatalogActionContext context) throws Exception {
        context.getNamespaceContext().setNamespace(context);
        CatalogEntry target = access.synthesize(catalog);

        addPropertyValues(source, target, catalog, excludeInherited, instrospection,
                (DistributiedLocalizedEntry) (source instanceof DistributiedLocalizedEntry ? source : null));
        context.getNamespaceContext().unsetNamespace(context);
        return target;
    }



    @Override
    public void processChildInheritance(CatalogEntry childEntity, CatalogDescriptor parentCatalogId, Object parentEntityId,
                                        CatalogActionContext context, CatalogDescriptor catalog, Instrospection instrospection) throws Exception {

        CatalogEntry parentEntity = context.triggerGet(parentCatalogId.getDistinguishedName(), parentEntityId);
        // add inherited values to child Entity
        addInheritedValuesToChild(parentEntity,childEntity,instrospection,catalog);

    }


    @Override
    public void addInheritedValuesToChild(CatalogEntry parentEntity, CatalogEntry childEntity, Instrospection instrospection,
                                          CatalogDescriptor childCatalog) throws Exception {


        if (parentEntity instanceof DistributiedLocalizedEntry) {
            DistributiedLocalizedEntry localized = (DistributiedLocalizedEntry) parentEntity;
            addPropertyValues(parentEntity, childEntity, childCatalog, false, instrospection, localized);
        } else {
            addPropertyValues(parentEntity, childEntity, childCatalog, false, instrospection, null);
        }

        access.setPropertyValue(this.ancestorIdField, childEntity, parentEntity.getId(), instrospection);


    }


    private void addPropertyValues(CatalogEntry source, CatalogEntry target, CatalogDescriptor catalog,
                                   boolean excludeInherited, Instrospection instrospection, DistributiedLocalizedEntry localizedObject) throws Exception {
        Collection<FieldDescriptor> fields = catalog.getFieldsValues();
        String fieldId;
        Object value;

        for (FieldDescriptor field : fields) {
            if (excludeInherited && keyDelgeate.isInheritedField(field,catalog)) {
                // ignore
            } else {
                fieldId = field.getFieldId();
                // ignore id fields
                if (!(CatalogEntry.ID_FIELD.equals(fieldId))) {
                        try{
                            value = access.getPropertyValue(field, source, localizedObject, instrospection);

                        }catch (Exception e){
                            value = null;
                        }
                        if (value != null) {
                            access.setPropertyValue(field, target, value, instrospection);
                        }


                }
            }
        }
    }


    @Override
    public Object getPropertyForeignKeyValue(CatalogDescriptor catalogDescriptor, FieldDescriptor field, CatalogEntry e,
                                             Instrospection instrospection) throws ReflectiveOperationException {
        String foreignKeyValuePropertyName = field.getFieldId()
                + (field.isMultiple() ? CatalogEntry.MULTIPLE_FOREIGN_KEY : CatalogEntry.FOREIGN_KEY);
        //check if property exists
        if(access.isReadableProperty(foreignKeyValuePropertyName,e, instrospection)){
           /*return valuedoReadProperty(
				field.getFieldId()
						+ (field.isMultiple() ? CatalogEntry.MULTIPLE_FOREIGN_KEY : CatalogEntry.FOREIGN_KEY),
				(FieldAccessSession) instrospection, e, true);*/
            return access.getPropertyValue(foreignKeyValuePropertyName, e, null, instrospection);
        }else{
            return null;

        }
    }

    @Override
    public String getDenormalizedFieldValue(CatalogEntry client, String fieldId, Instrospection instrospection,
                                            CatalogActionContext context) throws Exception {
        String catalogid = client.getCatalogType();
        CatalogDescriptor type = catalogService.getDescriptorForName(catalogid,context);
        FieldDescriptor field = type.getFieldDescriptor(fieldId);
        if (field == null) {
            throw new IllegalArgumentException("unknown field :" + fieldId);
        }

        return getDenormalizedFieldValue(field, instrospection, client, type);
    }

    @Override
    public String getDenormalizedFieldValue(FieldDescriptor field, Instrospection instrospection, CatalogEntry client,
                                            CatalogDescriptor type) throws ReflectiveOperationException {
        if (field.getDefaultValueOptions() != null && !field.getDefaultValueOptions().isEmpty()
                && field.getDataType() == CatalogEntry.INTEGER_DATA_TYPE) {
            Integer index = (Integer) access.getPropertyValue(field, client, null, instrospection);
            if (index != null) {
                return field.getDefaultValueOptions().get(index.intValue());
            }
        }
        return null;
    }

    @Override
    public Object getAllegedParentId(CatalogEntry result, Instrospection instrospection, FieldAccessStrategy access) throws ReflectiveOperationException {
        //return objectNativeInterface.valuedoReadProperty(this.ancestorIdField, instrospection, result, false);
        return access.getPropertyValue(ancestorIdField, result, null, instrospection);
    }

}
