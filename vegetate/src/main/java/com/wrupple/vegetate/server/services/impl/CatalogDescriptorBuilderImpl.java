package com.wrupple.vegetate.server.services.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.inject.Singleton;

import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.PersistentImageMetadata;
import com.wrupple.vegetate.server.domain.CatalogDescriptorImpl;
import com.wrupple.vegetate.server.domain.FieldDescriptorImpl;
import com.wrupple.vegetate.server.domain.ImageField;
import com.wrupple.vegetate.server.domain.PrimaryKeyField;
import com.wrupple.vegetate.server.domain.annotations.CatalogField;
import com.wrupple.vegetate.server.domain.annotations.CatalogFieldDefault;
import com.wrupple.vegetate.server.domain.annotations.CatalogFieldFormula;
import com.wrupple.vegetate.server.domain.annotations.CatalogFieldProperties;
import com.wrupple.vegetate.server.domain.annotations.CatalogFieldValues;
import com.wrupple.vegetate.server.domain.annotations.CatalogFieldWidget;
import com.wrupple.vegetate.server.domain.annotations.CatalogKey;
import com.wrupple.vegetate.server.domain.annotations.CatalogValue;
import com.wrupple.vegetate.server.services.CatalogDescriptorBuilder;

@Singleton
public class CatalogDescriptorBuilderImpl implements CatalogDescriptorBuilder {

