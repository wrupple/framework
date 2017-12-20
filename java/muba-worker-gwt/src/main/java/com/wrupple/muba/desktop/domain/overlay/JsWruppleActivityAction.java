package com.wrupple.muba.desktop.domain.overlay;

import com.google.gwt.resources.client.ImageResource;
import com.wrupple.muba.cms.domain.WruppleActivityAction;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;

@SuppressWarnings("serial")
public final class JsWruppleActivityAction extends JsEntity implements WruppleActivityAction {
	protected JsWruppleActivityAction() {
		super();
	}

	public native void setCommand(String string) /*-{
		this.command = string;
	}-*/;

	@Override
	public native String getCommand() /*-{
		return this.command;
	}-*/;


	@Override
	public native String getDescription() /*-{
		return this.description;
	}-*/;

	public ImageResource getImageResource() {
		Object rawObject = getImageResourceRaw();
		return (ImageResource) rawObject;
	}

	private native Object getImageResourceRaw() /*-{
		return this.imageResource;
	}-*/;

	public void setImageResource(ImageResource resource) {
		this.setImage(ImageTemplate.IMAGE_RESOURCE);
		this.setStaticImageUri(resource.getSafeUri());
	}

}