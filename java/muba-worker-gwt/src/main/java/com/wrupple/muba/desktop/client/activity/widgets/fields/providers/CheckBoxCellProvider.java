package com.wrupple.muba.desktop.client.activity.widgets.fields.providers;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.catalogs.domain.CatalogAction;
import com.wrupple.muba.desktop.client.services.presentation.CatalogFormFieldProvider;
import com.wrupple.muba.desktop.client.services.presentation.ImageTemplate;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionApplicationContext;
import com.wrupple.vegetate.domain.FieldDescriptor;
public class CheckBoxCellProvider implements CatalogFormFieldProvider {
	private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils
			.fromSafeConstant("<span><input type=\"checkbox\"  checked/></span>");
	private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils
			.fromSafeConstant("<span><input type=\"checkbox\" /></span>");
	
	private static final SafeHtml INPUT_CHECKED_DISABLED = SafeHtmlUtils
			.fromSafeConstant("<span><input type=\"checkbox\"  disabled=\"disabled\" checked/></span>");
	private static final SafeHtml INPUT_UNCHECKED_DISABLED = SafeHtmlUtils
			.fromSafeConstant("<span><input type=\"checkbox\" disabled=\"disabled\" /></span>");

	
	ImageTemplate imageTemplate;
	
	@Inject
	public CheckBoxCellProvider(ImageTemplate imageTemplate) {
		super();
		this.imageTemplate=imageTemplate;
	}

	@Override
	public Cell<Boolean> createCell(EventBus bus,
			ProcessContextServices contextServices,
			JsTransactionApplicationContext contextParameters,
			JavaScriptObject formDescriptor, FieldDescriptor d, CatalogAction mode) {
		String trueCustomImage = null;
		String falseCustomImage = null;
		
		if(formDescriptor!=null && d!=null){
			String fieldId = d.getFieldId();
			trueCustomImage = GWTUtils.getAttribute(formDescriptor, fieldId+"TrueCustomImage");
			falseCustomImage = GWTUtils.getAttribute(formDescriptor, fieldId+"FalseCustomImage");
		}
		
		//TODO customImageSize?
		
		return new CheckboxCell(mode,imageTemplate,falseCustomImage,trueCustomImage);
		
	}
	
	public static class  CheckboxCell extends AbstractEditableCell<Boolean, Boolean> {


		private final boolean dependsOnSelection;
		private final boolean handlesSelection;
		private boolean readMode;
		private ImageTemplate imageTemplate;
		private String falseCustomImage;
		private String trueCustomImage;

		/**
		 * Construct a new {@link CheckboxCell}.
		 * @param mode 
		 * @param imageTemplate 
		 * @param trueCustomImage 
		 * @param falseCustomImage 
		 * @param string 
		 */
		  public CheckboxCell( CatalogAction mode, ImageTemplate imageTemplate, String falseCustomImage, String trueCustomImage) {
		    this(false,false);
		    this.imageTemplate=imageTemplate;
		    this.falseCustomImage=falseCustomImage;
		    this.trueCustomImage=trueCustomImage;

              this.readMode = CatalogAction.READ == mode;
          }


		  /**
		   * Construct a new {@link CheckboxCell} that optionally controls selection.
		   *
		   * @param dependsOnSelection true if the cell depends on the selection state
		   * @param handlesSelection true if the cell modifies the selection state
		   */
		  protected CheckboxCell(boolean dependsOnSelection, boolean handlesSelection) {
		    super("change", "keydown");
		    this.dependsOnSelection = dependsOnSelection;
		    this.handlesSelection = handlesSelection;
		  }

		  @Override
		  public boolean dependsOnSelection() {
		    return dependsOnSelection;
		  }

		  @Override
		  public boolean handlesSelection() {
		    return handlesSelection;
		  }

		  @Override
		  public boolean isEditing(Context context, Element parent, Boolean value) {
		    // A checkbox is never in "edit mode". There is no intermediate state
		    // between checked and unchecked.
		    return false;
		  }

		  @Override
		  public void onBrowserEvent(Context context, Element parent, Boolean value, 
		      NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
		    String type = event.getType();

		    boolean enterPressed = "keydown".equals(type)
		        && event.getKeyCode() == KeyCodes.KEY_ENTER;
		    if ("change".equals(type) || enterPressed) {
		      InputElement input = parent.getFirstChild().getFirstChild().cast();
		      Boolean isChecked = input.isChecked();

		      /*
		       * Toggle the value if the enter key was pressed and the cell handles
		       * selection or doesn't depend on selection. If the cell depends on
		       * selection but doesn't handle selection, then ignore the enter key and
		       * let the SelectionEventManager determine which keys will trigger a
		       * change.
		       */
		      if (enterPressed && (handlesSelection() || !dependsOnSelection())) {
		        isChecked = !isChecked;
		        input.setChecked(isChecked);
		      }

		      /*
		       * Save the new value. However, if the cell depends on the selection, then
		       * do not save the value because we can get into an inconsistent state.
		       */
		      if (value != isChecked && !dependsOnSelection()) {
		        setViewData(context.getKey(), isChecked);
		      } else {
		        clearViewData(context.getKey());
		      }

		      if (valueUpdater != null) {
		        valueUpdater.update(isChecked);
		      }
		    }
		  }

		  @Override
		  public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
		    // Get the view data.
		    Object key = context.getKey();
		    Boolean viewData = getViewData(key);
		    
		    if (viewData != null && viewData.equals(value)) {
		      clearViewData(key);
		      viewData = null;
		    }
		    
		    if(readMode){
		    	 if (value != null && ((viewData != null) ? viewData : value)) {
		    		  if(trueCustomImage==null){
		    			  sb.append(INPUT_CHECKED_DISABLED);
		    		  }else{
		    			  sb.append(imageTemplate.fullsizeImageOutput(trueCustomImage));
		    		  }
				     
				    } else {
				    	if(falseCustomImage==null){
				    		sb.append(INPUT_UNCHECKED_DISABLED);
				    	}else{
				    		sb.append(imageTemplate.fullsizeImageOutput(falseCustomImage));
				    	}
				      
				    }
		    }else{
		    	 if (value != null && ((viewData != null) ? viewData : value)) {
				      sb.append(INPUT_CHECKED);
				    } else {
				      sb.append(INPUT_UNCHECKED);
				    }
		    }

		   
		  }
	}


}
