package com.wrupple.muba.bootstrap.server.chain.command.impl;

import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.wrupple.muba.bootstrap.domain.ContractDescriptor;
import com.wrupple.muba.bootstrap.domain.ExcecutionContext;
import com.wrupple.muba.bootstrap.server.service.ValidationGroupProvider;

public abstract class SyntaxParsingCommang implements Command {

	private final Validator validator;
	private final Class<?>[] groups;
	/**
	 * set context writing precedence of the value of a property in favor of what's in the sentence, over the contract
	 */
	protected boolean sentenceOverContract;

	@Inject
	public SyntaxParsingCommang(Validator validatorProvider, ValidationGroupProvider a) {
		super();
		sentenceOverContract = true;
		if (validatorProvider == null) {
			this.groups = null;
			this.validator = null;
		} else {
			this.groups = a.get();
			this.validator = validatorProvider;
		}
	}

	@Override
	public boolean execute(Context ctx) throws Exception {
		ExcecutionContext requestContext = (ExcecutionContext) ctx;
		Set<ConstraintViolation<?>> violations;

		Context context = createBlankContext(requestContext);
		Object contract = requestContext.getServiceContract();
		String key, value;
		if (contract == null) {

			String[] tokens = requestContext.getServiceManifest().getGrammar();
			for (int i = 0; i < tokens.length; i++) {
				key = tokens[i];
				if (requestContext.hasNext()) {
					value = requestContext.next();
					context.put(key, value);
				} else {
					value = null;
				}

			}
		} else {
			String[] tokens = requestContext.getServiceManifest().getGrammar();

			for (int i = 0; i < tokens.length; i++) {
				key = tokens[i];

				if (PropertyUtils.isWriteable(contract, key)
						&& (PropertyUtils.getProperty(contract, key) == null || sentenceOverContract)) {
					if (requestContext.hasNext()) {
						value = requestContext.next();
					} else {
						value = null;
					}
					PropertyUtils.setProperty(contract, key, value);
				}

			}

			if (validator == null) {

			} else {
				violations = (Set) validator.validate(contract, groups);
				if (violations == null || violations.isEmpty()) {
					ContractDescriptor descriptor = requestContext.getServiceManifest().getContractDescriptor();
					if (descriptor != null) {
						Collection<String> fields = descriptor.getFieldsIds();
						Object v;
						for (String field : fields) {
							v = PropertyUtils.getProperty(contract, field);
							context.put(field, v);
						}
					}
				} else {
					requestContext.setConstraintViolations(violations);
					return PROCESSING_COMPLETE;
				}
			}
		}

		requestContext.setServiceContext(context);
		return CONTINUE_PROCESSING;
	}

	protected abstract Context createBlankContext(ExcecutionContext requestContext);

}
