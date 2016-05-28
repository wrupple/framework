package com.wrupple.muba.catalogs.server.service.impl;

import java.util.HashSet;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.apache.bval.jsr303.ConstraintValidatorContextImpl;
import org.apache.bval.jsr303.GroupValidationContext;

import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
import com.wrupple.vegetate.domain.FilterData;
import com.wrupple.vegetate.server.domain.annotations.ConsistentFields;
import com.wrupple.vegetate.server.services.FieldConsistentcyValidator;
import com.wrupple.vegetate.server.services.impl.FilterDataUtils;

public class FieldConsistentcyValidatorImpl implements FieldConsistentcyValidator {

	@Override
	public void initialize(ConsistentFields constraintAnnotation) {

	}

	@Override
	public boolean isValid(List<Long> value, ConstraintValidatorContext c) {
		if (value == null) {
			return false;
		} else {
			ConstraintValidatorContextImpl cc = (ConstraintValidatorContextImpl) c;
			GroupValidationContext<CatalogExcecutionContext> ccc = (GroupValidationContext<CatalogExcecutionContext>) cc.getValidationContext();
			CatalogExcecutionContext context = (CatalogExcecutionContext) ccc.getBean();
			FilterData fd = FilterDataUtils.createSingleKeyFieldFilter(CatalogEntry.ID_FIELD, value);
			CatalogExcecutionContext read = context.getRequest().getStorageManager().spawn(context);
			read.setCatalog(FieldDescriptor.CATALOG_ID);
			read.setFilter(fd);
			 try {
				context.getRequest().getStorageManager().getRead().execute(read);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			 List<FieldDescriptor> fields = (List)read.getResults();
			 if(fields.size()==value.size()){
				// hard keys cannot be multiple	 
				 for(FieldDescriptor field: fields){
					 if((field.isHardKey() && field.getForeignCatalogName()==null )||(field.isMultiple() && field.isHardKey())){
						 return false;
					 }
				 }
			 }else{
				 return false;
			 }
			
			
			//no repeated fields
			return new HashSet<Long>(value).size()==value.size();
		}
		
	}

}
