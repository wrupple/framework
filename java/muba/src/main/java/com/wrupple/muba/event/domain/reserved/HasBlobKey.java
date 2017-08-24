package com.wrupple.muba.event.domain.reserved;

import com.wrupple.muba.event.domain.Entity;

public interface HasBlobKey extends Entity {
	
	final String BLOB_FIELD = "blobKey";

	String getBlobKey();
	
	void setBlobKey(String id);
}
