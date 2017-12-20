package com.wrupple.muba.desktop.client.services.logic;

import com.wrupple.muba.desktop.client.services.presentation.layout.IndexedLayoutDelegate;

public interface HistogramLayoutDelegate extends IndexedLayoutDelegate {

    void setNumericField(String numericField);

    void setFlipVertical(String value);

    void setGridIverted(String s);

    void setCumulative(String value);

    void setAverageNumericValues(String averageNumericValues);
}
