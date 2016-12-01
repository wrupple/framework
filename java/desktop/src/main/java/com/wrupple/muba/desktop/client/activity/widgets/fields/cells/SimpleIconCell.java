package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.inject.Inject;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public class SimpleIconCell extends AbstractCell<JsCatalogEntry> {

	private final ImageTemplate template;
	
	@Inject
	public SimpleIconCell(ImageTemplate template){
		super("click");
		this.template = template;
	}
	

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			JsCatalogEntry value, SafeHtmlBuilder sb) {
		sb.appendHtmlConstant("<div class='simple-icon-cell'>");

		String image;
		try{
			image=value.getImage();
		}catch(Exception e){
			GWT.log("image key not properly passed as string",e);
			//TODO not necesary if keys are beeing properly converted
			image=getImage(value);
		}
		
		String name = value.getName();
		
		SafeHtml imagehtml;
		
		if(image==null){
			String staticImage=value.getStaticImageUrl();
			if(staticImage==null){
				//no image
				if(value.getStaticImageUri()==null){
					if(name==null){
						imagehtml = template.noImageOutput();
					}else{
						imagehtml = null;
					}
					
					
				}else{
					imagehtml = template.urlImageOutput(value.getStaticImageUri());
				}
			}else{
				//staticImage has to be non null
				imagehtml = template.urlImageOutput(staticImage);
			}
		}else{
			if(image.equals(ImageTemplate.IMAGE_RESOURCE)){
				//safe uri resource 
				imagehtml = template.urlImageOutput(value.getStaticImageUri());
			}else if(image.startsWith("/")||image.startsWith("http://")){
				//FIXME use SafeUri... or is this safe? check all cells for safety XSS issues
				imagehtml = template.urlImageOutput(image);
			}else{
				//TODO use Vegetate binding
				imagehtml = template.smallImageOutput(image);
			}
			
		}
		if(imagehtml!=null){
			sb.appendHtmlConstant("<div class='simple-icon-cell-image'>");
			sb.append(imagehtml);
			sb.appendHtmlConstant("</div>");
		}
		
		if(name==null){
			name = value.getStringValue();
		}
		if(name==null){
			name = "";
		}
		if(imagehtml==null){
			sb.appendHtmlConstant("<div class='simple-icon-cell-label simple-icon-cell-textOnly'><span>");
		}else{
			sb.appendHtmlConstant("<div class='simple-icon-cell-label'><span>");
		}
		sb.appendEscaped(name);
		sb.appendHtmlConstant("</span></div>");

		sb.appendHtmlConstant("</div>");
	}


	private native String getImage(JsCatalogEntry value) /*-{
		return String(value.image);
	}-*/;

}
