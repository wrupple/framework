package com.wrupple.vegetate.domain;

public interface HasBlobKey extends Entity {
	
	final String BLOB_FIELD = "blobKey";

	String getBlobKey();
	
	void setBlobKey(String id);
}
