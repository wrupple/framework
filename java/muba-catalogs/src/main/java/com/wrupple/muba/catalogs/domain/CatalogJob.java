package com.wrupple.muba.catalogs.domain;

import com.wrupple.muba.event.domain.CatalogEntry;
import com.wrupple.muba.event.domain.DataEvent;
import com.wrupple.muba.event.domain.reserved.HasProperties;
import com.wrupple.muba.event.domain.reserved.HasSentence;

/**
 * Created by japi on 14/10/17.
 */
public interface CatalogJob extends DataEvent,HasProperties,HasSentence {

    // create,update,delete,evaluate
    //public String getName(); = Handler
    //void setName(String h);

    public String getSeed();
    void setSeed(String s);

    public String getDescription();


}
