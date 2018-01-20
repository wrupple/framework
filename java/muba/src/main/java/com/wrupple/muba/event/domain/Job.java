package com.wrupple.muba.event.domain;

public interface Job extends CatalogEntry {
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
