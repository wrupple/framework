package com.wrupple.muba.catalogs.server.service.impl;

import java.util.Arrays;
import java.util.Collection;

import javax.validation.ConstraintValidatorContext;

import org.apache.bval.jsr303.ConstraintValidatorContextImpl;
import org.apache.bval.jsr303.GroupValidationContext;

import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.server.domain.annotations.CatalogKey;
import com.wrupple.vegetate.server.services.CatalogKeyConstraintValidator;

public class CatalogKeyConstraintValidatorImpl implements CatalogKeyConstraintValidator {

	private String foreignCatalog;

	@Override
	public void initialize(CatalogKey constraintAnnotation) {
		this.foreignCatalog = constraintAnnotation.foreignCatalog();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext c) {
		if (value == null) {
			return true;
		} else {
			ConstraintValidatorContextImpl cc = (ConstraintValidatorContextImpl) c;
			GroupValidationContext<CatalogExcecutionContext> ccc = (GroupValidationContext<CatalogExcecutionContext>) cc.getValidationContext();
			CatalogExcecutionContext context = (CatalogExcecutionContext) ccc.getBean();

			try {
				CatalogExcecutionContext read=context.getRequest().getStorageManager().spawn(context);
				read.setFilter(null);
				read.setCatalog(foreignCatalog);
				if (value instanceof Collection) {
					Collection<Object> colection = (Collection<Object>) value;
					for (Object v : colection) {
						if (!foundValue(read, v)) {
							return false;
						}
					}
					return true;
				} else {
					return foundValue(read, value);
				}

			} catch (Exception e) {
				System.err.println(e);
				System.err.println(Arrays.toString(e.getStackTrace()));
				return false;
			}
		}

	}

	private boolean foundValue(CatalogExcecutionContext context, Object value) throws Exception {
		context.setEntry(value);
		
		context.getRequest().getStorageManager().getRead().execute(context);
		return context.getResults()!=null && !context.getResults().isEmpty();
	}

}
