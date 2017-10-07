package com.wrupple.muba.catalogs.server.service.impl;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.server.service.KeyDomainValidator;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;
import com.wrupple.muba.event.server.service.ObjectNativeInterface;

public class KeyDomainValidatorImpl implements KeyDomainValidator {

	private final SystemCatalogPlugin cms;
	private final Provider<RuntimeContext> exp;
	private final ObjectNativeInterface nativeInterface;
	private String foreignCatalog;
	private boolean unique;

	@Inject
	public KeyDomainValidatorImpl(Provider<RuntimeContext> exp, SystemCatalogPlugin cms, ObjectNativeInterface nativeInterface) {
		this.exp = exp;
		this.nativeInterface=nativeInterface;
		this.cms = cms;
	}

	@Override
	public void initialize(Annotation raw) {
		ForeignKey constraintAnnotation = (ForeignKey) raw;
		this.foreignCatalog = constraintAnnotation.foreignCatalog();
		this.unique = constraintAnnotation.unique();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext c) {
		// validate foreign keys
		if (value == null) {
			return true;
		} else {

			CatalogActionContext read = cms.spawn(exp.get());
			read.getRequest().setFilter(null);
			read.getRequest().setEntry(null);
			read.getRequest().setCatalog(foreignCatalog);
			if (unique || nativeInterface.isCollection(value)) {
				Collection<Object> colection = (Collection<Object>) value;
				Set<Object> uniqueCollection = new HashSet<Object>();
				for (Object p : colection) {
					uniqueCollection.add(p);
				}
				if (unique && colection.size() > uniqueCollection.size()) {
					return false;
				}
				try {
					return foundValues(read, uniqueCollection);
				} catch (Exception e) {
					throw new RuntimeException("Failed to validate foreign keys ", e);
				}
			} else {
				try {
					return foundValue(read, value);
				} catch (Exception e) {
					throw new RuntimeException("Failed to validate foreign key: " + value, e);
				}
			}

		}

	}

	private boolean foundValues(CatalogActionContext context, Set<Object> value) throws Exception {
		context.getRequest().setFilter(
				FilterDataUtils.createSingleKeyFieldFilter(context.getCatalogDescriptor().getKeyField(), value));

		context.getCatalogManager().getRead().execute(context);
		return context.getResults() != null && !context.getResults().isEmpty();
	}

	private boolean foundValue(CatalogActionContext context, Object value) throws Exception {
		context.getRequest().setEntry(value);

		context.getCatalogManager().getRead().execute(context);
		return context.getResults() != null && !context.getResults().isEmpty();
	}

}
