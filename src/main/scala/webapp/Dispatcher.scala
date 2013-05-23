package webapp

import org.scalatra.ScalatraServlet
import model.{NewsMessage, Position, Network}
import org.scalatra.json._
import org.json4s.{DefaultFormats, Formats}
import java.awt.image.BufferedImage
import java.awt.{Color, Rectangle}
import javax.imageio.{ImageWriteParam, ImageWriter, ImageIO}


class Dispatcher extends ScalatraServlet with NativeJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  println("dispatcher start")

  get("/simulation") {
    html.simulation.render()
  }

  get("/state") {
    contentType = formats("json")

    Network.asJson(params("subject"))
  }

  post("/sendMessage") {

    val req = parsedBody.extract[sendMessagePayload]

    val t = req.`type`
    val pos = Position(req.x.toInt, req.y.toInt)
    val id = Network.generateMessageId
    t match {
      case "news" => {
        Network.nodes(pos).receiveMessage(NewsMessage(id, req.subject, req.payload, 1f), Network.step)
      }
      case s => println("unknown message type " + s)
    }
  }

  get("/tick") {
    val subject = params("subject")
    contentType = formats("json")
    Network.tick
    Network.asJson(subject)
  }

  get("/tickImg") {
    val subject = params("subject")
    contentType = "image/png"
    Network.tick

    val buffer = new BufferedImage(Network.Size * 10, Network.Size * 10, BufferedImage.TYPE_INT_ARGB)
    val g = buffer.createGraphics()
    for (n <- Network.nodes.values) {
      val strength = n.data.get(subject).map(_.strength).getOrElse(0f)
      g.setColor(new Color(0f, 1f, 0f, strength))
      g.fill(new Rectangle(n.position.x * 10, n.position.y * 10, 10, 10))
    }

    ImageIO.write(buffer, "png", response.getOutputStream)

    Unit
  }

}

case class sendMessagePayload(`type`: String, subject: String, payload: String, x: String, y: String)
