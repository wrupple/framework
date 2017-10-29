package com.wrupple.batch.service.impl

import javax.inject.Inject

import com.wrupple.batch.service.{BatchJobRunner, BatchMessageDelegate, BatchModelResolver}
import com.wrupple.muba.bpm.domain.{ApplicationContext, VariableDescriptor}
import com.wrupple.muba.bpm.server.service.VariableEligibility
import com.wrupple.muba.event.domain.FieldDescriptor

class BatchJobRunnerImpl @Inject()(resolver: BatchModelResolver, messanger: BatchMessageDelegate) extends BatchJobRunner {


  override def solve(applicationContext: ApplicationContext): Boolean = !resolver.
    getJobsToRun(applicationContext).
    map(job => messanger.send(job)).
    filter(job => resolver.hasResult(applicationContext, job))
    .isEmpty


  override def canHandle(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext): Boolean = resolver.isInJobList(fieldDescriptor, applicationContext)

  override def handleAsVariable(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext): VariableEligibility = new VariableEligibility {
    override def createVariable(): VariableDescriptor = new VariableDescriptor {
      override def getField: FieldDescriptor = fieldDescriptor

      override def getValue: AnyRef = resolver.getReducedJobOutput(fieldDescriptor, applicationContext);
    }
  }


}
