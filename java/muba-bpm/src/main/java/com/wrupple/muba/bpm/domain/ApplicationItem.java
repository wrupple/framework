package com.wrupple.muba.bpm.domain;

import java.util.List;

import com.wrupple.muba.bootstrap.domain.CatalogEntry;
import com.wrupple.muba.bootstrap.domain.ImplicitIntent;
import com.wrupple.muba.bootstrap.domain.reserved.HasChildrenValues;
import com.wrupple.muba.bootstrap.domain.reserved.HasDistinguishedName;
import com.wrupple.muba.bootstrap.domain.reserved.HasProperties;
import com.wrupple.muba.bootstrap.domain.reserved.HasStakeHolder;

public interface ApplicationItem extends CatalogEntry,HasProperties,HasStakeHolder,HasDistinguishedName,ImplicitIntent,HasChildrenValues<Long,ApplicationItem>{
	
	public  final String CATALOG = "ApplicationItem";


    public String getOutputHandler();

    public void setOutputHandler(String string);

    //TODO input is deermined by service contract and first task's type, output by last task's type, maybe have an ephemeral field with these catalog id's

    List<Long> getProcess();

    <T extends ProcessTaskDescriptor> List<T> getProcessValue();

    public List<Long> getRequiredElements();

	public Long getPeer();

    public String getDescription();

}