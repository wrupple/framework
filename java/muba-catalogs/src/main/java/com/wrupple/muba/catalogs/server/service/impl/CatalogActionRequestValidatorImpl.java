package com.wrupple.muba.catalogs.server.service.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.event.server.service.ContextAwareValidator;
import com.wrupple.muba.event.server.service.PropertyValidationContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.FieldDescriptor;
import com.wrupple.muba.event.domain.reserved.HasConstrains;
import com.wrupple.muba.event.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.catalogs.domain.annotations.ValidCatalogActionRequest;
import com.wrupple.muba.catalogs.server.service.CatalogActionRequestValidator;
import com.wrupple.muba.event.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

public class CatalogActionRequestValidatorImpl implements CatalogActionRequestValidator {

	protected static final Logger log = LoggerFactory.getLogger(CatalogActionRequestValidatorImpl.class);

	private final SystemCatalogPlugin dictionary;
	private final Provider<RuntimeContext> exp;
	private final ContextAwareValidator delegate;
	/*
	 * secondary services
	 */

	private final LargeStringFieldDataAccessObject lsdao;

	@Inject
	public CatalogActionRequestValidatorImpl(ContextAwareValidator delegate, Provider<RuntimeContext> exp,
			SystemCatalogPlugin cms, LargeStringFieldDataAccessObject lsdao) {
		this.lsdao = lsdao;
		this.exp = exp;

		this.dictionary = cms;
		this.delegate = delegate;
	}

	@Override
	public void initialize(ValidCatalogActionRequest constraintAnnotation) {

	}

	@Override
	public boolean isValid(CatalogActionRequest req, final ConstraintValidatorContext validationContext) {
		// had to be done this way because security violations occur when
		// using reflection on the apache chain context map
		boolean report = true;
		log.debug("[VALIDATE CATALOG_TIMELINE ACTION REQUEST]");

		String action = req.getName();
		CatalogEntry entryValue = (CatalogEntry) req.getEntryValue();
		String catalog = (String) req.getCatalog();
		if(catalog==null){
			throw new NullPointerException("catalogId");
		}
		//after request interpret we expect contract domain to be a Long
		Long domain = (Long) req.getDomain();
		FilterData filter = req.getFilter();
		CatalogDescriptor descriptor = null;
		
		if (entryValue==null&& filter==null && (action==null||CatalogActionRequest.READ_ACTION.equals(action))) {
			return report;
		}

		log.trace("validate suficient action parameters");
		if ((CatalogActionRequest.CREATE_ACTION.equals(action) || CatalogActionRequest.WRITE_ACTION.equals(action))
				&& req.getEntryValue() == null) {
			// a writing action requires an incomming entry

			validationContext.buildConstraintViolationWithTemplate("{catalog.request.missingEntry}")
					.addConstraintViolation();
			report = false;
		}

		if (CatalogActionRequest.READ_ACTION.equals(action) && (req.getEntry() == null && filter == null)) {
			validationContext.buildConstraintViolationWithTemplate("{catalog.request.missingFilter}")
					.addConstraintViolation();
			validationContext.buildConstraintViolationWithTemplate("{catalog.request.missingId}")
					.addConstraintViolation();
			report = false;
		}

		log.trace("validate filters");
		if (report && filter != null) {
			List<? extends FilterCriteria> criterias = filter.getFilters();
			// lower limit < upper limit
			// TODO existing operators
			if (criterias != null) {
				FieldDescriptor local;
				for (FilterCriteria criteria : criterias) {
					if (criteria.getOperator() == null) {
						criteria.setOperator(FilterData.EQUALS);
						log.debug("no filter operator defined and default will be used {}", FilterData.EQUALS);
					}
					if (criteria.getPathTokenCount() == 0) {
						throw new IllegalArgumentException("Invalid filter criteira (no field defined)");
					}
					if (criteria.getValue() == null) {
						throw new IllegalArgumentException("Invalid filter criteira (no comparable value)");
					}
					try {
						descriptor = assertDescriptor(descriptor, catalog, domain);
					} catch (InvocationTargetException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);					}
					local = descriptor.getFieldDescriptor(criteria.getPath(0));
					if (local == null || !local.isFilterable()) {
						throw new IllegalArgumentException("Invalid filter criteira (unfilterable field)");
					}
					if (criteria.getPathTokenCount() > 1 && local.getCatalog() == null) {
						throw new IllegalArgumentException(
								"Invalid filter criteira (not a foreign key : " + local.getFieldId() + ")");
					}

				}
			}
		}

		
		if (report && entryValue != null) {
			log.trace("validate entry Value");
			try {
				descriptor = assertDescriptor(descriptor, catalog, domain);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			// otherwise the validator will descend to java beans
			if (HasAccesablePropertyValues.class.equals(descriptor.getClazz())) {
				log.debug("Dynamic validation of non-java-bean entry Value");
				Collection<FieldDescriptor> fields = descriptor.getFieldsValues();
				Annotation[] annotations;
				PropertyValidationContext accessStrategy;
				for (FieldDescriptor field : fields) {
					accessStrategy = buildFieldAccessStrategy(field, validationContext,
							(HasAccesablePropertyValues) entryValue);
					annotations = buildAnnotation(field, field);
					if (annotations != null) {

						// field level constraints
						try {
							if (delegate.processAnnotationInContext(annotations, descriptor.getClass(),
									accessStrategy)) {
								log.trace("invalid field {}",field.getFieldId());
								report = false;
							}
						} catch (ReflectiveOperationException e) {
							new RuntimeException("While Validating field:" + field.getFieldId(), e);
						}
					}

				}
				if (report) {
					annotations = buildAnnotation(descriptor, null);

					if (annotations != null) {
						accessStrategy = new PropertyValidationContext() {

							@Override
							public ConstraintValidatorContext getValidationContext() {
								return validationContext;
							}

							@Override
							public Type getJavaType() {
								return HasAccesablePropertyValues.class;
							}

							@Override
							public Object get(Object arg0) {
								return arg0;
							}

							@Override
							public String getDistinguishedName() {
								return CatalogActionRequest.FOREIGN_KEY;
							}
						};
						// type level constriants
						log.trace("validating entry level constraints");
						try {
							if (delegate.processAnnotationInContext(annotations, descriptor.getClass(),
									accessStrategy)) {
								log.trace("class level constraint encountered");
								report = false;
							}
						} catch (ReflectiveOperationException e) {
							new RuntimeException("While Validating entry level constriants", e);
						}
					}
				}

			}

		}

		return report;
	}

