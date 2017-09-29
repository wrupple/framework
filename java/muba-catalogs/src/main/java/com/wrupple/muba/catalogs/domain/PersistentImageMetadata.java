package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.reserved.HasBlobKey;

public interface PersistentImageMetadata extends HasBlobKey{
	String CATALOG ="PersistentImageMetadata";
	String CONTENT = "content";


	
}
