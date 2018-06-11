package com.wrupple.batch.servicios

import com.google.inject.Module
import com.wrupple.muba.event.server.service.ImplicitEventResolver.Registration

trait Actor {

  def modules: Seq[Module]

  def services[T <: Registration]: Seq[Class[T]]

}
