package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.wrupple.vegetate.domain.FilterData;

public interface FilterableDataProvider<T> extends ProvidesKey<T>  {
	
	public String getCatalog() ;

	public void setCatalog(String catalog);

	public FilterData getFilter() ;

	public void setFilter(FilterData filter) ;
	
	void addDataDisplay(HasData<T> display);

	public void forceUpdateOnDisplays();
	
}
