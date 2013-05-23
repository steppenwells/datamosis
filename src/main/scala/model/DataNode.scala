package model

class DataNode(val position: Position) {

  var neighbours: List[DataNode] = Nil

  def addNeighbour(node: DataNode) {
    neighbours = node :: neighbours
  }

  def bindNeighbours {
    for(
      x <- (position.x - 1) to (position.x + 1);
      y <- (position.x - 1) to (position.y + 1)
    ) {
      val nPos = Position(x,y)
      if(nPos != position) {
        Network.nodes.get(nPos).foreach( addNeighbour )
      }
    }
  }

}
