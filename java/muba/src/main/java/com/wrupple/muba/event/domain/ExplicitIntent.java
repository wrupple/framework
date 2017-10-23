package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasImplicitIntent;
import com.wrupple.muba.event.domain.reserved.HasLiveContext;
import com.wrupple.muba.event.domain.reserved.HasSentence;
import org.apache.commons.chain.Catalog;

import java.util.List;

public interface ExplicitIntent extends Event,HasSentence,HasImplicitIntent {


    String CATALOG = "ExplicitIntent";


	<T > T getConvertedResult();

	void setResult(Object result);

	void setError(Exception e);

}
