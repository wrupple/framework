package com.wrupple.muba.desktop.client.activity.widgets.editors.composite.delegates;

import com.wrupple.muba.cms.client.services.ContentManagementSystem;
import com.wrupple.muba.worker.shared.services.StorageManager;
import com.wrupple.muba.desktop.client.services.logic.DesktopManager;
import com.wrupple.vegetate.domain.FieldDescriptor;


/**
 * 
 * Add: select
 * Remove: unlink ¿remove?
 * 
 * @author japi
 *
 */
public class OneToOneRelationshipDelegate  extends OneToManyRelationshipDelegate{

	public OneToOneRelationshipDelegate(DesktopManager dm,
			FieldDescriptor descriptor, ContentManagementSystem cms,
			StorageManager sm, 
			String permanentDelete) {
		super(dm, descriptor, cms, sm,permanentDelete);
		super.limitToOne=true;
	}

}
