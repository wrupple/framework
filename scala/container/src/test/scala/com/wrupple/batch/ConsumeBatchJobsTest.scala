package com.wrupple.batch

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSuite}

class ConsumeBatchJobsTest
    extends FunSuite
    with BeforeAndAfter
    with MockitoSugar {

  //FIXME translate BPMTest, then SubmitToApplicationTest

  /*


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
