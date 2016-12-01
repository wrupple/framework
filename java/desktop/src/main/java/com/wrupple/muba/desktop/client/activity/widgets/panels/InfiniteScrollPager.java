package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;
import com.wrupple.vegetate.domain.FilterData;

/**
 * A scrolling pager that automatically increases the range every time the
 * scroll bar reaches the bottom. 
 * TODO increase when it is *near* the bottom
 * TODO block increments while a previous increment operation is beeing performed
 */
public class InfiniteScrollPager extends AbstractPager {

	
	int lastHeight = -128;
	/**
	 * The increment size.
	 */
	private int incrementSize = FilterData.DEFAULT_INCREMENT;

	/**
	 * The last scroll position.
	 */
	private int lastScrollPos = 0;

	/**TODO make layoutPanel
	 * The scrollable panel.
	 */
	private final ScrollPanel scrollable = new ScrollPanel();

	/**
	 * Construct a new {@link InfiniteScrollPager}.
	 */
	public InfiniteScrollPager() {
		initWidget(scrollable);
		scrollable.addStyleName("infiniteScroller");
		// Handle scroll events.
		scrollable.addScrollHandler(new ScrollHandler() {
			public void onScroll(ScrollEvent event) {
				// If scrolling up, ignore the event.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = scrollable.getVerticalScrollPosition();
				
				if (oldScrollPos >= lastScrollPos) {
					return;
				}

				HasRows view = getDisplay();
				if (view == null) {
					return;
				}
				int innerHeight = scrollable.getWidget().getOffsetHeight();
				if(innerHeight==0){
					int maxScrollPos=scrollable.getMaximumVerticalScrollPosition();
					
					if (lastScrollPos == maxScrollPos ) {
						// We are at the end, so increase the page size.
						increment();
					}
					
				}else{
					int maxScrollTop = innerHeight -scrollable.getOffsetHeight() ;
					if (lastScrollPos >= maxScrollTop) {
						// We are near the end, so increase the page size.
						increment();
					}
				}
				
			}
		});
	}

	@Override
	protected void onLoad() {
		onRangeOrRowCountChanged();
		super.onLoad();
	};

	protected void increment() {

		HasRows view = super.getDisplay();
		Range range = view.getVisibleRange();
		view.setVisibleRange(range.getStart(), range.getLength() + incrementSize);
	}


	/**
	 * Set the number of rows by which the range is increased when the scrollbar
	 * reaches the bottom.
	 * 
	 * @param incrementSize
	 *            the incremental number of rows
	 */
	public void setIncrementSize(int incrementSize) {
		this.incrementSize = incrementSize;
	}

	@Override
	public void setDisplay(HasRows view) {
		assert view instanceof Widget : "view must extend Widget";
		scrollable.setWidget((Widget) view);
		super.setDisplay(view);
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
	protected void onRangeOrRowCountChanged() {
		
		if (isAttached()) {
			Widget child = scrollable.getWidget();
			int childHeight = child.getOffsetHeight();
			if (childHeight == lastHeight) {
				
			} else {
				lastHeight = childHeight;
				int scrollHeight = scrollable.getParent().getOffsetHeight();
				boolean scroolIsEnabled = childHeight > scrollHeight;
				if (scroolIsEnabled) {
					// do nothing
				} else {
					increment();
				}
			}
		}

	}
}