package com.resurf.graph

import com.resurf.common.{GraphSummary, NodeProfile, WebRequest, RequestSummary}
import org.graphstream.graph._
import org.graphstream.graph.implementations.MultiGraph
import org.graphstream.algorithm.ConnectedComponents

import scala.collection.JavaConverters._
import org.slf4j.LoggerFactory


abstract class RGraphLike {
  def addNode(nodeId: String, details: Option[RequestSummary])

  def addLink(srcId: String, dstId: String, details: RequestSummary)

  def getLinkIdAsString(src: String, dst: String) = s"$src->$dst"

  def processRequest(newEvent: WebRequest): Unit
}

case class Link(linkId: String, srcId: String, dstId: String, repo: TimeRepo)



/** We have one such graph for each src IP */
class ReferrerGraph(user: String) extends RGraphLike {

  val SummaryAttribute = "Requests"

  private[this] lazy val logger = LoggerFactory.getLogger(this.getClass)
  private val graph: Graph = new MultiGraph(s"RG:$user", false, true)

  override def addNode(nodeId: String, details: Option[RequestSummary] = None) = {
    details match {
      case None =>
        graph.addNode(nodeId).asInstanceOf[Node]
      case Some(request) =>
        logger.debug(s"Adding node $nodeId with details $details")
        val node: Node = graph.getNode(nodeId)
        if (node == null) {
          logger.debug("First time seeing node: {}", nodeId)
          val newRepo = new TimeRepo()
          newRepo.add(request)
          this.addNode(nodeId)
          graph.getNode(nodeId).asInstanceOf[Node].setAttribute(SummaryAttribute, newRepo)
        } else {
          logger.debug("Updating existing node {}", nodeId)
          val attr: TimeRepo = node.asInstanceOf[Node].getAttribute[TimeRepo](SummaryAttribute)
          if (attr == null) graph.getNode(nodeId).asInstanceOf[Node].setAttribute(SummaryAttribute, attr)
          else attr.add(request)
        }
    }
  }

  override def addLink(srcId: String, dstId: String, details: RequestSummary) = {
    logger.debug(s"Adding edge from $srcId to $dstId")
    val edgeId = getLinkIdAsString(srcId, dstId)

    this.addNode(srcId)
    this.addNode(dstId, Some(details))

    // Gets the edge if it exists, else it returns null
    val edge: Edge = graph.getEdge(edgeId)
    if (edge == null) {
      logger.debug(s"New edge from $srcId to $dstId")
      graph.addEdge(edgeId.toString, srcId, dstId, true)
      val newRepo = new TimeRepo()
      newRepo.add(details)
      val e = graph.getEdge(edgeId.toString).asInstanceOf[Edge]
      assert(e != null, "Edge was just added, it should not be null")
      e.setAttribute(SummaryAttribute, newRepo)
    } else {
      edge.asInstanceOf[Edge].getAttribute[TimeRepo](SummaryAttribute).add(details)
    }
  }

  private[this] def getInternalNodes = graph.getEachNode.asScala.map((n: Node) => n)

  private[this] def getTimeRepo(element: Element): Option[TimeRepo] = {
    val at: TimeRepo = element.getAttribute(SummaryAttribute)
    if (at == null) None
    else Some(at.asInstanceOf[TimeRepo])
  }


  def getGraphSummary: GraphSummary = {
    val cc = new ConnectedComponents()
    cc.init(graph)

    GraphSummary(
      nodeCount = graph.getNodeCount,
      linkCount = graph.getEdgeCount,
      connectedComponentCount = cc.getConnectedComponentsCount
    )
  }

  // TODO: Here we return all the details of the node.
  // the timings, the degrees, the content types, etc.
  def getNodeDetailedInfo: Iterable[NodeProfile] = {
    for (n <- getInternalNodes) yield {
      val repo = getTimeRepo(n)

      // - Get incoming request
      // - Get outgoing requests
      // - For each incoming request find time gap to next outgoing

      val profile = NodeProfile(
        fanIn = n.getInDegree,
        fanOut = n.getOutDegree,
        totalRequest = repo match {
          case None => 0;
          case Some(t) => t.size
        }
      )
      logger.debug(s"On ${n.getId} $profile")
      profile
    }
  }

  def getLinks: Iterable[Link] = graph.getEachEdge.asScala.map {
    (x: Edge) =>
      val repo: TimeRepo = x.getAttribute(SummaryAttribute)
      Link(x.getId, x.getSourceNode.toString, x.getTargetNode.toString, repo)
  }

  def viz = graph.display()

  override def processRequest(newEvent: WebRequest) = {
    val referrer = newEvent.referrer
    referrer match {
      case None => addNode(newEvent.url.toString, Some(newEvent.getSummary))
      case Some(ref) =>
        addLink(ref.toString, newEvent.url.toString, newEvent.getSummary)
    }
  }
}
