package com.wrupple.batch.service.impl

import com.wrupple.batch.service.BatchModelResolver
import com.wrupple.muba.event.domain.FieldDescriptor


class BatchVariableDescriptor(
                               fieldDescriptor: FieldDescriptor,
                               applicationContext: ApplicationContext,
                               resolver: BatchModelResolver) extends VariableDescriptor {


  override def getField: FieldDescriptor = fieldDescriptor

  override def getValue: AnyRef = resolver.getReducedJobOutput(fieldDescriptor, applicationContext);
}
