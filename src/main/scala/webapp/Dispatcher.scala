package webapp

import org.scalatra.ScalatraServlet


class Dispatcher extends ScalatraServlet {
  println("dispatcher start")

  get("/simulation") {
    "hello world"
  }

}
