package com.resurf.graph

import com.resurf.common.{NodeProfile, WebRequest, RequestSummary}
import org.graphstream.graph._
import org.graphstream.graph.implementations.MultiGraph

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import org.slf4j.LoggerFactory


abstract class RGraphLike {
  def addNode(nodeId: String)

  def addLink(srcId: String, dstId: String, details: RequestSummary)

  def getLinkIdAsString(src: String, dst: String) = s"$src->$dst"

  def processRequest(newEvent: WebRequest): Unit
}

case class Link(linkId: String, srcId: String, dstId: String, repo: TimeRepo)

class TimeRepo() {
  private val repo: mutable.Buffer[RequestSummary] = new ArrayBuffer()
  def add(t: RequestSummary) = repo.append(t)

  override def toString: String = {
    "{ " + repo.mkString(" , ") + " }"
  }

  def getRepo: List[RequestSummary] = repo.toList
}

/** We have one such graph for each src IP*/
class ReferrerGraph(user: String) extends RGraphLike {

  private[this] val logger =  LoggerFactory.getLogger(this.getClass)
  private val graph: Graph = new MultiGraph(s"RG:$user",false,true)

  def addReceivingNode(nodeId: String, details: RequestSummary) = {
    logger.debug(s"Adding node $nodeId with details $details")
    val node: Node = graph.getNode(nodeId)
    if(node == null) {
      logger.debug("First time seeing node: {}", nodeId)
      val newRepo = new TimeRepo()
      newRepo.add(details)
      this.addNode(nodeId)
      graph.getNode(nodeId).asInstanceOf[Node].setAttribute("a", newRepo)
    } else {
      logger.debug("Updating existing node {}", nodeId)
      val attr: TimeRepo = node.asInstanceOf[Node].getAttribute[TimeRepo]("a")
      if(attr == null) graph.getNode(nodeId).asInstanceOf[Node].setAttribute("a", attr)
      else attr.add(details)
    }
  }

  override def addNode(nodeId: String) = graph.addNode(nodeId).asInstanceOf[Node]

  override def addLink(srcId: String, dstId: String, details: RequestSummary) = {
    logger.debug(s"Adding edge from $srcId to $dstId")
    val edgeId = getLinkIdAsString(srcId,dstId)

    graph.addNode(srcId)
    this.addReceivingNode(dstId,details)

    // Gets the edge if it exists, else it returns null
    val edge: Edge = graph.getEdge(edgeId)
    if(edge == null) {
      logger.debug(s"New edge from $srcId to $dstId")
      graph.addEdge(edgeId.toString,srcId,dstId)
      val newRepo = new TimeRepo()
      newRepo.add(details)
      val e = graph.getEdge(edgeId.toString).asInstanceOf[Edge]
      assert(e != null, "Edge was just added, it should not be null")
      e.setAttribute("a", newRepo)
    } else {
      edge.asInstanceOf[Edge].getAttribute[TimeRepo]("a").add(details)
    }
  }

  private def getInternalNodes = graph.getEachNode.asScala.map((n: Node) => n)

  // TODO: Here we return all the details of the node.
  // the timings, the degrees, the content types, etc.
  def getNodeDetailedInfo: Seq[NodeProfile] = {
    for( n <- getInternalNodes ) {
      println(n.getInDegree)
      println(n.getOutDegree)
    }
    ???
  }

  def getLinks: Iterable[Link] = graph.getEachEdge.asScala.map{
    (x: Edge) =>
      val repo: TimeRepo = x.getAttribute("a")
      Link(x.getId,x.getSourceNode.toString, x.getTargetNode.toString, repo)
  }

  def viz = graph.display()

  override def processRequest(newEvent: WebRequest) = {
    val referrer = newEvent.referrer
    referrer match {
      case None => addReceivingNode(newEvent.url.toString,newEvent.getSummary)
      case Some(ref) =>
        addLink(ref.toString, newEvent.url.toString, newEvent.getSummary)
    }
  }
}
