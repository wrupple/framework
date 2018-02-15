package com.wrupple.batch

import java.io.File

import com.typesafe.config.ConfigFactory

import scala.util.Properties

class Configuration(fileName: String) {

  val config = ConfigFactory.parseFile(new File(fileName)).resolve()

  def envOrElseConfig(name: String): String = {
    Properties.envOrElse(
      name.toUpperCase.replaceAll("""\.""", "_"),
      config.getString(name)
    )
  }
}
