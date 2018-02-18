package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasEvent;
import com.wrupple.muba.event.domain.reserved.HasLiveContext;
import com.wrupple.muba.event.domain.reserved.HasSentence;
import org.apache.commons.chain.Catalog;

import java.util.List;

public interface Intent extends Event,HasSentence,HasEvent {

	String ExplicitIntent_CATALOG = "Intent" ;

	<T > T getConvertedResult();

	void setResult(Object result);

	void setError(Exception e);

}
