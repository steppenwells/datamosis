package model

class DataNode(val position: Position) {

  var neighbours: List[DataNode] = Nil

  var inflightMessages: List[(Message, Long)] = Nil
  var processedMessageIds: List[Long] = Nil
  var data: Map[Subject, DataPoint] = Map()

  def addNeighbour(node: DataNode) {
    neighbours = node :: neighbours
  }

  def bindNeighbours {
    for(
      x <- (position.x - 1) to (position.x + 1);
      y <- (position.y - 1) to (position.y + 1)
    ) {
      val nPos = Position(x,y)
      if(nPos != position) {
        Network.nodes.get(nPos).foreach( addNeighbour )
      }
    }
  }

  def asDataNodeJson(s: Subject) = {
    val subjectData = data.get(s)
    DataNodeJson(
      subjectData.map(_.payload).getOrElse(""),
      subjectData.map(_.strength).getOrElse(0f),
      subjectData.map(_.`type`).getOrElse("")
    )
  }

  def receiveMessage(m: Message, step: Long) {
    synchronized {
      if(!processedMessageIds.contains(m.id)) {
        inflightMessages = (m -> step) :: inflightMessages

        data = data + (Subject(m.subject, m.`type`) -> DataPoint(m.payload, m.strength, m.`type`))

        processedMessageIds = m.id :: processedMessageIds
      }
    }
  }

  def clean() {
    synchronized {

      data = data.mapValues(d => d.copy(strength = d.strength * model.Message.timeDecayFactors(d.`type`)))
      data = data.filter { case (_, d) => d.strength > 0.15 }
    }
  }

  def tick(step: Long) {

    synchronized {

      for (
        (m, s) <- inflightMessages;
        node <- neighbours
      ) {
        if (s == step && m.strength > 0.15f){
          m match {
            case n: NewsMessage => node.receiveMessage(m, s + 1)
            case l: LocationMessage => node.receiveMessage(l.copy(strength = l.strength * Message.propDecayFactors("loc")), s + 1)
            case p: ProgramMessage => {
              if(p.target == node.position) {
                node.receiveMessage(p.transformToNews, s + 1)
              } else {
                node.receiveMessage(p, s + 1)
              }
            }
            case _ =>
          }
        }
      }

      inflightMessages = inflightMessages.filter{ case ((m, s)) => s > step }
    }
  }

}

case class DataPoint(payload: String, strength: Float, `type`: String)
case class Subject(subject: String, `type`: String)

case class DataNodeJson(payload: String, strength: Float, `type`: String)
