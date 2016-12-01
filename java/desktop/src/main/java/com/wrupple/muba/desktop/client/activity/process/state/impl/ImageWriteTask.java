package com.wrupple.muba.desktop.client.activity.process.state.impl;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.ProcessContextServices;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.FieldColumnRelation;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImageFieldImportData;
import com.wrupple.muba.desktop.client.activity.impl.CSVImportActiviy.ImportData;
import com.wrupple.muba.desktop.client.activity.widgets.impl.MultipleCatalogFileUpload.Value;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;

public class ImageWriteTask implements State.ContextAware<ImportData, ImportData> {
	
	
	class WriteImageIdOnResults extends DataCallback<List<JsCatalogEntry>>{

		final String imageId;
		final String imageField;
		private DesktopManager dm;
		
		
		public WriteImageIdOnResults(String imageId, String imageField,DesktopManager dm) {
			super();
			this.dm=dm;
			this.imageId = imageId;
			this.imageField = imageField;
		}


		@Override
		public void execute() {
			if(result!=null && !result.isEmpty()){
				//TODO NOTIFY USER WHEN DONE
				StateTransition<JsCatalogEntry> callback=nullCallback();
				for(JsCatalogEntry e : result){
					GWTUtils.setAttribute(e, imageField, imageId);
					context.getStorageManager().update(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), e.getCatalog(), e.getId(), e, callback);
				}
			}
		}
		
	}

	private ProcessContextServices context;

	@Override
	public void start(ImportData parameter, StateTransition<ImportData> onDonvaluee, EventBus bus) {
		JsArray<ImageFieldImportData> imageImportData = parameter.getImageFieldImportData();
		if(imageImportData!=null && imageImportData.length()>0){
			
			ImageFieldImportData importedImageFieldData ;
			String imageField ; 
			String discriminatorField;
			JsArray<Value> images;
			Value importedImage;
			String imageId;
			String imageFileName;
			int imageIdColumnIndex;
			int discriminatorColumnIndex;
			int hasPointInFileName;
			boolean foundAtLeastOneMatch;
			
			for(int i = 0 ; i< imageImportData.length(); i++){
				
				importedImageFieldData = imageImportData.get(i);
				
				imageField=importedImageFieldData.getField();
				discriminatorField=importedImageFieldData.getfieldNameMappedtoImageFilename();
				images =importedImageFieldData.getImageIdtoFileName();
				
				discriminatorColumnIndex = getDiscriminatorColumnIndex(discriminatorField,parameter);
				imageIdColumnIndex = getImageIdColumnIndex(imageField,parameter);
				
				for(int j = 0; j< images.length(); j++){
					importedImage = images.get(j);
					imageId = importedImage.getId();
					imageFileName = importedImage.getName();
					hasPointInFileName = imageFileName.indexOf('.');
					if(hasPointInFileName>0){
						imageFileName = imageFileName.substring(0, hasPointInFileName);
					}
					foundAtLeastOneMatch = putImageIdColumnValueInDiscriminatedRecords(imageId,imageFileName,imageIdColumnIndex,discriminatorColumnIndex,parameter.getCsv());
					/* TODO if no record matches filename search and update Data Store (parallel Process)
					
					FilterData discriminatorFilter;
			StateTransition<List<JsCatalogEntry>> writeImageIdOnResults;
			
					if(!foundAtLeastOneMatch){
						//find discriminated entries in datastore
						writeImageIdOnResults= new WriteImageIdOnResults(imageId, imageField);
						discriminatorFilter= FilterDataUtils.createSingleFieldFilter(discriminatorField, imageFileName);
						JSOHelper.setAttribute(discriminatorFilter, SimpleFilterableDataProvider.LOCAL_FILTERING, true);
						context.getStorageManager().read(parameter.getCatalog(), discriminatorFilter, writeImageIdOnResults);
					}*/
				}
			}
			
		}
		onDonvaluee.setResultAndFinish(parameter);
	}
	
	private boolean putImageIdColumnValueInDiscriminatedRecords(String imageId, String discriminativeValue, int imageIdColumnIndex,
			int discriminatorColumnIndex, JsArray<JsArrayString> csv) {
		JsArrayString record;
		String recordValue;
		boolean found=false;
		for(int i = 0 ; i < csv.length(); i++){
			record = csv.get(i);
			//FIXME use RepeatingCOmmand
			for(int j = 0 ; j < record.length(); j++){
				recordValue = record.get(discriminatorColumnIndex);
				if(recordValue!=null && recordValue.equals(discriminativeValue)){
					record.set(imageIdColumnIndex, imageId);
					found = true;
				}
			}
		}
		return found;
	}

	private int getDiscriminatorColumnIndex(String discriminatorField, ImportData parameter) {
		JsArray<FieldColumnRelation> mapping = parameter.getFieldColumnRelation();
		FieldColumnRelation relation;
		for(int i = 0 ; i < mapping.length(); i++){
			relation = mapping.get(i);
			if(relation.getPath().get(0).equals(discriminatorField)){
				return relation.getColumn();
			}
			
		}
		throw new IllegalArgumentException(discriminatorField+" not found in mapping");
	}

	private int getImageIdColumnIndex(String imageField, ImportData parameter) {
		//add (or find?) column
		JsArray<FieldColumnRelation> mapping = parameter.getFieldColumnRelation();
		FieldColumnRelation relation;
		for(int i = 0 ; i < mapping.length(); i++){
			relation = mapping.get(i);
			if(relation.getPath().get(0).equals(imageField)){
				return relation.getColumn();
			}
		}
		relation = JavaScriptObject.createObject().cast();
		relation.setColumn(parameter.getColumnNames().length());
		parameter.getColumnNames().push("image:field:column");
		relation.setDiscriminative(false);
		JsArrayString path=JsArrayString.createArray().cast();
		path.push(imageField);
		relation.setPath(path);
		parameter.getFieldColumnRelation().push(relation);
		
		JsArray<JsArrayString> csv = parameter.getCsv();
		JsArrayString record;
		int col = relation.getColumn();
		for(int i = 0 ; i < csv.length(); i++){
			record = csv.get(i);
			for(int j = 0 ; j < record.length(); j++){
				record.set(col, null);
			}
		}
		
		return col;
	}

	@Override
	public void setContext(ProcessContextServices context) {
		this.context=context;
	}

}
