package com.wrupple.muba.catalogs.server.service

import com.wrupple.muba.event.domain.FieldDescriptor


trait DataTypeMapper {

  def mapDataType(typeDefinition: String, fieldDescriptor: FieldDescriptor)
}
