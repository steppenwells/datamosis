package model

trait Message {

  def id: Long
  def subject: String
  def payload: String
  def strength: Float
}

case class NewsMessage(val id: Long, val subject: String, val payload: String, val strength: Float) extends Message
