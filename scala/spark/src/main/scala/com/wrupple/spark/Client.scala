package com.wrupple.spark

import com.wrupple.SparkClient

import scala.collection.JavaConverters._

object Client {

  def main(args: Array[String]) {


    val spark: SparkClient = new SparkClient(Configuracion)

    //java(http://www.pitman.co.za/projects/charva/index.html)
    //https://github.com/Tenchi2xh/Scurses

    var input = Array("Hola!")
    Console.println(input.apply(0))
    do {

      input = readLine("░░▒▓███ ODS >").split("""[ \t]""")

      spark.
        event.
        getInterpret(":").
        run(
          input.toList.asJava.listIterator(),
          spark.session
        )

    } while (!"adios".equalsIgnoreCase(input.apply(0)))

  }


}
