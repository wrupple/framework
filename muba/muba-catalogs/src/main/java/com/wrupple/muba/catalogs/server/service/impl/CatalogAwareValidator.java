package com.wrupple.muba.catalogs.server.service.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Constraint;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;

import org.apache.bval.jsr303.AnnotationProcessor;
import org.apache.bval.jsr303.ApacheFactoryContext;
import org.apache.bval.jsr303.ApacheValidatorFactory;
import org.apache.bval.jsr303.AppendValidationToMeta;
import org.apache.bval.jsr303.ConstraintValidationListener;
import org.apache.bval.jsr303.GroupValidationContext;
import org.apache.bval.jsr303.groups.Groups;
import org.apache.bval.jsr303.groups.GroupsComputer;
import org.apache.bval.jsr303.util.ConstraintDefinitionValidator;
import org.apache.bval.model.MetaBean;
import org.apache.bval.model.MetaProperty;
import org.apache.bval.util.AccessStrategy;
import org.apache.bval.util.ValidationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.catalogs.server.service.DatabasePlugin;
import com.wrupple.muba.catalogs.server.service.JSRAnnotationsDictionary;
import com.wrupple.muba.catalogs.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.catalogs.shared.services.ImplicitJoinUtils;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogDescriptor;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldConstraint;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterCriteria;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.domain.FilterDataOrdering;
import com.wrupple.vegetate.server.domain.annotations.CatalogFieldValues;
import com.wrupple.vegetate.server.domain.annotations.CatalogKey;

/**
 * 
 * 
 *
 * 
 * 
 * }
 * 
 * @author japi
 * 
 */
@Singleton
public class CatalogAwareValidator implements Validator {

	protected static final Logger log = LoggerFactory.getLogger(CatalogAwareValidator.class);

	private final Validator wrapped;

	/*
	 * secondary services
	 */
	private ApacheFactoryContext factoryContext;
	private AnnotationProcessor annotationProcessor;
	private final JSRAnnotationsDictionary dictionary;

	private final ApacheValidatorFactory factory;

	private final LargeStringFieldDataAccessObject lsdao;

	private final DatabasePlugin database;

