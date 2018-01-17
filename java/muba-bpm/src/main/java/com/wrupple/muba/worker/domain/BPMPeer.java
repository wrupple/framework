package com.wrupple.muba.worker.domain;

import com.wrupple.muba.event.domain.ManagedObject;

import java.util.Date;

public interface BPMPeer extends ManagedObject, com.wrupple.muba.event.domain.Host {


    /**
     * @return with a private key we can receive data and we know it's them for
     * sure
     */
    String getPrivateKey();

    void setPrivateKey(String key);


    void setExpirationDate(Date expirationDate);


    Object getLastLocation();

    String getBPUrlBase();


}
