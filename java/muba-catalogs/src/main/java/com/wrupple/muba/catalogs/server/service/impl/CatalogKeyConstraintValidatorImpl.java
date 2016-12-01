package com.wrupple.muba.catalogs.server.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.annotations.CatalogKey;
import com.wrupple.muba.catalogs.server.service.CatalogKeyConstraintValidator;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

public class CatalogKeyConstraintValidatorImpl implements CatalogKeyConstraintValidator {

	private final SystemCatalogPlugin cms;
	private final Provider<ExcecutionContext> exp;
	private String foreignCatalog;
	private boolean unique;

	@Inject
	public CatalogKeyConstraintValidatorImpl(Provider<ExcecutionContext> exp, SystemCatalogPlugin cms) {
		this.exp = exp;

		this.cms = cms;
	}

	@Override
	public void initialize(CatalogKey constraintAnnotation) {
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
			read.setFilter(null);
			read.setEntry(null);
			read.setCatalog(foreignCatalog);
			if (unique || value instanceof Collection) {
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
		context.setFilter(
				FilterDataUtils.createSingleKeyFieldFilter(context.getCatalogDescriptor().getKeyField(), value));

		context.getCatalogManager().getRead().execute(context);
		return context.getResults() != null && !context.getResults().isEmpty();
	}

	private boolean foundValue(CatalogActionContext context, Object value) throws Exception {
		context.setEntry(value);

		context.getCatalogManager().getRead().execute(context);
		return context.getResults() != null && !context.getResults().isEmpty();
	}

}