	@Inject
	public CatalogAwareValidator(LargeStringFieldDataAccessObject lsdao, ApacheValidatorFactory factory, JSRAnnotationsDictionary dictionary,
			ConstraintValidatorFactory constraintValidatorFactory,DatabasePlugin database) {
		factory.setConstraintValidatorFactory(constraintValidatorFactory);
		this.database=database;
		this.wrapped = factory.getValidator();
		this.factory = factory;
		this.dictionary = dictionary;
		this.lsdao = lsdao;
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
		if (object.getClass().equals(CatalogExcecutionContext.class)) {
			// had to be done this way because security violations occur when
			// using reflection on the apache chain context map
			CatalogExcecutionContext ctx = (CatalogExcecutionContext) object;

			log.trace("[VALIDATE CATALOG CONTEXT]");
			if (ctx.getAction() == null || ctx.getCatalog() == null) {
				throw new IllegalArgumentException("invalid Catalog Action Request");
			}

			if ((CatalogActionRequest.CREATE_ACTION.equals(ctx.getAction()) || CatalogActionRequest.WRITE_ACTION.equals(ctx.getAction()))
					&& ctx.getEntryValue() == null) {
				throw new IllegalArgumentException("invalid Catalog Action Request");
			}

			if (CatalogActionRequest.READ_ACTION.equals(ctx.getAction()) && (ctx.getEntry() == null && ctx.getFilter() == null)) {
				throw new IllegalArgumentException("invalid Catalog Action Request");
			}

			FilterData filter = ctx.getFilter();

			if (filter != null) {

				CatalogDescriptor catalog = ctx.getCatalogDescriptor();

				String[] columns = filter.getColumns();

				if (columns != null && !allFieldsContained(columns, catalog)) {
					throw new IllegalArgumentException("query specifies unknown fields");
				}

				List<? extends FilterDataOrdering> ordering = filter.getOrdering();

				if (ordering != null) {
					for (FilterDataOrdering o : ordering) {
						if (catalog.getFieldDescriptor(o.getField()) == null) {
							throw new IllegalArgumentException("unrecognized field :" + o.getField() + "@" + catalog.getCatalogId());
						}
					}
				}
				String[][] joins = filter.getJoins();
				if (joins != null) {

					String foreignCatalog, foreignField, localField;
					for (String[] state : joins) {
						if (state.length > 3 || state.length < 2) {
							throw new IllegalArgumentException("invalid join statement " + Arrays.toString(state));
						} else {
							foreignCatalog = state[0];
							foreignField = state[1];
							if (state.length == 2) {
								localField = foreignField;
							} else {
								localField = state[2];
							}

							if (foreignCatalog == null || foreignField == null || localField == null) {
								throw new IllegalArgumentException("invalid join statement " + Arrays.toString(state));
							} else {
								if (catalog.getFieldDescriptor(localField) == null) {
									throw new IllegalArgumentException("invalid join statement " + Arrays.toString(state));
								} else {
									try {
										if (database.getDescriptorForName(foreignCatalog, ctx) == null) {
											throw new IllegalArgumentException("invalid join statement " + Arrays.toString(state));
										}
									} catch (Exception e) {
										throw new IllegalArgumentException("invalid join statement " + Arrays.toString(state), e);
									} finally {
										// TODO validate nested
										// fields.field.field
									}
								}
							}
						}
					}
				}

				List<? extends FilterCriteria> criterias = filter.getFilters();

				if (criterias != null) {
					FieldDescriptor local;
					for (FilterCriteria criteria : criterias) {
						if (criteria.getOperator() == null) {
							criteria.setOperator(FilterData.EQUALS);
							ctx.getRequest().addWarning("Invalid filter criteira (no operator defined)");
						}
						if (criteria.getPathTokenCount() == 0) {
							throw new IllegalArgumentException("Invalid filter criteira (no field defined)");
						}
						if (criteria.getValue() == null) {
							throw new IllegalArgumentException("Invalid filter criteira (no comparable value)");
						}
						local = catalog.getFieldDescriptor(criteria.getPath(0));
						if (local == null || !local.isFilterable()) {
							throw new IllegalArgumentException("Invalid filter criteira (unfilterable field)");
						}
						if (criteria.getPathTokenCount() > 1 && local.getForeignCatalogName() == null) {
							throw new IllegalArgumentException("Invalid filter criteira (not a foreign key : " + local.getFieldId() + ")");
						}

					}
				}

			}

			CatalogEntry entry = (CatalogEntry) ctx.getEntryValue();

			if (entry == null) {
				return null;
			} else {
				return (Set) validateDynamicObject(ctx, groups);
			}

		} else {
			return wrapped.validate(object, groups);
		}
	}

	private boolean allFieldsContained(String[] columns, CatalogDescriptor catalog) {
		for (String field : columns) {
			if (catalog.getFieldDescriptor(field) == null) {
				return false;
			}
		}
		return true;
	}

