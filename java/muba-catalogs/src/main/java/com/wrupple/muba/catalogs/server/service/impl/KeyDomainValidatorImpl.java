package com.wrupple.muba.catalogs.server.service.impl;

import com.wrupple.muba.catalogs.server.domain.CatalogActionRequestImpl;
import com.wrupple.muba.event.EventBus;
import com.wrupple.muba.event.domain.CatalogDescriptor;
import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.ContainerContext;
import com.wrupple.muba.event.domain.DataEvent;
import com.wrupple.muba.event.domain.annotations.ForeignKey;
import com.wrupple.muba.event.server.service.KeyDomainValidator;
import com.wrupple.muba.event.server.service.ObjectNativeInterface;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KeyDomainValidatorImpl implements KeyDomainValidator {

    private final Provider<ContainerContext> exp;
    private final Provider<EventBus> bus;
	private final ObjectNativeInterface nativeInterface;
	private String foreignCatalog;
	private boolean unique;

	@Inject
    public KeyDomainValidatorImpl(@Named(ContainerContext.SYSTEM) Provider<ContainerContext> exp, Provider<EventBus> bus, ObjectNativeInterface nativeInterface) {
        this.exp = exp;
		this.bus = bus;
		this.nativeInterface=nativeInterface;
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

			CatalogActionRequestImpl context = new CatalogActionRequestImpl();
			context.setCatalog(foreignCatalog);
			context.setName(DataEvent.READ_ACTION);
            ContainerContext runtime = this.exp.get();
            if (unique && nativeInterface.isCollection(value)) {
				Collection<Object> colection = (Collection<Object>) value;
				Set<Object> uniqueCollection = new HashSet<Object>();
				for (Object p : colection) {
					uniqueCollection.add(p);
				}
				if (unique && colection.size() > uniqueCollection.size()) {
					return false;
				}
				try {
					return foundValues(foreignCatalog,runtime,context, uniqueCollection);
				} catch (Exception e) {
					throw new RuntimeException("Failed to validate foreign keys ", e);
				}
			} else {
				try {
					return foundValue(runtime,context, value);
				} catch (Exception e) {
					throw new RuntimeException("Failed to validate foreign key: " + value, e);
				}
			}

		}

	}

    private boolean foundValues(String foreignCatalog, ContainerContext runtime, CatalogActionRequestImpl context, Set<Object> value) throws Exception {
        context.setCatalog(CatalogDescriptor.CATALOG_ID);
        context.setEntry(foreignCatalog);
        CatalogDescriptor foreignCatalogDescriptor=bus.get().fireEvent(context,runtime,null);
        context.setEntry(null);
        context.setCatalog(foreignCatalog);
		context.setFilter(
				FilterDataUtils.createSingleKeyFieldFilter(foreignCatalogDescriptor.getKeyField(), value));

        //FIXME TEST FOR SATISFACTION OF CRITERIA without deserializing entries
		List<CatalogEntry> results = bus.get().fireEvent(context,runtime,null);
		return results != null && !results.isEmpty();
	}

    private boolean foundValue(ContainerContext runtime, CatalogActionRequestImpl context, Object value) throws Exception {
        context.setEntry(value);
//FIXME TEST FOR existence of key

        List<CatalogEntry> results = bus.get().fireEvent(context,runtime,null);
        return results != null && !results.isEmpty();
	}

}
