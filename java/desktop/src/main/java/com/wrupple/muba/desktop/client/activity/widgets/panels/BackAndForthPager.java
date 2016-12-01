package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.HasRows;

/**
 * TODO make LTR sensitive
 * 
 * @author japi
 *
 */
public class BackAndForthPager extends AbstractPager {

	public static String STYLE_NAME = "carrouselPager";
	/**
	 * The increment size.
	 */
	private final int incrementSize;
	
	/**
	 * The increment size.
	 */
	private final int pageSize;

	/**
	 * The last scroll position.
	 */
	private final int currentPosition;

	//TODO make layoutPanel
	private HorizontalPanel scrollable;
	/**
	 * Construct a new {@link InfiniteScrollPager}.
	 */
	public BackAndForthPager(int startingIndex, int pageSize, int incrementSize) {
		this.currentPosition=startingIndex;
		this.pageSize=pageSize;
		this.incrementSize=incrementSize;
		scrollable = new HorizontalPanel();
		scrollable.addStyleName(STYLE_NAME);
		initWidget(scrollable);
		
		Label backButton = new Label("<");
		backButton.addStyleName("backButton");
		Label forwardButton = new Label(">");
		forwardButton.addStyleName("forwardButton");
		scrollable.add(backButton);
		scrollable.add(forwardButton);
		
		// Handle scroll events.
		backButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				decrement();
			}
		});
		forwardButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				increment();
			}
		});
	
	}


	protected void increment() {
		HasRows view = super.getDisplay();
		int currentPageStart=getPageStart()+incrementSize;
		if(currentPageStart<0){
			currentPageStart=0;
		}
		view.setVisibleRange(currentPageStart, pageSize);
	}


	protected void decrement() {
		HasRows view = super.getDisplay();
		int currentPageStart=getPageStart()-incrementSize;
		if(currentPageStart<0){
			currentPageStart=0;
		}
		view.setVisibleRange(currentPageStart, pageSize);
	}
	/**
	 * Get the number of rows by which the range is increased when the scrollbar
	 * reaches the bottom.
	 * 
	 * @return the increment size
	 */
	public int getIncrementSize() {
		return incrementSize;
	}


	@Override
	public void setDisplay(HasRows view) {
		if(scrollable.getWidgetCount()>2){
			scrollable.remove(1);
		}
		scrollable.insert((IsWidget) view, 1);
		view.setVisibleRange(currentPosition, pageSize);
		super.setDisplay(view);
	}
	
	int lastRangeCorrection = -1;
	
	@Override
	protected void onRangeOrRowCountChanged() {
		int currentRowCount =getDisplay().getRowCount(); 
		if(currentRowCount<pageSize){
			int difference = pageSize -currentRowCount;
			int currentStart = getPageStart();
			HasRows view = super.getDisplay();
			int newStart = currentStart-difference;
			if(newStart<0){
				newStart=0;
			}
			if(lastRangeCorrection==newStart){
				return;
			}else{
				lastRangeCorrection= newStart;
				view.setVisibleRange(newStart, pageSize);
			}
		}
	}
	
	

}
