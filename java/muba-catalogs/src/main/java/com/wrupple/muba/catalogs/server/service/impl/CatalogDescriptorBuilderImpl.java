package com.wrupple.muba.catalogs.server.service.impl;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.PersistentImageMetadata;
import com.wrupple.muba.catalogs.domain.annotations.CatalogField;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldDefault;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldFormula;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldProperties;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldWidget;
import com.wrupple.muba.catalogs.domain.annotations.CatalogKey;
import com.wrupple.muba.catalogs.domain.annotations.CatalogValue;
import com.wrupple.muba.catalogs.server.domain.CatalogDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.FieldDescriptorImpl;
import com.wrupple.muba.catalogs.server.domain.fields.ImageField;
import com.wrupple.muba.catalogs.server.domain.fields.PrimaryKeyField;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;

@Singleton
public class CatalogDescriptorBuilderImpl implements CatalogDescriptorBuilder {
	protected static final Logger log = LoggerFactory.getLogger(CatalogDescriptorBuilderImpl.class);

	public CatalogDescriptor fromClass(Class<?> clazz, String catalogId, String cataogName, long numericId,
			CatalogDescriptor parent) {

		List<Field> cll = new ArrayList<Field>();
		Class<?> current = clazz;
		Field[] declaredFields;
		do {
			declaredFields = current.getDeclaredFields();
			for (Field f : declaredFields) {
				if (!java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
					cll.add(f);
				}

			}
			current = current.getSuperclass();
		} while (current != null && Object.class != current);

		FieldDescriptorImpl[] descriptors = new FieldDescriptorImpl[cll.size()];

		String id, name, widget, foreignCatalog, defaultValue, formulaValue;
		String[] defaultValueOptions, properties;
		boolean multiple, key, ephemeral, ignore, sortable, filterable, createable, writeable, detailable, summary,
				localized;

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
		for (int i = 0; i < cll.size(); i++) {
			field = cll.get(i);
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
			filterable = CatalogEntry.NAME_FIELD.equals(name);
			createable = true;
			writeable = true;
			detailable = true;
			summary = true;
			localized = false;
			ignore = false;

			argument = field.getAnnotation(CatalogField.class);
			defaultt = field.getAnnotation(CatalogFieldDefault.class);
			defaultValues = field.getAnnotation(CatalogFieldValues.class);
			foreignKey = field.getAnnotation(CatalogKey.class);
			foreignValue = field.getAnnotation(CatalogValue.class);
			formula = field.getAnnotation(CatalogFieldFormula.class);
			widgetAnnot = field.getAnnotation(CatalogFieldWidget.class);
			props = field.getAnnotation(CatalogFieldProperties.class);

			if (argument != null) {
				ephemeral = argument.ephemeral();
				sortable = argument.sortable();
				filterable = argument.filterable();
				createable = argument.createable();
				writeable = argument.writeable();
				detailable = argument.detailable();
				summary = argument.summary();
				ignore = argument.ignore();
				localized = argument.localized();
			}

			if (foreignKey != null) {
				foreignCatalog = foreignKey.foreignCatalog();
			}
			if (foreignValue != null) {
				foreignCatalog = foreignValue.foreignCatalog();
				if (foreignCatalog == null) {
					throw new IllegalArgumentException("no foreign catalog specified for " + id);
				}
				if (id.endsWith(CatalogEntry.FOREIGN_KEY) || id.endsWith(CatalogEntry.MULTIPLE_FOREIGN_KEY)) {
					ephemeral = true;
				} else {
					throw new IllegalArgumentException("Unknown Foreign Key Value Postfix:" + id);
				}

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
				formulaValue = formula.formula();
			}

			if (props != null) {
				properties = props.properties();
			}

			key = CatalogEntry.ID_FIELD.equals(id) || foreignCatalog != null;

			if (!ignore) {

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
				if (!(dataType < 0 || java.lang.reflect.Modifier.isStatic(field.getModifiers()))) {

					if (ephemeral) {
						fieldDescriptor = new FieldDescriptorImpl().makeEphemeral(id, name, foreignCatalog, multiple);
						if (widget != null) {
							fieldDescriptor.setWidget(widget);
						}
						if (defaultValueOptions != null) {
							fieldDescriptor.setDefaultValueOptions(Arrays.asList(defaultValueOptions));
						}

						if (properties != null) {
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

						if (CatalogEntry.ID_FIELD.equals(id)) {
							fieldDescriptor = new PrimaryKeyField(false);
						} else {

							fieldDescriptor = new FieldDescriptorImpl().makeKey(id, name, foreignCatalog, multiple);
							if (widget != null) {
								fieldDescriptor.setWidget(widget);
							}
							if (defaultValueOptions != null) {
								fieldDescriptor.setDefaultValueOptions(Arrays.asList(defaultValueOptions));
							}

							if (properties != null) {
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
					} else if (id.equals(PersistentImageMetadata.IMAGE_FIELD)) {
						fieldDescriptor = new ImageField();
						if (widget != null) {
							fieldDescriptor.setWidget(widget);
						}
						fieldDescriptor.setMultiple(multiple);

						if (defaultValueOptions != null) {
							fieldDescriptor.setDefaultValueOptions(Arrays.asList(defaultValueOptions));
						}

						if (properties != null) {
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

						if (properties != null) {
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
					if (parent != null) {
						if (parent.getFieldDescriptor(fieldDescriptor.getFieldId())!=null) {
							fieldDescriptor.setInherited(true);
							fieldDescriptor.setOwnerCatalogId(parent.getCatalog());
						}
					}

					descriptors[i] = fieldDescriptor;
				}

			}

		}
		CatalogDescriptor regreso = new CatalogDescriptorImpl(catalogId, clazz, numericId, cataogName,parent==null?null: parent.getId(),
				descriptors);
		log.trace("[PARSED NUMERIC_ID DESCRIPTOR] {}", catalogId);
		return regreso;

	}

	public static int getDataType(String fieldId, Class<?> fieldClass, Class<?> catalogClazz) {

		if (Long.TYPE.equals(fieldClass) || Integer.TYPE.equals(fieldClass) || fieldClass.equals(Long.class)
				|| fieldClass.equals(Integer.class)) {
			return CatalogEntry.INTEGER_DATA_TYPE;
		} else if (Number.class.isAssignableFrom(fieldClass) || Double.TYPE.equals(fieldClass)
				|| Float.TYPE.equals(fieldClass)) {
			return CatalogEntry.NUMERIC_DATA_TYPE;
		} else if (fieldClass.equals(String.class)) {
			return CatalogEntry.STRING_DATA_TYPE;
		} else if (Boolean.TYPE.equals(fieldClass) || fieldClass.equals(Boolean.class)) {
			return CatalogEntry.BOOLEAN_DATA_TYPE;
		} else if (fieldClass.equals(Date.class)) {
			return CatalogEntry.DATE_DATA_TYPE;
		} else {
			return CatalogEntry.OBJECT_DATA_TYPE;
		}
	}
}
