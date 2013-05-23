package model

object Network {

  val Size = 50 // a 100 by 100 grid

  var nextMessageId: Long = 0
  var step: Long = 0

  val nodes: Map[Position, DataNode] = { for (
      x <- 0 until Size;
      y <- 0 until Size)
    yield {
      val p = Position(x,y)
      p -> new DataNode(p)
    }
  }.toMap

  def init() {
    println("network init")
    nodes.values.foreach(n => n.bindNeighbours)
  }

  def asJson(s: String) = {

    val rows = for( x <- 0 until Size) yield {
      val cells = for (y <- 0 until Size) yield { nodes(Position(x,y)).asDataNodeJson(s) }
      NetworkRow(cells.toList)
    }
    NetworkJson(rows.toList)
  }

  def generateMessageId = {
    nextMessageId = nextMessageId + 1
    nextMessageId
  }

  def tick {
    nodes.values.foreach(_.tick(step))
    step = step + 1
  }
}

case class Position(x: Int, y: Int)

case class NetworkJson(rows: List[NetworkRow])
case class NetworkRow(nodes: List[DataNodeJson])

