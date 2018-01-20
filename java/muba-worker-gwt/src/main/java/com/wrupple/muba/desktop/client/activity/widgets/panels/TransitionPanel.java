package com.wrupple.muba.desktop.client.activity.widgets.panels;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.*;
import com.wrupple.muba.desktop.client.services.presentation.impl.*;
import com.wrupple.muba.desktop.client.services.presentation.impl.PanelTransition.Orientation;
import com.wrupple.muba.desktop.client.widgets.TaskContainer;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.muba.worker.client.services.impl.DataCallback;
import com.wrupple.muba.worker.server.service.StateTransition;
import com.wrupple.muba.worker.shared.widgets.HumanTaskWindow;

public class TransitionPanel extends ResizeComposite implements TaskContainer {

	class RemoveCurrentAndSetNew implements AnimationCallback {
		private IsWidget next;

		protected RemoveCurrentAndSetNew(IsWidget next) {
			super();
			this.next = next;
		}

		@Override
		public void onAnimationComplete() {
			if (current != null) {
				current.removeFromParent();
				current = null;
			}
			if(main.getWidgetCount()>1){
				for(Widget w:main){
					if(w!=next){
						main.remove(w);
					}
				}
			}
			setCurrent(next);
		}

		@Override
		public void onLayout(Layer layer, double progress) {
		}

	}
	
	class DetachModal extends DataCallback<Void>{

		@Override
		public void execute() {
			removeModal();
		}
		
	}
	
	class RemoveHandler implements ClickHandler{
		StateTransition<Void> callback;
		public RemoveHandler(
				StateTransition<Void> callback) {
			super();
			this.callback = callback;
		}


		@Override
		public void onClick(ClickEvent event) {
			callback.setResultAndFinish(null);
		}
		
	}
	

	protected final LayoutPanel main;
	
	private Widget current;
	private  String GLASS_STYLENAME = "gwt-PopupPanel";


	private TransitionPanel modalOverlay;

	private Label modalOverlayGlass;

	private HasText header;


    private HumanTaskWindow userContent;

	private StateTransition<JsTransactionApplicationContext> userInteractionTaskCallback;

	private StateTransition<Void> modalKiller;
	

	public TransitionPanel() {
		this(new LayoutPanel());
	}
	
	@Override
	protected void onUnload() {
		super.onUnload();
	}

	public TransitionPanel(LayoutPanel layoutPanel) {
		super();
		this.main = layoutPanel;
		initWidget(main);
	}
	
	public Widget getCurrentWidget() {
		return current;
	}

	public void clear() {
		main.clear();
	}

	@Override
	public void setWidget(IsWidget w) {
		if(w==null){
			main.clear();
			return;
		}
		
		if(w==current||w.asWidget()==current){
			//do nothing
		}else{
			if (main.getWidgetCount() == 0) {
				// firstwidget
				main.add(w);
				fadeTransition(w);
			} else if (main.getWidgetIndex(w.asWidget()) < 0) {
				//new widget is not currently added
				if(current!=null&&main.getWidgetIndex(current)<0){
					//current widget is not in either (weird!)
					current=null;
					// firstwidget
					main.add(w);
					fadeTransition(w);
				}else{
					// new widget
					main.add(w);
					transitionToNew(w);
				}
				
			} else {
				// new widget is already contained
				transitionFromOld(w);
				
			}
		}
		
	}
	
	private  void removeModal(){
		if(modalOverlayGlass!=null&&modalOverlayGlass.isAttached()){
			modalOverlayGlass.removeFromParent();
		}
		if(modalOverlay!=null&&modalOverlay.isAttached()){
			modalOverlay.removeFromParent();
		}
		modalOverlayGlass=null;
		modalOverlay=null;
		modalKiller=null;
	}
	
	private void transitionFromOld(IsWidget next) {
		//LayoutPanel container = main;

		AnimationCallback callback = new RemoveCurrentAndSetNew(next);
		
		FadingTransition.fade(next, current, callback).run(500);
		/*if (LocaleInfo.getCurrentLocale().isRTL()) {
			slideFromLeft(container, next.asWidget(), current, 100).animate(500, callback);
		} else {
			slideFromRight(container, next.asWidget(), current, 100).animate(500, callback);
		}
		*/
	}

