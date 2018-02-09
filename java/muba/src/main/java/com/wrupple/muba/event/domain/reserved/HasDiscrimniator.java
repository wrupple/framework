package com.wrupple.muba.event.domain.reserved;

public interface HasDiscrimniator  {

    final String FIELD = "distinguishedName";


    String getDiscriminator();

void setDiscriminator(String discrimniator);
}
