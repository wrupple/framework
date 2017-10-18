package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class EntrySynthesizerImpl implements EntrySynthesizer {
    protected static final Logger log = LoggerFactory.getLogger(EntrySynthesizerImpl.class);

    private final Pattern pattern;

    private final String TOKEN_SPLITTER;
    private final String ancestorIdField;
    private final FieldAccessStrategy access;


    @Inject
    public EntrySynthesizerImpl(@Named("catalog.ancestorKeyField") String ancestorIdField, @Named("template.token.splitter") String splitter /* "\\." */, @Named("template.pattern") Pattern pattern, /** "\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}" */FieldAccessStrategy access) {
        this.pattern = pattern;
        this.TOKEN_SPLITTER = splitter;
        this.ancestorIdField=ancestorIdField;
        this.access = access;
    }
	/*
	 * INHERITANCE
	 */



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

    }


    private void addPropertyValues(CatalogEntry source, CatalogEntry target, CatalogDescriptor catalog,
                                   boolean excludeInherited, Instrospection instrospection, DistributiedLocalizedEntry localizedObject) throws Exception {
        Collection<FieldDescriptor> fields = catalog.getFieldsValues();
        String fieldId;
        Object value;

        for (FieldDescriptor field : fields) {
            if (excludeInherited && field.isInherited()) {
                // ignore
            } else {
                fieldId = field.getFieldId();
                // ignore id fields
                if (!(CatalogEntry.ID_FIELD.equals(fieldId))) {

                    value = access.getPropertyValue(field, source, localizedObject, instrospection);
                    if (value != null) {
                        access.setPropertyValue(field, target, value, instrospection);
                    }
                }
            }
        }
    }


    @Override
    public CatalogEntry synthesizeChildEntity(Object parentEntityId, CatalogEntry o, Instrospection instrospection,
                                              CatalogDescriptor catalog, CatalogActionContext context) throws Exception {
        CatalogEntry childEntity = synthesizeCatalogObject(o, catalog, true, instrospection, context);
        access.setPropertyValue(this.ancestorIdField, childEntity, parentEntityId, instrospection);
        return childEntity;
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
        CatalogDescriptor type = context.getDescriptorForName(catalogid);
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
    public void evalTemplate(String template, PrintWriter out, String language, CatalogActionContext context) {
        log.trace("[WRITE DOCUMENT]");
        Matcher matcher = pattern.matcher(template);
        if (matcher.find()) {
            matcher.reset();
            int start;
            int end;
            int currentIndex = 0;
            String rawToken;
            while (matcher.find()) {
                start = matcher.start();
                if (start > 0 && template.charAt(start) != '\\') {
                    end = matcher.end();
                    out.println(template.substring(currentIndex, start));
                    rawToken = matcher.group();
                    try {
                        out.print(synthethizeFieldValue(rawToken.split(" "), context));
                    } catch (Exception e) {
                        out.println("Error processing token : " + rawToken);
                    }
                    currentIndex = end;
                }
            }
            if (currentIndex < template.length()) {
                out.println(template.substring(currentIndex, template.length()));
            }
        } else {
            out.println(template);
        }
    }

    @Override
    public Object synthethizeFieldValue(String[] split, CatalogActionContext context) throws Exception {
        throw new NotImplementedException();

        /*RuntimeContext ex = context.getRuntimeContext().spawnChild();
        ex.setSentence(split);
        ex.process();
        return ex.getResult();*/
    }

    @Override
    public Object getAllegedParentId(CatalogEntry result, Instrospection instrospection, FieldAccessStrategy access) throws ReflectiveOperationException {
        //return objectNativeInterface.valuedoReadProperty(this.ancestorIdField, instrospection, result, false);
        return access.getPropertyValue(ancestorIdField, result, null, instrospection);
    }

}
