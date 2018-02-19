package com.wrupple.muba.event.domain;

import com.wrupple.muba.event.domain.reserved.HasContract;
import com.wrupple.muba.event.domain.reserved.HasSentence;

public interface Invocation extends Contract,HasSentence,HasContract {

    String Invocation_CATALOG = "Invocation";

}
