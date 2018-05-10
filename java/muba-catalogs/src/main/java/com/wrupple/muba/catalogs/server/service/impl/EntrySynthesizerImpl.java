package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorService;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.EntrySynthesizer;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.server.service.ActionsDictionary;
import com.wrupple.muba.event.server.service.FieldAccessStrategy;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class EntrySynthesizerImpl implements EntrySynthesizer {
    protected static final Logger log = LogManager.getLogger(EntrySynthesizerImpl.class);

    private final Pattern pattern;

    private final String ancestorIdField;
    private final FieldAccessStrategy access;
    private final CatalogKeyServices keyDelgeate;
    private final CatalogDescriptorService catalogService;




    @Inject
    public EntrySynthesizerImpl(@Named("catalog.ancestorKeyField") String ancestorIdField, @Named("template.pattern") Pattern pattern, /** "\\$\\{([A-Za-z0-9]+\\.){0,}[A-Za-z0-9]+\\}" */FieldAccessStrategy access, CatalogKeyServices keyDelgeate,  CatalogDescriptorService catalogService) {
        this.pattern = pattern;
        this.ancestorIdField=ancestorIdField;
        this.access = access;
        this.keyDelgeate = keyDelgeate;
        this.catalogService = catalogService;
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