	private void transitionToNew(IsWidget next) {
		//LayoutPanel container = main;
		AnimationCallback callback = new RemoveCurrentAndSetNew(next);
		FadingTransition.fade(next, current, callback).run(500);
		/*
		 * TODO slide tasks using carousel-like sliding, not curtain. Since some widgets donde respond properly to constant resizing (such as maps)
		 * if (LocaleInfo.getCurrentLocale().isRTL()) {
			slideFromRight(container, next.asWidget(), current, 100).animate(500, callback);
		} else {
			slideFromLeft(container, next.asWidget(), current, 100).animate(500, callback);
		}*/

	}

	private void fadeTransition(IsWidget next) {
		AnimationCallback callback = new RemoveCurrentAndSetNew(next);
		FadingTransition.fade(next, null, callback).run(500);
	}


	private void setCurrent(IsWidget current) {
		this.current = current.asWidget();
	}

	
	private PanelAnimator slideFromRight(LayoutPanel wrapper, Widget child, Widget brother, int widthInPCT) {
		PanelAnimator animator = new SimplePanelAnimator();
		PanelTransition transition = new CurtainTransition(PanelTransition.Orientation.RIGHT, child, brother,wrapper,widthInPCT,Unit.PCT);
		animator.addTransition(transition);
		animator.setLayoutPanel(wrapper);
		return animator;
	}
	
	private PanelAnimator slideFromLeft(LayoutPanel wrapper, Widget child, Widget brother, int widthInPCT) {
		PanelAnimator animator = new SimplePanelAnimator();
		PanelTransition transition = new CurtainTransition(PanelTransition.Orientation.LEFT, child, brother,wrapper,widthInPCT,Unit.PCT);
		animator.addTransition(transition);
		animator.setLayoutPanel(wrapper);
		return animator;
	}
	

	private CurtainTransition createTransition(Orientation from, Widget child, Widget brother, LayoutPanel parent, double heightOrWidthInPixels){
		return new CurtainTransition(from,child,brother,parent,heightOrWidthInPixels,Unit.PX);
	}
	
	
	private PanelAnimator slideFromTop(LayoutPanel wrapper, Widget child, Widget brother, double heightInPixels) {
		PanelAnimator animator = new SimplePanelAnimator();
		PanelTransition transition = new CurtainTransition(PanelTransition.Orientation.TOP, child, brother,wrapper,heightInPixels, Unit.PX);
		animator.addTransition(transition);
		animator.setLayoutPanel(wrapper);
		return animator;
	}


	public void setHeader(HasText header){
		this.header=header;
	}

	@Override
    public TaskContainer spawnChild(StateTransition<Void> callback) {
        if(current== null){
			throw new IllegalArgumentException("cannot create a modal overlay on an empty transition panel");
		}
		
		if(modalOverlay!=null){
			//already has a modal nested, kill it
			modalKiller.execute();
		}
		if(modalOverlay!=null){
			throw new IllegalArgumentException("only one modal overlay per container is allowed");
		}
		
		Label glass = new Label("");
		glass.setStyleName(GLASS_STYLENAME);
		TransitionPanel modal = new TransitionPanel();
		modal.setHeader(glass);
		callback.hook(new DetachModal());
		glass.addClickHandler(new RemoveHandler(callback));
		
		this.modalOverlay=modal;
		this.modalKiller=callback;
		this.modalOverlayGlass = glass;
		
		main.add(glass);
		main.add(modal);
		Element container = main.getWidgetContainerElement(modal);
		container.addClassName(GLASS_STYLENAME+"-modalElement");
		
		//glass occupies the whoel screen area
		//main.setWidgetTopHeight(glass, 0, Unit.PX, 36, Unit.PX);
		main.setWidgetTopBottom(modal, 5, Unit.PCT, 5, Unit.PCT);
		main.setWidgetLeftRight(modal, 5, Unit.PCT, 5, Unit.PCT);
		return modal;
	}

	@Override
	public void setProcessName(String processName, String oldReplacedProcessName) {
		if(header!=null&&oldReplacedProcessName!=null){
			header.setText(oldReplacedProcessName);
		}
	}


	@Override
    public HumanTaskWindow getTaskContent() {
        return userContent;
	}

	@Override
    public void setTaskContent(HumanTaskWindow panel) {
        this.userContent=panel;
	}

	@Override
	public void setUserInteractionTaskCallback(StateTransition<JsTransactionApplicationContext> onDone) {
		this.userInteractionTaskCallback=onDone;
	}

	@Override
	public StateTransition<JsTransactionApplicationContext> getUserInteractionTaskCallback() {
		return userInteractionTaskCallback;
	}

	@Override
	public void setUserContentClass(String processUserAreaClass) {
		main.setStyleName(processUserAreaClass);
	}


}
