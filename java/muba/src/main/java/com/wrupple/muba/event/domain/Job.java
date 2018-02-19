package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasOutput;

public interface Job extends CatalogEntry,HasOutput {
    /**
     * usually sets a reference to the output of the application item on the output field and picks the next application item to invoke
     *
     * @return
     */
    Object getExit/*Handler*/();

    Object getCancel();

    Object getError();


    String getDescription();

    /**
     * @return explicitly links next activity to start when this finishes
     */
    CatalogEntry getExplicitSuccessorValue();


    Boolean getKeepOutput();

}
