package edu.luc.etl.cs313.scala.clickcounter.service
package api

import dispatch._
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable._

class ApiaryHttpSpec extends HttpSpec {
  val serviceRoot = host("private-14b8e-clickcounter.apiary-mock.com")
}

class LocalHttpSpec extends HttpSpec {
  val serviceRoot = host("localhost", 8080)
}

class HerokuHttpSpec extends HttpSpec {
  val serviceRoot = host("http://laufer-clickcounter.herokuapp.com")
}

trait HttpSpec extends Specification with JsonMatchers {

  sequential

  def serviceRoot: Req

  def beEqualToDouble(d: Double) = beEqualTo(d) ^^ ((_: String).toDouble)

  val cMin = 0

  val cMax = 5

  "The click counter service, on its collection of counters," should {

    "allow the creation of a new counter" in {
      val request = serviceRoot / "counters" / "123" <<? Map("min" -> cMin.toString, "max" -> cMax.toString)
      val response = Http(request.PUT)
      response().getStatusCode === 201
    }

    "include the newly created counter in the list of counters" in {
      val request = serviceRoot / "counters"
      val response = Http(request OK as.String)
      response() contains "123"
    }

    "retrieve an existing counter" in {
      val request = serviceRoot / "counters" / "123"
      val response = Http(request OK as.String)
      val counter = response()
      counter must / ("min" -> beEqualToDouble(cMin))
      counter must / ("value" -> beEqualToDouble(cMin))
      counter must / ("max" -> beEqualToDouble(cMax))
    }

    "delete an existing counter" in {
      val request = serviceRoot / "counters" / "123"
      val response = Http(request.DELETE)
      response().getStatusCode === 204
    }
  }

  "The click counter service, on a specific counter," should {

    "allow the creation of a new counter" in {
      val request = serviceRoot / "counters" / "123" <<? Map("min" -> cMin.toString, "max" -> cMax.toString)
      val response = Http(request.PUT)
      response().getStatusCode === 201
    }

    "refuse to decrement the counter initially" in {
      val request = serviceRoot / "counters" / "123" / "decrement"
      val response = Http(request.POST)
      response().getStatusCode === 409
    }

    "increment the counter" in {
      val request = serviceRoot / "counters" / "123" / "increment"
      val response = Http(request.POST)
      val result = response()
      result.getStatusCode === 204
      val request2 = serviceRoot / "counters" / "123"
      val response2 = Http(request2 OK as.String)
      val counter = response2()
      counter must / ("min" -> beEqualToDouble(cMin))
      counter must / ("value" -> beEqualToDouble(cMin + 1))
      counter must / ("max" -> beEqualToDouble(cMax))
    }

    "reset the counter" in {
      val request = serviceRoot / "counters" / "123" / "reset"
      val response = Http(request.POST)
      val result = response()
      result.getStatusCode === 204
      val request2 = serviceRoot / "counters" / "123"
      val response2 = Http(request2 OK as.String)
      val counter = response2()
      counter must / ("min" -> beEqualToDouble(cMin))
      counter must / ("value" -> beEqualToDouble(cMin))
      counter must / ("max" -> beEqualToDouble(cMax))
    }

    "retrieve a counter value stream" in {
      todo
    }
  }
}