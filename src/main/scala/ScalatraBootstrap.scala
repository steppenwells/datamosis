import model.Network
import org.scalatra._
import javax.servlet.ServletContext
import webapp.Dispatcher

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {

    println("init")

    Network.init

    // Mount one or more servlets
    context.mount(new Dispatcher, "/*")
  }
}