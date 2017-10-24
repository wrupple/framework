package com.wrupple.muba.catalogs.server.service.impl

import java.util
import javax.inject.{Inject, Named}

import com.wrupple.muba.catalogs.domain.{CatalogActionContext, SparkTableRegistry, _}
import com.wrupple.muba.catalogs.server.service.{DataTypeMapper, TableDescriptorBuilder}
import com.wrupple.muba.event.domain._
import com.wrupple.muba.event.domain.impl.CatalogDescriptorImpl
import com.wrupple.muba.event.domain.reserved.HasAccesablePropertyValues
import com.wrupple.muba.event.server.domain.impl.FieldDescriptorImpl
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.{DataFrame, SQLContext}

import scala.collection.JavaConverters._

class HiveTableDescriptorBuilder @Inject()(val typeMapper: DataTypeMapper, @Named("catalog.storage.spark") val sparkStorage: String) extends TableDescriptorBuilder with Logging {

  @Override
  override def fromTable(context: CatalogActionContext,
                         sqLContext: SQLContext,
                         tableMetadata: SparkTableRegistry): CatalogDescriptor = {
    logger.info(s"building catalog descriptor of table ");

    val regreso: CatalogDescriptorImpl = new CatalogDescriptorImpl
    if (tableMetadata.getClazz() == null) {
      regreso.setClazz(classOf[HasAccesablePropertyValues])
    } else {
      regreso.setClazz(tableMetadata.getClazz)
    }
    regreso.setDescriptiveField(tableMetadata.getDescriptiveField)
    regreso.setDistinguishedName(tableMetadata.getDistinguishedName)
    regreso.setId(tableMetadata.getAssignedCatalogKey())
    regreso.setKeyField(tableMetadata.getKeyField)
    regreso.setName(tableMetadata.getName)
    regreso.setConsolidated(true)
    regreso.setParent(tableMetadata.getParent)

    var dfColumns: DataFrame = sqLContext.sql(s"DESCRIBE ${tableMetadata.getSchema}.${tableMetadata.getDistinguishedName}")

    val fields: util.Map[String, FieldDescriptor] = new util.LinkedHashMap[String, FieldDescriptor]
    if (tableMetadata.getFieldsIds == null) {

    } else {
      val requiredFields = tableMetadata.getFieldsIds.asScala;
      dfColumns = dfColumns.filter(dfColumns(FIELD_DN_HIVE_DESCRIPTION).isin(requiredFields))
    }

    val descriptionColums = dfColumns.columns;
    val fieldIdRowIndex = descriptionColums.indexOf(FIELD_DN_HIVE_DESCRIPTION);
    val dataTypeRowIndex = descriptionColums.indexOf(DATA_TYPE_HIVE_DESCRIPTION);

    dfColumns.collect().foreach(row => {
      var field: FieldDescriptorImpl = new FieldDescriptorImpl().
        makeDefault(row.getString(fieldIdRowIndex), row.getString(fieldIdRowIndex), CatalogEntry.STRING_DATA_TYPE)
      typeMapper.mapDataType(row.getString(dataTypeRowIndex), field)
      fields.put(field.getFieldId, field)
    })

    regreso.setFieldsValues(fields)

    regreso.setStorage(java.util.Arrays.asList(sparkStorage));
    return regreso


  }
}
