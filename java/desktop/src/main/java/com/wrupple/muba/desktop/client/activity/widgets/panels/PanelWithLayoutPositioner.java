package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegate;

public class PanelWithLayoutPositioner extends ResizeComposite  implements HasScrollHandlers{
	
	protected final LayoutPanel panel;
	protected IndexedLayoutDelegate indexedLayoutDelegate;
	protected double zoomFactor;

	public PanelWithLayoutPositioner(IndexedLayoutDelegate indexedLayoutDelegate) {
		super();
		zoomFactor=1;
		this.indexedLayoutDelegate=indexedLayoutDelegate;
		panel = new LayoutPanel();
		 setAlwaysShowScrollBars(false);
		initWidget(panel);
		// Hack to account for the IE6/7 scrolling bug described here:
	    //   http://stackoverflow.com/questions/139000/div-with-overflowauto-and-a-100-wide-table-problem
	   getScrollableElement().getStyle().setProperty("zoom", "1");
	   
	}
	
	
	protected Element positionElement(IsWidget w, int i) {
		if(panel.getWidgetIndex(w)<0){
			panel.add(w);
		}
		Element container = panel.getWidgetContainerElement(w.asWidget());
		
		indexedLayoutDelegate.positionElement(panel,container,w.asWidget(),i);
		
		return container;
	}
	
	public void clear() {
		panel.clear();
	}

	public void trim(int fromIndex, int toIndex) {
		for(; fromIndex<=toIndex ; fromIndex++){
			panel.remove(fromIndex);
		}
	}
	
	public void forceLayout() {
		panel.forceLayout();
	}
	
	public void animate(int milis){
		panel.animate(milis);
	}

	@Override
	public HandlerRegistration addScrollHandler(ScrollHandler handler) {
		return panel.addDomHandler(handler, ScrollEvent.getType());
	}

	public IndexedLayoutDelegate getIndexedLayoutDelegate() {
		return indexedLayoutDelegate;
	}

	public void setIndexedLayoutDelegate(IndexedLayoutDelegate indexedLayoutDelegate) {
		this.indexedLayoutDelegate = indexedLayoutDelegate;
	}

	public double getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(double zoomFactor) {
		if(zoomFactor<.1){
			//no less than 
			zoomFactor=.1;
		}
		this.zoomFactor = zoomFactor;
	}
	
	public void setOverflow(String string) {
		getElement().getStyle().setProperty( "overflow", string);
	}
	
	private Element getScrollableElement() {
		return panel.getElement();
	}
	
	/**
	   * Sets whether this panel always shows its scroll bars, or only when
	   * necessary.
	   * 
	   * @param alwaysShow <code>true</code> to show scroll bars at all times
	   */
	  public void setAlwaysShowScrollBars(boolean alwaysShow) {
	    getScrollableElement().getStyle().setOverflow(alwaysShow ? Overflow.SCROLL : Overflow.AUTO);
	  }
	
	public int getVerticalScroll() {
		return getScrollableElement().getScrollTop();
	}


	public int getHorisontalScroll(){
		return getScrollableElement().getScrollLeft();
	}
	
	
	public int getViewPortHeight() {
		int h =  indexedLayoutDelegate.getCellPositioner().getViewPortHeight();
		if(h<=0){
			h= (int) (GWTUtils.getNonZeroParentHeight(this)*zoomFactor);
		}
		return h;
	}

	public int getViewPortWidth() {
		int w = indexedLayoutDelegate.getCellPositioner().getViewPortWidth();
		if(w<=0){
			return (int) (GWTUtils.getNonZeroParentWidth(this)*zoomFactor);
		}
		return w;
	}
}
