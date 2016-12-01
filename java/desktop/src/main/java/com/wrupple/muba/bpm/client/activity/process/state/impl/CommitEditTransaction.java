package com.wrupple.muba.bpm.client.activity.process.state.impl;

import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.AbstractCommitUserTransactionImpl;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.catalogs.domain.CatalogActionRequest;
import com.wrupple.muba.desktop.client.event.BeforeEntryCreatedEvent;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.muba.desktop.client.services.presentation.impl.GWTUtils;
import com.wrupple.muba.desktop.domain.overlay.JsCatalogEntry;
import com.wrupple.muba.desktop.domain.overlay.JsTransactionActivityContext;
import com.wrupple.vegetate.client.services.StorageManager;
import com.wrupple.vegetate.domain.CatalogEntry;

public class CommitEditTransaction extends AbstractCommitUserTransactionImpl {

	
	@Override
	public void start(JsTransactionActivityContext context,
			StateTransition<JsTransactionActivityContext> onDone, EventBus bus) {
		super.start(context, onDone, bus);
		
		
		final JsCatalogEntry entry = context.getUserOutput();
		final DesktopManager dm = super.context.getDesktopManager();
		String transactionType = activityDescriptor.getTransactionType();
		final StorageManager sm = this.context.getStorageManager();
		final String catalog = activityDescriptor.getCatalogId();
		
		boolean canceled = context.isCanceled();
		boolean skip = context.setRecoveredOutput(false);
		String id = context.getTargetEntryId();
		if(id ==null){
			id = entry.getId();
		}
		if(skip||CatalogActionRequest.READ_ACTION.equals(transactionType)){
			onDone.setResultAndFinish(context);
		}else  if(CatalogActionRequest.WRITE_ACTION.equals(transactionType)){
			/*
			 * THIS TRANSACTION HAS SOME SORT OF USER OUTPUT THAT MUST BE COMMITED
			 */
			
			if(entry==null){
				//no user output, most likely canceled
				
			}else{
				
				boolean draft = GWTUtils.getAttributeAsBoolean(entry, CatalogEntry.DRAFT_FIELD);
				boolean commitEntry;
				boolean deleteDraft;
				if(draft){
					GWTUtils.setAttribute(entry, CatalogEntry.DRAFT_FIELD, false);
					if(canceled){
						deleteDraft=true;
						commitEntry=false;
					}else{
						//user interaction ended normally
						commitEntry = true;
						deleteDraft= false;
					}
				}else{
					if(canceled){
						deleteDraft=true;
						commitEntry=false;
					}else{
						//user interaction ended normally,
						commitEntry = true;
						deleteDraft= false;
					}
				}
				
				StateTransition<JsCatalogEntry> callback=new EntryUpdateCallback(context,onDone);
				if(commitEntry){
					sm.update(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, id, entry, callback);
				}else if(deleteDraft){
					sm.delete(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(), catalog, id, callback);
				}
				
			}
			
		}else if(CatalogActionRequest.CREATE_ACTION.equals(transactionType)){
			if(canceled){
				//no commiting required
				onDone.setResultAndFinish(context);
			}else{
				final StateTransition<JsCatalogEntry> callback=new EntryUpdateCallback(context,onDone);
				BeforeEntryCreatedEvent e = new BeforeEntryCreatedEvent(entry,this.context);
				bus.fireEvent(e);
				if(e.isClosed()||e.getCallback()==null){
					sm.create(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(),catalog, entry, callback);
				}else{
					e.getCallback().hook(new DataCallback<Void>() {

						@Override
						public void execute() {
							sm.create(dm.getCurrentActivityHost(), dm.getCurrentActivityDomain(),catalog, entry, callback);				
						}
					});
					
				}
				
				
			}
		}
	}
}
