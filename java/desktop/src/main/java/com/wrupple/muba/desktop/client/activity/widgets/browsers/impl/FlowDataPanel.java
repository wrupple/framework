package com.wrupple.muba.desktop.client.activity.widgets.browsers.impl;

import java.util.Collections;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import com.wrupple.muba.desktop.client.services.presentation.layout.ValueDependableStyleDelegate;

public class FlowDataPanel<T> extends CellList<T> implements  RequiresResize{
	
	
	interface InlineTemplate extends SafeHtmlTemplates {
		@Template("<span onclick=\"\" __idx=\"{0}\" class=\"{1}\" style=\"outline:none; {3}\" >{2}</span>")
		SafeHtml div(int idx, String classes, SafeHtml cellContents,String styleContents);

	}
	
	private ValueDependableStyleDelegate styleDelegate;
	private boolean inline;
	private String cellWrapperClass;

	InlineTemplate template = GWT.create(InlineTemplate.class);

	public FlowDataPanel(Cell<T> cell,ProvidesKey<T > keyProvider,ValueDependableStyleDelegate styleDelegate) {
		super(cell,keyProvider);
		inline = true;
	}

	@Override
	protected void renderRowValues(SafeHtmlBuilder sb, List<T> values,
			int start, SelectionModel<? super T> selectionModel) {
		if(!inline){
			super.renderRowValues(sb, values, start, selectionModel);
		}else{
			Cell<T> cell = super.getCell();
			int length = values.size();
			int end = start + length;
			String styleClass=cellWrapperClass==null?"inlineForeignKeyValue":cellWrapperClass;
			String appliedStyle ;
			for (int i = start; i < end; i++) {
				T value = values.get(i - start);

				SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
				Context context = new Context(i, 0, getValueKey(value));
				cell.render(context, value, cellBuilder);
				
				if(styleDelegate!=null&&value!=null){
					appliedStyle = styleDelegate.getCSSAttributes( value);
					if(appliedStyle==null){
						appliedStyle = "";
					}
				}else{
					appliedStyle ="";
				}
				
				sb.append(template.div(i, styleClass, cellBuilder.toSafeHtml(),appliedStyle));
			}
		}
		
	}

	@Override
	public void onResize() {
		// TODO Auto-generated method stub
		
	}
	public void setCellWrapperClass(String cellWrapperClass) {
		this.cellWrapperClass = cellWrapperClass;
	}

	public void setInline(boolean inline) {
		this.inline = inline;
	}

	public void upateValue(int visibleIndex, JavaScriptObject receivedUpdate) {
		T c = (T)receivedUpdate;
		setRowData(visibleIndex,Collections.singletonList(c) );
	}
	
	@Override
	public Cell<T> getCell() {
		return super.getCell();
	}

}
