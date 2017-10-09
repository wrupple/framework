package com.wrupple.muba.catalogs.server.service.impl;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.CatalogActionRequest;
import com.wrupple.muba.event.domain.RuntimeContext;
import com.wrupple.muba.catalogs.domain.CatalogActionContext;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.annotations.InheritanceTree;
import com.wrupple.muba.event.server.service.CatalogInheritanceValidator;
import com.wrupple.muba.catalogs.server.service.SystemCatalogPlugin;

public class CatalogInheritanceValidatorImpl implements CatalogInheritanceValidator {
	private final Provider<RuntimeContext> exp;
    private String catalogid;

    @Inject
	public CatalogInheritanceValidatorImpl(Provider<RuntimeContext> exp) {
		super();
		this.exp = exp;
	}

	@Override
	public void initialize(InheritanceTree constraintAnnotation) {
        catalogid=constraintAnnotation.catalog();
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


			CatalogDescriptor currentParent = null;
			Set<Long> keySet = new HashSet<Long>();
			while (currentParentKey != null) {
				if (keySet.contains(currentParentKey)) {
					return false;
				}

				//currentParent = dictionary.getDescriptorForKey(currentParentKey, readingContext);

                CatalogActionRequestImpl request = new CatalogActionRequestImpl();
                try {
                    request.setCatalog(catalogid);
                    request.setEntry(currentParentKey);
                    currentParent =exc.getEventBus().fireEvent(request ,exc,null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
