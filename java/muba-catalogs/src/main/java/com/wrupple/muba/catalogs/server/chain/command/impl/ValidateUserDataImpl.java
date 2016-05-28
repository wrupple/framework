package com.wrupple.muba.catalogs.server.chain.command.impl;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.chain.Context;

import com.wrupple.muba.catalogs.server.chain.command.ValidateUserData;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FilterData;

@Singleton
public class ValidateUserDataImpl implements ValidateUserData {

	private final Provider<Validator> validatorProvider;
	private final Class<?>[] groups;

	@Inject
	public ValidateUserDataImpl(Provider<Validator> validatorProvider) {
		super();
		this.validatorProvider = validatorProvider;
		groups = new Class<?>[] { javax.validation.groups.Default.class };
	}

	@Override
	public boolean execute(Context c) throws Exception {
		CatalogExcecutionContext context = (CatalogExcecutionContext) c;
		CatalogEntry incomming = (CatalogEntry) context.getEntryValue();
		FilterData filter = context.getFilter();
		String targetEntryId = (String) context.get(CatalogActionRequest.CATALOG_ENTRY_PARAMETER);
		
		if (targetEntryId != null) {
			if ("null".equalsIgnoreCase(targetEntryId)) {
				context.put(CatalogActionRequest.CATALOG_ENTRY_PARAMETER, null);
				targetEntryId = null;
			}
		}
		if (incomming != null || filter != null) {
			Validator validator = validatorProvider.get();
			Set<ConstraintViolation<?>> aggregate = (Set) validator.validate(context, groups);
			context.setConstraintViolations(aggregate);
		}

		return CONTINUE_PROCESSING;
	}


}
