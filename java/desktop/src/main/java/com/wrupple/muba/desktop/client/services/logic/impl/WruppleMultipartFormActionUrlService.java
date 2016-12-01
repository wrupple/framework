package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.logic.MultipartFormActionUrlService;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogActionRequest;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.PersistentImageMetadata;

@Singleton
public class WruppleMultipartFormActionUrlService implements
		MultipartFormActionUrlService {
	
	static class ReadAndSetFormAction extends DataCallback<JsArrayString>{
		
		FormPanel form;
		
		
		public ReadAndSetFormAction(FormPanel form) {
			super();
			this.form = form;
		}

		@Override
		public void execute() {
			form.setAction(result.get(0));
		}
	}
	
	final StorageManager sm;
	private DesktopManager dm;
	
	@Inject
	public WruppleMultipartFormActionUrlService(StorageManager sm,DesktopManager dm) {
		super();
		this.dm=dm;
		this.sm=sm;
	}
	

	@Override
	public void setUploadUrl(final FormPanel form) throws Exception {
		String domainToken = dm.getCurrentActivityDomain();
		//TODO forms cannot really be submitted to foreign peers
		String peer = dm.getCurrentActivityHost();
		JsCatalogActionRequest action = JsCatalogActionRequest.newRequest(domainToken, CatalogActionRequest.LOCALE, PersistentImageMetadata.CATALOG, CatalogActionRequest.UPLOAD_URL, null, null, null, null);
		sm.getRemoteStorageUnit(peer).callStringArrayService(action,new ReadAndSetFormAction(form));
	}


	@Override
	public void getUrl(final StateTransition<String> callback) throws Exception  {
		String domainToken =dm.getCurrentActivityDomain();
		String peer = dm.getCurrentActivityHost();
		JsCatalogActionRequest action = JsCatalogActionRequest.newRequest(domainToken, CatalogActionRequest.LOCALE, PersistentImageMetadata.CATALOG, CatalogActionRequest.UPLOAD_URL, null, null, null, null);
		sm.getRemoteStorageUnit(peer).callStringArrayService(action,new DataCallback<JsArrayString>() {
			@Override
			public void execute() {
				callback.setResultAndFinish(result.get(0));
			}
		});
	}

	

}
