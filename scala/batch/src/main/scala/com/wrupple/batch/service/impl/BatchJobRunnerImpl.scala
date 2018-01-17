package com.wrupple.batch.service.impl

import javax.inject.Inject

import com.wrupple.batch.service.{BatchJobRunner, BatchMessageDelegate, BatchModelResolver}
import com.wrupple.muba.event.domain.FieldDescriptor
import org.apache.logging.log4j.scala.Logging

class BatchJobRunnerImpl @Inject()(resolver: BatchModelResolver, messanger: BatchMessageDelegate)
  extends BatchJobRunner with Logging {


  override def solve(applicationContext: ApplicationContext): Boolean = {
    logger.debug("Solving batch jobs if available");
    !resolver.
      getJobsToRun(applicationContext).
      map(job => messanger.send(job)).
      filter(job => resolver.hasResult(applicationContext, job))
      .isEmpty
  }


  override def canHandle(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext): Boolean = {
    if (logger.delegate.isTraceEnabled) {
      logger.trace(s"is ${fieldDescriptor.getFieldId} solvable by batch?")
    }
    resolver.isInJobList(fieldDescriptor, applicationContext)
  }

  override def handleAsVariable(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext): VariableEligibility = {

    if (logger.delegate.isTraceEnabled) {
      logger.trace(s"future batch variable for ${fieldDescriptor.getFieldId}")
    }

    new VariableEligibility {
      override def createVariable(): VariableDescriptor = new BatchVariableDescriptor(fieldDescriptor, applicationContext, resolver)
    }
  }


}

