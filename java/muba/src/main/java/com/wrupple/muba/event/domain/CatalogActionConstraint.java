package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasProperties;
import com.wrupple.muba.event.domain.reserved.HasSentence;

/**
 * Created by japi on 14/10/17.
 */
public interface CatalogActionConstraint extends DataEvent, HasProperties, HasSentence {

    // create,update,delete,evaluate
    //public String getName(); = Handler
    //void setName(String h);

    String getSeed();
    void setSeed(String s);

    String getDescription();


}
