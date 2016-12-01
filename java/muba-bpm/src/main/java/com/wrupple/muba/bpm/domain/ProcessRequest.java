package com.wrupple.muba.bpm.domain;

import java.util.Date;

import com.wrupple.muba.bootstrap.domain.ExplicitIntent;
import com.wrupple.muba.bootstrap.domain.ImplicitIntent;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;
public interface ProcessRequest extends ManagedObject ,HasEntryId,ImplicitIntent,ExplicitIntent{
	
	int getStatus();
	
	void setStatus(int i);
	
	public Date getHandled();

	public void setHandled(Date handled);
	
	public void setHandler(Long personId);
	
	boolean isArchived();
	
	void setArchived(Boolean disposable);
	
	Date getDue();
	
	/**
	 * @return if the input was an activity booking id , the output would be an activity tracking id
	 */
	Object getOutputId();
	
}
