package com.wrupple.batch.service.impl

import java.util

import com.wrupple.batch.service.BatchJobInterpret
import com.wrupple.muba.bpm.domain.ApplicationContext
import org.apache.commons.chain.Context

class BatchJobInterpretImpl extends BatchJobInterpret {

  override def resolve(sentence: util.ListIterator[String], c: Context, batchRunnerGivenName: String) = {
    val context: ApplicationContext = c.asInstanceOf[ApplicationContext]
    //COUNT AS FIELD
    val next = sentence.next()
    val delegateInterpret = context.getRuntimeContext.getEventBus.getInterpret(next);
    if (delegateInterpret == null) {
      sentence.previous();
    } else {
      delegateInterpret.resolve(sentence, c, batchRunnerGivenName);
    }

  }
}
