package com.wrupple.muba.catalogs.server.service.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wrupple.muba.catalogs.server.service.JSRAnnotationsDictionary;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.annotations.*;
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl;
import com.wrupple.muba.event.domain.impl.ImageField;
import com.wrupple.muba.event.domain.impl.PrimaryKeyField;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorBuilder;

import static com.wrupple.muba.event.domain.PersistentCatalogEntity.IMAGE_FIELD;

@Singleton
public class CatalogDescriptorBuilderImpl implements CatalogDescriptorBuilder {
    @Inject
    public CatalogDescriptorBuilderImpl(JSRAnnotationsDictionary cms) {
        this.cms = cms;
    }

    protected static final Logger log = LogManager.getLogger(CatalogDescriptorBuilderImpl.class);


	private JSRAnnotationsDictionary cms;

    public <T extends CatalogEntry> CatalogDescriptor fromClass(Class<T> clazz, String catalogId, String cataogName, long numericId,
                                                                CatalogDescriptor parent) throws  RuntimeException {
        return work(null,clazz,catalogId,cataogName,numericId,parent);
    }

	public <T extends CatalogEntry> CatalogDescriptor work(CatalogDescriptorImpl regreso,Class<T> clazz, String catalogId, String cataogName, Long numericId,
			CatalogDescriptor parent) throws  RuntimeException {

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

		String id, name,  foreignCatalog, defaultValue;
		String[] defaultValueOptions, properties,formulaValue;
		boolean multiple, key, ephemeral, ignore, sortable, filterable, createable, writeable, detailable, summary,
				localized;

		int dataType;
		FieldDescriptorImpl fieldDescriptor;
		Class<?> declaringClass, declaredGeneric;
		ParameterizedType genericType;


		CatalogField argument;
		CatalogFieldDefault defaultt;
		CatalogFieldValues defaultValues;
		ForeignKey foreignKey;
		CatalogValue foreignValue;
		CatalogFieldSentence formula;
		CatalogFieldProperties props;

		Field field;
		for (int i = 0; i < cll.size(); i++) {
			field = cll.get(i);
			id = field.getName();
			name = id;
			declaringClass = field.getType();
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
			foreignKey = field.getAnnotation(ForeignKey.class);
			foreignValue = field.getAnnotation(CatalogValue.class);
			formula = field.getAnnotation(CatalogFieldSentence.class);
			props = field.getAnnotation(CatalogFieldProperties.class);


			if (argument != null) {
				ephemeral = argument.generated();
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
						fieldDescriptor = new FieldDescriptorImpl().makeGenerated(id, name, foreignCatalog, multiple);
						fieldDescriptor.setDataType(dataType);
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
						fieldDescriptor.setSentence(formulaValue==null?null:Arrays.asList(formulaValue));
						fieldDescriptor.setLocalized(localized);
						fieldDescriptor.setSortable(sortable);
						fieldDescriptor.setSummary(summary);
						fieldDescriptor.setWriteable(writeable);
					} else if (key) {

						if (CatalogEntry.ID_FIELD.equals(id)) {
							fieldDescriptor = new PrimaryKeyField(false);
						} else {

							fieldDescriptor = new FieldDescriptorImpl().makeKey(id, name, foreignCatalog, multiple);

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
							fieldDescriptor.setSentence(formulaValue==null?null:Arrays.asList(formulaValue));
							fieldDescriptor.setLocalized(localized);
							fieldDescriptor.setSortable(sortable);
							fieldDescriptor.setSummary(summary);
							fieldDescriptor.setWriteable(writeable);
						}
					} else if (id.equals(IMAGE_FIELD)) {
						fieldDescriptor = new ImageField();

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
						fieldDescriptor.setSentence(formulaValue==null?null:Arrays.asList(formulaValue));
						fieldDescriptor.setLocalized(localized);
						fieldDescriptor.setSortable(sortable);
						fieldDescriptor.setSummary(summary);
						fieldDescriptor.setWriteable(writeable);
					} else {
						/* FIXME widget bingings
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
						*/
						fieldDescriptor = new FieldDescriptorImpl().makeDefault(id, name, dataType);
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
						fieldDescriptor.setSentence(formulaValue==null?null:Arrays.asList(formulaValue));
						fieldDescriptor.setLocalized(localized);
						fieldDescriptor.setSortable(sortable);
						fieldDescriptor.setSummary(summary);
						fieldDescriptor.setWriteable(writeable);
					}
					/* deduced on demand
					if (parent != null) {
						if (parent.getFieldDescriptor(fieldDescriptor.getDistinguishedName())!=null) {
							fieldDescriptor.setInherited(true);
							fieldDescriptor.setOwnerCatalogId(parent.getDistinguishedName());
						}
					}*/

                    //PARSE annotations as constraint objects
                    Annotation[] allAnnotations = field.getAnnotations();

					List<Constraint> constraints =null;
                    Constraint built ;
					for(Annotation an: allAnnotations){
                        try {
                            built = this.cms.buildConstraint(an);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        if(built!=null){
					        if(constraints==null){
                                constraints = new ArrayList<Constraint>(2);
                            }
                            constraints.add(built);
                        }
                    }
                    fieldDescriptor.setConstraintsValues(constraints);
                    descriptors[i] = fieldDescriptor;
				}

			}

		}
		if(regreso ==null){
			regreso = new CatalogDescriptorImpl(catalogId, clazz, numericId, cataogName,parent==null?null: parent.getId(),
					descriptors);
		}else{
			regreso.setDistinguishedName(catalogId);
			regreso.setClazz(clazz);
			regreso.setId(numericId);
			regreso.setName(cataogName);
			regreso.setParent(parent==null?null: parent.getId());
			regreso.setDescriptors(descriptors);
		}
		regreso.setParentValue(parent);

		log.debug("[PARSED JAVA DESCRIPTOR] {}", clazz);

		if(!regreso.getClazz().equals(HasAccesablePropertyValues.class)){
			regreso.setConsolidated(true);
		}

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

    @Override
    public <T extends CatalogEntry> CatalogDescriptor fromClass(Class<T> clazz, String catalogId, String catalogName, CatalogDescriptor parent) throws RuntimeException {
        return work(null,clazz,catalogId,catalogName,null,parent);
    }


	@Override
	public <T extends CatalogEntry> CatalogDescriptor in(CatalogDescriptor self, Class<T> clazz, String catalogId, String catalogName, long numericId, CatalogDescriptor parent) throws RuntimeException {
		return null;
	}

}
