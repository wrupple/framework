package com.wrupple.muba.bpm.domain;

import com.wrupple.muba.event.domain.CatalogEntry;

import java.util.List;

public interface Job extends CatalogEntry {

    List<Long> getDependencies();

    /**
     * usually sets a reference to the output of the application item on the output field and picks the next application item to invoke
     *
     * @return
     */
    String getExit/*Handler*/();

    String getCancel();

    String getError();


    String getDescription();

    /**
     * @return explicitly links next activity to start when this finishes
     */
    CatalogEntry getExplicitSuccessorValue();

}
