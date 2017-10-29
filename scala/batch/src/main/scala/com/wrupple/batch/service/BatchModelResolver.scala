package com.wrupple.batch.service

import com.wrupple.muba.bpm.domain.{ApplicationContext, WorkRequest}
import com.wrupple.muba.event.domain.FieldDescriptor

trait BatchModelResolver {
  def addJob(applicationContext: ApplicationContext, job: WorkRequest, resolvesField: String): WorkRequest

  def isInJobList(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext): Boolean

  def getJobsToRun(applicationContext: ApplicationContext): Iterable[WorkRequest]

  def hasResult(applicationContext: ApplicationContext, job: WorkRequest): Boolean

  def getReducedJobOutput(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext): AnyRef

}
