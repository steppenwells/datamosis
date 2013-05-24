package webapp

import org.scalatra.ScalatraServlet
import model._
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
      case "loc" => {
        Network.nodes(pos).receiveMessage(LocationMessage(id, req.subject, req.payload, 1f), Network.step)
      }
      case "prog" => {
        val target = Position(req.targetX.get.toInt, req.targetY.get.toInt)
        Network.nodes(pos).receiveMessage(ProgramMessage(id, req.subject, req.payload, 1f, target), Network.step)
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
    val PixelSize = 5

    val subject = params("subject")
    contentType = "image/png"
    Network.tick

    val buffer = new BufferedImage(Network.Size * PixelSize, Network.Size * PixelSize, BufferedImage.TYPE_INT_ARGB)
    val g = buffer.createGraphics()
    for (n <- Network.nodes.values) {
      val newsStrength = n.data.get(Subject(subject, "news")).map(_.strength).getOrElse(0f)
      val locStrength = n.data.get(Subject(subject, "loc")).map(_.strength).getOrElse(0f)
      val progStrength = n.data.get(Subject(subject, "prog")).map(_.strength).getOrElse(0f)
      g.setColor(new Color(newsStrength, locStrength, progStrength, 1f))
      g.fill(new Rectangle(n.position.x * PixelSize, n.position.y * PixelSize, PixelSize, PixelSize))
    }

    ImageIO.write(buffer, "png", response.getOutputStream)

    Unit
  }

  get("/nodeData") {
    contentType = formats("json")

    val pos = Position(params("x").toInt, params("y").toInt)
    val node = Network.nodes(pos)

    val data = for ((sub, data)<- node.data) yield{
      NodeDataOutput(sub.`type`, sub.subject, data.payload, data.strength)
    }
    NodeDataResponse(data.toList.sortBy(_.strength))
  }
}

case class NodeDataOutput(`type`: String, subject: String, payload: String, strength: Float)
case class NodeDataResponse(nodeData: List[NodeDataOutput])

case class sendMessagePayload(
 `type`: String,
 subject: String,
 payload: String,
 x: String,
 y: String,
 targetX: Option[String],
 targetY: Option[String])
