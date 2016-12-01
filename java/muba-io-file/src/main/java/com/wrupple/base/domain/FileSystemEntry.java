package com.wrupple.base.domain;

import com.wrupple.muba.catalogs.domain.ContentNode;
import com.wrupple.muba.catalogs.domain.Versioned;

public interface FileSystemEntry extends ContentNode ,Versioned{
	
	String getMime();
	
	/**
	 * @return size in bytes
	 */
	long getSize();
	
	boolean isDirectory();
	
	String getParent();
	
	String getValue();
	
}
