package com.wrupple.muba.desktop.client.services.presentation.impl;

import com.google.gwt.core.client.JsArray;
import com.wrupple.muba.desktop.client.activity.widgets.browsers.impl.AggregateDataCanvas;
import com.wrupple.muba.desktop.client.services.presentation.AggregatedCatalogEntryRenderService;
import com.wrupple.muba.desktop.domain.RaphaelAttributes;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogKey;
import org.sgx.raphael4gwt.raphael.Path;
import org.sgx.raphael4gwt.raphael.PathCmd;
import org.sgx.raphael4gwt.raphael.Set;
import org.sgx.raphael4gwt.raphael.Text;

import java.util.List;

public class PieChartGroupRenderService implements AggregatedCatalogEntryRenderService {

	protected double rad;
	double angle;
	double start;
	int cx,cy,r;
	
	private Set chart;
	
	private String canvasGroupLabelPath;
	private String canvasGroupRGBColorPath;
	

	public void setGroupLabelPath(String v){
		this.canvasGroupLabelPath=v;
	}
	
	public void setGroupRGBColorPath(String v){
		this.canvasGroupRGBColorPath=v;
	}
	
	@Override
	public void startDrawing(AggregateDataCanvas<JsCatalogEntry> canvas) {
		if(chart != null){
			chart.remove();
			chart =null;
		}
		rad = Math.PI / 180;
		angle = 0;
		start = 0;	
		int height = GWTUtils.getNonZeroParentHeight(canvas);
		int width = GWTUtils.getNonZeroParentWidth(canvas);
		int smallest = height>=width ? width : height;
		this.cx= width/2;
		this.cy=height/2;
		this.r=(int) (smallest*.35);
	}
	
	@Override
	public void renderGroup(AggregateDataCanvas<JsCatalogEntry> canvas,List<JsCatalogEntry> data,
			JsArray<JsCatalogEntry> members, String value) {
		
		if(chart==null){
			chart = canvas.getPaper().set();
		}
			
	
		
		RaphaelAttributes attributes;
		
		String stroke = "#ffffff";
		String baseColor = getGroupBaseColor(members,value);
		String label = getGroupLabel(members,value);
		double numericValue = getGroupNumericValue(data,members,value);
		double angleplus = 360 * numericValue / getDataTotal(data);
		double popangle = angle + (angleplus / 2);
		
		int delta = 30;
		
		attributes = new RaphaelAttributes();
		attributes.setFill(baseColor);
		attributes.setStroke(stroke);
		attributes.setStrokeWidth(3);
		Path p = sector(cx, cy, r, angle, angle + angleplus, attributes, canvas);
		
		int textx=(int) (cx + (r + delta + 55) * Math.cos(-popangle * rad));
		int txtxy=(int)(cy + (r + delta + 25)* Math.sin(-popangle * rad));
		Text txt = canvas.getPaper().text(textx,txtxy , label);
		attributes = new RaphaelAttributes();
		attributes.setFill(baseColor);
		attributes.setStroke(RaphaelAttributes.NONE);
		attributes.setOpacity(1);
		attributes.setFontFamily(RaphaelAttributes.ARIAL_SANS_SERIFF);
		attributes.setFontSize("20px");
		txt.attr(attributes.getValue());
		
		angle += angleplus;
		chart.push(p);
		chart.push(txt);
		start += .1;
	}



	protected Path sector(int cx, int cy, int r, double startAngle, double endAngle, RaphaelAttributes params,  AggregateDataCanvas<?> canvas) {
		double x1 = (int) (cx + r * Math.cos(-startAngle * rad));
		double x2 = cx + r * Math.cos(-endAngle * rad);
		double y1 = cy + r * Math.sin(-startAngle * rad);
		double y2 = cy + r * Math.sin(-endAngle * rad);
		
		PathCmd builder = new PathCmd(cx,cy);
		//builder.M(cx, cy);
		builder.L(x1, y1);
		builder.A(r, r, 0, (endAngle - startAngle > 180) ? 1 : 0, 0, x2, y2);
		builder.Z();
		Path  path = canvas.getPaper().path(builder);
		path.attr(params.getValue());
		return path;
	}
	
	
	

	private String getGroupBaseColor(JsArray<JsCatalogEntry> members, String value) {
		JsCatalogKey member = members.get(0);
		return "#"+GWTUtils.getAttributeFromPath(member, canvasGroupRGBColorPath);
	}

	private String getGroupLabel(JsArray<JsCatalogEntry> members, String value) {
		JsCatalogKey member = members.get(0);
		return GWTUtils.getAttributeFromPath(member, canvasGroupLabelPath);
	}
	
	private double getGroupNumericValue(List<JsCatalogEntry> data,
			JsArray<JsCatalogEntry> members, String value) {
		return members.length();
	}
	private double getDataTotal(List<JsCatalogEntry> data) {
		return data.size();
	}
}
