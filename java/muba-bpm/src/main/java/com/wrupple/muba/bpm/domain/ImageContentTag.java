package com.wrupple.muba.bpm.domain;

public interface ImageContentTag {
	
	String CATALOG = "ImageContentTag";

	String getImage();
	
	String getTaggedCatalog();
	
	String getTaggedEntry();
	
	int getStartX();
	
	int getStartY();
	
	int getEndX();
	
	int getEndY();
	
}