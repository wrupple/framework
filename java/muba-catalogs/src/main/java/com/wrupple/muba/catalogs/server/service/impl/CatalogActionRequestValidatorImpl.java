package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.server.chain.command.CatalogRequestInterpret;
import com.wrupple.muba.catalogs.server.service.CatalogDescriptorService;
import com.wrupple.muba.event.ServiceBus;
import com.wrupple.muba.event.domain.annotations.ValidCatalogActionRequest;
import com.wrupple.muba.event.domain.impl.CatalogActionRequestImpl;
import com.wrupple.muba.event.server.domain.impl.RuntimeContextImpl;
import com.wrupple.muba.event.server.service.CatalogActionRequestValidator;
import com.wrupple.muba.catalogs.server.service.CatalogKeyServices;
import com.wrupple.muba.catalogs.server.service.JSRAnnotationsDictionary;
import com.wrupple.muba.event.domain.*;
import com.wrupple.muba.event.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues;
import com.wrupple.muba.event.domain.reserved.HasConstrains;
import com.wrupple.muba.event.server.service.ContextAwareValidator;
import com.wrupple.muba.event.server.service.LargeStringFieldDataAccessObject;
import com.wrupple.muba.event.server.service.PropertyValidationContext;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class CatalogActionRequestValidatorImpl implements CatalogActionRequestValidator {

	protected static final Logger log = LogManager.getLogger(CatalogActionRequestValidatorImpl.class);

	private final JSRAnnotationsDictionary dictionary;
	private final CatalogKeyServices keyDelegate;
    private final Provider<SessionContext> exp;
    private final Provider<ServiceBus> applicationProvider;
	private final ContextAwareValidator delegate;
	private final CatalogDescriptorService descriptorService;
	private final CatalogRequestInterpret requestInterpret;
	/*
	 * secondary services
	 */

	private final LargeStringFieldDataAccessObject lsdao;

	@Inject
    public CatalogActionRequestValidatorImpl(ContextAwareValidator delegate, @Named(SessionContext.SYSTEM) Provider<SessionContext> exp,
                                             JSRAnnotationsDictionary cms, CatalogKeyServices keyDelegate, Provider<ServiceBus> applicationProvider, CatalogDescriptorService descriptorService, CatalogRequestInterpret requestInterpret, LargeStringFieldDataAccessObject lsdao) {
        this.keyDelegate = keyDelegate;
        this.applicationProvider = applicationProvider;
        this.requestInterpret = requestInterpret;
		this.descriptorService = descriptorService;
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
		if(req.getCatalog()!=null && req.getEntryValue()!=null ){
		    if(req.getEntryValue() instanceof CatalogEntry){
		        String typ = ((CatalogEntry) req.getEntryValue()).getCatalogType();
		        if(typ!=null){
		            if(!req.getCatalog().equals(typ)){
                        validationContext.buildConstraintViolationWithTemplate("{catalog.request.mismatchValue}")
                                .addConstraintViolation();
		                report = false;
                    }
                }
            }
        }

		String action = req.getName();
		CatalogEntry entryValue = (CatalogEntry) req.getEntryValue();
		String catalog = (String) req.getCatalog();
		if(catalog==null){
			report=false;
            validationContext.buildConstraintViolationWithTemplate("{catalog.request.withoutCatalog}");
            return report;
		}
		//after request interpret we expect contract domain to be a Long
		Long domain =  req.getDomain();
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
					descriptor = assertDescriptor(descriptor, catalog, domain,req);
					local = descriptor.getFieldDescriptor(criteria.getPath(0));
					if (local == null || !local.isFilterable()) {
                        if(local==null){
                            throw new IllegalArgumentException("Invalid filter criteira "+criteria+" (unmageable path: "+criteria.getPath(0)+")");
                        }else{
                            throw new IllegalArgumentException("Invalid filter criteira (unfilterable field: "+local.getDistinguishedName()+"@"+descriptor.getDistinguishedName()+")");
                        }
					}
					if (criteria.getPathTokenCount() > 1 && local.getCatalog() == null) {
						throw new IllegalArgumentException(
								"Invalid filter criteira (not a foreign key : " + local.getDistinguishedName() + ")");
					}

				}
			}
		}

		
		if (report && entryValue != null) {
			log.trace("validate entry Value");
				descriptor = assertDescriptor(descriptor, catalog, domain, req);

			// otherwise the validator will (should) descend to java beans
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
								log.trace("invalid field {}",field.getDistinguishedName());
								report = false;
							}
						} catch (ReflectiveOperationException e) {
							new RuntimeException("While Validating field:" + field.getDistinguishedName(), e);
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

							@Override
							public void setDistinguishedName(String dn) {

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

	private CatalogDescriptor assertDescriptor(CatalogDescriptor descriptor, String catalogId, Long domain, CatalogActionRequest parentRequest)  {
		if (descriptor == null) {
//at this point this very validator should allow this as a valid  request no more questions asked
            CatalogActionRequestImpl internalRequest = new CatalogActionRequestImpl();
            internalRequest.setDomain(domain);
            internalRequest.setParentValue(parentRequest);
            internalRequest.setCatalog(CatalogDescriptor.CATALOG_ID);
            internalRequest.setEntry(catalogId);
            internalRequest.setName(DataContract.READ_ACTION);
            internalRequest.setFollowReferences(true);

            CatalogActionContext context = (CatalogActionContext) requestInterpret.getProvider(null/* :S */).get();
            context.switchContract(internalRequest);
            context.setRuntimeContext(assertSystemRuntime());
            try {
			    descriptor = descriptorService.getDescriptorForName(catalogId,context);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			if(descriptor==null){
				throw new IllegalArgumentException("no such catalog "+catalogId);
			}

		}
		return descriptor;
	}

	private RuntimeContext systemRuntime;

    private RuntimeContext assertSystemRuntime() {
        if(systemRuntime==null){
            systemRuntime = new RuntimeContextImpl(applicationProvider.get(),exp.get());
        }
        return systemRuntime;
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
						field.getDistinguishedName() + " attempted to validate unsupported field type " + dataType);
			}
		}

		return new PropertyValidationContext() {

			@Override
			public Object get(Object arg0) {
				return entryValue.getPropertyValue(field.getDistinguishedName());
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
				return field.getDistinguishedName();
			}

			@Override
			public void setDistinguishedName(String dn) {

			}

		};
	}


	private Annotation[] buildAnnotation(HasConstrains source, FieldDescriptor field) {
		List<Constraint> constraints = source.getConstraintsValues();
		boolean key = field != null & keyDelegate.isJoinableValueField(field)
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
