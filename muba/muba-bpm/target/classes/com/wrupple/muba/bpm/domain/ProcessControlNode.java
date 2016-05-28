package com.wrupple.muba.bpm.domain;

import java.util.Date;

import com.wrupple.vegetate.domain.HasCatalogId;
import com.wrupple.vegetate.domain.HasEntryId;
public interface ProcessControlNode extends ManagedObject,HasCatalogId ,HasEntryId{
	
	int getStatus();
	
	void setStatus(int i);
	
	public Date getHandled() ;

	public void setHandled(Date handled) ;
	
	public void setHandler(Long personId);
	
	public Long getHandler();
	
	boolean isDisposable();
	
	void setDisposable(Boolean disposable);
	
	Date getDue();
	
}
