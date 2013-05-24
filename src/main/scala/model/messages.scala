package model

trait Message {

  def id: Long
  def subject: String
  def payload: String
  def strength: Float

  def `type`: String
}

object Message {
  val timeDecayFactors: Map[String, Float] = Map(
    "news" -> 0.96f,
    "prog" -> 0f,
    "loc" -> 1f
  )

  val propDecayFactors: Map[String, Float] = Map(
      "news" -> 1f,
      "prog" -> 1f,
      "loc" -> 0.75f
    )
}

case class NewsMessage(val id: Long, val subject: String, val payload: String, val strength: Float) extends Message {
  def `type` = "news"
}

case class LocationMessage(val id: Long, val subject: String, val payload: String, val strength: Float) extends Message {
  def `type` = "loc"
}

case class ProgramMessage(val id: Long, val subject: String, val payload: String, val strength: Float, val target: Position) extends Message {
  def `type` = "prog"

  def transformToNews = NewsMessage(Network.generateMessageId, subject, payload, strength)
}
