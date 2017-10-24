package com.wrupple.muba.bpm

import com.google.inject.{Guice, Injector, Module}
import com.wrupple.muba.event.EventBus
import org.apache.logging.log4j.scala.Logging
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSuite}

import scala.collection.JavaConverters._

class ConsumeBatchJobsTest extends FunSuite with BeforeAndAfter with MockitoSugar with Logging {

  //FIXME translate BPMTest, then SubmitToApplicationTest

  System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE")

  protected var injector: Injector = null


  def init(m: List[Module]): Unit = {
    injector = Guice.createInjector(m.asJava)
    registerServices(injector.getInstance(classOf[EventBus]))
  }

  protected def registerServices(switchs: EventBus): Unit = {
    println("register services!!");
  }


  /*
  // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
  libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.21" % "test"

  // this works
  val objects = Array("a", 1)
  val arrayOfObject = objects.asInstanceOf[Array[Object]]
  AJavaClass.printObjectArray(arrayOfObject)

    test ("test login service") {

      // (1) init
      val service = mock[LoginService]

      // (2) setup: when someone logs in as "johndoe", the service should work;
      //            when they try to log in as "joehacker", it should fail.
      when(service.login("johndoe", "secret")).thenReturn(Some(User("johndoe")))
      when(service.login("joehacker", "secret")).thenReturn(None)

      // (3) access the service
      val johndoe = service.login("johndoe", "secret")
      val joehacker = service.login("joehacker", "secret")

      // (4) verify the results
      assert(johndoe.get == User("johndoe"))
      assert(joehacker == None)

    }
  */
}