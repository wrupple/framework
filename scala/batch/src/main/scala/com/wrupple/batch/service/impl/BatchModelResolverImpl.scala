package com.wrupple.batch.service.impl

import com.wrupple.batch.service.BatchModelResolver
import com.wrupple.muba.bpm.domain.{ApplicationContext, WorkRequest}
import com.wrupple.muba.event.domain.FieldDescriptor
import org.apache.logging.log4j.scala.Logging

import scala.collection.JavaConverters._
import scala.collection.mutable

class BatchModelResolverImpl extends BatchModelResolver with Logging {
  private final val JOBS = "wrupple.batch.jobs"

  override def addJob(applicationContext: ApplicationContext, job: WorkRequest, resolvesField: String) = {
    val jobs: mutable.HashMap[String, WorkRequest] = assertJobs(applicationContext)
    if (logger.delegate.isDebugEnabled) {
      logger.debug(s"new batch job request for field ${resolvesField} ")
    }

    jobs.put(resolvesField, job)
    job
  }


  override def isInJobList(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext): Boolean = {
    val jobs: mutable.HashMap[String, WorkRequest] = assertJobs(applicationContext)
    val resolvedField = fieldDescriptor.getFieldId

    val resolved = jobs.contains(resolvedField)

    if (logger.delegate.isDebugEnabled) {
      if (resolved) {
        logger.debug(s"    field ${resolvedField} is solvable")
      } else {
        logger.debug(s"    no batch job for field ${resolvedField} ")
      }
    }

    resolved
  }

  override def getJobsToRun(applicationContext: ApplicationContext) = {
    val jobs: mutable.HashMap[String, WorkRequest] = assertJobs(applicationContext)

    if (logger.delegate.isDebugEnabled) {
      logger.debug(s"fields solvable by batch ${jobs.keySet} ")
    }

    jobs.values
  }

  private def assertJobs(applicationContext: ApplicationContext) = {
    applicationContext.
      asScala.
      asInstanceOf[mutable.Map[String, mutable.HashMap[String, WorkRequest]]].
      getOrElseUpdate(JOBS,
        {
          new mutable.HashMap[String, WorkRequest]()
        }
      )
  }

  override def getReducedJobOutput(fieldDescriptor: FieldDescriptor, applicationContext: ApplicationContext) = {
    val jobs: mutable.HashMap[String, WorkRequest] = assertJobs(applicationContext)
    if (logger.delegate.isDebugEnabled) {
      logger.debug(s"batch job result for variable ${fieldDescriptor.getFieldId}")
    }
    val job = jobs.get(fieldDescriptor.getFieldId).get
    if (logger.delegate.isDebugEnabled) {
      logger.debug(s"     result is ${job.getConvertedResult}")
    }
    job.getConvertedResult
  }

  override def hasResult(applicationContext: ApplicationContext, job: WorkRequest) = {
    val r = job.getConvertedResult != null;
    if (logger.delegate.isDebugEnabled) {
      if (r) {
        logger.debug("result available")
      } else {
        logger.debug("result NOT available")
      }
    }
    r
  }
}
