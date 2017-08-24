package com.wrupple.muba.catalogs.server.service.impl;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.catalogs.domain.CatalogDescriptor;
import com.wrupple.muba.catalogs.domain.annotations.InheritanceTree;
import com.wrupple.muba.catalogs.server.service.CatalogInheritanceValidator;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

public class CatalogInheritanceValidatorImpl implements CatalogInheritanceValidator {
	private final SystemCatalogPlugin dictionary;
	private final Provider<RuntimeContext> exp;

	@Inject
	public CatalogInheritanceValidatorImpl(Provider<RuntimeContext> exp, SystemCatalogPlugin cms) {
		super();
		this.exp = exp;
		this.dictionary = cms;
	}

	@Override
	public void initialize(InheritanceTree constraintAnnotation) {
	}

	@Override
	public boolean isValid(Long v, ConstraintValidatorContext c) {
		if (v == null) {
			return true;
		} else {
			// checks parent hierarchu for duplicates, and cycles and such
			Long currentParentKey = (Long) v;
			RuntimeContext exc = exp.get();
			// TODO what domain to use for this context? Catalog Action Request
			// has the requested domain, but we cannot aaccess it although
			// numeric ids are supposed to trascend domains anymay
			CatalogActionContext readingContext = dictionary.spawn(exc);

			CatalogDescriptor currentParent = null;
			Set<Long> keySet = new HashSet<Long>();
			while (currentParentKey != null) {
				if (keySet.contains(currentParentKey)) {
					return false;
				}

				currentParent = dictionary.getDescriptorForKey(currentParentKey, readingContext);

				if (currentParent == null) {
					currentParentKey = null;
				} else {
					currentParentKey = currentParent.getParent();
				}
			}

			return true;
		}

	}

}