	public CatalogDescriptor fromClass(Class<?> clazz, String catalogId, String cataogName, long numericId) {

		Field[] declaredFields = clazz.getDeclaredFields();
		FieldDescriptorImpl[] descriptors = new FieldDescriptorImpl[declaredFields.length];

		String id, name, widget, foreignCatalog, defaultValue, formulaValue;
		String[] defaultValueOptions, properties;
		boolean multiple, key, ephemeral, ignore, sortable, filterable, createable, writeable, detailable, summary, localized;

		int dataType;
		FieldDescriptorImpl fieldDescriptor;
		Class<?> declaringClass, declaredGeneric;
		ParameterizedType genericType;

		CatalogField argument;
		CatalogFieldDefault defaultt;
		CatalogFieldValues defaultValues;
		CatalogKey foreignKey;
		CatalogValue foreignValue;
		CatalogFieldFormula formula;
		CatalogFieldWidget widgetAnnot;
		CatalogFieldProperties props;
		
		Field field;
		for (int i = 0; i < declaredFields.length; i++) {
			field = declaredFields[i];
			id = field.getName();
			name = id;
			declaringClass = field.getType();
			widget = null;
			defaultValueOptions = null;
			foreignCatalog = null;
			defaultValue = null;
			formulaValue = null;
			properties = null;
			ephemeral = false;
			sortable = false;
			filterable = false;
			createable = true;
			writeable = true;
			detailable = true;
			summary = true;
			localized = false;
			ignore=false;

			if (Collection.class.isAssignableFrom(declaringClass)) {
				genericType = (ParameterizedType) field.getGenericType();
				declaredGeneric = (Class<?>) genericType.getActualTypeArguments()[0];
				dataType = getDataType(id, declaredGeneric, clazz);
				multiple = true;
			} else {
				declaredGeneric = declaringClass;
				multiple = false;
				dataType = getDataType(id, declaringClass, clazz);
			}

			argument = field.getAnnotation(CatalogField.class);
			defaultt = field.getAnnotation(CatalogFieldDefault.class);
			defaultValues = field.getAnnotation(CatalogFieldValues.class);
			foreignKey = field.getAnnotation(CatalogKey.class);
			foreignValue = field.getAnnotation(CatalogValue.class);
			formula = field.getAnnotation(CatalogFieldFormula.class);
			widgetAnnot = field.getAnnotation(CatalogFieldWidget.class);
			props=field.getAnnotation(CatalogFieldProperties.class);

			if (argument != null) {
				ephemeral = argument.ephemeral();
				sortable = argument.sortable();
				filterable = argument.filterable();
				createable = argument.createable();
				writeable = argument.writeable();
				detailable = argument.detailable();
				summary = argument.summary();
				ignore=argument.ignore();
				localized = argument.localized();
			}

			if (foreignKey != null) {
				foreignCatalog =foreignKey.foreignCatalog();
			}
			if(foreignValue!=null){
				foreignCatalog = foreignValue.foreignCatalog();
				if(foreignCatalog==null){
					foreignCatalog=declaredGeneric.getSimpleName();
				}
				if(!id.endsWith(CatalogEntry.FOREIGN_KEY)||!id.endsWith(CatalogEntry.FOREIGN_KEY)){
					throw new IllegalArgumentException("Unknown Foreign Key Value Postfix");
				}
				ephemeral = true;
			}
			
			if (widgetAnnot != null) {
				widget = widgetAnnot.widget();
			}
			
			if (defaultt != null) {
				defaultValue = defaultt.defaultValue();
			}

			if (defaultValues != null) {
				defaultValueOptions = defaultValues.defaultValueOptions();
			}

			if (formula != null) {
				formulaValue =formula.formula();
			}
			
			if(props!=null){
				properties = props.properties();
			}


			key = CatalogEntry.ID_FIELD.equals(id) || foreignCatalog != null;

			if (dataType < 0||java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
				ignore = true;
			}

			if (!ignore) {

				if (ephemeral) {
					fieldDescriptor = new FieldDescriptorImpl().makeEphemeral(id, name, foreignCatalog, multiple);
					if (widget != null) {
						fieldDescriptor.setWidget(widget);
					}
					if (defaultValueOptions != null) {
						fieldDescriptor.setDefaultValueOptions(Arrays.asList(defaultValueOptions));
					}
					
					if(properties!=null){
						fieldDescriptor.setProperties(Arrays.asList(properties));
					}
					fieldDescriptor.setCreateable(createable);
					fieldDescriptor.setDefaultValue(defaultValue);
					fieldDescriptor.setDetailable(detailable);
					fieldDescriptor.setFilterable(filterable);
					fieldDescriptor.setFormula(formulaValue);
					fieldDescriptor.setLocalized(localized);
					fieldDescriptor.setSortable(sortable);
					fieldDescriptor.setSummary(summary);
					fieldDescriptor.setWriteable(writeable);
				} else if (key) {
					
					if(CatalogEntry.ID_FIELD.equals(id)){
						fieldDescriptor = new PrimaryKeyField(false);
					}else{
						
						fieldDescriptor = new FieldDescriptorImpl().makeKey(id, name, foreignCatalog, multiple);
						if (widget != null) {
							fieldDescriptor.setWidget(widget);
						}
						if (defaultValueOptions != null) {
							fieldDescriptor.setDefaultValueOptions(Arrays.asList(defaultValueOptions));
						}
						
						if(properties!=null){
							fieldDescriptor.setProperties(Arrays.asList(properties));
						}
						fieldDescriptor.setCreateable(createable);
						fieldDescriptor.setDefaultValue(defaultValue);
						fieldDescriptor.setDetailable(detailable);
						fieldDescriptor.setFilterable(filterable);
						fieldDescriptor.setFormula(formulaValue);
						fieldDescriptor.setLocalized(localized);
						fieldDescriptor.setSortable(sortable);
						fieldDescriptor.setSummary(summary);
						fieldDescriptor.setWriteable(writeable);
					}
				} else if(id.equals(PersistentImageMetadata.IMAGE_FIELD)){
					fieldDescriptor = new ImageField();
					if (widget != null) {
						fieldDescriptor.setWidget(widget);
					}
					fieldDescriptor.setMultiple(multiple);
					
					if (defaultValueOptions != null) {
						fieldDescriptor.setDefaultValueOptions(Arrays.asList(defaultValueOptions));
					}
					
					if(properties!=null){
						fieldDescriptor.setProperties(Arrays.asList(properties));
					}
					fieldDescriptor.setCreateable(createable);
					fieldDescriptor.setDefaultValue(defaultValue);
					fieldDescriptor.setDetailable(detailable);
					fieldDescriptor.setFilterable(filterable);
					fieldDescriptor.setFormula(formulaValue);
					fieldDescriptor.setLocalized(localized);
					fieldDescriptor.setSortable(sortable);
					fieldDescriptor.setSummary(summary);
					fieldDescriptor.setWriteable(writeable);
				} else {
					if (widget == null) {
						switch (dataType) {
						case CatalogEntry.BOOLEAN_DATA_TYPE:
							widget = "checkBox";
							break;
						case CatalogEntry.DATE_DATA_TYPE:
							widget = "date";
							break;
						default:
							if (multiple) {
								widget = "multiText";
							} else {
								if (defaultValueOptions == null) {
									widget = "text";
								} else {
									widget = "listPicker";
								}
							}

						}
					}
					fieldDescriptor = new FieldDescriptorImpl().makeDefault(id, name, widget, dataType);
					fieldDescriptor.setMultiple(multiple);
					
					
					if (defaultValueOptions != null) {
						fieldDescriptor.setDefaultValueOptions(Arrays.asList(defaultValueOptions));
					}
					
					if(properties!=null){
						fieldDescriptor.setProperties(Arrays.asList(properties));
					}
					fieldDescriptor.setCreateable(createable);
					fieldDescriptor.setDefaultValue(defaultValue);
					fieldDescriptor.setDetailable(detailable);
					fieldDescriptor.setFilterable(filterable);
					fieldDescriptor.setFormula(formulaValue);
					fieldDescriptor.setLocalized(localized);
					fieldDescriptor.setSortable(sortable);
					fieldDescriptor.setSummary(summary);
					fieldDescriptor.setWriteable(writeable);
				}
				
				
				descriptors[i] = fieldDescriptor;
			}

		}
		CatalogDescriptor regreso = new CatalogDescriptorImpl(catalogId, clazz, numericId, cataogName, descriptors);
		
		//TODO inheritance
		
		
		return regreso;

	}


	public static int getDataType(String fieldId, Class<?> fieldClass, Class<?> catalogClazz) {

		if (Long.TYPE.equals(fieldClass) || Integer.TYPE.equals(fieldClass) || fieldClass.equals(Long.class) || fieldClass.equals(Integer.class)) {
			return CatalogEntry.INTEGER_DATA_TYPE;
		} else if (Number.class.isAssignableFrom(fieldClass) || Double.TYPE.equals(fieldClass) || Float.TYPE.equals(fieldClass)) {
			return CatalogEntry.NUMERIC_DATA_TYPE;
		} else if (fieldClass.equals(String.class)) {
			return CatalogEntry.STRING_DATA_TYPE;
		} else if (Boolean.TYPE.equals(fieldClass) || fieldClass.equals(Boolean.class)) {
			return CatalogEntry.BOOLEAN_DATA_TYPE;
		} else if (fieldClass.equals(Date.class)) {
			return CatalogEntry.DATE_DATA_TYPE;
		} else {
			System.err.println("not supported field " + fieldId + " of type :" + fieldClass + "in " + catalogClazz);
			return -1;
		}
	}
}
