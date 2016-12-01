package com.wrupple.muba.desktop.client.services.presentation;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;

public interface ImageTemplate {
	int BIG = 512;
	int THUMBNAIL = 128;
	int SMALL = 96;
	int TINY = 30;
	
	String IMAGE_RESOURCE="data:image";
	
	String THEMED_RESOURCE="themeResource";
	
	SafeHtml tinyImageOutput(String fileId);
	
	SafeHtml smallImageOutput(String fileId);
	
	SafeHtml thumbnailImageOutput(String fileId);
	
	SafeHtml bigImageOutput(String fileId);
	
	SafeHtml fullsizeImageOutput(String fileId);

	SafeHtml noImageOutput();

	SafeHtml urlImageOutput(String staticImage);

	SafeHtml urlImageOutput(SafeUri staticImageUri);

	
}
