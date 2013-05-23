package model

object Network {

  val Size = 50 // a 100 by 100 grid

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
}

case class Position(x: Int, y: Int)
