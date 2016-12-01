package com.wrupple.muba.bootstrap.domain.reserved;

import com.wrupple.muba.bootstrap.domain.Entity;

public interface HasBlobKey extends Entity {
	
	final String BLOB_FIELD = "blobKey";

	String getBlobKey();
	
	void setBlobKey(String id);
}
