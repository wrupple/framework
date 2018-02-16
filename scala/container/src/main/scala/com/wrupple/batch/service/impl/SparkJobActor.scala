package com.wrupple.batch.service.impl

import com.wrupple.muba.worker.domain.ApplicationContext
import com.wrupple.muba.worker.server.service.StateTransition
import org.apache.spark.launcher.{SparkAppHandle, SparkLauncher}

class SparkJobActor(applicationContext: ApplicationContext,
                    stateTransition: StateTransition[ApplicationContext],
                    appResource: String,
                    mainClass: String,
                    master: String,
                    conf: Option[Map[String, String]])
    extends {

  def receive = {

    val launcher = new SparkLauncher()
      .setAppResource(appResource)
      .setMainClass(mainClass)
      .setMaster(master)
    if (conf.isDefined) {
      for ((key, value) <- conf.get.toList) {
        launcher.setConf(key, value)
      }
    }

    val listener = new SparkAppHandle.Listener {
      override def infoChanged(handle: SparkAppHandle): Unit = {}
      override def stateChanged(handle: SparkAppHandle): Unit = {
        if (handle.getState.isFinal) {
          stateTransition.execute(applicationContext)
        }
      }
    }

    val handle = launcher.startApplication(listener)

  }
}

object SparkJobActor {
  case object StateChanged
  case class Finished(state: SparkAppHandle.State)
}