	private Set<ConstraintViolation<CatalogExcecutionContext>> validateDynamicObject(CatalogExcecutionContext ctx, Class<?>[] groups) {
		if (factoryContext == null) {
			factoryContext = new ApacheFactoryContext(factory);
			annotationProcessor = new AnnotationProcessor(factoryContext);
		}
		MetaBean arg = new MetaBean();
		arg.setBeanClass(CatalogExcecutionContext.class);
		arg.setId(CatalogExcecutionContext.class.getName());
		arg.setName(arg.getId());
		Class<?> owner = arg.getBeanClass();

		CatalogDescriptor catalog = ctx.getCatalogDescriptor();
		Collection<FieldDescriptor> fields = catalog.getFieldsValues();

		Annotation[] annotations;
		Constraint vcAnno;
		// ADD VALIDATABLE PROPERTIES TO META BEAN
		MetaProperty prop;
		AccessStrategy access;
		log.trace("[BUILDING CATALOG ENTRY VALIDATION CONTEXT]");
		for (FieldDescriptor field : fields) {

			annotations = buildAnnotation(field);
			if (annotations != null) {
				access = buildFieldAccessStrategy(field);
				// append property to main meta bean
				prop = addMetaProperty(arg, access);
				for (Annotation annotation : annotations) {
					ConstraintDefinitionValidator.validateConstraintDefinition(annotation);
					// vcAnno = points to actial validator class
					// (UpperCaseValidator.class)
					vcAnno = annotation.annotationType().getAnnotation(Constraint.class);

					if (vcAnno != null) {

						try {
							// append validation data to property
							annotationProcessor.processAnnotation(annotation, prop, owner, access, new AppendValidationToMeta(prop));
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}

		}
		GroupValidationContext<CatalogExcecutionContext> context = createContext(arg, new GroupsComputer(), ctx, CatalogExcecutionContext.class, groups);
		Groups sequence = context.getGroups();
		context.setCurrentGroup(sequence.getGroups().get(0));
		log.trace("[VALIDATE CATALOG ENTRY]");
		ValidationHelper.validateBean(context);

		return context.getListener().getConstraintViolations();
	}

	private Annotation[] buildAnnotation(FieldDescriptor field) {
		java.util.List<FieldConstraint> constraints = field.getConstraintsValues();
		boolean key = ImplicitJoinUtils.isJoinableValueField(field)
				&& field.isKey() /* not ephemerals */;
		boolean normalized = field.getDefaultValueOptions() != null;
		if (constraints == null && !key && !normalized) {
			return null;
		}
		int size = constraints.size();
		if (key) {
			size++;
		}
		if (normalized) {
			size++;
		}
		if (size == 0) {
			return null;
		}
		Annotation[] regreso = new Annotation[size];
		FieldConstraint constraint;
		boolean keyValidationFound = false, normalizationFound = false;
		for (int i = 0; i < size; i++) {
			if (key && !keyValidationFound && i >= (regreso.length)) {
				regreso[i] = dictionary.buildCatalogKeyValidation(field);
			} else if (normalized && !normalizationFound && i >= (regreso.length)) {
				regreso[i] = dictionary.buildNormalizationValidation(field);
			} else {
				constraint = constraints.get(i);
				regreso[i] = dictionary.buildAnnotation(constraint);
				if (regreso[i].getClass().isAssignableFrom(CatalogKey.class)) {
					keyValidationFound = true;
				} else if (regreso[i].getClass().isAssignableFrom(CatalogFieldValues.class)) {
					normalizationFound = true;
				}

			}
		}

		return regreso;
	}

	private AccessStrategy buildFieldAccessStrategy(FieldDescriptor field) {
		Type javaType;
		if (field.isMultiple()) {
			javaType = List.class;
		} else {
			int dataType = field.getDataType();
			switch (dataType) {
			case CatalogEntry.BOOLEAN_DATA_TYPE:
				javaType = Boolean.class;
				break;
			case CatalogEntry.NUMERIC_DATA_TYPE:
				javaType = Double.class;
				break;
			case CatalogEntry.INTEGER_DATA_TYPE:
				javaType = Long.class;
				break;
			case CatalogEntry.STRING_DATA_TYPE:
				javaType = String.class;
				break;
			case CatalogEntry.LARGE_STRING_DATA_TYPE:
				javaType = lsdao.getLargeStringClass();
				break;
			case CatalogEntry.DATE_DATA_TYPE:
				javaType = Date.class;
				break;
			case CatalogEntry.CATALOG_ENTRY_DATA_TYPE:
			case CatalogEntry.BLOB_DATA_TYPE:
			default:
				throw new IllegalArgumentException(field.getFieldId() + " attempted to validate unsupported field type " + dataType);
			}
		}

		return new CatalogContextFieldAccessStrategy(javaType, field);
	}

	private MetaProperty addMetaProperty(MetaBean parentMetaBean, AccessStrategy access) {
		final MetaProperty result = new MetaProperty();
		final String name = access.getPropertyName();
		result.setName(name);
		result.setType(access.getJavaType());
		parentMetaBean.putProperty(name, result);
		return result;
	}

	private <T> GroupValidationContext<T> createContext(MetaBean metaBean, GroupsComputer groupsComputer, T object, Class<T> objectClass, Class<?>... groups) {

		ConstraintValidationListener<T> listener = new ConstraintValidationListener<T>(object, objectClass);
		GroupValidationContextImpl<T> context = new GroupValidationContextImpl<T>(listener, factoryContext.getMessageInterpolator(),
				factoryContext.getTraversableResolver(), metaBean);
		context.setBean(object, metaBean);
		context.setGroups(groupsComputer.computeGroups(groups));
		return context;
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
		return wrapped.validateProperty(object, propertyName, groups);
	}

	@Override
	public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
		return wrapped.validateValue(beanType, propertyName, value, groups);
	}

	@Override
	public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
		return wrapped.getConstraintsForClass(clazz);
	}

	@Override
	public <T> T unwrap(Class<T> type) {
		return wrapped.unwrap(type);
	}
}
