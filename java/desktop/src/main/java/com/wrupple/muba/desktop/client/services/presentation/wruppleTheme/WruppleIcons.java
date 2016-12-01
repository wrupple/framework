package com.wrupple.muba.desktop.client.services.presentation.wruppleTheme;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface WruppleIcons extends ClientBundle {

	@Source("wrupple-big.png")
	ImageResource wruppleBig();

	@Source("wrupple-icon.png")
	ImageResource wruppleIcon();

	@Source("wrupple-small.png")
	ImageResource wrupple();

}
