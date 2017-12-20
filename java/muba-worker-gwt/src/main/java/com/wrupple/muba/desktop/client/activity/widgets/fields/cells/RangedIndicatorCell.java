package com.wrupple.muba.desktop.client.activity.widgets.fields.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;

public class RangedIndicatorCell extends AbstractCell<Double> {
	
	public interface IndicatorStyles extends CssResource{
		
		String getIndicatorStyle();
	}
	
	
	public interface IndicatorTemplate  extends SafeHtmlTemplates {
		@Template("<div class=\"{0}\"><span style=\"width: {1}%;  background-color: {2};\">{3}</span></div>")
		SafeHtml output(String meterClass, int pct,String rgb,int colotpoint);
	}
	
	private final int RANGE_SIZE;
	IndicatorTemplate template;
	IndicatorStyles style;
	private final int RANGE_MAX;
	
	public RangedIndicatorCell( int max,IndicatorStyles style) {
		RANGE_MAX = Math.abs(max);
		RANGE_SIZE = Math.abs(RANGE_MAX-(RANGE_MAX*-1));
		this.style=style;
		this.template=GWT.create(IndicatorTemplate.class);
	}
	
	public RangedIndicatorCell( int max) {
		this(max,null);
	}


	public IndicatorStyles getStyle() {
		return style;
	}

	public void setStyle(IndicatorStyles style) {
		this.style = style;
	}

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			Double value, SafeHtmlBuilder sb) {
		double absoluteValue = value+RANGE_MAX;
		double ratio = absoluteValue/RANGE_SIZE;
		int width = (int)(ratio*100);
		
		//0% is red 100% is green
		//1530 is red 1010 is green
		int colorPoint = (int)(ratio*520);
		colorPoint = 520-colorPoint;
		colorPoint = colorPoint + 1010;
		
		String rgb = GWTUtils.getColorFromSpace(colorPoint, 0, 0, 0);
		String styleName = style==null?"":style.getIndicatorStyle();
		
		SafeHtml html = template.output(styleName, width,rgb,colorPoint);
		sb.append(html);
	}

}
