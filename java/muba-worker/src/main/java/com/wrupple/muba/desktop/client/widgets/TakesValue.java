package com.wrupple.muba.desktop.client.widgets;

public interface TakesValue<V> {

    V getValue();

    void setValue(V value);
}
