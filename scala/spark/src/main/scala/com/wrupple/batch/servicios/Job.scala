package com.wrupple.batch.servicios

import com.google.inject.Module
import com.wrupple.muba.event.server.service.ImplicitEventResolver.Registration

trait Job {

  def modules: Seq[Module]

  def jobs[T <: Registration]: Seq[Class[T]]

}
