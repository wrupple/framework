package com.wrupple.vegetate.domain;

public interface CatalogActionTriggerHandler {
	
	public interface Trigger {
		void before (CatalogDescriptor catalog,CatalogKey old, CatalogKey e,CatalogExcecutionContext context)throws Exception;
		void after(CatalogDescriptor catalog,CatalogKey e, CatalogExcecutionContext context) throws Exception;
		CatalogTrigger getTrigger();
	}
	
	void process(CatalogActionTrigger trigger) ;

	Trigger getUpdateHandler();
	void addUpdate(Trigger trigger);
	
	Trigger getCreateHandler();
	void addCreate(Trigger trigger);
	
	Trigger getDeleteHandler();
	void addDelete(Trigger trigger);
}
