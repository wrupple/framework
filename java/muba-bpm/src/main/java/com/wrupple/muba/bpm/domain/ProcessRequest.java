package com.wrupple.muba.bpm.domain;

import java.util.Date;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.CatalogIntent;
import com.wrupple.muba.bootstrap.domain.ExplicitIntent;
import com.wrupple.muba.bootstrap.domain.ImplicitIntent;
import com.wrupple.muba.bootstrap.domain.reserved.HasEntryId;
public interface ProcessRequest extends ManagedObject ,CatalogIntent,ExplicitIntent{

	
	//Date getDue();
	

}
