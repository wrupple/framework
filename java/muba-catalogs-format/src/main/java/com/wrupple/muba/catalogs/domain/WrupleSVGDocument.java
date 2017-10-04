package com.wrupple.muba.catalogs.domain;

public interface WrupleSVGDocument extends ContentNode {
	final String CATALOG="SVGDocument";

	public String getValue();
	
	public void setValue(String string);
}
