package com.wrupple.muba.desktop.client.activity.process.impl;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.impl.SequentialProcess;
import com.wrupple.muba.bpm.client.activity.process.state.HumanTask;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.desktop.client.activity.process.CanvasDrawingProcess;
import com.wrupple.muba.desktop.client.activity.widgets.BigFatMessage;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;

/**
 * 
 * Inputs and outputs base 64 data Urls
 * 
 * @author japi
 *
 */
public class CanvasDrawingProcessImpl extends SequentialProcess<String, String> implements CanvasDrawingProcess {

	@Inject
	public CanvasDrawingProcessImpl() {
		add(new DrawingTask());
	}
	
	
	static class DrawingTask implements HumanTask<String,String>{
		Canvas canvas;
		private StateTransition<String> onDone;
		private Context2d drawContext;
		private boolean drawing;
		public DrawingTask() {
			
			canvas = Canvas.createIfSupported();
			if(canvas!=null){

				canvas.setWidth("100%");
				canvas.setHeight("100%");
				drawContext= canvas.getContext2d();
				drawContext.setLineWidth(4);
				drawContext.setStrokeStyle(CssColor.make(0,0,0));
				drawing=false;
				
				canvas.addDoubleClickHandler(new DoubleClickHandler(){

					@Override
					public void onDoubleClick(DoubleClickEvent event) {
						event.preventDefault();
						drawing=false;
						onDone.setResultAndFinish(canvas.toDataUrl());
					}});
				
				
				canvas.addMouseDownHandler(new MouseDownHandler() {
					
					@Override
					public void onMouseDown(MouseDownEvent event) {
						drawing=true;
						drawContext.beginPath();
					}
				});
				canvas.addTouchStartHandler(new TouchStartHandler() {
					
					@Override
					public void onTouchStart(TouchStartEvent event) {
						event.preventDefault();
						drawing=true;
						drawContext.beginPath();
					}
				});
				
				canvas.addMouseMoveHandler(new MouseMoveHandler() {
					
					@Override
					public void onMouseMove(MouseMoveEvent e) {
						
						if(drawing){
							double x = e.getRelativeX(canvas.getCanvasElement());
							double y = e.getRelativeY(canvas.getCanvasElement());
							drawContext.lineTo(x, y);
							drawContext.stroke();
						}
					}
				});
				canvas.addTouchMoveHandler(new TouchMoveHandler() {
					
					@Override
					public void onTouchMove(TouchMoveEvent ee) {
						ee.preventDefault();
						if(drawing){
							JsArray<Touch> touches = ee.getTouches();
							if(touches!=null && touches.length()>1){
								drawing=false;
								onDone.setResultAndFinish(canvas.toDataUrl());
							}else{
								Touch e = ee.getNativeEvent().getTouches().get(0);
								double x = e.getRelativeX(canvas.getCanvasElement());
								double y = e.getRelativeY(canvas.getCanvasElement());
								drawContext.lineTo(x, y);
								drawContext.stroke();
							}
						}						
					}
				});
				
				canvas.addMouseUpHandler(new MouseUpHandler() {
					
					@Override
					public void onMouseUp(MouseUpEvent event) {
						drawing=false;
						drawContext.closePath();
					}
				});
			}
		}
		
		@Override
		public void start(String parameter, StateTransition<String> onDone, EventBus bus) {
			if(canvas==null){
				onDone.setResultAndFinish(parameter);
			}else{
				canvas.setCoordinateSpaceHeight(GWTUtils.getNonZeroParentHeight(canvas));
				canvas.setCoordinateSpaceWidth(GWTUtils.getNonZeroParentWidth(canvas));
				if(parameter!=null && parameter.length()>0){
					ImageElement image = Document.get().createImageElement();
					image.setSrc(parameter);
					drawContext.drawImage(image, 0, 0);
				}
				this.onDone =onDone;
			}
		}

		@Override
		public Widget asWidget() {
			if(canvas==null){
				return new BigFatMessage("Unsupported Feature");
			}
			return canvas;
		}
		
	}

}
