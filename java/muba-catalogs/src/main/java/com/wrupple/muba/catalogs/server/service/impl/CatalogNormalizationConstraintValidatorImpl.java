package com.wrupple.muba.catalogs.server.service.impl;

import javax.validation.ConstraintValidatorContext;

import com.wrupple.muba.catalogs.domain.annotations.CatalogFieldValues;
import com.wrupple.muba.catalogs.server.service.CatalogNormalizationConstraintValidator;

public class CatalogNormalizationConstraintValidatorImpl implements CatalogNormalizationConstraintValidator {


	private String[] normalizedValues;

	@Override
	public void initialize(CatalogFieldValues constraintAnnotation) {
		this.normalizedValues=constraintAnnotation.defaultValueOptions();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if(normalizedValues==null){
			return false;
		}else{
			if(value instanceof String){
				String denormalizedValue = (String) value;
				for(String posibility : normalizedValues){
					if(posibility.equals(denormalizedValue)){
						return true;
					}
				}
				return false;
			}else{
				int index = (Integer) value;
				return index>=0 && index < normalizedValues.length;
			}
		}
	}

}
