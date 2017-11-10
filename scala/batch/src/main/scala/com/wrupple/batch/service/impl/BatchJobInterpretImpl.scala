package com.wrupple.batch.service.impl

import java.util

import com.wrupple.batch.service.BatchJobInterpret
import com.wrupple.muba.bpm.domain.ApplicationContext
import org.apache.commons.chain.Context
import org.apache.logging.log4j.scala.Logging

class BatchJobInterpretImpl extends BatchJobInterpret with Logging {

  override def resolve(sentence: util.ListIterator[String], c: Context, batchRunnerGivenName: String) = {
    logger.trace("<BatchInterpret>")
    val context: ApplicationContext = c.asInstanceOf[ApplicationContext]
    //COUNT AS FIELD
    val next = sentence.next()
    val delegateInterpret = context.getRuntimeContext.getEventBus.getInterpret(next);
    if (delegateInterpret == null) {
      logger.trace(s"No interpret with DN ${next} is available")
      sentence.previous();
    } else {
      logger.debug(s"Delegating to interpret ${delegateInterpret} identified by token ${next}")

      delegateInterpret.resolve(sentence, c, batchRunnerGivenName);
    }
    logger.trace("</BatchInterpret>")

  }
}
