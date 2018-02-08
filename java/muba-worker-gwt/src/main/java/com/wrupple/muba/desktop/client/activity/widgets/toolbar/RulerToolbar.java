package com.wrupple.muba.desktop.client.activity.widgets.toolbar;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.desktop.client.activity.widgets.WruppleActivityToolbarBase;
import com.wrupple.muba.desktop.client.activity.widgets.panels.DockLayoutTransactionPanel.DockToolbarProperties;
import com.wrupple.muba.worker.shared.factory.dictionary.ToolbarMap;
import com.wrupple.muba.desktop.domain.overlay.JsProcessTaskDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTaskToolbarDescriptor;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.shared.domain.ReconfigurationBroadcastEvent;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow.ToolbarDirection;
import com.wrupple.muba.worker.shared.widgets.Toolbar;

public class RulerToolbar extends WruppleActivityToolbarBase implements Toolbar {

	boolean vertical;
	private LayoutPanel main;
	private boolean orderAscending;
	private double rulerBucketValue;
	private int rulerBucketSize;
	private int viewportHeight;
	private int viewportWidth;
	private int horisontalScroll;
	private int verticalScroll;

	@Inject
	public RulerToolbar(ToolbarMap toolbarMap) {
		super(toolbarMap);
		main = new LayoutPanel();
		initWidget(main);
	}

	@Override
	public void initialize(JsTaskToolbarDescriptor toolbarDescriptor, JsProcessTaskDescriptor parameter, JsTransactionApplicationContext contextParameters,
			EventBus bus, ProcessContextServices contextServices) {
		super.initialize(toolbarDescriptor, parameter, contextParameters, bus, contextServices);
		DockToolbarProperties p = toolbarDescriptor.getPropertiesObject().cast();
		ToolbarDirection regreso = p.getDirection();
        vertical = ToolbarDirection.SOUTH != regreso && ToolbarDirection.NORTH != regreso;
        if(isAttached()){
			draw();
		}
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		draw();
	}

	@Override
    public void applyAlterations(ReconfigurationBroadcastEvent properties, ProcessContextServices contextServices, EventBus eventBus, JsTransactionApplicationContext contextParamenters) {
        super.applyAlterations(properties, contextServices, eventBus, contextParamenters);
		draw();
	}

	@Override
	public void setValue(JavaScriptObject value) {

	}

	@Override
	public JavaScriptObject getValue() {
		return null;
	}

	private void draw() {
		int rulerLength;

		if (vertical) {
			rulerLength = getOffsetHeight();
		} else {
			rulerLength = getOffsetWidth();
		}
		if(rulerLength<=0||rulerBucketSize<=0){
			GWT.log("Ruler Toolbar not ready to draw: rulerLegth:"+rulerLength+", bucket Size:"+rulerBucketSize);
		}else{
			int viewPortSize;
			int scroll;
			if (vertical) {
				scroll = verticalScroll;
				viewPortSize = viewportHeight;
			} else {
				scroll = horisontalScroll;
				viewPortSize = viewportWidth;
			}

			int bucketIndex = 0;

			int widgetPosition = 0;
			
			if (orderAscending) {
				widgetPosition = widgetPosition - scroll;
			} else {
				widgetPosition = widgetPosition + scroll;
				int rulerSizeDifference;
				rulerSizeDifference = viewPortSize - rulerLength;
				widgetPosition = widgetPosition - rulerSizeDifference;
			}

			double bucketValue;
			Widget bucketWidget;

			while (widgetPosition <= rulerLength) {
				bucketValue = bucketIndex * rulerBucketValue;
				bucketWidget = getBucketWidget(bucketIndex, bucketValue);
				if (orderAscending) {
					if (vertical) {
						main.setWidgetTopHeight(bucketWidget, widgetPosition, Unit.PX, rulerBucketSize, Unit.PX);
					} else {
						main.setWidgetLeftWidth(bucketWidget, widgetPosition, Unit.PX, rulerBucketSize, Unit.PX);
					}
				} else {
					if (vertical) {
						main.setWidgetBottomHeight(bucketWidget, widgetPosition, Unit.PX, rulerBucketSize, Unit.PX);
					} else {
						main.setWidgetRightWidth(bucketWidget, widgetPosition, Unit.PX, rulerBucketSize, Unit.PX);
					}
				}
				widgetPosition += rulerBucketSize;
				bucketIndex++;
			}

			// current position is off the visible area
			for (; bucketIndex < main.getWidgetCount(); bucketIndex++) {
				// bucket value is negligible since widget is not visible
				bucketValue = bucketIndex * rulerBucketValue;
				bucketWidget = getBucketWidget(bucketIndex, rulerBucketValue);
				if (orderAscending) {
					if (vertical) {
						main.setWidgetTopHeight(bucketWidget, widgetPosition, Unit.PX, rulerBucketSize, Unit.PX);
					} else {
						main.setWidgetLeftWidth(bucketWidget, widgetPosition, Unit.PX, rulerBucketSize, Unit.PX);
					}
				} else {
					if (vertical) {
						main.setWidgetBottomHeight(bucketWidget, widgetPosition, Unit.PX, rulerBucketSize, Unit.PX);
					} else {
						main.setWidgetRightWidth(bucketWidget, widgetPosition, Unit.PX, rulerBucketSize, Unit.PX);
					}
				}
				widgetPosition += rulerBucketSize;
			}
		}

	}

	private Widget getBucketWidget(int bucketIndex, double bucketValue) {
		Label regreso;
		int amountOfBuckets = main.getWidgetCount();
		if (bucketIndex < amountOfBuckets) {
			regreso = (Label) main.getWidget(bucketIndex);
		} else if (bucketIndex == amountOfBuckets) {
			regreso = new Label();
			main.add(regreso);
		} else {
			throw new IndexOutOfBoundsException("No ruler bucket for index " + bucketIndex);
		}
		regreso.setText(String.valueOf((int) bucketValue));
		return regreso;
	}

	public void setVerticalScroll(String s) {
		if (s != null)
			this.verticalScroll = Integer.parseInt(s);
	}

	public void setHorisontalScroll(String s) {

		if (s != null)
			this.horisontalScroll = Integer.parseInt(s);
	}

	public void setRulerBucketSize(String s) {
		this.rulerBucketSize = Integer.parseInt(s);
	}

	public void setRulerDescending(String s) {
		this.orderAscending = s == null;
	}

	public void setRulerBucketValue(String s) {
		this.rulerBucketValue = Double.parseDouble(s);
	}

	public void setViewportWidth(String s) {
		if (s != null)
			this.viewportWidth = Integer.parseInt(s);
    }

	public void setViewportHeight(String s) {
		if (s != null)
			this.viewportHeight = Integer.parseInt(s);
    }

}
