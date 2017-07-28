package com.wrupple.muba.bootstrap.domain.reserved;

/**
 * previously known as "saveToField"
 *
 * Created by japi on 28/07/17.
 */
public interface HasOutput  {

    /**
     *
     * @return the destination field where processed data is referenced
     */
    String getOutputField();

    /**
     *
     * @param f  the destination field where processed data is referenced
     */
    void setOutputField(String f);

}
