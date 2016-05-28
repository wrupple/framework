package com.wrupple.muba.catalogs.server.service.impl;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintValidatorContext;

import org.apache.bval.jsr303.ConstraintValidatorContextImpl;
import org.apache.bval.jsr303.GroupValidationContext;

import com.wrupple.muba.catalogs.server.service.CatalogInheritanceValidator;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor;
import com.wrupple.muba.catalogs.server.service.CatalogPropertyAccesor.Session;
import com.wrupple.vegetate.domain.CatalogActionRequest;
import com.wrupple.vegetate.domain.CatalogEntry;
import com.wrupple.vegetate.domain.CatalogExcecutionContext;
import com.wrupple.vegetate.server.domain.annotations.InheritanceTree;

public class CatalogInheritanceValidatorImpl implements CatalogInheritanceValidator {
	private final CatalogPropertyAccesor accessor;
	private String parentCatalog;
	Session session;
	@Inject
	public CatalogInheritanceValidatorImpl(CatalogPropertyAccesor accessor) {
		super();
		this.accessor = accessor;
	}

	
	@Override
	public void initialize(InheritanceTree constraintAnnotation) {
	}

	@Override
	public boolean isValid(Object v, ConstraintValidatorContext c) {
		if (v == null) {
			return true;
		} else {
			GroupValidationContext<CatalogExcecutionContext> validationContext = (GroupValidationContext<CatalogExcecutionContext>) ((ConstraintValidatorContextImpl) c).getValidationContext();
			CatalogExcecutionContext context = (CatalogExcecutionContext) validationContext.getBean();
			CatalogContextFieldAccessStrategy access=  (CatalogContextFieldAccessStrategy) validationContext.getAccess();
			if(parentCatalog==null){
				parentCatalog=access.getField().getForeignCatalogName();
			}
			Long currentParentKey = (Long) v;
			CatalogExcecutionContext readingContext=context.getRequest().getStorageManager().spawn(context);
			readingContext.setAction(CatalogActionRequest.READ_ACTION);
			readingContext.setCatalog(parentCatalog);
			CatalogEntry currentParent;
			Set<Long> keySet = new HashSet<>();
			try{
				while(currentParentKey!=null){
					if(keySet.contains(currentParentKey)){
						return false;
					}
					readingContext.setEntry(currentParentKey);
					context.getRequest().getStorageManager().getRead().execute(readingContext);
					currentParent = readingContext.getResults()==null?null:readingContext.getResults().isEmpty()?null:readingContext.getResults().get(0);
					if(currentParent==null){
						currentParentKey = null;
					}else{
						if(session==null){
							session = accessor.newSession(currentParent);
						}
						
						currentParentKey = (Long) accessor.getPropertyValue(readingContext.getCatalogDescriptor(), access.getField(), currentParent, null, session);
						keySet.add(currentParentKey);
					}
				}
			}catch(Exception e){
				throw new IllegalArgumentException(e);
			}
			
			
			return true;
		}
		
	}

}
