package com.wrupple.batch.service.impl

import com.wrupple.batch.service.BatchModelResolver
import com.wrupple.muba.bpm.domain.{ApplicationContext, WorkRequest}
import com.wrupple.muba.event.domain.FieldDescriptor

import scala.collection.JavaConverters._
import scala.collection.mutable

class BatchModelResolverImpl extends BatchModelResolver {
  private final val JOBS = "batch.jobs"

  override def addJob(applicationContext: ApplicationContext, job: WorkRequest, resolvesField: String) = {
    val jobs: mutable.HashMap[String, WorkRequest] = applicationContext.asScala.asInstanceOf[mutable.Map[String, mutable.HashMap[String, WorkRequest]]].getOrElseUpdate(JOBS, {
      new mutable.HashMap[String, WorkRequest]()
    })
    jobs.put(resolvesField, job)
    job
  }

  override def isInJobList(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext): Boolean = {
    val jobs: mutable.HashMap[String, WorkRequest] = applicationContext.asScala.asInstanceOf[mutable.Map[String, mutable.HashMap[String, WorkRequest]]].getOrElseUpdate(JOBS, {
      new mutable.HashMap[String, WorkRequest]()
    })


    jobs.contains(fieldDescriptor.getFieldId)

  }

  override def getJobsToRun(applicationContext: ApplicationContext) = {
    val jobs: mutable.HashMap[String, WorkRequest] = applicationContext.asScala.asInstanceOf[mutable.Map[String, mutable.HashMap[String, WorkRequest]]].getOrElseUpdate(JOBS, {
      new mutable.HashMap[String, WorkRequest]()
    })

    jobs.values
  }

  override def hasResult(applicationContext: ApplicationContext, job: WorkRequest) = {
    job.getConvertedResult != null;
  }

  override def getReducedJobOutput(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext) = {
    val jobs: mutable.HashMap[String, WorkRequest] = applicationContext.asScala.asInstanceOf[mutable.Map[String, mutable.HashMap[String, WorkRequest]]].getOrElseUpdate(JOBS, {
      new mutable.HashMap[String, WorkRequest]()
    })

    val job = jobs.get(fieldDescriptor.getFieldId).get
    job.getConvertedResult
  }

}