	private CatalogDescriptor assertDescriptor(CatalogDescriptor descriptor, String catalogId, Long domain) throws InvocationTargetException, IllegalAccessException {
		if (descriptor == null) {
//at this point this very validator should allow this as a valid  request no more questions asked
			RuntimeContext system = this.exp.get();
			CatalogActionRequestImpl context = new CatalogActionRequestImpl();
				context.setDomain(domain);
				context.setCatalog(CatalogDescriptor.CATALOG_ID);
				context.setEntry(catalogId);
            context.setName(DataEvent.READ_ACTION);

			try {
				descriptor = system.getEventBus().fireEvent(context,system,null);
			} catch (Exception e) {
			    log.error("Error while attempting to read catalog id ",e);
				throw new RuntimeException(e);
			}
			if(descriptor==null){
				throw new IllegalArgumentException(catalogId+"@"+domain);
			}
		}
		return descriptor;
	}

	private PropertyValidationContext buildFieldAccessStrategy(final FieldDescriptor field,
			final ConstraintValidatorContext validationContext, final HasAccesablePropertyValues entryValue) {
		final Type javaType;
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
				throw new IllegalArgumentException(
						field.getFieldId() + " attempted to validate unsupported field type " + dataType);
			}
		}

		return new PropertyValidationContext() {

			@Override
			public Object get(Object arg0) {
				return entryValue.getPropertyValue(field.getFieldId());
			}

			@Override
			public ConstraintValidatorContext getValidationContext() {
				return validationContext;
			}

			@Override
			public Type getJavaType() {
				return javaType;
			}

			@Override
			public String getDistinguishedName() {
				return field.getFieldId();
			}

		};
	}

	private Annotation[] buildAnnotation(HasConstrains source, FieldDescriptor field) {
		List<Constraint> constraints = source.getConstraintsValues();
		boolean key = field != null & dictionary.isJoinableValueField(field)
				&& field.isKey() /* not ephemerals */;
		boolean normalized = field != null && field.getDefaultValueOptions() != null;
		if (constraints == null && !key && !normalized) {
			return null;
		}
		int size = constraints == null ? 0 : constraints.size();
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
		int j = 0;
		if (key) {
			regreso[j] = dictionary.buildCatalogKeyValidation(field);
			j++;
		}
		if (normalized) {
			regreso[j] = dictionary.buildNormalizationValidation(field);
			j++;
		}

		Constraint constraint;
		if (constraints != null) {
			Annotation rannotation;
			for (int i = 0; i < size; i++) {

				constraint = constraints.get(i);
				rannotation = dictionary.buildAnnotation(constraint);
				if (CatalogKey.class.isAssignableFrom(rannotation.getClass())) {
				} else if (CatalogFieldValues.class.isAssignableFrom(rannotation.getClass())) {
				} else {
					regreso[j] = rannotation;
					j++;
				}

			}
		}

		return regreso;
	}

}
